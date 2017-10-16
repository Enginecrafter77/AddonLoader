package com.ec.entries;

import com.ec.addonloader.lib.Icons;
import com.ec.main.Routines;

public class EditEntry extends APEntry {

	public EditEntry()
	{
		super("Edit", Icons.IC_EDIT);
	}
	
	@Override
	public void onSelected(String ssid)
	{
		Routines.setNewPassword(ssid);
	}
	
}
