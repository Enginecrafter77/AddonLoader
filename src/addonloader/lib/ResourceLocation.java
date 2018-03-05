package addonloader.lib;

import java.io.IOException;
import java.io.InputStream;
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
	 * @param path The jar-relative path to resource
	 */
	public ResourceLocation(String path)
	{
		this.address = this.getClass().getClassLoader().getResource(path);
	}
	
	/**
	 * Opens input stream for reading the data from the resource.
	 * @return InputStream pointing at resource.
	 * @throws IOException
	 */
	public URL address()
	{
		return address;
	}
	
	/**
	 * Opens input stream for reading the data from the resource.
	 * Has the same effect as {@link #address() address}.openStream()
	 * @return InputStream pointing at resource.
	 * @throws IOException
	 */
	public InputStream openStream() throws IOException
	{
		return this.address.openStream();
	}
}
