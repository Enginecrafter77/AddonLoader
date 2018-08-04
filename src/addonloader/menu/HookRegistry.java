package addonloader.menu;

import java.util.ArrayList;
import java.util.Iterator;

import addonloader.menu.ActionHook.HookType;
import addonloader.util.ExtraCarrier;

/**
 * HookRegistry provides simple hook-like manager
 * implementation. It stores several hook manager
 * instances, each providing a {@link #hook_list list},
 * storing the attached hooks of all {@link ActionHook.HookType types}.
 * Then, during runtime, the hooks are iterated over,
 * and executed by the {@link #runHooks(int) manager} using simple
 * integer indicating ordinal of the wanted type.
 * @author Enginecrafter77
 * @see ActionHook
 */
public enum HookRegistry implements ExtraCarrier<String>{
	
	/** Run when updating IP address list. */
	UPDATE_IPS,
	/** When starting up network interface. */
	START_NET,
	/** When user selects program to run */
	RUN_PROG,
	/** When user selects tool to run */
	RUN_TOOL,
	/** When user clicks wifi network in wifi menu. */
	WIFI_CONNECT,
	/** When user clicks on Version entry in main menu. */
	DISPLAY_VERISON;
	
	public final ArrayList<ActionHook> hook_list;
	private String arg;
	
	private HookRegistry()
	{
		hook_list = new ArrayList<ActionHook>();
	}
	
	/**
	 * Adds method to menu override registry.
	 * @param m The method to add.
	 */
	public void addHook(ActionHook m)
	{
		this.hook_list.add(m);
	}
	
	@Override
	public void loadCarrier(String s)
	{
		this.arg = s;
	}
	
	@Override
	public String fetchCarrier()
	{
		return this.arg;
	}
	
	/**
	 * Called internally to run methods Before the default code.
	 * @return Should be the default code run after this? Used to create complete override.
	 */
	public int runHooks(int index)
	{
		Iterator<ActionHook> i = this.hook_list.iterator();
		int num_run = 0;
		while(i.hasNext())
		{
			ActionHook hook = i.next();
			if(hook.type.equals(HookType.values()[index]))
			{
				num_run++;
				hook.run();
			}
		}
		return num_run;
	}
}
