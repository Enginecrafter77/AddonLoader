package com.ec.main;

import com.ec.entries.*;

import addonloader.lib.Icon;
import addonloader.main.ActionRegistry;
import addonloader.main.Addon;
import addonloader.main.MenuAddon;
import addonloader.main.MenuRegistry;
import addonloader.menu.MappedMenu;
import addonloader.menu.MenuEntry;
import addonloader.menu.MethodOverride;

@Addon(name = "PassMemory", apilevel = 53)
public class Main extends MenuAddon {
	
	public static MappedMenu passwords;
	public static APDetailMenu apdetail;
	public static APDetail aps;
	public static MenuEntry ce;
	public static MenuEntry addnet;
	public static MenuEntry delete;
	public static MenuEntry edit;
	public static MenuEntry info;
	public static MethodOverride connect;
	
	@Override
	public void init()
	{
		KeyStorage.init();
		passwords = new MappedMenu().setParent(MenuRegistry.system);
		apdetail = new APDetailMenu();
		aps = new APDetail();
		connect = new ConnectOverride();
		ce = new ConnectEntry();
		delete = new DeleteEntry();
		edit = new EditEntry();
		info = new InfoEntry();
		addnet = new AddNetwork();
	}

	@Override
	public void load()
	{
		ActionRegistry.WIFI_CONNECT.addMethod(new ConnectOverride());
		apdetail.add(ce, delete, edit, info);
		passwords.addToParent("Passwords", Icon.KEY);
		passwords.add(aps, addnet);
	}

	@Override
	public void finish(){}

}
