package com.ec.main;

import lejos.ev3.startup.ListMenu;

import com.ec.addonloader.main.MORegistry;
import com.ec.addonloader.menu.MethodOverride;
import com.ec.entries.APDetail;

public class ConnectOverride implements MethodOverride{

	@Override
	public void run()
	{
		String ssid = MORegistry.getRegistry(MORegistry.Type.WIFI_CONNECT).getExtra();
		System.out.println("Connecting to " + ssid);
		if(ssid.equals("[HIDDEN]"))
		{
			try
			{
				String[] ssids = KeyStorage.getSSIDs();
				ListMenu menu = new ListMenu(ssids);
				int selection = menu.getSelection(0);
				if(selection >= 0)
				{
					ssid = ssids[selection];
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		APDetail.apConnect(ssid);
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
