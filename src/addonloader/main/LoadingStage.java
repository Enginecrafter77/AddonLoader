package addonloader.main;

import java.util.ArrayList;

import addonloader.lib.AddonException;

/**
 * Enum representing single loading stage that will be run
 * by the addonloader using the function {@link #proccess(ArrayList)}
 * @author Enginecrafter77
 */
public enum LoadingStage {
	
	INIT, //The initializing stage
	LOAD, //The loading stage
	FINISH; //After that all completed, clean up.
	
	/**
	 * Processes stage on {@link MenuAddon} array.
	 * @param s The addon list to be processed.
	 */
	public void proccess(ArrayList<MenuAddon> addons)
	{
		for(int i = 0; i < addons.size(); i++)
		{
			MenuAddon m = addons.get(i);
			System.out.println(this.name() + ' ' + m.name);
			try
			{
				switch(this)
				{
				case INIT:
					m.init();
					break;
				case LOAD:
					m.load();
					break;
				case FINISH:
					m.finish();
					break;
				}
			}
			catch(Exception e)
			{
				new AddonException(m, e, this).printStackTrace();
			}
		}
	}
	
}
