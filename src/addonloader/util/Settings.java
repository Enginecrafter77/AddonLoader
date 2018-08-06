package addonloader.util;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

/**
 * Simple wrapper around {@link Properites},
 * allowing to easily access objects.
 * @author Enginecrafter77
 */
public class Settings extends Properties {
	
	private static final long serialVersionUID = 7280540884135786522L;
	
	private File path;
	private static final String boolean_key = "[bool]%s.key", int_key = "[int]%s.key", float_key = "[float]%s.key";
	
	public Settings(File path)
	{
		this.path = path;
	}
	
	public Settings(String path)
	{
		this(new File(path));
	}
	
	public void load()
	{
		try
		{
			this.load(new FileReader(path));
		}
		catch(IOException exc)
		{
			exc.printStackTrace();
		}
	}
	
	public void store()
	{
		try
		{
			this.store(new FileWriter(path), this.path.getName());
		}
		catch(IOException exc)
		{
			exc.printStackTrace();
		}
	}
	
	public int get_integer(String key)
	{
		return Integer.parseInt(this.getProperty(String.format(int_key, key), "0"));
	}
	
	public boolean get_boolean(String key)
	{
		return Boolean.parseBoolean(this.getProperty(String.format(boolean_key, key), "false"));
	}
	
	public float get_float(String key)
	{
		return Float.parseFloat(this.getProperty(String.format(float_key, key), "0F"));
	}
	
	public void set_integer(String key, int value)
	{
		this.setProperty(String.format(int_key, key), String.valueOf(value));
	}
	
	public void set_boolean(String key, boolean value)
	{
		this.setProperty(String.format(boolean_key, key), String.valueOf(value));
	}
	
	public void set_float(String key, float value)
	{
		this.setProperty(String.format(float_key, key), String.valueOf(value));
	}
	
}
