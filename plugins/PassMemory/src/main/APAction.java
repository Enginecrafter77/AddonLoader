package com.ec.main;

import java.io.StringReader;

import addonloader.menu.InputMethod;
import addonloader.menu.SimpleMenuEntry;
import addonloader.util.Icon;
import lejos.MainMenu;
import lejos.NetUtils;

public class APAction extends SimpleMenuEntry {

	private char action;
	
	public APAction(String name, Icon icon, char action)
	{
		super(name, icon);
		this.action = action;
	}
	
	@Override
	public void run()
	{
		String ssid = Main.access_point.fetchCarrier();
		String psk = Main.key_storage.getProperty(ssid);
		
		try
		{
			switch(action)
			{
			case 'c':
				NetUtils.writeConfig(ssid, psk, false);
				break;
			case 'm':
				psk = InputMethod.current.call(); //Technically not PSK anymore, but used to simply store the output.
				Main.key_storage.setProperty(ssid, NetUtils.computePSK(ssid, psk));
				break;
			case 'd':
				Main.key_storage.remove(ssid);
				break;
			case 'i':
				MainMenu.default_viewer.open(new StringReader(String.format("SSID: %s\nPSK: [SECRET]\nENCRYPTION: %s", ssid, psk.isEmpty())));
				break;
			}
		}
		catch(Exception exc)
		{
			exc.printStackTrace();
		}
	}

}
