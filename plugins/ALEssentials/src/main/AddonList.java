package main;

import com.ec.addonloader.main.MenuAddon;
import com.ec.addonloader.main.MenuRegistry;
import com.ec.addonloader.menu.MenuEntry;
import com.ec.addonloader.lib.Icons;
import com.ec.addonloader.lib.MenuUtils;

import lejos.ev3.startup.ListMenu;

public class AddonList extends MenuEntry {

	public AddonList()
	{
		super("Addons", Icons.IC_LIST);
	}
	
	@Override
	public void run()
	{
		MenuAddon[] addons = MenuRegistry.getAddons();
		String[] items = new String[addons.length];
		for(int i = 0; i < addons.length; i++)
		{
			items[i] = addons[i].getName();
		}
		
		ListMenu menu = new ListMenu(items);
		int selection = 0;
		while(selection >= 0)
		{
			MenuUtils.newScreen("Addons");
			selection = menu.getSelection(selection);
			if(selection >= 0)
			{
				Main.addonmenu.display(addons[selection]);
			}
		}
	}

}
