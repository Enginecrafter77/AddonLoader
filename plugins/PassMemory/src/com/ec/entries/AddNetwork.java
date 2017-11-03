package com.ec.entries;

import com.ec.addonloader.lib.Icons;
import com.ec.addonloader.lib.MenuUtils;
import com.ec.addonloader.menu.MenuEntry;
import com.ec.addonloader.util.InputMethod;
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
		String name = InputMethod.current.getString();
		MenuUtils.newScreen("Password");
		String pwd = InputMethod.current.getString();
		KeyStorage.add(name, pwd);
	}

}
