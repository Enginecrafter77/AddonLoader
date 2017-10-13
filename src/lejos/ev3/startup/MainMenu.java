package lejos.ev3.startup;

import static com.ec.addonloader.util.Icons.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.jar.JarFile;

import com.ec.addonloader.lib.MenuUtils;
import com.ec.addonloader.lib.ResourceLocation.DataSize;
import com.ec.addonloader.main.*;
import com.ec.addonloader.menu.MappedMenu;
import com.ec.addonloader.menu.MORegistry;
import com.ec.addonloader.util.Icons;
import com.ec.addonloader.util.LoadingAnimation;

import lejos.ev3.startup.ListMenu;
import lejos.ev3.startup.Utils;
import lejos.utility.Delay;
import lejos.ev3.startup.Config;
import lejos.hardware.Battery;
import lejos.hardware.Bluetooth;
import lejos.hardware.BluetoothException;
import lejos.hardware.BrickFinder;
import lejos.hardware.Button;
import lejos.hardware.Key;
import lejos.hardware.LocalBTDevice;
import lejos.hardware.LocalWifiDevice;
import lejos.hardware.RemoteBTDevice;
import lejos.hardware.Sound;
import lejos.hardware.Wifi;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.Font;
import lejos.hardware.lcd.LCD;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.port.Port;
import lejos.hardware.port.TachoMotorPort;
import lejos.internal.ev3.EV3IOPort;
import lejos.internal.io.Settings;
import lejos.internal.io.SystemSettings;
import lejos.internal.io.NativeHCI.LocalVersion;
import lejos.remote.ev3.Menu;
import lejos.remote.ev3.RMIRemoteEV3;

@SuppressWarnings({"restriction"})
public class MainMenu implements Menu {	
	private static final String JAVA_RUN_CP = "jrun -cp ";
	private static final String JAVA_DEBUG_CP = "jrun -Xdebug -Xrunjdwp:transport=dt_socket,server=y,address=8000,suspend=y -cp ";
	
	public static final int TYPE_PROGRAM = 0;
	public static final int TYPE_SAMPLE = 1;
	public static final int TYPE_TOOL = 2;
	
	private static final String defaultProgramProperty = "lejos.default_program";
	private static final String defaultProgramAutoRunProperty = "lejos.default_autoRun";
	private static final String pinProperty = "lejos.bluetooth_pin";
	private static final String ntpProperty = "lejos.ntp_host";
	
	public static final String PROGRAMS_DIRECTORY = "/home/lejos/programs";
	public static final String LIB_DIRECTORY = "/home/lejos/lib";
	public static final String SAMPLES_DIRECTORY = "/home/root/lejos/samples";
	public static final String TOOLS_DIRECTORY = "/home/root/lejos/tools";
	public static final String MENU_DIRECTORY = "/home/root/lejos/menu";
	public static final String START_BLUETOOTH = "/home/root/lejos/bin/startbt";
	public static final String START_WLAN = "/home/root/lejos/bin/startwlan";
	public static final String START_PAN = "/home/root/lejos/bin/startpan";
	public static final String PAN_CONFIG = "/home/root/lejos/config/pan.config";
	public static final String WLAN_INTERFACE = "wlan0";
	public static final String PAN_INTERFACE = "br0";
	
	protected static final int IND_SUSPEND = -1;
	protected static final int IND_NONE = 0;
	protected static final int IND_NORMAL = 1;
	protected static final int IND_FULL = 2;
	
	public static AddonLoader al;
	public PANConfig panConfig = new PANConfig();
	public static String hostname;
	public static List<String> ips = new ArrayList<String>();
	public static String panAddress;
	public static String rmiAddress = "";
	public static String wlanAddress;
	public static MainMenu self;
	public static TextLCD lcd;
	public int timeout = 0;
	
	protected IndicatorThread ind = new IndicatorThread();
	protected BatteryIndicator indiBA = new BatteryIndicator();
	protected PipeReader pipeReader = new PipeReader();
	protected RConsole rcons = new RConsole();
	protected RemoteMenuThread remoteMenuThread = new RemoteMenuThread();
	
	private static String version = "Unknown";
	private boolean btVisibility;
	private int shouldExit;
	private static GraphicMenu curMenu;
	private static LocalBTDevice bt;
	private static Process program; // the running user program, if any
	private static String programName; // The name of the running program
	private static boolean suspend = false;
	private static EchoThread echoIn, echoErr;
	private WaitScreen waitScreen;
	
	public static void main(String[] args) throws Exception
	{
		//DEBUGME Debuggins options
		System.setOut(new PrintStream("/tmp/menu-out"));
		System.setErr(new PrintStream("/tmp/menu-err"));
		LoadingAnimation a = new LoadingAnimation();
		waitForEV3BootComplete();
		LocalEV3.get().getLED().setPattern(1);
		a.start("Starting Menu");
		a.setState("Loading addons", 10);
		AddonLoader.init();
		al = AddonLoader.instance;
		System.out.println("Menu started");
		hostname = args.length > 0 ? args[0] : "TESTING";
		version = args.length > 1 ? args[1] : "UNKNOWN";
		
		a.setState("Init addons", 35);
		al.initStage();
		System.out.println("Host name: " + hostname);
		System.out.println("Version: " + version);
		System.out.println("Boot complete");
		self = new MainMenu();
		lcd = LocalEV3.get().getTextLCD();
		File file = getDefaultProgram();
		WaitScreen.init();
		MainMenu.self.waitScreen = WaitScreen.instance;
		a.setProgress(45);
		if(file != null)
		{
			String auto = Settings.getProperty(defaultProgramAutoRunProperty, "");			
			if(auto.equals("ON") && !Button.LEFT.isDown())
			{
				System.out.println("Auto executing default program " + file.getPath());
				try
				{
					JarFile jar = new JarFile(file);
					String mainClass = jar.getManifest().getMainAttributes().getValue("Main-class");
					jar.close();
					exec(file, JAVA_RUN_CP + file.getPath() + " lejos.internal.ev3.EV3Wrapper " + mainClass, PROGRAMS_DIRECTORY);
				}
				catch (IOException e)
				{
					System.err.println("Exception running autorun program");
				}
			}
		}
		a.setState("Load addons", 55);
		al.loadStage();
		
		a.setState("Init menu", 65);
		MainMenu.self.initialize(a);
		a.setProgress(90);
		
		a.setState("Finish up", 95);
		al.finishStage();
		
		new TuneThread().start();
		System.out.println("Starting the menu");
		LocalEV3.get().getLED().setPattern(0);
		a.stop();
		self.mainMenu();
		self.stopMenu();
	}

