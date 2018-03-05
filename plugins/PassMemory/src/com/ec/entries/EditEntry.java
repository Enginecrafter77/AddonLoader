package com.ec.entries;

import addonloader.lib.Icon;
import com.ec.main.Routines;

public class EditEntry extends APEntry {

	public EditEntry()
	{
		super("Edit", Icon.EDIT);
	}
	
	@Override
	public void onSelected(String ssid)
	{
		Routines.setNewPassword(ssid);
	}
	
}
