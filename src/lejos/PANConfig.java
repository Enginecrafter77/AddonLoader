package lejos;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import addonloader.menu.MappedMenu;
import addonloader.util.MenuUtils;
import addonloader.util.StockIcon;
import addonloader.util.ui.Icon;
import lejos.hardware.Bluetooth;
import lejos.hardware.BluetoothException;
import lejos.hardware.BrickFinder;
import lejos.hardware.Button;
import lejos.hardware.RemoteBTDevice;
import lejos.utility.TextMenu;

public class PANConfig {
	public static final int MODE_NONE = 0;
	public static final int MODE_AP = 1;
	public static final int MODE_APP = 2;
	public static final int MODE_BTC = 3;
	public static final int MODE_USBC = 4;

	final String[] modeIDS = { "NONE", "AP", "AP+", "BT", "USB" };
	final String[] modeNames = { "None", "Access Pt", "Access Pt+", "BT Client", "USB Client" };
	final String[] serviceNames= {"NAP", "PANU", "GN"};
	final Icon[] modeIcons = {StockIcon.AP_DISABLED, StockIcon.ACCESSPOINT, StockIcon.AP_PLUS, StockIcon.BLUETOOTH, StockIcon.USB};
	final String defaultIP = "208";
	static final String autoIP = "0.0.0.0";
	static final String anyAP = "*";
	
	String[] IPAddresses = {autoIP, autoIP, autoIP, autoIP, autoIP };
	String[] IPNames = {"Address", "Netmask", "Brdcast", "Gateway", "DNS	"};
	String[] IPIDS = {"IP", "NM", "BC", "GW", "DN"};
	
	int curMode = MODE_NONE;
	String BTAPName = anyAP;
	String BTAPAddress = anyAP;
	String BTService = "NAP";
	String persist = "N";
	Boolean changed = false;

	public PANConfig()
	{
		loadConfig();
	}

	public int getCurrentMode()
	{
		return curMode;
	}
	
	public void saveConfig()
	{
		System.out.println("Save PAN config");
		try {
			PrintWriter out = new PrintWriter(Reference.PAN_CONFIG);
			out.print(modeIDS[curMode] + " " + BTAPName.replace(" ", "\\ ") + " " + BTAPAddress);
			for(String ip : IPAddresses)
				out.print(" " + ip);
			out.print(" " + BTService + " " + persist);
			out.println();
			out.close();
			changed = false;
		} catch (IOException e) {
			System.out.println("Failed to write PAN config to " + Reference.PAN_CONFIG + ": " + e);
		}			
	}
	
	private String getConfigString(String[] vals, int offset, String def)
	{
		if (vals == null || offset >= vals.length || vals[offset] == null || vals[offset].length() == 0)
			return def;
		return vals[offset];
	}
	
	public void loadConfig()
	{
		System.out.println("Load PAN config");
		String[] vals = null;
		try {
			BufferedReader in = new BufferedReader(new FileReader(Reference.PAN_CONFIG));
			// nasty cludge preserve escaped spaces (convert them to no-break space
			String line = in.readLine().replace("\\ ", "\u00a0");
			vals = line.split("\\s+");
			in.close();
		} catch (IOException e) {
			System.out.println("Failed to load PAN config from " + Reference.PAN_CONFIG + ": " + e);
		}			
		String mode = getConfigString(vals, 0, modeIDS[MODE_NONE]);
		// turn mode into value
		curMode = MODE_NONE;
		for(int i = 0; i < modeIDS.length; i++)
			if (modeIDS[i].equalsIgnoreCase(mode))
			{
				curMode = i;
				break;
			}
		// be sure to convert no-break space back - ahem.
		BTAPName = getConfigString(vals, 1, anyAP).replace("\u00a0", " ");
		BTAPAddress = getConfigString(vals, 2, anyAP);
		for(int i = 0; i < IPAddresses.length; i++)
			IPAddresses[i] = getConfigString(vals, i + 3, autoIP);
		if (curMode == MODE_AP && IPAddresses[0].equals(autoIP))
			IPAddresses[0] = "10.0.1.1";
		BTService = getConfigString(vals, 8, "NAP");
		persist = getConfigString(vals, 9, "N");
		changed = false;
	}
	
	
	public void init(int mode)
	{
		MappedMenu.pan = new MappedMenu("PAN", modeNames, modeIcons);
		
		if (mode != curMode)
		{
			for(int i = 0; i < IPAddresses.length; i++)
				IPAddresses[i] = autoIP;
			switch(mode)
			{
			case MODE_AP:
				IPAddresses[0] = "10.0.1.1";
				break;
			case MODE_APP:
				// For access point plus we need to use a sub-net within the
				// sub-net being used for WiFi. Set a default that may work for
				// most - well it does for me!
				if (MainMenu.wlanAddress != null)
				{
					String[] parts = MainMenu.wlanAddress.split("\\.");
					if (parts.length == 4)
					{
						IPAddresses[0] = parts[0] + '.' + parts[1] + '.' + parts[2] + '.' + defaultIP;
					}
				}
				break;
			}
			BTAPName = anyAP;
			BTAPAddress = anyAP;
			BTService = "NAP";
			persist = "N";
			curMode = mode;
			changed = true;
		}
	}

