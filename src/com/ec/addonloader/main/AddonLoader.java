package com.ec.addonloader.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.ec.addonloader.main.LoadingStager.LoadingStage;
import lejos.ev3.startup.ExceptionHandler;

/**
 * The main class of AddonLoader. It does the main job of addon loading process.
 * On construction, it loads all the jars to it's addons field and then, the menu
 * instructs it to firstly init, then load and finally finish every addon instance.
 * @author Enginecrafter77
 * @see MenuAddon
 */
public class AddonLoader {
	
	/** Where is the configuration supposed to be. */
	private String config;
	public String addonFolder;
	private boolean doDebug;
	public static boolean isDisabled;
	/** Instance of running AddonLoader */
	public static AddonLoader instance;
	private ArrayList<MenuAddon> addons = new ArrayList<MenuAddon>();
	public Properties props;
	
	/**
	 * Constructs AddonLoader instance with given configuration path. Can be used for debug.
	 * @param config The path to configuration file.
	 * @throws FileNotFoundException If addon folder or the config file wouldn't be found.
	 * @throws IOException If the properties cannot be loaded or when axes fall from sky.
	 */
	public AddonLoader(String config) throws FileNotFoundException, IOException
	{
		props = new Properties();
		props.load(new FileReader(config));
		isDisabled = Boolean.parseBoolean(props.getProperty("addonloader.disabled"));
		this.doDebug = Boolean.parseBoolean(props.getProperty("addonloader.debug"));
		this.addonFolder = props.getProperty("addonloader.directory");
		this.config = config;
	}
	
	private void loadAddons() throws FileNotFoundException
	{
		if(isDisabled)
		{
			return;
		}
		
		File dir = new File(addonFolder);
		if(!dir.isDirectory())
		{
			throw new FileNotFoundException(addonFolder + " is expected to be directory!");
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
	
	private void loadJar(File f) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException
	{
		debug("Searching in " + f.getName());
		JarFile jar = new JarFile(f);
		Enumeration<JarEntry> en = jar.entries();
		
		URLClassLoader cl = URLClassLoader.newInstance(new URL[]{new URL("jar:file:" + f.getAbsolutePath() + "!/")});
		MenuAddon main = null;
		
		IterateLoop:
		while(en.hasMoreElements())
		{
			JarEntry et = en.nextElement();
			if(et.isDirectory() || !et.getName().endsWith(".class"))
			{
				continue;
			}
			
		    Class<?> cls = cl.loadClass(et.getName().substring(0, et.getName().length() - 6).replace('/', '.'));
		    debug("Examining class " + cls.getName());
		    if(MenuAddon.class.isAssignableFrom(cls))
		    {
		    	debug("-Class is MenuAddon");
		    	Annotation[] anr = cls.getAnnotations();
		    	debug("-Annotations found: " + anr.length);
			    for(Annotation an : anr)
			    {
			    	debug("--annotation name: " + an.toString());
			    	if(Addon.class.isAssignableFrom(an.getClass()))
			    	{
			    		debug("--Annotation is addon");
			    		main = (MenuAddon)cls.newInstance();
			    		main.addonName = ((Addon)an).name();
			    		main.jarfile = f;
			    		break IterateLoop;
			    	}
			    }
		    }
		}
		if(main == null)
		{
			throw new ClassNotFoundException("Cannot find MenuAddon class in file " + f.getName());
		}
		addons.add(main);
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
		MORegistry.init();
		Thread.setDefaultUncaughtExceptionHandler(instance.doDebug ? new ExceptionHandler.DebugHandler() : new ExceptionHandler());
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
	 * Calls {@code MenuAddon.init()} on all addons currently loaded.
	 */
	public void initStage()
	{
		LoadingStager.loadStage(addons, LoadingStage.INIT);
	}
	
	/**
	 * Calls {@code MenuAddon.load()} on all addons currently loaded.
	 */
	public void loadStage()
	{
		LoadingStager.loadStage(addons, LoadingStage.LOAD);
	}
	
	/**
	 * Calls {@code MenuAddon.finish()} on all addons currently loaded.
	 */
	public void finishStage()
	{
		LoadingStager.loadStage(addons, LoadingStage.FINISH);
	}
	
	/**
	 * Called to save AddonLoader settings.
	 */
	public void saveSettings()
	{
		try
		{
			props.store(new FileOutputStream(config), "Addon Loader configuration file");
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public void debug(String txt)
	{
		if(doDebug)
		{
			System.out.println(txt);
		}
	}
	
}
