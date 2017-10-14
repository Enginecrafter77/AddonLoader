package com.ec.entries;

import com.ec.addonloader.lib.Icons;
import com.ec.main.KeyStorage;
import com.ec.main.Main;

public class DeleteEntry extends APEntry {
	
	public DeleteEntry()
	{
		super("Delete", Icons.ICDelete);
	}
	
	@Override
	public void onSelected(String ssid)
	{
		try
		{
			System.out.println("Running remove on [" + ssid + "] from DeleteEntry.");
			KeyStorage.remove(ssid);
			Main.apdetail.cnt = false;
			Main.aps.updateList = true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
}