	/**
	 * Test to see if the IP address string is the special case auto address
	 * @param ip
	 * @return true if the address is the auto address.
	 */
	private boolean isAutoIP(String ip)
	{
		return ip.equals(autoIP);
	}

	/**
	 * Return an IP address suitable for display, replace the auto address with a
	 * more readable version.
	 * @param ip
	 * @return the display string
	 */
	private String getDisplayIP(String ip)
	{
		return isAutoIP(ip) ? "<Auto>" : ip;
	}

	private boolean isAnyAP(String bt)
	{
		return bt.equals(anyAP);
	}
	
	private String getDisplayAP(String bt)
	{
		return isAnyAP(bt) ? "Any Access Point" : bt;
	}
	
	/**
	 * Validate and cleanup the IP address
	 * @param address
	 * @return validated IP or null if there is an error.
	 */
	private String getValidatedIP(String address)
	{
		try 
		{
			return InetAddress.getByName(address).getHostAddress();
		}
		catch (UnknownHostException e)
		{
			return null;
		}
	}
	

	/**
	 * Allow the user to enter an IP address
	 * @param title String to display as the title of the screen
	 * @param ip IP address to edit
	 * @return new validated address
	 */
	private String enterIP(String title, String ip)
	{
		String[] parts = ip.split("\\.");
		for(int i = 0; i < parts.length; i++)
			parts[i] = "000".substring(parts[i].length()) + parts[i];
		String address = parts[0] + "." + parts[1] + "." + parts[2] + "." + parts[3];
		int curDigit = 0;
		while (true)
		{
			MainMenu.self.newScreen(title);
			MainMenu.lcd.drawString(address, 2, 4);
			if (curDigit < 0)
				curDigit = 14;
			if (curDigit >= 15)
				curDigit = 0;
			Utils.drawRect(curDigit * 10 + 18, 60, 13, 20);
			MainMenu.lcd.refresh();
			int key = MainMenu.self.getButtonPress();
			switch (key)
			{
				case Button.ID_ENTER:
				{ // ENTER
					// remove leading zeros
					String ret = getValidatedIP(address);
					if (ret == null)
						MainMenu.self.msg("Invalid address");
					else
						return ret;
					break;
				}
				case Button.ID_LEFT:
				{ // LEFT
					curDigit--;
					if (curDigit < 0)
						curDigit = 14;
					if (address.charAt(curDigit) == '.')
						curDigit--;
					break;
				}
				case Button.ID_RIGHT:
				{ // RIGHT
					curDigit++;
					if (curDigit >= 15)
						curDigit = 0;
					if (address.charAt(curDigit) == '.')
						curDigit++;
					break;
				}
				case Button.ID_ESCAPE:
				{ // ESCAPE
					return ip;
				}
				case Button.ID_UP:
				{
					int val = (address.charAt(curDigit) - '0');
					if (++val > 9)
						val = 0;
					address = address.substring(0, curDigit) + ((char)('0' + val)) + address.substring(curDigit + 1);
					break;
				}
				case Button.ID_DOWN:
				{
					int val = (address.charAt(curDigit) - '0');
					if (--val < 0)
						val = 9;
					address = address.substring(0, curDigit) + ((char)('0' + val)) + address.substring(curDigit + 1);
					break;
				}
			}			   
		}
	}

	/**
	 * Allow the user to choose between an automatic or manual IP address if
	 * manual allow the address to be edited 
	 * @param title
	 * @param ip
	 * @return new ip address
	 */
	private String getIPAddress(String title, String ip)
	{
		String [] strings = {"Automatic", "Advanced"};
		TextMenu menu = new TextMenu(strings, 4);
		MainMenu.self.newScreen(title);
		String dispIP = getDisplayIP(ip);
		MainMenu.lcd.drawString(dispIP, (MainMenu.lcd.getTextWidth() - dispIP.length())/2, 2);
		menu.setItems(strings);
		int selection = menu.select(isAutoIP(ip) ? 0 : 1);
		switch (selection)
		{
		case 0:
			return autoIP;
		case 1:
			return enterIP(title, ip);
		default:
			return ip;
		}
	}
	
