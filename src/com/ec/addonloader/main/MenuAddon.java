package com.ec.addonloader.main;

import java.io.File;

/**
 * Used to define addon main class. Class must be annotated with <i>Addon</i>
 * @author Enginecrafter77
 */
public abstract class MenuAddon {
	
	/**
	 * The name of addon. It is set either by the user
	 * or automatically by the {@link AddonLoader} from
	 * the annotation {@link Addon}.
	 */
	protected String addonName;
	/**
	 * The jar file of the addon. Provides access to the jarfile by the addon,
	 * without the addon actually knowing where the jar resides. It is set
	 * by the {@link AddonLoader} on jar loading.
	 */
	protected File jarfile;
	
	/** The first stage of loading process.*/
	public abstract void init();
	/** The main stage of loading process.*/
	public abstract void load();
	/** The last stage of loading process.*/
	public abstract void finish();
	
	/**
	 * @return Addon name as stated by annotation.
	 * @see {@link #addonName}
	 */
	public String getName()
	{
		return addonName;
	}
	
	/**
	 * Gets annotation File where the annotation is loaded from.
	 * @return jarfile.
	 * @see {@link #jarfile}
	 */
	public File getJarFile()
	{
		return this.jarfile;
	}
}
