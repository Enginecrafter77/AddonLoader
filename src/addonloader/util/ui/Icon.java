package addonloader.util.ui;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;

import addonloader.util.BinaryStorage;
import lejos.hardware.lcd.Image;

public interface Icon extends Callable<Image> {
	
	/**
	 * Loads the image specified by this Icon.
	 * @throws IOException If the FS shits things up.
	 */
	public Image call() throws IOException;
	
	/**
	 * Loads multiple images into memory buffer.
	 * @param icons The icons to be loaded.
	 * @return Memory-Buffered {@link Image images}
	 * @throws IOException If the FS shits things up.
	 */
	public static Image[] loadIcons(Icon[] icons) throws IOException
	{
		Image[] res = new Image[icons.length];
		for(int i = 0; i < icons.length; i++)
		{
			res[i] = icons[i].call();
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
	
	public static class ImageStorage extends BinaryStorage<Image>
	{
		public ImageStorage(File file)
		{
			super(file);
		}
		
		public ImageStorage(String path)
		{
			super(path);
		}

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
	}
	
}
