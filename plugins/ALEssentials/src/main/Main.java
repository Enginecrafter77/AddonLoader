package main;

import java.io.IOException;

import lejos.ev3.startup.MainMenu;

import com.ec.addonloader.lib.MenuUtils;
import com.ec.addonloader.main.Addon;
import com.ec.addonloader.main.AddonLoader;
import com.ec.addonloader.main.MenuAddon;
import com.ec.addonloader.main.MenuRegistry;
import com.ec.addonloader.menu.MappedMenu;
import com.ec.addonloader.menu.MenuEntry;
import com.ec.addonloader.lib.Icons;

@Addon(name = "ALEssential", apilevel = 4)
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
		disable = new MenuEntry("Disable AL", Icons.ICNo){
			@Override
			public void run()
			{
				AddonLoader.instance.props.setProperty("enabled", String.valueOf(!MenuUtils.askConfirm("Disable Addon Loader?", false)));
				AddonLoader.instance.props.store("AddonLoader config");
			}
		};
		restart = new MenuEntry("Restart menu", Icons.IC_REFRESH){
			@Override
			public void run()
			{
				MainMenu.self.restart();
			}
		};
		exit = new MenuEntry("Exit menu", Icons.ICNo){
			@Override
			public void run()
			{
				MainMenu.self.setMenuExit(true);
			}
		};
		reboot = new MenuEntry("Reboot", Icons.IC_REBOOT){
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
		advboot.addToParent("Advanced", Icons.ICEV3);
		advboot.add(reboot, restart, exit);
	}

	@Override
	public void finish()
	{
		upd.start();
	}
}
