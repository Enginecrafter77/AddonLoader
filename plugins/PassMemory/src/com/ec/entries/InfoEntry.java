package com.ec.entries;

import com.ec.addonloader.util.Icons;
import com.ec.main.KeyStorage;

import lejos.ev3.startup.MainMenu;

public class InfoEntry extends APEntry {
	
	public InfoEntry()
	{
		super("Info", Icons.ICInfo);
	}
	
	@Override
	public void onSelected(String ssid)
	{
		try
		{
			MainMenu.self.newScreen();
			KeyStorage.viewInfo(ssid);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
}
