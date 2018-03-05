package com.ec.entries;

import com.ec.main.KeyStorage;
import com.ec.main.Main;

import addonloader.lib.Icon;
import addonloader.menu.MenuEntry;
import addonloader.util.MenuUtils;
import lejos.ListMenu;

public class APDetail extends MenuEntry {
	
	protected boolean updateList;
	
	public APDetail()
	{
		super("Manage Keys", Icon.LIST);
		this.updateList = false;
	}
	
	@Override
	public void run()
	{
		try
		{
			String[] aps = KeyStorage.getSSIDs();
			ListMenu menu = new ListMenu(aps);
			APDetailMenu submenu = Main.apdetail;
			int selection = 0;
			while(selection >= 0)
			{
				MenuUtils.newScreen("AP Keys");
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
	
}
