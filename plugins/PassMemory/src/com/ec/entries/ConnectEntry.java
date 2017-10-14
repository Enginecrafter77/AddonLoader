package com.ec.entries;

import com.ec.addonloader.lib.Icons;

public class ConnectEntry extends APEntry{
	
	public ConnectEntry()
	{
		super("Connect", Icons.ICWifi);
	}
	
	@Override
	public void onSelected(String ap)
	{
		APDetail.apConnect(ap);
	}
	
}
