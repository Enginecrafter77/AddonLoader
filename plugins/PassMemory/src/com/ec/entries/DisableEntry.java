package com.ec.entries;

import com.ec.addonloader.menu.MenuEntry;
import com.ec.addonloader.lib.Icons;

import lejos.ev3.startup.WaitScreen;

public class DisableEntry extends MenuEntry{
	
	public DisableEntry()
	{
		super("Stop Wifi", Icons.ICNone);
	}
	
	@Override
	public void run()
	{
		WaitScreen.instance.begin("Switch\nwifi\noff");
		WaitScreen.instance.status("Writing config");
		APDetail.apConnect("null", "null");
		WaitScreen.instance.end();
	}
	
}
