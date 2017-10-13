package main;

import com.ec.addonloader.main.MenuAddon;
import com.ec.addonloader.menu.MappedMenu;

public class AddonMenu extends MappedMenu{
	
	public MenuAddon subject;
	
	public void display(MenuAddon subject)
	{
		this.subject = subject;
		MappedMenu.newScreen(subject.getName());
		int selection = this.getSelection(0);
		this.onExternalAction(selection);
	}
}
