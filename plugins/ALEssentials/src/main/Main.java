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

@Addon(name = "ALEssential", apilevel = 2)
public class Main extends MenuAddon {
	
	public static MappedMenu menu;
	public static MappedMenu advboot;
	public static AddonMenu addonmenu;
	public static MenuEntry reboot;
	public static MenuEntry restart;
	public static MenuEntry exit;
	public static MenuEntry addons;
	public static MenuEntry delete;
	public static MenuEntry disable;
	public static MenuEntry debug;
	
	@Override
	public void init()
	{
		menu = new MappedMenu().setParent(MenuRegistry.system);
		advboot = new MappedMenu().setParent(MenuRegistry.boot_menu);
		addons = new AddonList();
		addonmenu = new AddonMenu();
		delete = new MenuEntry("Delete", Icons.ICDelete){
			@Override
			public void run()
			{
				Main.addonmenu.subject.getJarFile().delete();
			}
		};
		disable = new MenuEntry("Disable", Icons.ICNo){
			@Override
			public void run()
			{
				AddonLoader.instance.props.setProperty("addonloader.disabled", String.valueOf(MenuUtils.askConfirm("Disable Addon Loader?", false)));
				AddonLoader.instance.saveSettings();
			}
		};
		debug = new MenuEntry("Debug", Icons.ICDebug){
			@Override
			public void run()
			{
				AddonLoader.instance.props.setProperty("addonloader.debug", String.valueOf(MenuUtils.askConfirm("Enable debug?", true)));
				AddonLoader.instance.saveSettings();
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
		menu.addToParent("Addon Loader", Icons.IC_ADDON);
		menu.addMenuEntries(addons, disable, debug);
		advboot.addToParent("Advanced", Icons.ICEV3);
		advboot.addMenuEntries(reboot, restart, exit);
		addonmenu.addMenuEntry(delete);
	}

	@Override
	public void finish(){}
}
