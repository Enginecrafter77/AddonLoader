package addonloader.main;

import java.io.File;

/**
 * Used to define addon main class. Class must be annotated with <i>Addon</i>
 * @author Enginecrafter77
 */
public abstract class MenuAddon {
	
	/**
	 * The name of addon. It is set by the {@link AddonLoader} from
	 * the annotation {@link Addon} before the {@link #init()}
	 */
	protected String name;
	/**
	 * The jar file of the addon. Provides access to the jarfile by the addon,
	 * without the addon actually knowing where the jar resides. It is set
	 * by the {@link AddonLoader} on jar loading.
	 */
	protected File jarfile;
	
	/** The first stage of loading process.*/
	protected abstract void init();
	/** The main stage of loading process.*/
	protected abstract void load();
	/** The last stage of loading process.*/
	protected abstract void finish();
	
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
