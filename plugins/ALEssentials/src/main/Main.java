package main;

import java.io.IOException;

import addonloader.main.MenuAddon;
import addonloader.menu.MappedMenu;
import addonloader.menu.SimpleMenuEntry;
import addonloader.menu.SubmenuEntry;
import addonloader.util.MenuUtils;
import addonloader.util.StockIcon;
import lejos.MainMenu;

public class Main extends MenuAddon {

	public static SubmenuEntry advboot;
	public static SimpleMenuEntry reboot;
	public static SimpleMenuEntry restart;
	public static SimpleMenuEntry exit;
	public static SimpleMenuEntry disable;
	public static InteractServer upd;
	
	public Main()
	{
		super("AL-Essential");
	}
	
	@Override
	public void init()
	{
		upd = new InteractServer();
		advboot = new SubmenuEntry("Advanced", StockIcon.EV3BRICK);
		disable = new SimpleMenuEntry("Disable AL", StockIcon.NO){
			@Override
			public void run()
			{
				MainMenu.addon_loader.props.setProperty("enabled", String.valueOf(!MenuUtils.askConfirm("Disable Addon Loader?")));
				MainMenu.addon_loader.props.store("AddonLoader config");
			}
		};
		restart = new SimpleMenuEntry("Restart menu", StockIcon.REFRESH){
			@Override
			public void run() {MainMenu.self.restart();}
		};
		exit = new SimpleMenuEntry("Exit menu", StockIcon.NO){
			@Override
			public void run() {MainMenu.self.setMenuExit(true);}
		};
		reboot = new SimpleMenuEntry("Reboot", StockIcon.REBOOT){
			@Override
			public void run()
			{
				MainMenu.self.setMenuExit(true);
				MainMenu.self.suspend();
				try {Runtime.getRuntime().exec("init 6");}
				catch(IOException e){}
				MainMenu.lcd.drawString("Rebooting", 2, 6);
				MainMenu.lcd.refresh();
			}
		};
	}

	@Override
	public void load()
	{
		upd.start();
		advboot.add(reboot);
		advboot.add(restart);
		advboot.add(exit);
		MappedMenu.system.add(disable);
		MappedMenu.boot_menu.add(advboot);
	}

	@Override
	public void cleanup()
	{
		upd.interrupt();
	}
}
