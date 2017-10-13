package com.ec.entries;

import com.ec.addonloader.menu.MappedMenu;
import com.ec.addonloader.menu.MenuEntry;
import com.ec.addonloader.util.Icons;
import com.ec.main.KeyStorage;
import com.ec.main.Main;

import lejos.ev3.startup.ListMenu;
import lejos.ev3.startup.NetUtils;
import lejos.ev3.startup.WaitScreen;

public class APDetail extends MenuEntry {
	
	protected boolean updateList;
	
	public APDetail()
	{
		super("Manage Keys", Icons.IC_LIST);
		this.updateList = false;
	}
	
	@Override
	public void onEntrySelected()
	{
		try
		{
			String[] aps = KeyStorage.getSSIDs();
			ListMenu menu = new ListMenu(aps);
			APDetailMenu submenu = Main.apdetail;
			int selection = 0;
			while(selection >= 0)
			{
				MappedMenu.newScreen("AP Keys");
				selection = menu.getSelection(selection);
				if(selection >= 0)
				{
					String ap = aps[selection];
					submenu.display(ap);
				}
				
				if(updateList)
				{
					aps = KeyStorage.getSSIDs();
					menu.setItems(aps);
					updateList = false;
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static void apConnect(String ssid)
	{
		String pwd = KeyStorage.getPass(ssid);
		if(pwd == null)
		{
			EditEntry.setNewPassword(ssid);
		}
		WaitScreen.instance.begin("Connect\nto\n" + ssid);
		WaitScreen.instance.status("Write config");
		apConnect(ssid, pwd);
		WaitScreen.instance.end();
	}
	
	public static void apConnect(String ssid, String pwd)
	{		
		NetUtils.connect(ssid, pwd);
	}
	
}
