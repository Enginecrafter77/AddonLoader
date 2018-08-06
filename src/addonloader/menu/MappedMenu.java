package addonloader.menu;

import java.io.IOException;
import addonloader.util.DataCarrier;
import addonloader.util.ui.Icon;
import addonloader.util.ui.MenuCircular;
import addonloader.util.ui.StockIcon;
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
public class MappedMenu extends MenuCircular<MenuEntry> implements DataCarrier<String>{
	
	private static final long serialVersionUID = 8618770778403876527L;
	
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
	/** The PAN options menu. */
	public static MappedMenu pan;
	/* STATIC MENU DEFINITIONS */
		
	/** The menu title displayed at top of the screen. */
	public final String title;
	/** The extra data you can pass to menu entry. */
	private String extra;
	
	/**
	 * Default, recommended MappedMenu constructor used
	 * to construct simple empty MappedMenu.
	 * @param title The menu title.
	 */
	public MappedMenu(String title)
	{
		super();
		this.title = title;
	}
	
	/**
	 * MappedMenu constructor used to construct default LeJOS menu entries.
	 * Implemented for backwards compatibility.
	 * @param title Menu Title
	 * @param items The item labels to be used
	 * @param icons The item icons to be used
	 */
	public MappedMenu(String title, String[] items, Icon[] icons)
	{
		this(title);
		MenuEntry entry;
		for(int index = 0; index < items.length; index++)
		{
			entry = new NoOpEntry(items[index], icons[index]);
			try
			{
				if(entry.get_icon() instanceof StockIcon) ((StockIcon)entry.get_icon()).cache();
			}
			catch(IOException exc)
			{
				exc.printStackTrace();
				continue;
			}
			this.add(entry);
		}
	}
	
	@Override
	public boolean add(MenuEntry entry)
	{
		entry.set_parent(this);
		return super.add(entry);
	}
	
	@Override
	protected Image load_icon(int index)
	{
		Icon icn = this.get(index).get_icon();
		try
		{
			return icn.call();
		}
		catch(IOException exc)
		{
			System.err.println("[ERROR] Failed retrieving icon " + icn.toString());
			return null;
		}
	}

	@Override
	protected String load_label(int index)
	{
		return this.get(index).get_name();
	}
	
	@Override
	public void load_carrier(String extra)
	{
		this.extra = extra;
	}
	
	@Override
	public String fetch_carrier()
	{
		return this.extra;
	}
	
	@Override
	public String toString()
	{
		return String.format("MappedMenu-%s[%d]", this.title, this.size());
	}

}