	private static void waitForEV3BootComplete()
	{
		File bootLock = new File("/var/run/bootlock");
		while(bootLock.exists())
		{
			Delay.msDelay(500);
		}
	}
	
	/**
	 * Initializing method
	 */
	public void initialize(LoadingAnimation a)
	{
		System.out.println("Initialisation started...");
		NetUtils.init();
		this.updateIPAddresses();
		bt = Bluetooth.getLocalDevice();
		a.setState("Start network", 75);
		this.startNetworkServices();			
		this.startDaemons();
		System.out.println("Initialisation complete");
	}
	
	/**
	 *  Start the background threads
	 */
	private void startDaemons()
	{
		ind.start();
		rcons.start();
		pipeReader.start();
		BrickFinder.startDiscoveryServer(panConfig.getCurrentMode() == PANConfig.MODE_APP);
		remoteMenuThread.start();
	}
	
	/**
	 * Display the main system menu.
	 * Allow the user to select File, Bluetooth, Sound, System operations.
	 */
	private void mainMenu()
	{
		//DEBUGME MainMenu
		MappedMenu menu = MenuRegistry.menu;
		int selection = 0;
		while(true)
		{
			newScreen(hostname);
			ind.setDisplayState(IND_FULL);
			selection = menu.getSelection(selection);
			ind.setDisplayState(IND_NORMAL);
			switch(selection)
			{
				case 0:
					mainRunDefault();
					break;
				case 1:
					filesMenu(PROGRAMS_DIRECTORY);
					break;
				case 2:
					samplesMenu();
					break;
				case 3:
					toolsMenu();
					break;
				case 4:
					bluetoothMenu();
					break;
				case 5:
					wifiMenu();
					break;
				case 6:
					panConfig.panMenu();
					break;
				case 7:
					soundMenu();
					break;
				case 8:
					systemMenu();
					break;
				case 9:
					displayVersion();
					break;
				case -1:
					bootMenu();
					break;
				default:
					menu.onExternalAction(selection);
					break;
			}
			
			if(this.shouldExit > 0)
			{
				break;
			}
		}
		
		if(this.shouldExit == 2)
		{
			this.shutdown();
		}
	}
	
	protected String getRunningName()
	{
		return programName;
	}
	
	/**
	 * Present the Bluetooth menu to the user.
	 * @author Rewritten by Enginecrafter77
	 */
	private void bluetoothMenu()
	{		
		if(bt == null)
		{
			msg("BT is off");
			return;
		}
		
		MappedMenu menu = MenuRegistry.bluetooth;
		int selection = 0;
		while(selection >= 0)
		{
			newScreen("Bluetooth");
			selection = menu.getSelection(selection);
			LCD.drawString("Visible: " + (bt.getVisibility() ? "ON" : "OFF"), 1, 2);
			switch(selection)
			{
				case 0:
					bluetoothSearch();
					break;
				case 1:
					bluetoothDevices();
					break;
				case 2:
					changeBTVisibility();
					break;
				case 3:
					bluetoothChangePIN();
					break;
				case 4:
					bluetoothInformation();
				default:
					menu.onExternalAction(selection);
			}
		}
	}
	
	/**
	 * Changes Bluetooth visibility.
	 */
	public void changeBTVisibility()
	{
		btVisibility = !bt.getVisibility();
		try
		{
			bt.setVisibility(btVisibility);
		}
		catch(BluetoothException e)
		{
			e.printStackTrace();
		}
		ind.updateNow();
	}
 
