package addonloader.betamenu;

import java.util.Collection;
import java.util.Iterator;

import addonloader.betamenu.bar.BatteryApplet;
import addonloader.betamenu.bar.DockBar;
import addonloader.betamenu.bar.WifiApplet;
import addonloader.betamenu.net.ConnectionManager;
import addonloader.main.Reference;
import addonloader.menu.MappedMenu;
import addonloader.menu.MenuEntry;
import addonloader.menu.SimpleMenuEntry;
import addonloader.menu.SubmenuEntry;
import addonloader.util.LoadingScreen;
import addonloader.util.Settings;
import addonloader.util.input.DefaultKeyboard;
import addonloader.util.input.InputMethod;
import addonloader.util.input.PipeInput;
import addonloader.util.ui.StockIcon;
import lejos.hardware.Button;
import lejos.hardware.RemoteBTDevice;
import lejos.hardware.lcd.LCD;
import lejos.utility.TextMenu;

public class BetaMenu extends MappedMenu {
	
	private static final long serialVersionUID = -7226674803840849026L;
	public static Settings settings;
	public static BetaMenu instance;
	
	public static void main(String[] args)
	{
		LCD.drawString("Wait for client", 0, 3);
		LCD.clear();
		PipeInput.attach_std();
		InputMethod.set_fallback(new DefaultKeyboard());
		InputMethod.add(new PipeInput(Reference.MENU_DIRECTORY + "/input"));
		settings = new Settings(Reference.LEJOS_HOME + "/settings.properties");
		instance = new BetaMenu(args.length > 0 ? args[0] : "EV3");
		settings.load();
		instance.open(0);
		settings.store();
	}
	
	//			//
	//	CLASS	//
	//			//
	
	private SubmenuEntry wifi, bluetooth, pan, system, sound, btsettings;
	private MappedMenu btdevmenu;
	private ConnectionManager conman;
	private LoadingScreen loading_screen;
	private DockBar menubar;
	
	public BetaMenu(String hostname)
	{
		super(hostname);
		this.loading_screen = new LoadingScreen();
		this.loading_screen.start("BetaMenu");
		
		this.loading_screen.setState("ConnectionMan", 10);
		this.conman = new ConnectionManager("######");
		this.loading_screen.setState("Init Menu", 50);
		this.menubar = new DockBar(3, 750);
		this.wifi = new SubmenuEntry("Wifi", StockIcon.WIFI);
		this.bluetooth = new SubmenuEntry("Bluetooth", StockIcon.BLUETOOTH);
		this.pan = new SubmenuEntry("PAN", StockIcon.NETWORK);
		this.system = new SubmenuEntry("System", StockIcon.GEAR);
		this.sound = new SubmenuEntry("Sound", StockIcon.SOUND);
		this.btsettings = new SubmenuEntry("Settings", StockIcon.GEAR);
		this.btdevmenu = new MappedMenu("BT DEV");
		
		this.loading_screen.setState("Add Applets", 75);
		this.menubar.add_applet(new BatteryApplet());
		this.menubar.add_applet(new WifiApplet(conman));
		this.init_menu_entries();
		
		this.loading_screen.setState("Add Menus", 90);
		this.add(wifi);
		this.add(bluetooth);
		this.add(pan);
		this.add(system);
		this.add(sound);
		this.loading_screen.stop();
	}
	
	@Override
	public int open(int selection)
	{
		menubar.start();
		System.out.println("Menu started");
		
		while(true)
		{
			LCD.clear();
			this.menubar.load_carrier("BetaMenu");
			selection = super.open(selection);
			if(selection < 0) break;
			else this.get(selection).run();
		}
		
		System.out.println("Menu finished");
		menubar.interrupt();
		return selection;
	}
	
	@Override
	public boolean add(MenuEntry entry)
	{
		if(entry instanceof SubmenuEntry) ((SubmenuEntry)entry).set_title_service(this.menubar);
		return super.add(entry);
	}
	
	private void init_menu_entries()
	{
		wifi.add(new WifiConnectEntry(this.conman, this.loading_screen));
		sound.add(new SoundEntry("M-Volume", 0));
		sound.add(new SoundEntry("B-Volume", 1));
		sound.add(new SoundEntry("B-Freq", 2));
		sound.add(new SoundEntry("B-Length", 3));
		bluetooth.add(btsettings);
		
		wifi.add(new SimpleMenuEntry("View IP", StockIcon.INFO) {
			@Override
			public void run()
			{
				this.textflash(conman.get_address(Reference.WLAN_INTERFACE).getHostAddress());
			}
		});
		
		system.add(new SimpleMenuEntry("Keyboard Test", StockIcon.LIST) {
			@Override
			public void run()
			{
				String out = InputMethod.enter();
				
				LCD.clear();
				LCD.drawString("Entered:", 1, 3);
				LCD.drawString(out, 1, 4);
				Button.waitForAnyPress();
				LCD.clear();
			}
		});
		
		bluetooth.add(new SimpleMenuEntry("Search", StockIcon.SEARCH) {
			@Override
			public void run()
			{
				RemoteBTDevice[] devices = conman.search_bluetooth();
				String[] devlist = new String[devices.length];
				for(int index = 0; index < devices.length; index++) devlist[index] = devices[index].getName();
				
				int selection = new TextMenu(devlist).select();
				if(selection > -1) conman.bluetooth_pair(devices[selection]);
			}
		});
		bluetooth.add(new SimpleMenuEntry("Visibility", StockIcon.EYE) {
			@Override
			public void run()
			{
				this.textflash((conman.bluetooth_toggle_visibility() ? "V" : "Inv") + "isibile");
			}
		});
		bluetooth.add(new SimpleMenuEntry("Devices", StockIcon.LIST) {
			@Override
			public void run()
			{
				Collection<RemoteBTDevice> devices = conman.bluetooth.getPairedDevices();
				RemoteBTDevice[] device_list = new RemoteBTDevice[devices.size()];
				String[] devstrlist = new String[devices.size()];
				Iterator<RemoteBTDevice> itr = devices.iterator();
				for(int index = 0; index < devices.size(); index++)
				{
					device_list[index] = itr.next();
					devstrlist[index] = device_list[index].getName();
				}
				
				int selection = new TextMenu(devstrlist).select();
				if(selection < 0) return;
				btdevmenu.load_carrier(device_list[selection].getAddress());
				btdevmenu.get(btdevmenu.open(selection)).run();
			}
		});
	}
}
