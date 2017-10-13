package com.ec.addonloader.menu;

import com.ec.addonloader.util.MenuIcon;

import lejos.ev3.startup.MainMenu;

public class MenuEntry {
	
	private MenuIcon ic;
	private String name;
	
	public MenuEntry(String name, MenuIcon ic)
	{
		this.name = name;
		this.ic = ic;
	}
	
	public MenuEntry(String name, String ic)
	{
		this.name = name;
		this.ic = new MenuIcon(ic);
	}
	
	/**
	 * Method called when menu entry is selected. By default, looks if menu entry specifies another menu,
	 * and if so, displays it. It can be safely overriden without the <i>super.onEntrySelected()</i> call.
	 */
	protected void onEntrySelected()
	{
		MappedMenu menu = this.provideMenu();
		if(menu != null)
		{
			int selection = 0;
			while(true)
			{
				MainMenu.self.newScreen(this.name);
				selection = menu.getSelection(selection);
				if(selection >= 0)
				{
					menu.onExternalAction(selection);
				}
				else
				{
					break;
				}
			}
		}
	}
	
	public MenuIcon getIcon()
	{
		return ic;
	}
	
	public String getName()
	{
		return this.name;
	}
	
	/**
	 * Specifies if the entry provides another menu. Used mainly by method onEntrySelectd()
	 * @return
	 * @see #onEntrySelected
	 */
	protected MappedMenu provideMenu()
	{
		return null;
	}
	
	@Override
	public String toString()
	{
		return "MenuEntry(" + this.getName() + ")";
	}
}
