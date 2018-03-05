package addonloader.menu;

import addonloader.lib.Icon;

/**
 * Class representing entry in MappedMenu. MenuEntry extends Runnable,
 * so when the user selects this menu, some code is run.
 * 
 * MenuEntry consists of {@link #name} and {@link #ic icon}
 * @author Enginecrafter77
 */
public abstract class MenuEntry implements Runnable{
	
	/** The name of the menu entry */
	public final Icon icon;
	/** The icon of the menu entry */
	public final String name;
	
	/**
	 * Constructs MenuEntry using name and MenuIcon instance to provide icon.
	 * @param name Name of the entry
	 * @param ic Icon for the entry
	 */
	public MenuEntry(String name, Icon ic)
	{
		this.name = name;
		this.icon = ic;
	}
		
	@Override
	public String toString()
	{
		return "MenuEntry(" + this.name + ")";
	}
}
