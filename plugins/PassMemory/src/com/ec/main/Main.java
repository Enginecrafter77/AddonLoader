package com.ec.main;

import com.ec.addonloader.main.Addon;
import com.ec.addonloader.main.MenuAddon;
import com.ec.addonloader.main.MenuRegistry;
import com.ec.addonloader.menu.MappedMenu;
import com.ec.addonloader.menu.MenuEntry;
import com.ec.addonloader.menu.MethodOverride;
import com.ec.addonloader.util.Icons;
import com.ec.entries.*;

@Addon(name = "PassMemory")
public class Main extends MenuAddon{
	
	public static MappedMenu passwords;
	public static APDetailMenu apdetail;
	public static APDetail aps;
	public static MenuEntry disablewifi;
	public static MenuEntry ce;
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
		disablewifi = new DisableEntry();
		ce = new ConnectEntry();
		delete = new DeleteEntry();
		edit = new EditEntry();
		info = new InfoEntry();
	}

	@Override
	public void load()
	{
		ConnectOverride.init();
		apdetail.addMenuEntries(ce, delete, edit, info);
		passwords.addToParent("Passwords", Icons.ICPIN);
		passwords.addMenuEntries(disablewifi, aps);
	}

	@Override
	public void finish(){}

}
