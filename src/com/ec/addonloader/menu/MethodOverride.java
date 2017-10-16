package com.ec.addonloader.menu;

import com.ec.addonloader.main.MORegistry;

/**
 * The interface that stores code snippet run by
 * the manager {@link MORegistry}. It extends the Runnable interface,
 * so it can be called by the manager. It also includes two methods,
 * {@link #disableDefaultCode()} and {@link #runBefore()}.
 * @author Enginecrafter77
 */
public interface MethodOverride extends Runnable {
	/**
	 * Method used by the manager to tell the menu to
	 * not run the default code. It can be used to create
	 * complete override of the method. If the method returns
	 * true, the code is disabled and won't run. If false,
	 * the code is run as normally. It will normally have effect
	 * only if used with runBefore returned true.
	 * @return true to disable default code.
	 * @see {@link #runBefore()}
	 */
	public abstract boolean disableDefaultCode();
	
	/**
	 * Method used by the manager to tell the menu to run the method
	 * before or after the default code. If the method is run before,
	 * this method is supposed to return true. If false, the method override
	 * will be run after the default code.
	 * @return true if the code is run before the default.
	 */
	public abstract boolean runBefore();
}
