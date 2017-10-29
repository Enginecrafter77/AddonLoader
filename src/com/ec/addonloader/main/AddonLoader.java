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
import lejos.ev3.startup.Reference;

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
			
			String clsname = et.getName().substring(0, et.getName().length() - 6).replace('/', '.');
			try
			{
				Class<?> cls = cl.loadClass(clsname);
			    if(MenuAddon.class.isAssignableFrom(cls))
			    {
			    	Annotation[] anr = cls.getAnnotations();
			    	for(Annotation an : anr)
				    {
				    	if(Addon.class.isAssignableFrom(an.getClass()))
				    	{
				    		Addon adn = (Addon)an;
				    		if(adn.apilevel() < Reference.API_LEVEL)
				    		{
				    			throw new InstantiationException(adn.name() + " uses too old API level " + adn.apilevel());
				    		}
				    		main = (MenuAddon)cls.newInstance();
				    		main.addonName = adn.name();
				    		main.jarfile = f;
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
				System.err.println("[WARNING] " + f.getName() + " contains unloadable class " + clsname);
			}
		}
		if(main == null)
		{
			throw new ClassNotFoundException("Cannot find MenuAddon class in file " + f.getName());
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
}
