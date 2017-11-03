package addonloader.lib;

import lejos.hardware.lcd.Image;

public class MenuIcon extends Image{
	
	private static final long serialVersionUID = 8991149934540804800L;
	
	public MenuIcon(byte[] data)
	{
		super(32, 32, data);
	}
	
	public MenuIcon(String data)
	{
		super(32, 32, Icons.stringToBytes8(data));
	}
	
	/**
	 * Returns 8-bit string representation of this image.
	 */
	@Override
	public String toString()
	{
		return Icons.bytesToString8(this.getData());
	}

}