	/**
	 * Allow the user to choose the Bluetooth service to connect to.
	 * @param title
	 * @param service
	 * @return new service
	 */
	private String getBTService(String title, String service)
	{
		String [] strings = serviceNames;
		TextMenu menu = new TextMenu(strings, 4);
		MainMenu.self.newScreen(title);
		MainMenu.lcd.drawString(service, (MainMenu.lcd.getTextWidth() - service.length())/2, 2);
		menu.setItems(strings);
		int item = 0;
		while(!strings[item].equalsIgnoreCase(service))
			item++;
		int selection = menu.select(item);
		if (selection > 0)
			return strings[selection];
		else
			return service;
	}
	
	
	public void configureAdvanced()
	{
		int selection = 0;
		int extra = (curMode == MODE_BTC ? 2 : curMode == MODE_USBC ? 1 : 0);
		String [] strings = new String[IPAddresses.length + extra];
		TextMenu menu = new TextMenu(strings);
		while(selection >= 0)
		{
			MainMenu.self.newScreen(modeNames[curMode]);
			for(int i = 0; i < IPAddresses.length; i++)
				strings[i] = IPNames[i] + " " + getDisplayIP(IPAddresses[i]);
			if (extra > 0)
				strings[IPAddresses.length] = "Persist " + persist;
			if (extra > 1)
				strings[IPAddresses.length+1] = "Service " + BTService;
			
			menu.setItems(strings);
			selection = menu.select(selection);
			if (selection < 0) break;
			changed = true;
			if (selection < IPAddresses.length)
				IPAddresses[selection] = getIPAddress(IPNames[selection], IPAddresses[selection]);
			else if (selection == IPAddresses.length)
			{
				if(MenuUtils.askConfirm("Persist Connection"))
				{
					persist = "Y";
				}
				else
				{
					persist = "N";
				}
			}
			else
				BTService = getBTService("Service", BTService);


		}
	}
	
	
	/**
	 * Display all currently known Bluetooth devices.
	 */
	private void selectAP()
	{
		MainMenu.self.newScreen("Devices");
		MainMenu.lcd.drawString("Searching...", 3, 2);
		List<RemoteBTDevice> devList;
		try {
			devList = (List<RemoteBTDevice>) Bluetooth.getLocalDevice().getPairedDevices();
		} catch (BluetoothException e) {
			return;
		}
		if (devList.size() <= 0)
		{
			MainMenu.self.msg("No known devices");
			return;
		}
		
		String[] names = new String[devList.size()];
		int i=0;
		for (RemoteBTDevice btrd: devList)
		{
			names[i] = btrd.getName();
			i++;
		}

		TextMenu deviceMenu = new TextMenu(names);
		int selected = 0;
		MainMenu.self.newScreen("Devices");
		selected = deviceMenu.select(selected);
		if (selected >= 0)
		{
			RemoteBTDevice btrd = devList.get(selected);
			//byte[] devclass = btrd.getDeviceClass();
			BTAPName = btrd.getName();
			BTAPAddress = btrd.getAddress();
			changed = true;
		}
	}
	
	public void configureBTClient(String title)
	{
		String [] strings = {"Any", "Select", "Advanced"};
		TextMenu menu = new TextMenu(strings, 4);
		while (true)
		{
			MainMenu.self.newScreen(title);
			String dispIP = getDisplayAP(BTAPName);
			MainMenu.lcd.drawString(dispIP, (MainMenu.lcd.getTextWidth() - dispIP.length())/2, 2);
			if (!isAnyAP(BTAPName))
				MainMenu.lcd.drawString(BTAPAddress, (MainMenu.lcd.getTextWidth() - BTAPAddress.length())/2, 3);					
			int selection = menu.select(isAnyAP(BTAPName) ? 0 : 1);
			switch (selection)
			{
			case 0:
				BTAPName = anyAP;
				BTAPAddress = anyAP;
				changed = true;
				return;
			case 1:
				selectAP();
				break;
			case 2:
				configureAdvanced();
				break;
			default:
				return;
			}	
		}
	}
	
	public void configure()
	{
		if (curMode == MODE_NONE)
			return;
		if (curMode == MODE_BTC)
			configureBTClient(modeNames[curMode]);
		else
			configureAdvanced();
	}
	
	public void panMenu()
	{
		int selection = 0;
		MappedMenu menu = MappedMenu.pan;
		do
		{
			selection = menu.open();
			if (selection >= 0)
			{
				init(selection);
				configure();
			}
		} while (selection >= 0);
		if (changed)
		{
			WaitScreen.instance.begin("Restart\nPAN\nServices");
			WaitScreen.instance.status("Save configuration");
			saveConfig();
			MainMenu.self.startNetwork(Reference.START_PAN, true);
			WaitScreen.instance.status("Restart name server");
			BrickFinder.stopDiscoveryServer();
			BrickFinder.startDiscoveryServer(curMode == MODE_APP);
			WaitScreen.instance.end();
		}
	}
}