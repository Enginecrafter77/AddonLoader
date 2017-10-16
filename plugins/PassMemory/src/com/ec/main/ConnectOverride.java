package com.ec.main;

import lejos.ev3.startup.ListMenu;

import com.ec.addonloader.main.MORegistry;
import com.ec.addonloader.menu.MappedMenu;
import com.ec.addonloader.menu.MethodOverride;

public class ConnectOverride implements MethodOverride{

	@Override
	public void run()
	{
		String ssid = MORegistry.getRegistry(MORegistry.Type.WIFI_CONNECT).getExtra();
		System.out.println("Connecting to " + ssid);
		boolean hidden = false;
		if(ssid.equals("[HIDDEN]"))
		{
			try
			{
				String[] ssids = KeyStorage.getSSIDs();
				ListMenu menu = new ListMenu(ssids);
				MappedMenu.newScreen("Select ID");
				int selection = menu.getSelection(0);
				if(selection >= 0)
				{
					ssid = ssids[selection];
				}
				hidden = true;
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		Routines.apConnect(ssid, hidden);
	}

	@Override
	public boolean disableDefaultCode()
	{
		return true;
	}

	@Override
	public boolean runBefore()
	{
		return true;
	}

}
