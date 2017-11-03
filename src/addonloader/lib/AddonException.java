package addonloader.lib;

import addonloader.main.LoadingStage;
import addonloader.main.MenuAddon;

public class AddonException extends Exception {
	private static final long serialVersionUID = 3685105936835969469L;

	public AddonException(MenuAddon source, Exception cause, LoadingStage s)
	{
		super("Error at " + s.name() + " in addon " + source.getName() + " (" + cause.getClass().getName()+ ")");
		this.setStackTrace(cause.getStackTrace());
	}
	
}
