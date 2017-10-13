package com.ec.entries;

import com.ec.addonloader.menu.MenuEntry;
import com.ec.addonloader.util.Icons;
import com.ec.main.Main;

public class ConnectEntry extends MenuEntry{
	
	public ConnectEntry()
	{
		super("Connect", Icons.ICWifi);
	}
	
	@Override
	public void onEntrySelected()
	{
		String ssid = Main.apdetail.getExtra();
		APDetail.apConnect(ssid);
	}
	
}
