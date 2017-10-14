package com.ec.entries;

import com.ec.addonloader.menu.MappedMenu;
import com.ec.addonloader.lib.Icons;
import com.ec.main.KeyStorage;

import lejos.ev3.startup.Keyboard;
import lejos.ev3.startup.ListMenu;

public class EditEntry extends APEntry {

	public EditEntry()
	{
		super("Edit", Icons.IC_EDIT);
	}
	
	@Override
	public void onSelected(String ssid)
	{
		setNewPassword(ssid);
	}
	
	public static void setNewPassword(String ssid)
	{
		ListMenu l = new ListMenu(new String[]{"WPA/WPA2", "Open"});
		boolean password = l.getSelection(0) == 0;
		
		try
		{
			if(password)
			{
				MappedMenu.newScreen("Password");
				String pwd = Keyboard.getString();
				if(pwd != null)
				{
					KeyStorage.setPass(ssid, pwd);
				}
			}
			else
			{
				KeyStorage.setPass(ssid, "");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
}
