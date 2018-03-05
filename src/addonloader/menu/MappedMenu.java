package addonloader.menu;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import addonloader.lib.ExtraCarrier;
import addonloader.lib.Icon;
import lejos.GraphicMenu;
import lejos.MainMenu;
import lejos.hardware.lcd.Image;

/**
 * MappedMenu is simply menu, where you can dynamically add new entries.
 * Is is subclass of the {@link GraphicMenu}, so it can be directly used
 * in the original implementation.
 * 
 * MappedMenu consists of default entries, provided in constructor,
 * and {@link #custom_entries} List, that is used to add custom
 * entries during runtime.
 * @author Enginecrafter77
 */
public class MappedMenu extends GraphicMenu implements ExtraCarrier<String>{
	
	/** List containing all the custom menu entires */
	private final List<MenuEntry> custom_entries;
	/** The extra data you can pass to menu entry */
	private String extra;
	
	/**
	 * Constructs MappedMenu using list of default {@code items} and {@code icons} with the custom menu line.
	 * @param items Default items to always display.
	 * @param icons Icons for default items.
	 * @param line The menu line.
	 */
	public MappedMenu(String[] items, Image[] icons, int line)
	{
		super(items, icons, line);
		this.icons = icons;
		this.custom_entries = new LinkedList<MenuEntry>();
	}
	
	/**
	 * Constructs MappedMenu using list of default {@code items} and {@code icons} on the default(3) line.
	 * @param items Default items to always display.
	 * @param icons Icons for default items.
	 */
	public MappedMenu(String[] items, Image[] icons)
	{
		this(items, icons, 3);
	}
	
	/**
	 * Constructs MappedMenu using list of default {@code items} and {@code icons} on the default(3) line.
	 * @param items Default items to always display.
	 * @param icons Icons for default items.
	 */
	public MappedMenu(String[] items, Icon[] icons)
	{
		this(items, Icon.toImages(icons), 3);
	}
	
	/**
	 * Constructs MappedMenu with specified number of empty default entries.
	 * @param nitems Number of default empty entries.
	 */
	public MappedMenu(int nitems)
	{
		this(new String[nitems], new Image[nitems]);
		for(int i = 0; i < nitems; i++)
		{
			this._items[i] = "";
		}
	}
	
	/**
	 * Creates empty MappedMenu instance with no default items.
	 * Mainly used for creation of custom menus.
	 */
	public MappedMenu()
	{
		this(0);
	}
	
	/**
	 * Automatically adds this MappedMenu as submenu to parent MappedMenu.
	 * The parent is set in the constructor {@link #MappedMenu(String[], String[], int, MappedMenu)}
	 * as the fourth parameter. This basically generates {@link MenuEntry} from {@code name} and {@code icon}
	 * and makes the {@link MenuEntry#provideMenu()} return {@code this}.
	 * If parent wasn't set, does nothing.
	 * @param name The text to be displayed above the entry.
	 * @param icon The icon to be displayed as the menu entry.
	 */
	public void addToParent(String name, Icon icon, MappedMenu parent)
	{
		parent.add(new MenuEntry(name, icon) {
			@Override
			public void run()
			{
				parent.openMenu();
			}
		});
	}
	
	/**
	 * Adds menu entries to this menu.
	 * This method adds items to {@link #custom_entries registry}, and
	 * expands the items and icons array.
	 * @param entries The entries to be added.
	 */
	public void add(MenuEntry... entries)
	{
		int last_index = this._items.length;
		int size = last_index + entries.length;
		String[] items = Arrays.copyOf(this._items, size);
		Image[] icons = Arrays.copyOf(this.icons, size);
		for(int index = 0; index < entries.length; index++)
		{
			items[last_index + index] = entries[index].name;
			icons[last_index + index] = entries[index].icon.loadIcon();
			this.custom_entries.add(entries[index]);
		}
		this.setItems(items, icons);
	}
	
	@Override
	public void setExtra(String extra)
	{
		this.extra = extra;
	}
	
	@Override
	public String getExtra()
	{
		return this.extra;
	}
	
	@Override
	public String toString()
	{
		return String.format("MappedMenu(%s:%d)", this._title, this.custom_entries.size());
	}
	
	/**
	 * Called internally to provide option for custom menu entries.
	 * The main menu calls {@code switch(selection)} and makes cases for default entries.
	 * At the end of the switch there is case to respond to escape and a {@code default} case that responds to
	 * any other numbers than the defaults, and so there it is called to launch {@link MenuEntry#onEntrySelected()}.
	 * You may also want to make your own menu, and make it launch some code. Then, just add some MenuEntry (ies) to
	 * it and call this function.
	 * @param selection The index of selected item.
	 */
	public void onExternalAction(int selection)
	{
		if(selection >= 0)
		{
			this.getCustomEntry(selection).run();
		}
	}
	
	/**
	 * @param index MappedMenu global entry index.
	 * @return MenuEntry based on MappedMenu global index.
	 */
	public MenuEntry getCustomEntry(int index)
	{
		//Compute first the size occupied by default entries, and then removes the number from index.
		return this.custom_entries.get(index - (this._items.length - this.custom_entries.size()));
	}
	
	/**
	 * Helper function to open the menu without writing too much code.
	 */
	public void openMenu()
	{
		int selection = 0;
		while(selection > -1)
		{
			MainMenu.self.newScreen(this._title);
			selection = this.getSelection(selection);
			if(selection > -1) this.onExternalAction(selection);
		}
	}

}
