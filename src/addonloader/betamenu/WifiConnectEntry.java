package addonloader.betamenu;

import addonloader.betamenu.net.AccessPoint;
import addonloader.betamenu.net.ConnectionManager;
import addonloader.menu.SimpleMenuEntry;
import addonloader.util.LoadingScreen;
import addonloader.util.input.InputMethod;
import addonloader.util.ui.StockIcon;
import lejos.hardware.lcd.LCD;
import lejos.utility.TextMenu;

public class WifiConnectEntry extends SimpleMenuEntry {

	private final ConnectionManager conman;
	private final LoadingScreen loading_screen;;
	
	public WifiConnectEntry(ConnectionManager conman, LoadingScreen screen)
	{
		super("Connect", StockIcon.SEARCH);
		this.conman = conman;
		this.loading_screen = screen;
	}

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
		AccessPoint target = selection == access_points.length ? new AccessPoint(InputMethod.enter()) : access_points[selection];
		
		String pass = InputMethod.enter();
		loading_screen.start("Connecting");
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

}
