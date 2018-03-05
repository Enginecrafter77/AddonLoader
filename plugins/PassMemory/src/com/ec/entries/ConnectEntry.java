package com.ec.entries;

import addonloader.lib.Icon;
import com.ec.main.Routines;

public class ConnectEntry extends APEntry{
	
	public ConnectEntry()
	{
		super("Connect", Icon.WIFI);
	}
	
	@Override
	public void onSelected(String ap)
	{
		Routines.apConnect(ap, false);
	}
	
}
