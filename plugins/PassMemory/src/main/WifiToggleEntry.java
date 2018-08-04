package main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import addonloader.menu.SimpleMenuEntry;
import addonloader.util.StockIcon;
import lejos.Reference;

public class WifiToggleEntry extends SimpleMenuEntry {

	public WifiToggleEntry()
	{
		super("Wifi ON/OFF", StockIcon.ACCESSPOINT);
	}

	@Override
	public void run()
	{
		try
		{
			BufferedReader operstate = new BufferedReader(new FileReader("/sys/class/net/" + Reference.WLAN_INTERFACE + "/operstate"));
			boolean is_up = operstate.readLine().equals("up");
			operstate.close();
			
			Runtime.getRuntime().exec(String.format("ip link set %s %s", Reference.WLAN_INTERFACE, is_up ? "down" : "up"));
		}
		catch(IOException e)
		{
			System.err.println("[ERROR] Reading sysfs values failed.");
		}
	}

}
