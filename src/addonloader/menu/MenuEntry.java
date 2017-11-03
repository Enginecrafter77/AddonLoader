package addonloader.menu;

import addonloader.lib.MenuIcon;
import lejos.MainMenu;

/**
 * Class representing entry in MappedMenu.
 * MenuEntry extends Runnable, so when the user selects this menu,
 * the code is run.
 * @author Enginecrafter77
 */
public class MenuEntry implements Runnable{
	
	private final MenuIcon ic;
	private final String name;
	private final Runnable procedure;
	
	/**
	 * Constructs MenuEntry using name and MenuIcon instance to provide icon.
	 * @param name Name of the entry
	 * @param ic Icon for the entry
	 */
	public MenuEntry(String name, MenuIcon ic)
	{
		this.name = name;
		this.ic = ic;
		procedure = null;
	}
	
	/**
	 * Simplified version of the first constructor, uses String8 to construct MenuIcon.
	 * @param name Name of the entry
	 * @param ic String8 icon representation, commonly found in {@link Icons} class.
	 */
	public MenuEntry(String name, String ic)
	{
		this.name = name;
		this.ic = new MenuIcon(ic);
		procedure = null;
	}
	
	/**
	 * Constructs MenuEntry that runs Runnable code. Can be used to create aliases for MenuEntries.
	 * @param name Name of the entry
	 * @param ic String8 icon representation, commonly found in {@link Icons} class.
	 * @param code Procedure to be run.
	 */
	public MenuEntry(String name, String ic, Runnable code)
	{
		this.name = name;
		this.ic = new MenuIcon(ic);
		this.procedure = code;
	}
	
	/**
	 * Constructs MenuEntry that opens new MappedMenu.
	 * Commonly used to access submenus, and so easily construct them.
	 * @param name Name of the entry
	 * @param ic String8 icon representation, commonly found in {@link Icons} class.
	 * @param opens Menu to be opened when entry is selected.
	 */
	public MenuEntry(String name, String ic, final MappedMenu opens)
	{
		this.name = name;
		this.ic = new MenuIcon(ic);
		this.procedure = new Runnable(){
			@Override
			public void run()
			{
				openMenu(opens);
			}
		};
	}
	
	/**
	 * Method called when menu entry is selected.
	 */
	public void run()
	{
		if(procedure != null)
		{
			this.procedure.run();
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
	 * Method to easily implement entry opening menu.
	 */
	protected void openMenu(MappedMenu menu)
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
	
	@Override
	public String toString()
	{
		return "MenuEntry(" + this.getName() + ")";
	}
}
