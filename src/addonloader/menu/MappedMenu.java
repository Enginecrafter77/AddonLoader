package addonloader.menu;

import java.util.Iterator;

import addonloader.util.ExtraCarrier;
import addonloader.util.ui.Icon;
import addonloader.util.ui.MenuCircular;
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
public class MappedMenu extends MenuCircular<MenuEntry> implements ExtraCarrier<String>{
	
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
	private Image[] icon_cache;
	
	/**
	 * Default, recommended MappedMenu constructor used
	 * to construct simple empty MappedMenu.
	 * @param title The menu title.
	 */
	public MappedMenu(String title)
	{
		super();
		this.title = title;
		this.icon_cache = new Image[0];
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
		for(int index = 0; index < items.length; index++) this.add(new NoOpEntry(items[index], icons[index]));
		this.reload_cache();
	}
	
	/**
	 * Reloads the cached icons buffer.
	 */
	public void reload_cache()
	{
		icon_cache = new Image[this.size()]; //Load the icon buffer with specific icons.
		Iterator<MenuEntry> entry = this.iterator();
		for(int index = 0; entry.hasNext(); index++) 
		{
			Icon icn = entry.next().getIcon();
			try
			{
				icon_cache[index] = icn.call();
			}
			catch(Exception exc)
			{
				System.err.println("[ERROR] Couldn't load icon " + icn.toString());
			}
		}
	}
	
	@Override
	public int open()
	{
		if(this.icon_cache.length != this.size()) this.reload_cache();
		return super.open();
	}
	
	@Override
	protected Image load_icon(int index)
	{
		return icon_cache[index];
	}

	@Override
	protected String load_label(int index)
	{
		return this.get(index).getName();
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
		return String.format("MappedMenu-%s[%d]", this.title, this.size());
	}

}
