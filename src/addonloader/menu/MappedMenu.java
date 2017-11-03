package addonloader.menu;

import java.util.ArrayList;

import addonloader.lib.ExtraCarrier;
import addonloader.lib.Icons;
import lejos.GraphicMenu;
import lejos.hardware.lcd.Image;

public class MappedMenu extends GraphicMenu implements ExtraCarrier<String>{
	
	private String[] icons;
	private final ArrayList<MenuEntry> customEntries;
	private static final int defaultLine = 3;
	private MappedMenu parent;
	private String extra;
	
	/**
	 * Constructs MappedMenu using list of default {@code items} and {@code icons} with the custom menu line.
	 * @param items Default items to always display.
	 * @param icons Icons for default items.
	 * @param line The menu line.
	 */
	public MappedMenu(String[] items, String[] icons, int line)
	{
		super(items, icons, line);
		this.icons = icons;
		this.customEntries = new ArrayList<MenuEntry>();
	}
	
	/**
	 * Constructs MappedMenu using list of default {@code items} and {@code icons} on the default(3) line.
	 * @param items Default items to always display.
	 * @param icons Icons for default items.
	 */
	public MappedMenu(String[] items, String[] icons)
	{
		this(items, icons, defaultLine);
	}
	
	/**
	 * Constructs MappedMenu with specified number of empty default entries.
	 * @param nitems Number of default empty entries.
	 */
	public MappedMenu(int nitems)
	{
		this(null, null);
		String[] items = new String[nitems];
		String[] icons = new String[nitems];
		for(int i = 0; i < nitems; i++)
		{
			items[i] = "";
			icons[i] = "";
		}
		this.icons = icons;
		this.setItems(items, icons);
	}
	
	/**
	 * Creates empty MappedMenu instance with no default items.
	 * Mainly used for creation of custom items.
	 */
	public MappedMenu()
	{
		this(0);
	}
	
	/**
	 * Attaches parent menu to this menu instance to use function {@link #addToParent(String, String)}.
	 * @param parent The parent menu.
	 * @return Instance of this menu.
	 */
	public MappedMenu setParent(MappedMenu parent)
	{
		this.parent = parent;
		return this;
	}
	
	/**
	 * @return List of icons for items in this menu.
	 */
	public String[] getIcons()
	{
		return this.icons;
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
	public void addToParent(String name, String icon)
	{
		if(parent != null)
		{
			this.parent.add(new MenuEntry(name, icon, this));
		}
	}
	
	/**
	 * Reloads icons for this menu. Useful when making changing icons.
	 */
	public void updateIcons()
	{
		for(int i = 0; i < icons.length; i++)
		{
			this._icons[i] = Icons.stringToBytes8(icons[i]);
			this._iconImages[i] = new Image(32, 32, _icons[i]);
		}
	}
	
	/**
	 * Adds menu entries to this menu.
	 * NOTE if you want correct order, swap the entire order of custom entries.
	 * @param m
	 */
	public void add(MenuEntry... m)
	{
		String[] nitems = new String[this.getItems().length + m.length];
		String[] nicons = new String[this.icons.length + m.length];
		for(int i = 0; i < nitems.length; i++)
		{
			if(i < this.getItems().length)
			{
				nitems[i] = this.getItems()[i];
				nicons[i] = this.icons[i];
			}
			else
			{
				int index = (nitems.length - i) - 1;
				nitems[i] = m[index].getName();
				nicons[i] = m[index].getIcon().toString();
				customEntries.add(m[index]);
			}
		}
		this.setItems(nitems, nicons);
	}
	
	@Override
	public void setItems(String[] items, String[] icons)
	{
		super.setItems(items, icons);
		this.icons = icons;
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
		return this.customEntries.get(index - (this.getItems().length - this.customEntries.size()));
	}

}
