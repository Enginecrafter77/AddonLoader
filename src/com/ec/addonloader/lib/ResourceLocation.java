package com.ec.addonloader.lib;

import java.net.URL;

/**
 * Class used to address files stored inside this jar file itself.
 * This class provides you with a URL of the file.
 * @author Enginecrafter77
 */
public class ResourceLocation{
	
	private URL address;
	
	/**
	 * Constructs ResourceLocation with given path.
	 * @param path
	 */
	public ResourceLocation(String path)
	{
		this.address = this.getClass().getClassLoader().getResource(path);
	}
	
	public URL address()
	{
		return address;
	}
}
