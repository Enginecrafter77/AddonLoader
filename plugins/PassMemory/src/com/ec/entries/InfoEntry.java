package com.ec.entries;

import com.ec.addonloader.lib.Icons;
import com.ec.addonloader.lib.MenuUtils;
import com.ec.main.KeyStorage;

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
			MenuUtils.newScreen();
			KeyStorage.viewInfo(ssid);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
}
