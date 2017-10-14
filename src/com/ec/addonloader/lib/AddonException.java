package com.ec.addonloader.lib;

import com.ec.addonloader.main.MenuAddon;
import com.ec.addonloader.main.LoadingStager.LoadingStage;

public class AddonException extends Exception {
	private static final long serialVersionUID = 3685105936835969469L;

	public AddonException(MenuAddon source, Exception cause, LoadingStage s)
	{
		super("Error at " + s.getName() + " in addon " + source.getName() + " (" + cause.getClass().getName()+ ")");
		this.setStackTrace(cause.getStackTrace());
	}
	
}
