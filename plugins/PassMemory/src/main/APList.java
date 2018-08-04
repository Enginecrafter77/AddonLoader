package com.ec.main;

import java.util.Enumeration;

import addonloader.menu.SimpleMenuEntry;
import addonloader.util.StockIcon;
import lejos.MainMenu;
import lejos.utility.TextMenu;

public class APList extends SimpleMenuEntry {

	public APList()
	{
		super("Known APs", StockIcon.LIST);
	}

	@Override
	public void run()
	{
		//TODO FIX - Displays PSKs (values), not keys as expected.
		Enumeration<Object> enm = Main.key_storage.elements();
		String[] known_access_points = new String[Main.key_storage.size()];
		
		for(int index = 0; enm.hasMoreElements(); index++) known_access_points[index] = (String)enm.nextElement();
		
		TextMenu access_point_selection = new TextMenu(known_access_points);
		MainMenu.self.newScreen("Known APs");
		int selection = access_point_selection.select();
		if(selection < 0) return;
		
		Main.access_point.loadCarrier(known_access_points[selection]);
		while(selection > -1)
		{
			MainMenu.self.newScreen(known_access_points[selection]);
			selection = Main.access_point.select();
			Main.access_point.onExternalAction(selection);
		}
	}

}
