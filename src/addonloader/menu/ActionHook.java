package addonloader.menu;

/**
 * ActionHook provides simple hook-like interface,
 * providing {@link HookRegistry} fairly simple access
 * to running hooks on code. Hook consists of type,
 * specified by enum {@link HookType}, {@link #parent}
 * reference, and the {@link #run()} method provided by Runnable.
 * @author Enginecrafter77
 */
public abstract class ActionHook implements Runnable {
	
	/** The hook type */
	public final HookType type;
	/** The registry where the hook is registered and contained */
	public final HookRegistry parent;
	
	/**
	 * Initializes {@link ActionHook} using parent registry and given type.
	 * @param reg The registry where the hook is supposed to be attached.
	 * @param type {@link HookType Type} of the hook.
	 */
	public ActionHook(HookRegistry reg, HookType type)
	{
		this.parent = reg;
		this.type = type;
	}
	
	/**
	 * Attaches the hook to parent registry.
	 */
	public void attach()
	{
		this.parent.addHook(this);
	}
	
	/**
	 * HookType is simple hook type specification. It simply describes what
	 * the tagged hook should do, and when it should be run.
	 * @author Enginecrafter77(Michal Hutira)
	 */
	public static enum HookType {OVERRIDE, PREPEND, APPEND};
}
