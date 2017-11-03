package com.ec.main;

import lejos.ev3.startup.ListMenu;
import lejos.ev3.startup.NetUtils;
import lejos.ev3.startup.WaitScreen;

import com.ec.addonloader.lib.MenuUtils;
import com.ec.addonloader.util.InputMethod;

public class Routines {
	
	public static void apConnect(String ssid, boolean hidden)
	{
		String pwd = KeyStorage.getPass(ssid);
		if(pwd == null)
		{
			setNewPassword(ssid);
		}
		WaitScreen.instance.begin("Connect\nto\n" + ssid);
		WaitScreen.instance.status("Write config");
		NetUtils.writeConfig(ssid, pwd, hidden);
		WaitScreen.instance.status("Connecting");
		NetUtils.connect();
		WaitScreen.instance.end();
	}
	
	public static void setNewPassword(String ssid)
	{
		ListMenu l = new ListMenu(new String[]{"WPA/WPA2", "Open"});
		MenuUtils.newScreen("Encryption");
		String pwd;
		if(l.getSelection(0) == 0)
		{
			MenuUtils.newScreen("Password");
			pwd = InputMethod.current.getString();
		}
		else
		{
			pwd = "";
		}
		KeyStorage.add(ssid, pwd);
	}
	
}
