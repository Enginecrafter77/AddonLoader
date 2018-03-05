package com.ec.entries;

import java.io.IOException;

import addonloader.lib.Icon;
import addonloader.menu.MenuEntry;
import com.ec.main.Main;

public abstract class APEntry extends MenuEntry{
	
	public APEntry(String name, Icon icon)
	{
		super(name, icon);
	}
	
	@Override
	public final void run()
	{
		try
		{
			this.onSelected(this.getSSID());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public abstract void onSelected(String ssid) throws IOException;
	
	public String getSSID()
	{
		return Main.apdetail.getExtra();
	}
	
}
