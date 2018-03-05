package addonloader.lib;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import lejos.Reference;
import lejos.hardware.lcd.Image;

/**
 * Provides access to icons through the {@link addonloader.lib.BinaryStorage BinaryStorage}.
 * The enum uses entry's ordinal number to access them inside the storage, so the entries should
 * not be reordered. If you'll reorder them, it will swap the two icons.
 */
public enum Icon {
	
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
	
	private static final BinaryStorage<Image> images = new BinaryStorage<Image>(Reference.MENU_DIRECTORY + "/Icons.stg") {
		@Override
		public Image readTag(DataInputStream is) throws IOException
		{
			int width = is.readShort();
			int height = is.readShort();
			byte[] buf = new byte[is.readShort()];
			is.read(buf);
			return new Image(width, height, buf);
		}
		
		@Override
		public void writeTag(Image content, DataOutputStream os) throws IOException
		{
			byte[] buffer = content.getData();
			os.writeShort(content.getWidth());
			os.writeShort(content.getHeight());
			os.writeShort(buffer.length);
			os.write(buffer);
		}
	};
	
	/**
	 * Loads the specified icon from storage into memory buffer.
	 * @return Memory-Buffered {@link Image image}
	 */
	public Image loadIcon()
	{
		try
		{
			return images.read(this.ordinal());
		}
		catch(Exception e)
		{
			return null;
		}
	}
	
	@Override
	public String toString()
	{
		Image img = this.loadIcon();
		return bytesToString8(img.getData());
	}
	
	/**
	 * Loads multiple images into memory buffer.
	 * @param icons The icons to be loaded.
	 * @return Memory-Buffered {@link Image images}
	 */
	public static Image[] toImages(Icon[] icons)
	{
		Image[] res = new Image[icons.length];
		for(int i = 0; i < icons.length; i++)
		{
			res[i] = icons[i].loadIcon();
		}
		return res;
	}
	
	/**
	 * @author leJOS Team
	 * @param str - The String as input
	 * @return byte[] containing the image
	 */
	@Deprecated
	public static byte[] stringToBytes8(String str)
	{
        int len = str.length();
        byte[] r = new byte[len];
        for (int i = 0; i < len; ++i) {
            r[i] = (byte)str.charAt(i);
        }
        return r;
    }
	
	/**
	 * @author Enginecrafter77
	 * @param array - The image data
	 * @return String representing the bytes
	 */
	@Deprecated
	public static String bytesToString8(byte[] array)
	{
		int len = array.length;
		char[] c = new char[len];
		for(int i = 0; i < len; ++i)
		{
			c[i] = (char)array[i];
		}
		return String.valueOf(c);
	}
}
