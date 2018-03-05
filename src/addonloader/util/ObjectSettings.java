package addonloader.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 * Simple wrapper around {@link Properites},
 * allowing to easily access objects.
 * @author Enginecrafter77
 */
public class ObjectSettings extends Properties{
	
	private static final long serialVersionUID = -8939834947658913650L;
	
	private final File path;
	
	public ObjectSettings(File path) throws FileNotFoundException, IOException
	{
		this.path = path;
		this.load(new FileReader(path));
	}
	
	public ObjectSettings(String path) throws FileNotFoundException, IOException
	{
		this(new File(path));
	}
	
	public boolean getBoolean(String key, boolean def)
	{
		return Boolean.parseBoolean(this.getProperty(key, String.valueOf(def)));
	}
	
	public int getInteger(String key, int def)
	{
		return Integer.parseInt(this.getProperty(key, String.valueOf(def)));
	}
	
	public float getFloat(String key, float def)
	{
		return Float.parseFloat(this.getProperty(key, String.valueOf(def)));
	}
	
	public void set(String key, Object val)
	{
		this.setProperty(key, val.toString());
	}
	
	public void store(String comment)
	{
		try
		{
			this.store(new FileOutputStream(path), comment);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
}
