package com.ec.addonloader.main;

import java.util.ArrayList;
import java.util.Iterator;

import com.ec.addonloader.lib.ExtraCarrier;
import com.ec.addonloader.menu.MethodOverride;

/**
 * The class that provides menu overrides.
 * Menu overrides are basically code snippets that
 * are run when something is being launched, or just after it.
 * It is an dynamic way to hardly modify menu behaviour, but it's
 * a bit RAM costly. Is could be done differently, but with more CPU time
 * wasted on reflection-searching classes in a tradeoff, so this was the better choice.
 * The registry stores interfaces, {@link MethodOverride}, that represent the
 * actual code run after or before method.
 * @author Enginecrafter77
 * @see MethodOverride
 */
public class MORegistry extends ArrayList<MethodOverride> implements ExtraCarrier<String>{
	
	private static final long serialVersionUID = 7261544297467984335L;
	public static MORegistry[] methods;
	private String arg;
	
	/**
	 * Called internally to initialise method registers.
	 * Calling this twice will screw up already set up overrides.
	 */
	public static void init()
	{
		methods = new MORegistry[Type.values().length];
		for(int i = 0; i < methods.length; i++)
		{
			methods[i] = new MORegistry();
		}
	}
	
	/**
	 * Gets the desired registry based on provided {@link Type}
	 * @param type Override class, where it belongs.
	 * @return The matching MORegistry.
	 */
	public static MORegistry getRegistry(MORegistry.Type type)
	{
		return MORegistry.methods[type.index];
	}
	
	/**
	 * Adds method to menu override registry.
	 * @param m The method to add.
	 */
	public void addMethod(MethodOverride m)
	{
		this.add(m);
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
		Iterator<MethodOverride> i = this.iterator();
		boolean runDef = true;
		while(i.hasNext())
		{
			MethodOverride m = i.next();
			if(m.runBefore())
			{
				m.run();
				if(runDef && m.disableDefaultCode())
				{
					runDef = false;
				}
			}
		}
		return runDef;
	}
	
	/**
	 * Called internally to run methods After the default code.
	 */
	public void runMethodsA()
	{
		Iterator<MethodOverride> i = this.iterator();
		while(i.hasNext())
		{
			MethodOverride m = i.next();
			if(!m.runBefore())
			{
				m.run();
			}
		}
	}
	
	/**
	 * Runs all methods.
	 */
	public void runAllMethods()
	{
		Iterator<MethodOverride> i = this.iterator();
		while(i.hasNext())
		{
			i.next().run();
		}
	}
	
	/**
	 * Enumeration of types of {@link MORegistry}.
	 * MORegistries are added dynamically only
	 * by adding an entry to this enumeration.
	 * @author Enginecrafter77
	 */
	public static enum Type
	{
		UPDATE_IPS(0),
		START_NET(1),
		RUN_PROG(2),
		RUN_TOOL(3),
		WIFI_CONNECT(4),
		DISPLAY_VERISON(5);
		
		private final int index;
		private Type(int i)
		{
			this.index = i;
		}
	}
}
