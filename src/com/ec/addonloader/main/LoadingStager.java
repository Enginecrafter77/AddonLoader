package com.ec.addonloader.main;

import java.util.ArrayList;

import com.ec.addonloader.lib.AddonException;

public class LoadingStager {
	
	public static void loadStage(ArrayList<MenuAddon> addons, LoadingStage s)
	{
		for(int i = 0; i < addons.size(); i++)
		{
			MenuAddon m = addons.get(i);
			System.out.println(s.getName() + ' ' + m.getName());
			try
			{
				switch(s)
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
				new AddonException(m, e, s).printStackTrace();
			}
		}
	}
	
	public static enum LoadingStage
	{
		INIT(0),
		LOAD(1),
		FINISH(2);
		
		private final int index;
		private LoadingStage(int index)
		{
			this.index = index;
		}
		
		public int getIndex()
		{
			return this.index;
		}
		
		public String getName()
		{
			switch(index)
			{
			case 0:
				return "init";
			case 1:
				return "load";
			case 2:
				return "finish";
			default:
				return "global";
			}
		}
	}
	
}
