package com.ec.entries;

import com.ec.addonloader.menu.MappedMenu;

import lejos.ev3.startup.MainMenu;

public class APDetailMenu extends MappedMenu{
	
	protected boolean cnt;
		
	public void display(String ap)
	{
		this.cnt = true;
		this.setExtra(ap);
		int selection = 0;
		while(selection >= 0 && cnt)
		{
			MainMenu.self.newScreen(ap);
			selection = this.getSelection(selection);
			if(selection >= 0)
			{
				System.out.println("Selected [" + ap + "]");
				this.onExternalAction(selection);
			}
		}
	}
	
}
