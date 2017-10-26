package com.ec.addonloader.menu;

import com.ec.addonloader.main.MORegistry;

/**
 * The interface that stores code snippet run by
 * the manager {@link MORegistry}. It extends the Runnable interface,
 * so it can be called by the manager. It also includes two methods,
 * {@link #runAfter()} and {@link #runBefore()}.
 * @author Enginecrafter77
 */
public interface MethodOverride extends Runnable {
	/**
	 * Method used by the manager to tell the menu to run the method
	 * before the default code.
	 * @return Return false to disable execution of the default code. Otherwise, return true. 
	 */
	public abstract boolean runBefore();
	
	/**
	 * Method used by the manager to tell the menu to run the method
	 * after the default code. This method has NO effect on the default code, as it is run after it.
	 */
	public abstract void runAfter();
}
