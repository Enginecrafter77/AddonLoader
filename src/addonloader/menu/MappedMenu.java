package addonloader.menu;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import addonloader.util.ExtraCarrier;
import addonloader.util.Icon;
import lejos.GraphicMenu;
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
//TODO Phase upt GraphicMenu in favor of custom-managed dynamic menu implementation.
public class MappedMenu extends GraphicMenu implements ExtraCarrier<String>{
	
	/* STATIC MENU DEFINITIONS */
	/** The main menu */
	public static MappedMenu root;
	/** The system menu */
	public static MappedMenu system;
	/** The sound menu */
	public static MappedMenu sound;
	/** The bluetooth menu */
	public static MappedMenu bluetooth;
	/** The generic file menu */
	public static MappedMenu file;
	/** The executable file menu */
	public static MappedMenu executable;
	/** Menu for bluetooth device */
	public static MappedMenu bluetooth_dev;
	/** The menu displayed when pressing escape in main menu.*/
	public static MappedMenu boot_menu;
	/* STATIC MENU DEFINITIONS */
	
	/** The extra data you can pass to menu entry */
	private String extra;
	/** List containing all the custom menu entires */
	protected final List<MenuEntry> custom_entries;
	
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
	public MappedMenu(String[] items, Icon[] icons) throws IOException
	{
		this(items, Icon.loadIcons(icons), 3);
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
			try
			{	//Try to load icon first, to avoid empty entry name insertion.
				icons[last_index + index] = entries[index].getIcon().call();
				items[last_index + index] = entries[index].getName();
			}
			catch(IOException exc)
			{
				System.err.println("[ERROR] I/O error while loading icon for " + entries[index].getName());
				continue;
			}
			this.custom_entries.add(entries[index]);
		}
		this.setItems(items, icons);
	}
	
	@Override
	public void loadCarrier(String extra)
	{
		this.extra = extra;
	}
	
	@Override
	public String fetchCarrier()
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
		if(selection >= 0) this.getCustomEntry(selection).run();
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

}
