package main;

import java.io.IOException;

import addonloader.lib.Icon;
import addonloader.main.Addon;
import addonloader.main.AddonLoader;
import addonloader.main.MenuAddon;
import addonloader.main.MenuRegistry;
import addonloader.menu.MappedMenu;
import addonloader.menu.MenuEntry;
import addonloader.util.MenuUtils;
import lejos.MainMenu;

@Addon(name = "ALEssential", apilevel = 53)
public class Main extends MenuAddon {

	public static MappedMenu advboot;
	public static MenuEntry reboot;
	public static MenuEntry restart;
	public static MenuEntry exit;
	public static MenuEntry disable;
	public static InteractServer upd;
	
	@Override
	public void init()
	{
		upd = new InteractServer();
		advboot = new MappedMenu().setParent(MenuRegistry.boot_menu);
		disable = new MenuEntry("Disable AL", Icon.NO){
			@Override
			public void run()
			{
				AddonLoader.instance.props.setProperty("enabled", String.valueOf(!MenuUtils.askConfirm("Disable Addon Loader?", false)));
				AddonLoader.instance.props.store("AddonLoader config");
			}
		};
		restart = new MenuEntry("Restart menu", Icon.REFRESH){
			@Override
			public void run()
			{
				MainMenu.self.restart();
			}
		};
		exit = new MenuEntry("Exit menu", Icon.NO){
			@Override
			public void run()
			{
				MainMenu.self.setMenuExit(true);
			}
		};
		reboot = new MenuEntry("Reboot", Icon.REBOOT){
			@Override
			public void run()
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
	public void load()
	{
		MenuRegistry.system.add(disable);
		advboot.addToParent("Advanced", Icon.EV3BRICK);
		advboot.add(reboot, restart, exit);
	}

	@Override
	public void finish()
	{
		upd.start();
	}
}
