package com.ec.entries;

import lejos.ev3.startup.Keyboard;

import com.ec.addonloader.lib.Icons;
import com.ec.addonloader.lib.MenuUtils;
import com.ec.addonloader.menu.MenuEntry;
import com.ec.main.KeyStorage;

public class AddNetwork extends MenuEntry {

	public AddNetwork()
	{
		super("Add Netwrok", Icons.ICAccessPointPlus);
	}

	@Override
	public void run()
	{
		MenuUtils.newScreen("Enter SSID");
		String name = Keyboard.getString();
		MenuUtils.newScreen("Password");
		String pwd = Keyboard.getString();
		KeyStorage.add(name, pwd);
	}

}
