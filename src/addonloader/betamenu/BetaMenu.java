package addonloader.betamenu;

import java.util.Collection;
import java.util.Iterator;

import addonloader.betamenu.app.BatteryApplet;
import addonloader.betamenu.app.WifiApplet;
import addonloader.betamenu.net.AccessPoint;
import addonloader.betamenu.net.ConnectionManager;
import addonloader.main.DefaultKeyboard;
import addonloader.menu.MappedMenu;
import addonloader.menu.MenuEntry;
import addonloader.menu.SimpleMenuEntry;
import addonloader.menu.SubmenuEntry;
import addonloader.util.InputMethod;
import addonloader.util.LoadingScreen;
import addonloader.util.MenuUtils;
import addonloader.util.StockIcon;
import lejos.hardware.Button;
import lejos.hardware.RemoteBTDevice;
import lejos.hardware.lcd.LCD;
import lejos.utility.TextMenu;

public class BetaMenu extends MappedMenu {
	
	private static final long serialVersionUID = -7226674803840849026L;
	public static BetaMenu instance;
	
	public static void main(String[] args)
	{
		LCD.drawString("Wait for client", 0, 3);
		TerminalServer.attach_std();
		LCD.clear();
		InputMethod.set_fallback(new DefaultKeyboard());
		InputMethod.add(new TerminalServer());
		instance = new BetaMenu(args.length > 0 ? args[0] : "EV3");
		instance.open();
	}
	
	//			//
	//	CLASS	//
	//			//
	
	private SubmenuEntry wifi, bluetooth, pan, system, sound, btsettings;
	private MappedMenu btdevmenu;
	private ConnectionManager conman;
	private LoadingScreen loading_screen;
	private DockBar menubar;
	private int exit;
	
	public BetaMenu(String hostname)
	{
		super(hostname);
		this.loading_screen = new LoadingScreen();
		this.loading_screen.start("BetaMenu");
		
		this.loading_screen.setState("ConnectionMan", 10);
		this.conman = new ConnectionManager("####");
		this.loading_screen.setState("Init Menu", 50);
		this.menubar = new DockBar(3, 750);
		this.wifi = new SubmenuEntry("Wifi", StockIcon.WIFI);
		this.bluetooth = new SubmenuEntry("Bluetooth", StockIcon.BLUETOOTH);
		this.pan = new SubmenuEntry("PAN", StockIcon.NETWORK);
		this.system = new SubmenuEntry("System", StockIcon.GEAR);
		this.sound = new SubmenuEntry("Sound", StockIcon.SOUND);
		this.btsettings = new SubmenuEntry("Settings", StockIcon.GEAR);
		this.btdevmenu = new MappedMenu("BT DEV");
		
		this.loading_screen.setState("ConnectionMan", 10);
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
	public int open()
	{
		menubar.start();
		System.out.println("Menu started");
		
		int selection;
		while(exit == 0)
		{
			selection = super.open();
			if(selection < 0) exit = 1;
			this.get(selection).run();
		}
		
		System.out.println("Menu finished");
		menubar.interrupt();
		return exit;
	}
	
	@Override
	public boolean add(MenuEntry entry)
	{
		if(entry instanceof SubmenuEntry) ((SubmenuEntry)entry).set_title_service(this.menubar);
		return super.add(entry);
	}
	
	private void init_menu_entries()
	{		
		wifi.add(new SimpleMenuEntry("Connect", StockIcon.SEARCH) {
			@Override
			public void run()
			{
				AccessPoint[] access_points = conman.search_wifi();
				String[] menu_items = new String[access_points.length + 1];
				for(int index = 0; index < access_points.length; index++) menu_items[index] = access_points[index].essid;
				menu_items[access_points.length] = "Add Hidden..."; //Special entry for hidden AP.
				
				LCD.clear();
				int selection = new TextMenu(menu_items).select();
				if(selection < 0) return;
				String pass = InputMethod.enter();
				loading_screen.start("Connecting");
				AccessPoint target = selection == access_points.length ? new AccessPoint(InputMethod.enter()) : access_points[selection];
				try
				{
					loading_screen.setState("Computing PSK", 15);
					pass = target.compute_psk(pass);
					loading_screen.setState("Writing WPA", 50);
					conman.configure_wpa(target, pass);
					loading_screen.setState("Querying AP");
					Process proc = conman.wifi_connect();
					loading_screen.readStream(proc.getInputStream(), 2);
					proc.waitFor();
					loading_screen.setState("Parsing IP", 95);
					conman.update_interfaces();
				}
				catch(Exception exc)
				{
					exc.printStackTrace();
				}
				loading_screen.stop();
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
				conman.bluetooth.setVisibility(MenuUtils.askConfirm("Make Visible?"));
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
				btdevmenu.get(btdevmenu.open()).run();
			}
		});
		bluetooth.add(btsettings);
		
		system.add(new SimpleMenuEntry("Keyboard", StockIcon.LIST) {
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
	}
}
