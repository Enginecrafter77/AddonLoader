package addonloader.util;

import java.io.IOException;

import addonloader.main.Reference;
import addonloader.util.ui.Icon;
import lejos.hardware.lcd.Image;

/**
 * Provides access to icons through the {@link addonloader.util.BinaryStorage BinaryStorage}.
 * The enum uses entry's ordinal number to access them inside the storage, so the entries should
 * not be reordered. If you'll reorder them, it will swap the two icons.
 */
public enum StockIcon implements Icon {
	
	JAVA,
	REBOOT,
	PLUS,
	NO,
	YES,
	REFRESH,
	LIST,
	ADDON,
	QUESTION_MARK,
	TERMINAL,
	GEAR,
	AP_DISABLED,
	EDIT,
	AUTORUN,
	SLEEP,
	FORMAT,
	DELETE,
	KEY,
	SEARCH,
	EYE,
	POWER,
	LEJOS,
	DEBUG,
	EV3BRICK,
	INFO,
	USB,
	AP_PLUS,
	ACCESSPOINT,
	NETWORK,
	WIFI,
	BLUETOOTH,
	TOOLS,
	SAMPLES,
	PROGRAMS,
	DIRECTORY,
	DEFAULT,
	SOUND,
	WIFI_SMALL,
	KEY_OK,
	KEY_DEL,
	KEY_SHIFT,
	KEY_SHIFTON,
	KEY_SYMBOLS;
	
	private static final BinaryStorage<Image> stock_image_database = new ImageStorage(Reference.MENU_DIRECTORY + "/icon-pack.db");
	
	/** Buffer used to cache icon currently attempted to load. */
	private Image cache;
	
	/**
	 * Loads the specified icon from storage into memory buffer.
	 * @return Memory-Buffered {@link Image image}
	 */
	public Image call() throws IOException
	{
		this.cache();
		return cache;
	}
	
	public void cache() throws IOException
	{
		if(cache == null) cache = stock_image_database.read(this.ordinal());
	}
	
	@Override
	public String toString()
	{
		return this.name();
	}
}
