package addonloader.main;

import java.io.File;

/**
 * Used to define addon main class. Class must be annotated with <i>Addon</i>
 * @author Enginecrafter77
 */
public abstract class MenuAddon {
	
	/**
	 * The jar file of the addon. Provides access to the jarfile by the addon,
	 * without the addon actually knowing where the jar resides. It is set
	 * by the {@link AddonLoader} on jar loading.
	 */
	protected File jarfile;
	
	/**
	 * The name of addon. It is set by the {@link AddonLoader} from
	 * the annotation {@link Addon} before the {@link #init()}
	 */
	public final String name;
	
	public final int apilevel;
	
	protected MenuAddon(final int apilevel, final String name)
	{
		this.apilevel = apilevel;
		this.name = name;
	}
	
	/** The first stage of loading process.*/
	protected abstract void init();
	/** The main stage of loading process.*/
	protected abstract void load();
	/** The last stage of loading process.*/
	protected abstract void cleanup();
	
	/**
	 * @return The location of the addon jar file.
	 */
	public File getJarFile()
	{
		return this.jarfile;
	}
	
	/**
	 * @return The name of the addon as described by {@link Addon} annotation.
	 */
	public String getName()
	{
		return this.name;
	}
}
