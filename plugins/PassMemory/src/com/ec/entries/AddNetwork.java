package com.ec.entries;

import addonloader.lib.Icon;
import addonloader.menu.MenuEntry;
import addonloader.util.MenuUtils;
import addonloader.util.InputMethod;
import com.ec.main.KeyStorage;

public class AddNetwork extends MenuEntry {

	public AddNetwork()
	{
		super("Add Netwrok", Icon.AP_PLUS);
	}

	@Override
	public void run()
	{
		MenuUtils.newScreen("Enter SSID");
		String name = InputMethod.current.getString();
		MenuUtils.newScreen("Password");
		String pwd = InputMethod.current.getString();
		KeyStorage.add(name, pwd);
	}

}
