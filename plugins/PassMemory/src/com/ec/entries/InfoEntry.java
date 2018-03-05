package com.ec.entries;

import addonloader.lib.Icon;
import addonloader.util.MenuUtils;
import com.ec.main.KeyStorage;

public class InfoEntry extends APEntry {
	
	public InfoEntry()
	{
		super("Info", Icon.INFO);
	}
	
	@Override
	public void onSelected(String ssid)
	{
		try
		{
			MenuUtils.newScreen();
			KeyStorage.viewInfo(ssid);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
}
