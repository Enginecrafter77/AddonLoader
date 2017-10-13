package main;

import java.io.IOException;

import com.ec.addonloader.menu.MappedMenu;
import com.ec.addonloader.menu.MenuEntry;
import com.ec.addonloader.util.Icons;

import lejos.ev3.startup.MainMenu;

public class AdvBootEntry extends MenuEntry{

	public AdvBootEntry()
	{
		super("Advanced", Icons.ICEV3);
	}
	
	public static void init()
	{
		Main.restart = new MenuEntry("Restart menu", Icons.IC_REFRESH){
			@Override
			public void onEntrySelected()
			{
				MainMenu.self.restart();
			}
		};
		Main.exit = new MenuEntry("Exit menu", Icons.ICNo){
			@Override
			public void onEntrySelected()
			{
				MainMenu.self.setMenuExit(true);
			}
		};
		Main.reboot = new MenuEntry("Reboot", Icons.IC_REBOOT){
			@Override
			public void onEntrySelected()
			{
				MainMenu.self.setMenuExit(true);
				MainMenu.self.suspend();
				try
				{
					Runtime.getRuntime().exec("init 6");
				}
				catch(IOException e){}
				MainMenu.lcd.drawString("Rebooting", 2, 6);
				MainMenu.lcd.refresh();
			}
		};
	}
	
	@Override
	public void onEntrySelected()
	{
		MappedMenu menu = this.provideMenu();
		MainMenu.self.newScreen(this.getName());
		int selection = menu.getSelection(0);
		if(selection >= 0)
		{
			menu.onExternalAction(selection);
		}
	}
	
	@Override
	public MappedMenu provideMenu()
	{
		return Main.advboot;
	}

}