	/**
	 * Allow the user to change the Bluetooth PIN.
	 */
	private void bluetoothChangePIN()
	{
		String pinStr = SystemSettings.getStringSetting(pinProperty, "1234");
		int len = pinStr.length();
		byte[] pin = new byte[len];
		for (int i = 0; i < len; i++)
		{
			pin[i] = (byte)pinStr.charAt(i);
		}
		
		int[] newpin = MenuUtils.enterPin("Enter EV3 pin");
		if(newpin != null)
		{			
			try
			{
				PrintStream out = new PrintStream(new FileOutputStream("/etc/bluetooth/btpin"));
				out.println(String.valueOf(newpin));
				out.close();
				waitScreen.begin("Restart BT");
				Runtime.getRuntime().exec(START_BLUETOOTH).waitFor();
				waitScreen.end();
			}
			catch(Exception e)
			{
				System.err.println("Failed to set new bluetooth pin: ");
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Performs the Bluetooth search operation.
	 * Displays menu with the found devices, so user can pair the BT
	 * device with the EV3.
	 */
	private void bluetoothSearch()
	{
		newScreen();
		Collection<RemoteBTDevice> c = bt.search();
		if(c.size() == 0)
		{
			msg("No device found");
			return;
		}
		RemoteBTDevice[] devices = new RemoteBTDevice[c.size()];
		String[] entries = new String[c.size()];
		Iterator<RemoteBTDevice> itr = c.iterator();
		for(int i = 0; itr.hasNext(); i++)
		{
			devices[i] = itr.next();
			entries[i] = devices[i].getName();
		}
		
		ListMenu menu = new ListMenu(entries);
		int selection = 0;
		while(selection >= 0)
		{
			newScreen("Devices");
			selection = menu.getSelection(selection);
			if(selection >= 0)
			{
				RemoteBTDevice btd = devices[selection];
				newScreen("Pairing");
				int[] pin = MenuUtils.enterPin("PIN for " + btd.getName());
				if(pin == null)
				{
					selection = -1;
				}
				this.waitScreen.begin("Pairing\nwith\n" + btd.getName());
				boolean scf = true;
				try
				{
					Bluetooth.getLocalDevice().authenticate(btd.getAddress(), String.valueOf(pin));
				}
				catch (Exception e)
				{
					scf = false;
					e.printStackTrace();
				}
				this.waitScreen.end();
				msg(scf ? "Successful" : "Failed");
			}
		}
	}

	/**
	 * Display all currently known Bluetooth devices.
	 */
	private void bluetoothDevices()
	{
		newScreen();
		List<RemoteBTDevice> devList = (List<RemoteBTDevice>) Bluetooth.getLocalDevice().getPairedDevices();
		if(devList.size() <= 0)
		{
			msg("No known devices");
			return;
		}
		
		newScreen("Devices");
		String[] names = new String[devList.size()];
		for(int i = 0; i < devList.size(); i++)
		{
			names[i] = devList.get(i).getName();
		}

		ListMenu deviceMenu = new ListMenu(names);
		MappedMenu subMenu = MenuRegistry.bluetooth_dev;
		int selection = 0;
		while(selection >= 0)
		{
			newScreen();
			selection = deviceMenu.getSelection(selection);
			if(selection >= 0)
			{
				newScreen();
				RemoteBTDevice btrd = devList.get(selection);
				lcd.drawString(btrd.getName(), 2, 2);
				lcd.drawString(btrd.getAddress(), 0, 3);
				int subSelection = subMenu.getSelection(0);
				if(subSelection == 0)
				{
					try
					{
						Bluetooth.getLocalDevice().removeDevice(btrd.getAddress());
						devList = (List<RemoteBTDevice>) Bluetooth.getLocalDevice().getPairedDevices();
						names = new String[devList.size()];
						for(int i = 0; i < devList.size(); i++)
						{
							names[i] = devList.get(i).getName();
						}
						deviceMenu.setItems(names);
					}
					catch (BluetoothException e)
					{
						e.printStackTrace();
					}
				}
				else if(subSelection >= 0)
				{
					subMenu.onExternalAction(subSelection);
				}
			}
		}
	}

	private static final String[] bluetoothVersions = {"1.0b", "1.1", "1.2", "2.0", "2.1", "3.0", "4.0"};
	/**
	 * Display Bluetooth information
	 */
	private void bluetoothInformation()
	{
		newScreen("Information");
		LocalBTDevice lbt = Bluetooth.getLocalDevice();
		LocalVersion ver = lbt.getLocalVersion();
		String v = ver.hci_ver >= bluetoothVersions.length ? "" : bluetoothVersions[ver.hci_ver];
		lcd.drawString("HCI " + v + " " + Integer.toHexString(ver.hci_ver) + "/" + Integer.toHexString(ver.hci_rev), 0, 2);
		v = ver.lmp_ver >= bluetoothVersions.length ? "n/a" : bluetoothVersions[ver.lmp_ver];
		lcd.drawString("LMP " + v + " " + Integer.toHexString(ver.lmp_ver) + "/" + Integer.toHexString(ver.lmp_subver), 0, 3);
		lcd.drawString("ID: " + lbt.getFriendlyName(), 0, 5);
		lcd.drawString(lbt.getBluetoothAddress(), 0, 6);
		getButtonPress();
	}
	
	/**
	 * Run the default program (if set).
	 */
	private void mainRunDefault()
	{
		File file = getDefaultProgram();
		if(file == null)
		{
	   		msg("No default set");
		}
		else
		{
			System.out.println("Executing default program " + file.getPath());
			try
			{
				JarFile jar = new JarFile(file);
				String mainClass = jar.getManifest().getMainAttributes().getValue("Main-class");
				jar.close();
				exec(file, JAVA_RUN_CP + file.getPath() + " lejos.internal.ev3.EV3Wrapper " + mainClass, PROGRAMS_DIRECTORY);
			}
			catch(IOException e)
			{
				System.err.println("Exception running program");
			}
		}
	}
	
	/**
	 * Present the system menu.
	 * Allow the user to format the filesystem. Change timeouts and control
	 * the default program usage.
	 */
	private void systemMenu()
	{
		MappedMenu menu = MenuRegistry.system;
		int selection = 0;
		while(selection >= 0)
		{
			this.newScreen("System");
			lcd.drawString("Battery: " + (Math.round(Battery.getVoltage() * 1000.0F) / 1000.0F), 1, 1);
			lcd.drawString(MenuUtils.getFreeRam(), 1, 2);
			selection = menu.getSelection(selection);
			switch(selection)
			{
			case 0:
				if(MenuUtils.getYesNo("Delete all programs?", false))
				{
					MenuUtils.removeContent("/home/lejos/programs");
				}
				break;
			case 1:
				systemAutoRun();
				break;
			case 2:
				String newName = Keyboard.getString();
				if(newName != null)
				{
					setName(newName);
				}
				break;
			case 3:
				String host = Keyboard.getString();
				if(host != null)
				{
					Settings.setProperty(ntpProperty, host);
				}
				break;
			case 4:
				System.out.println("Menu suspended.");
				this.suspend();
				Button.waitForAnyPress();
				this.resume();
				break;
			case 5:
				EV3IOPort.closeAll();
				break;
			case 6:
				if(Settings.getProperty(defaultProgramProperty, "").equals(""))
				{
					this.msg("No default set.");
				}
				else
				{
					Settings.setProperty(defaultProgramProperty, "");
					Settings.setProperty(defaultProgramAutoRunProperty, "");
					this.msg("Unset sucessfull");
				}
				break;
			default:
				menu.onExternalAction(selection);
			}
		}
	}
	
	/**
	 * Present details of the default program
	 * Allow the user to specify run on system start etc.
	 */
	private void systemAutoRun()
	{
		File f = getDefaultProgram();
		if (f == null)
		{
	   		msg("No default set");
	   		return;
		}
		
		newScreen("Auto Run");
		lcd.drawString("Default Program:", 0, 2);
		lcd.drawString(f.getName(), 1, 3);
		
		String current = Settings.getProperty(defaultProgramAutoRunProperty, "");
		boolean yes = MenuUtils.getYesNo("Run at power up?", current.equals("ON"));
		if(yes)
		{
			Settings.setProperty(defaultProgramAutoRunProperty, yes ? "ON" : "OFF");
		}
	}
	
	private void bootMenu()
	{
		MappedMenu menu = MenuRegistry.boot_menu;
		int selection = menu.getSelection(1);
		switch(selection)
		{
		case 0:
			this.shouldExit = 2;
			break;
		case 1:
			break;
		default:
			menu.onExternalAction(selection);
		}
	}
	
	/**
	 * Get the default program as a file
	 */
	private static File getDefaultProgram()
	{
		String file = Settings.getProperty(defaultProgramProperty, "");
		if (file != null && file.length() > 0)
		{
			File f = new File(file);
			if (f.exists())
				return f;
			
		   	Settings.setProperty(defaultProgramProperty, "");
		   	Settings.setProperty(defaultProgramAutoRunProperty, "OFF");
		}
		return null;
	}

	/**
	 * Display the sound menu.
	 * Allow the user to change volume and key click volume.
	 * Completely rewritten from original LeJOS version.
	 * Beautiful :D
	 * @author Enginecrafter77
	 */
	private void soundMenu()
	{
		MappedMenu menu = MenuRegistry.sound;
		String[] res = menu.getItems();
		int selection = 0;
		int mv = Integer.parseInt(Settings.getProperty(Sound.VOL_SETTING, "0"));
		int kv = Integer.parseInt(Settings.getProperty(Button.VOL_SETTING, "0"));
		int kf = Integer.parseInt(Settings.getProperty(Button.FREQ_SETTING, "0"));
		int kl = Integer.parseInt(Settings.getProperty(Button.LEN_SETTING, "0"));
		while(selection >= 0)
		{
			newScreen("Sound");
			res[0] = "Master Volume: " + mv;
			res[1] = "Key Volume: " + kv;
			res[2] = "Freq: " + kf + "Hz";
			res[3] = "Length: " + kl + "ms";
			selection = menu.getSelection(selection);
			switch(selection)
			{
			case 0:
				mv = MenuUtils.cycleValue(mv, 0, 100, 10);
				Sound.playTone(500, 500, mv);
				Sound.setVolume(mv);
				break;
			case 1:
				kv = MenuUtils.cycleValue(kv, 0, 100, 10);
				Button.setKeyClickVolume(kv);
				break;
			case 2:
				kf = MenuUtils.cycleValue(kf, 100, 1000, 100);
				Button.setKeyClickTone(Key.ENTER, kf);
				break;
			case 3:
				kl = MenuUtils.cycleValue(kl, 50, 500, 50);
				Button.setKeyClickLength(kl);
				break;
			default:
				menu.onExternalAction(selection);
			}
		}
		MainMenu.lcd.clear();
		this.waitScreen.begin("Saving");
		Settings.setProperty(Sound.VOL_SETTING, String.valueOf(mv));
		Settings.setProperty(Button.VOL_SETTING, String.valueOf(kv));
		Settings.setProperty(Button.FREQ_SETTING, String.valueOf(kf));
		Settings.setProperty(Button.LEN_SETTING, String.valueOf(kl));
		Delay.msDelay(500);
		this.waitScreen.end();
	}
	
	/**
	 * Display system version information.
	 */
	private void displayVersion()
	{
		MORegistry m = MORegistry.getRegistry(MORegistry.Type.DISPLAY_VERISON);
		newScreen("Version");
		if(m.runMethodsB())
		{
			lcd.drawString("leJOS:", 0, 2);
			lcd.drawString(version, 6, 2);
			lcd.drawString("Menu:", 0, 3);
			lcd.drawString(Config.VERSION, 6, 3);
			getButtonPress();
		}
		m.runMethodsA();
	}
	
	/**
	 * Read a button press.
	 * If the read timesout then exit the system.
	 * @return The bitcode of the button.
	 */
	protected int getButtonPress()
	{
		long timeoutCnt = (timeout == 0 ? Long.MAX_VALUE : (timeout*60000)/200);
		while (timeoutCnt-- > 0)
		{	
			int value = Button.waitForAnyPress(200);
			if (value != 0) return value;
			if (suspend)
				waitResume();
		}
		shutdown();
		return 0;
	}
	
	/**
	 * Present the menu for a single file.
	 * Rewritten from original LeJOS fileMenu.
	 * @param file
	 * @author Enginecrafter77
	 */
	private void fileMenu(File file)
	{
		MappedMenu menu = MenuRegistry.file;
		String[] items = menu.getItems();
		String[] icons = menu.getIcons();
		String extension = Utils.getExtension(file.getName());
		boolean isDirectory = file.isDirectory();
		if(isDirectory)
		{
			items[0] = "Open";
			icons[0] = Icons.ICFiles;
		}
		else if(extension.equals("jar"))
		{
			items[0] = "Executable";
			icons[0] = Icons.ICEV3;
		}
		else if(extension.equals("wav"))
		{
			items[0] = "Play File";
			icons[0] = Icons.ICSound;
		}
		else
		{
			items[0] = "View File";
			icons[0] = Icons.ICEV3;
		}
		menu.updateIcons();
		newScreen(file.getName());
		LCD.drawString("Size: " + DataSize.formatDataSize(file.length(), DataSize.BYTE), 1, 2);
		int selection = menu.getSelection(0);
		switch(selection)
		{
		case 0:
			if(isDirectory)
			{
				filesMenu(file);
			}
			else if(extension.equals("jar"))
			{
				executableMenu(file);
			}
			else if(extension.equals("wav"))
			{
				Sound.playSample(file);
			}
			else
			{
				try
				{
					Viewer.view(file.getPath());
				}
				catch(IOException e)
				{
					System.err.println("Exception viewing file");
				}
			}
			break;
		case 1:
			file.delete();
			break;
		default:
			menu.onExternalAction(selection);
			break;
		}
	}
	
	/**
	 * Used to display menu for executable files.
	 * @param file
	 * @author Enginecrafter77
	 */
	private void executableMenu(File file)
	{
		MappedMenu menu = MenuRegistry.executable;
		boolean isTool = file.getAbsolutePath().startsWith(MainMenu.MENU_DIRECTORY);
		String directory = file.getParent();
		newScreen(file.getName());
		int selection = menu.getSelection(0);
		switch(selection)
		{
		case 0:
			System.out.println("Running program: " + file.getPath());
			if(isTool)
			{
				MORegistry tr = MORegistry.getRegistry(MORegistry.Type.RUN_TOOL);
				if(tr.runMethodsB())
				{
					execInThisJVM(file);
				}
				tr.runMethodsA();
			}
			else
			{
				MORegistry tr = MORegistry.getRegistry(MORegistry.Type.RUN_PROG);
				if(tr.runMethodsB())
				{
					JarFile jar = null;
					try
					{
						jar = new JarFile(file);
						String mainClass = jar.getManifest().getMainAttributes().getValue("Main-class");
						jar.close();
						exec(file, JAVA_RUN_CP + file.getPath() + " lejos.internal.ev3.EV3Wrapper " + mainClass, directory);
					}
					catch(IOException e)
					{
						System.err.println("Exception running program");
					}
				}
				tr.runMethodsA();
			}
			break;
		case 1:
			System.out.println("Debugging program: " + file.getPath());
			JarFile jar = null;
			try
			{
				jar = new JarFile(file);
				String mainClass = jar.getManifest().getMainAttributes().getValue("Main-class");
				jar.close();
				exec(file, JAVA_DEBUG_CP + file.getPath() + " lejos.internal.ev3.EV3Wrapper " + mainClass, directory);
			}
			catch(IOException e)
			{
				System.err.println("Exception running program");
			}
			break;
		case 2:
			Settings.setProperty(defaultProgramProperty, file.getAbsolutePath());
			break;
		default:
			menu.onExternalAction(selection);
			break;
		}
	}
	
	/**
	 * Present the menu for a menu tool.
	 * @param file
	 */
	private void toolMenu(File file)
	{
		MORegistry m = MORegistry.getRegistry(MORegistry.Type.RUN_TOOL);
		if(m.runMethodsB())
		{
			String fileName = file.getName();
			String ext = Utils.getExtension(fileName);
			if(ext.equals("jar"))
			{
				execInThisJVM(file);
			}
		}
		m.runMethodsA();
	}

	
	/**
	 * Execute a program and display its output to System.out and error stream to System.err
	 */
	private static void exec(File jar, String command, String directory)
	{
		self.suspend();
		try
		{
			if(jar != null)
			{
				String jarName = jar.getName();
				programName = jarName.substring(0, jarName.length() - 4);
			}
			
			WaitScreen.drawLaunchScreen();
			
			program = new ProcessBuilder(command.split(" ")).directory(new File(directory)).start();
			BufferedReader input = new BufferedReader(new InputStreamReader(program.getInputStream()));
			BufferedReader err= new BufferedReader(new InputStreamReader(program.getErrorStream()));
			
			echoIn = new EchoThread(jar.getPath().replace(".jar", ".out"), input, System.out);
			echoErr = new EchoThread(jar.getPath().replace(".jar", ".err"), err, System.err);
			
			echoIn.start(); 
			echoErr.start();
			
			System.out.println("Executing " + command + " in " + directory);
			
			while(true)
			{
				int b = Button.getButtons(); 
				if(b == 6)
				{
					System.out.println("Killing the process");
					program.destroy(); 
					break;
				}
				if(!echoIn.isAlive() && !echoErr.isAlive())
				{
					break;           
				}
				Delay.msDelay(200);
			}
			program.waitFor();
			System.out.println("Program finished");
		}
		catch (Exception e)
		{
			System.err.println("Failed to execute program: " + e);
		}
		finally
		{
			Button.LEDPattern(0);
			program = null;
			self.resume();
		}
	}
	
	/**
	 * Execute a program and display its output to System.out and error stream to System.err
	 */
	private static void startProgram(String command, File jar) {
		MORegistry m = MORegistry.getRegistry(MORegistry.Type.RUN_PROG);
		if(m.runMethodsB())
		{
			try
			{
				if(program != null) return;
				String[] args = command.split(" ");
				File directory = jar.getParentFile();
				
				programName = jar.getName().replace(".jar", "");
				
				program = new ProcessBuilder(args).directory(directory).start();
				echoIn = new EchoThread(jar.getPath().replace(".jar",".out"), new BufferedReader(new InputStreamReader(program.getInputStream())), System.out);
				echoErr = new EchoThread(jar.getPath().replace(".jar",".err"), new BufferedReader(new InputStreamReader(program.getErrorStream())), System.err);
				
				echoIn.start();
				echoErr.start();
				self.suspend();
				WaitScreen.drawLaunchScreen();		  
				
				System.out.println("Executing " + command + " in " + directory);
			}
			catch(Exception e)
			{
				System.err.println("Failed to start program: " + e);
				e.printStackTrace();
				self.resume();
			}
		}
		m.runMethodsA();
	}
	
	@Override
	public void stopProgram() {		   
		try {  
			if (program == null) return;
			program.destroy();
			program.waitFor();
			System.out.println("Program finished");
		  }
		  catch (Exception e) {
			System.err.println("Failed to stop program: " + e);
		  }
		  finally
		  {
			  Button.LEDPattern(0);
			  program = null;
			  self.resume();			  
		  }
	}
   
	/**
	 * Display the files in the file system.
	 * Allow the user to choose a file for further operations.
	 * Rewritten by Enginecrafter77
	 */
	private void filesMenu(File dir)
	{
		ListMenu menu = new ListMenu();
		File[] files = dir.listFiles();;
		menu.setItems(Utils.filesToString(files));
		
		int selection = 0;
		while(selection >= 0)
		{
			newScreen("Files");
			selection = menu.getSelection(selection);
			if(selection >= 0)
			{
				fileMenu(files[selection]);
				files = dir.listFiles();
				menu.setItems(Utils.filesToString(files));
			}
		}
	}
	
	private void filesMenu(String dir)
	{
		this.filesMenu(new File(dir));
	}
	
	/**
	 * Display the tools from the tools directory.
	 * Allow the user to choose a file for further operations.
	 */
	private void toolsMenu()
	{
		ListMenu menu = new ListMenu();
		File[] files = new File(TOOLS_DIRECTORY).listFiles();
		String[] items = new String[files.length];
		if(files.length == 0)
		{
			msg("No tools found.");
			return;
		}
		for(int i = 0; i < files.length; i++)
		{
			items[i] = formatFileName(files[i].getName());
		}
		menu.setItems(items);
		
		int selection = 0;
		while(selection >= 0)
		{
			newScreen("Tools");			
			selection = menu.getSelection(selection);
			if (selection >= 0)
			{
				toolMenu(files[selection]);
			}
		}
	}
	
	/**
	 * Method to add spaces before capital letters and remove .jar extension.
	 * 
	 * @param fileName
	 * @return
	 */
	static private String formatFileName(String fileName) {
		StringBuffer formattedName = new StringBuffer(""+fileName.charAt(0));
		for(int i=1;i<fileName.length();i++) { //Skip the first letter-can't put space before first word
			if(fileName.charAt(i)=='.') break;
			if(Character.isUpperCase(fileName.charAt(i))) {
				formattedName.append(' ');
			}
			formattedName.append(fileName.charAt(i));
			
		}
		return formattedName.toString();
	}
	
	/**
	 * Display the samples in the file system.
	 * Allow the user to choose a file for further operations.
	 */
	private void samplesMenu()
	{
		ListMenu menu = new ListMenu();
		//System.out.println("Finding files ...");
		int selection = 0;
		do {
			File[] files = (new File(SAMPLES_DIRECTORY)).listFiles();
			int len = 0;
			for (int i = 0; i < files.length && files[i] != null; i++)
				len++;
			if (len == 0)
			{
				msg("No samples found");
				return;
			}
			newScreen("Samples");
			String fileNames[] = new String[len];
			String[] icons = new String[len];
			for (int i = 0; i < len; i++){
				fileNames[i] = files[i].getName();
				String ext = Utils.getExtension(files[i].getName());
			   if (ext.equals("jar"))
					icons[i] = ICMProgram;
				else if (ext.equals("wav"))
					icons[i] = ICMSound;
				else
					icons[i] = ICMFile;
			}
			menu.setItems(fileNames,icons);
			selection = menu.getSelection(selection);
			if (selection >= 0)
				fileMenu(files[selection]);
		} while (selection >= 0);
	}

	
	/**
	 * Start a new screen display using the current title.
	 */
	public void newScreen()
	{
		lcd.clear();
		ind.updateNow();
	}
	
	/**
	 * Start a new screen display.
	 * Clear the screen and set the screen title.
	 * @param title
	 */
	public void newScreen(String title)
	{
		indiBA.setTitle(title);
		newScreen();
	}
	
	/**
	 * Display a status message
	 * @param msg
	 */
	public void msg(String msg)
	{
		newScreen();
		lcd.drawString(msg, 0, 2);
		long start = System.currentTimeMillis();
		int button;
		int buttons = Button.readButtons();
		do
		{
			Thread.yield();
			
			int buttons2 = Button.readButtons();
			button = buttons2 & ~buttons;
		} while (button != Button.ID_ESCAPE && System.currentTimeMillis() - start < 2000);
	}

	/**
	 * If the menu is suspended wait for it to be resumed. Handle any program exit
	 * while we wait.
	 */
	public void waitResume()
	{
		while (suspend) {
			if (program != null && !echoIn.isAlive() && !echoErr.isAlive()) {
				stopProgram();
				break;
			}
			int b = Button.getButtons(); 
			if (b == 6) {
				if (program != null)
					stopProgram();
				else
					// should we do this?
					resume();
				break;
			}
			Delay.msDelay(200);
		}		
	}
	
	/**
	 * Sets the currently running menu.
	 * @param menu
	 */
	public void setCurrentMenu(GraphicMenu menu)
	{
		MainMenu.curMenu = menu;
	}
	
	@Override
	public String getExecutingProgramName() {
		if (program == null) return null;
		return programName;
	}
	
	/**
	 * Get all the IP addresses for the device, return true if either the wlan or
	 * pan address has changed.
	 */
	public synchronized boolean updateIPAddresses()
	{
		//TODO Add hook
		List<String> result = new ArrayList<String>();
		Enumeration<NetworkInterface> interfaces;
		String oldWlan = wlanAddress;
		String oldPan = panAddress;
		wlanAddress = null;
		panAddress = null;
		ips.clear();
		try
		{
			interfaces = NetworkInterface.getNetworkInterfaces();
		} catch (SocketException e)
		{
			System.err.println("Failed to get network interfaces: " + e);
			return false;
		}
		while (interfaces.hasMoreElements()){
			NetworkInterface current = interfaces.nextElement();
			try
			{
				if (!current.isUp() || current.isLoopback() || current.isVirtual()) continue;
			} catch (SocketException e)
			{
				System.err.println("Failed to get network properties: " + e);
			}
			Enumeration<InetAddress> addresses = current.getInetAddresses();
			while (addresses.hasMoreElements()){
				InetAddress current_addr = addresses.nextElement();
				if (current_addr.isLoopbackAddress()) continue;
				result.add(current_addr.getHostAddress());
				//System.out.println("Interface name " + current.getName());
				if (current.getName().equals(WLAN_INTERFACE))
					wlanAddress = current_addr.getHostAddress();
				else if (current.getName().equals(PAN_INTERFACE))
					panAddress = current_addr.getHostAddress();
			}
		}
		ips = result;
		// have any of the important addresses changed?
		return !(oldWlan == wlanAddress || (oldWlan != null && wlanAddress != null && wlanAddress.equals(oldWlan))) ||
				!(oldPan == panAddress || (oldPan != null && panAddress != null && panAddress.equals(oldPan)));
	}
	
	@Override
	public void runProgram(String programName) {
		JarFile jar = null;
		String fullName = PROGRAMS_DIRECTORY + "/" + programName + ".jar";
		try {
			File jarFile = new File(fullName);
			jar = new JarFile(jarFile);
			String mainClass = jar.getManifest().getMainAttributes().getValue("Main-class");
			jar.close();
			startProgram(JAVA_RUN_CP + fullName + " lejos.internal.ev3.EV3Wrapper " + mainClass, jarFile);
		} catch (IOException e) {
			System.err.println("Failed to run program");
		}
	}

	@Override
	public boolean deleteFile(String fileName) {
		File f = new File(fileName);
		return f.delete();
	}

	@Override
	public String[] getProgramNames() {
		File[] files = (new File(PROGRAMS_DIRECTORY)).listFiles();
		String[] fileNames = new String[files.length];
		for(int i=0;i<files.length;i++) {
			fileNames[i] = files[i].getName();
		}
		return fileNames;
	}

	@Override
	public void runSample(String programName) {
		JarFile jar = null;
		String fullName = SAMPLES_DIRECTORY + "/" + programName + ".jar";
		try {
			File jarFile = new File(fullName);
			jar = new JarFile(jarFile);
			String mainClass = jar.getManifest().getMainAttributes().getValue("Main-class");
			jar.close();
			startProgram(JAVA_RUN_CP + fullName + " lejos.internal.ev3.EV3Wrapper " + mainClass, jarFile);
		} catch (IOException e) {
			System.err.println("Failed to run program");
		}
	}

	@Override
	public void debugProgram(String programName) {
		JarFile jar = null;
		String fullName = PROGRAMS_DIRECTORY + "/" + programName + ".jar";
		try {
			File jarFile = new File(fullName);
			jar = new JarFile(jarFile);
			String mainClass = jar.getManifest().getMainAttributes().getValue("Main-class");
			jar.close();
			startProgram(JAVA_DEBUG_CP + fullName + " lejos.internal.ev3.EV3Wrapper " + mainClass, jarFile);
		} catch (IOException e) {
			System.err.println("Failed to run program");
		}
	}

	@Override
	public String[] getSampleNames() {
		File[] files = (new File(SAMPLES_DIRECTORY)).listFiles();
		String[] fileNames = new String[files.length];
		for(int i=0;i<files.length;i++) {
			fileNames[i] = files[i].getName();
		}
		return fileNames;
	}

	@Override
	public long getFileSize(String filename) {
		return new File(filename).length();
	}

	@Override
	public boolean uploadFile(String fileName, byte[] contents) {
		try {
			FileOutputStream out = new FileOutputStream(fileName);
			out.write(contents);
			out.close();
			return true;
		} catch (IOException e) {
			System.out.println("Failed to upload file: " + e);
			return false;
		}
	
	}
	
	private void wifiMenu()
	{
		//TODO HOOK
		MappedMenu menu = MenuRegistry.wifimenu;
		
	}
	
	public void wifiSearch()
	{
		System.out.println("Finding access points ...");
		LocalWifiDevice wifi = Wifi.getLocalDevice("wlan0");
		String[] names;
		try
		{
			names = wifi.getAccessPointNames();
		}
		catch(Exception e)
		{
			System.err.println("Exception getting access points" +e);
			names = new String[0];
		}
		
		if(names.length == 0)
		{
			msg("No Access Points found");
			return;
		}
		ListMenu menu = new ListMenu(names);
		
		int selection = 0;
		while(selection >= 0)
		{
			newScreen("Access Pts");
			selection = menu.getSelection(selection);
			if(selection >= 0)
			{
				NetUtils.connectAP(names[selection]);
			 	selection = -1;
			}
		}
	}
	
	/**
	 * Reset all motors to zero power and float state
	 * and reset tacho counts
	 */
	public static void resetMotors() {
		for(String portName: new String[]{"A","B","C","D"}) {
			Port p = LocalEV3.get().getPort(portName);
			TachoMotorPort mp = p.open(TachoMotorPort.class);
			mp.controlMotor(0, TachoMotorPort.FLOAT);
			mp.resetTachoCount();
			mp.close();
		}
	}

	@Override
	public byte[] fetchFile(String fileName) {
		File f = new File(fileName);
		FileInputStream in;
		try {
			in = new FileInputStream(f);
			byte[] data = new byte[(int)f.length()];
			in.read(data);
			in.close();
			return data;
		} catch (IOException e) {
			System.err.println("Failed to fetch file: " + e);
			return null;
		}

	}

	@Override
	public String getSetting(String setting) {
		return Settings.getProperty(setting, null);
	}

	@Override
	public void setSetting(String setting, String value) {
		Settings.setProperty(setting, value);
	}

	@Override
	public void deleteAllPrograms() {
		File dir = new File(PROGRAMS_DIRECTORY);
		for (String fn : dir.list()) {
			File aFile = new File(dir,fn);
			System.out.println("Deleting " + aFile.getPath());
			aFile.delete();
		}
	}

	@Override
	public String getVersion() {
		return version;
	}

	@Override
	public String getMenuVersion() {
		return Config.VERSION;
	}

	@Override
	public String getName() {
		return hostname;
	}

	@Override
	public void setName(String name) {
		waitScreen.begin("Change\nSystem\nName");
		hostname = name;
		waitScreen.status("Save new name");
		// Write host to /etc/hostname
		try {
			PrintStream out = new PrintStream(new FileOutputStream("/etc/hostname"));
			out.println(name);
			out.close();
		} catch (FileNotFoundException e) {
			System.err.println("Failed to write to /etc/hostname: " + e);
		}
		
		try {
			Process p = Runtime.getRuntime().exec("hostname " + hostname);
			int status = p.waitFor();
			System.out.println("hostname returned " + status);
		} catch (Exception e) {
			System.err.println("Failed to execute hostname: " + e);
		}
		
		startNetwork(START_WLAN, false);
		startNetwork(START_PAN, true);
		waitScreen.end();
	}
	
	public void startNetworkServices()
	{
		System.out.println("Starting RMI");
		String rmiIP = (wlanAddress != null ? wlanAddress : (panAddress != null ? panAddress : "127.0.0.1"));
		System.out.println("Setting java.rmi.server.hostname to " + rmiIP);
		System.setProperty("java.rmi.server.hostname", rmiIP);
		
		try
		{
			LocateRegistry.createRegistry(1099); 
			System.out.println("java RMI registry created.");
			RMIRemoteEV3 ev3 = new RMIRemoteEV3();
			Naming.rebind("//localhost/RemoteEV3", ev3);
			RMIRemoteMenu remoteMenu = new RMIRemoteMenu(self);
			Naming.rebind("//localhost/RemoteMenu", remoteMenu);
		}
		catch (Exception e)
		{
			System.err.println("RMI failed to start: " + e);
		}
			
		//String dt = SntpClient.getDate(Settings.getProperty(ntpProperty, "1.uk.pool.ntp.org"));
		//System.out.println("Date and time is " + dt);
		//Runtime.getRuntime().exec("date -s " + dt);
	}
	
	
	protected void startNetwork(String startup, boolean startServices) {
		try {
			Process p = Runtime.getRuntime().exec(startup);
			BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String statusMsg;
			while((statusMsg = input.readLine()) != null)
			{
				waitScreen.status(statusMsg);
			}
			int status = p.waitFor();
			System.out.println("start returned " + status);
			updateIPAddresses();
			Delay.msDelay(2000);
			if (startServices)
			{
				waitScreen.status("Start services");
				startNetworkServices();
				Delay.msDelay(2000);
			}
		} catch (Exception e) {
			System.err.println("Failed to execute: " + startup + " : " + e);
			e.printStackTrace();
		
		}
	}
	
	private void execInThisJVM(File jar)
	{
		suspend();
		try
		{
			LCD.clearDisplay();
			JarMain m = new JarMain(jar);
			m.run();
			m.close();
		}
		catch(Exception e)
		{
			toolException(e);
			System.err.println("Exception in execution of tool: " + e);
			e.printStackTrace();
		}
		finally
		{
			resume();
		}
	}
	
	private void toolException(Throwable t) {
		Sound.buzz();
		TextLCD lcd = BrickFinder.getDefault().getTextLCD(Font.getSmallFont());
		int offset = 0;
		// Get rid of invocation exception
		if (t.getCause() != null) t = t.getCause();
		while (true)
		{
			lcd.clear();
			lcd.drawString("Tool exception:", offset, 1);
			lcd.drawString(t.getClass().getName(), offset, 3);
			if (t.getMessage() != null) lcd.drawString(t.getMessage(), offset, 4);		
			
			if (t.getCause() != null) {
				lcd.drawString("Caused by:", offset, 5);
				lcd.drawString(t.getCause().toString(), offset, 6);
			}
			
			StackTraceElement[] trace = t.getStackTrace();
			for(int i=0;i<7 && i < trace.length ;i++) lcd.drawString(trace[i].toString(), offset, 8+i);
			
			lcd.refresh();
			int id = Button.waitForAnyEvent();
			if (id == Button.ID_ESCAPE) break;
			if (id == Button.ID_LEFT) offset += 5;
			if (id == Button.ID_RIGHT)offset -= 5;
			if (offset > 0) offset = 0;
		}
		lcd.clear();
	}

	@Override
	public void suspend() {
		suspend = true;
		ind.suspend();
		lcd.clear();
		LCD.setAutoRefresh(false);
		lcd.refresh();
		curMenu.quit();
	}

	@Override
	public void resume() {
		lcd.clear();
		lcd.refresh();
		LCD.setAutoRefresh(true);
		ind.resume();
		suspend = false;
	}
	
	public boolean isSuspended()
	{
		return MainMenu.suspend;
	}
	
	private void stopMenu()
	{
		System.out.println("Menu finished");
		System.gc();
		System.exit(0);
	}
	
	/**
	 * Shut down the EV3
	 */
	@Override
	public void shutdown()
	{
		this.suspend();
		try
		{
			Runtime.getRuntime().exec("init 0");
		}
		catch (IOException e){}
		lcd.drawString("  Shutting down", 0, 6);
		lcd.refresh();
		System.exit(0);
	}
	
	public void setMenuExit(boolean t)
	{
		this.shouldExit = t ? 1 : 0;
	}

	/**
	 * Restarts the menu system.
	 */
	public void restart()
	{
		try
		{
			ProcessBuilder b = new ProcessBuilder("/home/root/lejos/bin/startmenu").inheritIO();
			b.start();
			this.stopMenu();
		}
		catch(Exception e)
		{
			System.err.println("Error when restarting menu");
			e.printStackTrace();
		}
	}
	
}
