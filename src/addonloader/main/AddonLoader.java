package addonloader.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import addonloader.util.ObjectSettings;
import lejos.ExceptionHandler;
import lejos.Reference;

/**
 * The main class of AddonLoader. It does the main job of addon loading process.
 * On construction, it loads all the jars to it's addons field and then, the menu
 * instructs it to firstly init, then load and finally finish every addon instance.
 * @author Enginecrafter77
 * @see MenuAddon
 */
public class AddonLoader {
	
	/** Instance of running AddonLoader. */
	public static AddonLoader instance;
	/** AddonLoader configuration. */
	public ObjectSettings props;
	/** List of all addons to be runned. */
	private ArrayList<MenuAddon> addons = new ArrayList<MenuAddon>();
	
	/**
	 * Constructs AddonLoader instance with given configuration path. Can be used for debug.
	 * @param config The path to configuration file.
	 * @throws FileNotFoundException If addon folder or the config file wouldn't be found.
	 * @throws IOException If the properties cannot be loaded or when axes fall from sky.
	 */
	public AddonLoader(String config) throws FileNotFoundException, IOException
	{
		props = new ObjectSettings(config);
	}
	
	/**
	 * Scan addon folder for addons, and then process every jar to create MenuAddon list.
	 * @throws FileNotFoundException If given directory is not found, or it is not a directory.
	 */
	private void loadAddons() throws FileNotFoundException
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
	 * @throws IOException When the filesystem or kernel fails, not my fault.
	 * @throws ClassNotFoundException When there is no annotating file in given jar.
	 * @throws IllegalAccessException IDK
	 */
	private void loadJar(File file) throws IOException, ClassNotFoundException, IllegalAccessException
	{
		JarFile jar = new JarFile(file);
		Enumeration<JarEntry> en = jar.entries();
		
		URLClassLoader loader = URLClassLoader.newInstance(new URL[]{new URL("jar:file:" + file.getAbsolutePath() + "!/")});
		MenuAddon main = null;
		
		IterateLoop:
		while(en.hasMoreElements())
		{
			JarEntry entry = en.nextElement();
			if(entry.isDirectory() || !entry.getName().endsWith(".class"))
			{
				continue;
			}
			
			String clsname = entry.getName().substring(0, entry.getName().length() - 6).replace('/', '.');
			try
			{
				Class<?> cur_class = loader.loadClass(clsname);
			    if(MenuAddon.class.isAssignableFrom(cur_class))
			    {
			    	Annotation[] anr = cur_class.getAnnotations();
			    	for(Annotation an : anr)
				    {
				    	if(Addon.class.isAssignableFrom(an.getClass()))
				    	{
				    		Addon adn = (Addon)an;
				    		if(adn.apilevel() < Reference.API_LEVEL)
				    		{
				    			throw new InstantiationException(adn.name() + " uses incompatible API level " + adn.apilevel());
				    		}
				    		main = (MenuAddon)cur_class.newInstance();
				    		main.jarfile = file;
				    		main.name = adn.name();
				    		break IterateLoop;
				    	}
				    }
			    }
			}
			catch(NoClassDefFoundError e)
			{
				/* This try-catch block allows the addon to handle classes, that contain non-ev3-loadable code, like
				 * pc windowing for client-side implementation etc.
				 */
				System.err.println("[WARNING] " + file.getName() + " contains unloadable class " + clsname);
			}
			catch(InstantiationException e)
			{
				System.err.println("[ERR] " + e.getMessage());
				jar.close();
				return;
			}
			catch(Exception e)
			{
				// These should not happed, if yes, warn.
				e.printStackTrace();
			}
		}
		if(main == null)
		{
			throw new ClassNotFoundException("Cannot find annotating class in file " + file.getName());
		}
		addons.add(main);
		jar.close();
	}
	
	/**
	 * Initializes AddonLoader instance to load all the addons.
	 * @throws see {@link #AddonLoader(config)}
	 */
	public static void init() throws IOException
	{
		AddonLoader.instance = new AddonLoader("/home/root/lejos/config/addons.conf");
		AddonLoader.instance.loadAddons();
		MenuRegistry.mainRegistry();
		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());
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
	
	/**
	 * Processes given {@link LoadingStage}
	 * @param s The stage to be processed.
	 */
	public void processStage(LoadingStage s)
	{
		s.proccess(addons);
	}
}
