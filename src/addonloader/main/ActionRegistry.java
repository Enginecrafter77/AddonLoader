package addonloader.main;

import java.util.ArrayList;
import java.util.Iterator;

import addonloader.lib.ExtraCarrier;
import addonloader.menu.MethodOverride;

/**
 * The class that provides menu overrides.
 * Every time user adds {@link MethodOverride}, it is stored here in ArrayList.
 * It is an dynamic way to modify menu behaviour, but it is
 * kinda hungry for RAM. Another way was to use more CPU time
 * reflection-searching classes, but I thought this was the better way.
 * Anyway, reflection itself takes RAM, too.
 * @author Enginecrafter77
 * @see MethodOverride
 */
public enum ActionRegistry implements ExtraCarrier<String>{
	
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
	
	public final ArrayList<MethodOverride> methods;
	private String arg;
	
	private ActionRegistry()
	{
		methods = new ArrayList<MethodOverride>();
	}
	
	/**
	 * Adds method to menu override registry.
	 * @param m The method to add.
	 */
	public void addMethod(MethodOverride m)
	{
		this.methods.add(m);
	}
	
	@Override
	public void setExtra(String s)
	{
		this.arg = s;
	}
	
	@Override
	public String getExtra()
	{
		return this.arg;
	}
	
	/**
	 * Called internally to run methods Before the default code.
	 * @return Should be the default code run after this? Used to create complete override.
	 */
	public boolean runMethodsB()
	{
		Iterator<MethodOverride> i = this.methods.iterator();
		boolean runDef = true;
		while(i.hasNext())
		{
			runDef = i.next().runBefore();
		}
		return runDef;
	}
	
	/**
	 * Called internally to run methods After the default code.
	 */
	public void runMethodsA()
	{
		Iterator<MethodOverride> i = this.methods.iterator();
		while(i.hasNext())
		{
			i.next().runAfter();
		}
	}
}
