package addonloader.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import addonloader.util.ObjectSettings;
import lejos.Reference;

/**
 * The main class of AddonLoader. It does the main job of addon loading process.
 * On construction, it loads all the jars to it's addons field and then, the menu
 * instructs it to firstly init, then load and finally finish every addon instance.
 * @author Enginecrafter77
 * @see MenuAddon
 */
public class AddonLoader {
	
	/** AddonLoader configuration. */
	public final ObjectSettings props;
	/** List of all addons to be runned. */
	public final ArrayList<MenuAddon> addons;
	
	/**
	 * Constructs AddonLoader instance with given configuration path. Can be used for debug.
	 * @param config The path to configuration file.
	 * @throws FileNotFoundException If addon folder or the config file wouldn't be found.
	 * @throws IOException If the properties cannot be loaded or when axes fall from sky.
	 */
	public AddonLoader(String config) throws FileNotFoundException, IOException
	{
		props = new ObjectSettings(config);
		addons = new ArrayList<MenuAddon>();
	}
	
	/**
	 * Scan addon folder for addons, and then process every jar to create MenuAddon list.
	 * @throws FileNotFoundException If given directory is not found, or it is not a directory.
	 */
	public void loadAddons() throws FileNotFoundException
	{
		if(!Boolean.parseBoolean(props.getProperty("enabled", "true")))
		{
			return;
		}
		
		File dir = new File(props.getProperty("addons_dir"));
		if(!dir.isDirectory())
		{
			throw new FileNotFoundException(dir.getAbsolutePath() + " is expected to be directory!");
		}
		File[] dirc = dir.listFiles();
		for(File f : dirc)
		{
			if(!f.getName().endsWith(".jar"))
			{
				continue;
			}
			
			try
			{
				loadJar(f);
			}
			catch(Exception e)
			{
				e.printStackTrace();
				continue;
			}
		}
	}
	
	/**
	 * Loads given file, supposing it is a JarFile.
	 * @param file The file to scan for {@link MenuAddon} class.
	 * @throws IOException When the filesystem or kernel fails, not our fault.
	 * @throws ClassNotFoundException When there is no main class in given plugin.
	 */
	private void loadJar(File file) throws IOException, ClassNotFoundException
	{
		JarFile jar = new JarFile(file);
		Enumeration<JarEntry> en = jar.entries();
		
		URLClassLoader loader = URLClassLoader.newInstance(new URL[]{new URL("jar:file:" + file.getAbsolutePath() + "!/")});
		MenuAddon main = null;
		
		while(en.hasMoreElements())
		{
			JarEntry entry = en.nextElement();
			if(entry.isDirectory() || !entry.getName().endsWith(".class")) continue;
			
			String clsname = entry.getName().substring(0, entry.getName().length() - 6).replace('/', '.'); //Replace FS-like path with java-like canonical.
			try
			{
				Class<?> cur_class = loader.loadClass(clsname); //Load the currently unknown class.
				if(MenuAddon.class.isAssignableFrom(cur_class)) //Check if it looks like Main class.
				{
					main = (MenuAddon)cur_class.newInstance();
					main.jarfile = file;
					if(main.apilevel < Reference.API_LEVEL) throw new InstantiationException(String.format("Addon %s uses incompatible API level (%d).", main.name, main.apilevel));
				}
			}
			catch(NoClassDefFoundError exc) // This block allows the addon to handle classes that contain non-ev3-loadable code, such as java AWT, SWING, and other external libraries.
			{
				System.err.println(String.format("[ERROR] Class %s in %s refers to unloadable class. More info below.", clsname, file.getName()));
				exc.printStackTrace();
			}
			catch(NoSuchMethodError exc) //If we encounter wrong constructor declaration (one that requires parameters) [Target line 104 newInstance()]
			{
				System.err.println(String.format("[ERROR] Plugin %s has wrong constructor declaration in class %s. (Should be parameterless)", jar.getName(), clsname));
			}
			catch(IllegalAccessException exc) //If we encounter wrong constructor declaration (one that declares itself protected or private) [Target line 104 newInstance()]
			{
				System.err.println(String.format("[ERROR] Plugin %s has wrong constructor declaration in class %s. (Should be public)", jar.getName(), clsname));
			}
			catch(InstantiationException exc) //Triggered while encountering fatal error, like incompatible API.
			{
				System.err.println("[FATAL] " + exc.getMessage());
				jar.close();
				return;
			}
		}
		
		jar.close();
		if(main == null) throw new ClassNotFoundException("No suitable main class found in " + file.getName());
		addons.add(main);
	}
	
	/**
	 * @return List of addons currently loaded.
	 */
	public MenuAddon[] getAddons()
	{
		MenuAddon[] res = new MenuAddon[this.addons.size()];
		res = this.addons.toArray(res);
		return res;
	}
}
