package com.ec.main;

import com.ec.addonloader.menu.MORegistry;
import com.ec.addonloader.menu.MethodOverride;
import com.ec.entries.APDetail;

public class ConnectOverride implements MethodOverride{

	private static MORegistry registry;
	
	public static void init()
	{
		registry = MORegistry.getRegistry(MORegistry.Type.WIFI_CONNECT);
		registry.add(new ConnectOverride());
	}
	
	@Override
	public void run()
	{
		String ssid = registry.getExtra();
		System.out.println("Connecting to " + ssid);
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
