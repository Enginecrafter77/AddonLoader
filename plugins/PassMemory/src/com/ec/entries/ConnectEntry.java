package com.ec.entries;

import com.ec.addonloader.lib.Icons;
import com.ec.main.Routines;

public class ConnectEntry extends APEntry{
	
	public ConnectEntry()
	{
		super("Connect", Icons.ICWifi);
	}
	
	@Override
	public void onSelected(String ap)
	{
		Routines.apConnect(ap, false);
	}
	
}
