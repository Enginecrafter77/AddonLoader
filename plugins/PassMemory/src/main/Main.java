package com.ec.main;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import addonloader.main.MenuAddon;
import addonloader.menu.MappedMenu;
import addonloader.menu.SimpleMenuEntry;
import addonloader.menu.SubmenuEntry;
import addonloader.util.StockIcon;

public class Main extends MenuAddon {
	
	public static File key_storage_file;
	public static Properties key_storage;
	public static ConnectAction cncact;
	public static SubmenuEntry password_manager;
	public static MappedMenu access_point;
	public static SimpleMenuEntry access_point_list;
	public static SimpleMenuEntry wifi_toggle;
	public static APAction ap_connect;
	public static APAction ap_info;
	public static APAction ap_modify;
	public static APAction ap_delete;
	
	public Main()
	{
		super(71, "PassMemory");
	}
	
	@Override
	public void init()
	{
		key_storage_file = new File("/home/root/lejos/config/stored_pass");
		key_storage = new Properties();
		cncact = new ConnectAction();
		
		password_manager = new SubmenuEntry("Passwords", StockIcon.KEY);
		access_point = new MappedMenu();
		access_point_list = new APList();
		wifi_toggle = new WifiToggleEntry();
		
		ap_connect = new APAction("Connect", StockIcon.AP_PLUS, 'c');
		ap_info = new APAction("Information", StockIcon.INFO, 'i');
		ap_modify = new APAction("Change Password", StockIcon.KEY, 'm');
		ap_delete = new APAction("Delete", StockIcon.DELETE, 'd');
	}

	@Override
	public void load()
	{
		cncact.attach();
		access_point.add(ap_connect, ap_info, ap_modify, ap_delete);
		password_manager.add(wifi_toggle, access_point_list);
		MappedMenu.system.add(password_manager);
		
		try
		{
			key_storage.load(new FileReader(key_storage_file));
		}
		catch(IOException exc)
		{
			System.err.println("[ERROR] Failed loading key storage.");
		}
	}

	@Override
	public void cleanup()
	{
		try
		{
			Main.key_storage.store(new FileWriter(Main.key_storage_file), "PassMemory(r) AP PSK KeyStorage");
		}
		catch(IOException exc)
		{
			exc.printStackTrace();
		}
	}

}
