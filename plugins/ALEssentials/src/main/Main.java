package main;

import com.ec.addonloader.lib.MenuUtils;
import com.ec.addonloader.main.Addon;
import com.ec.addonloader.main.AddonLoader;
import com.ec.addonloader.main.MenuAddon;
import com.ec.addonloader.main.MenuRegistry;
import com.ec.addonloader.menu.MappedMenu;
import com.ec.addonloader.menu.MenuEntry;
import com.ec.addonloader.util.Icons;

@Addon(name = "ALEssential")
public class Main extends MenuAddon {
	
	public static MappedMenu menu;
	public static MappedMenu advboot;
	public static AddonMenu addonmenu;
	public static MenuEntry reboot;
	public static MenuEntry advbootentry;
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
		advbootentry = new AdvBootEntry();
		delete = new MenuEntry("Delete", Icons.ICDelete){
			@Override
			public void onEntrySelected()
			{
				Main.addonmenu.subject.getJarFile().delete();
			}
		};
		disable = new MenuEntry("Disable", Icons.ICNo){
			@Override
			public void onEntrySelected()
			{
				AddonLoader.instance.props.setProperty("addonloader.disabled", String.valueOf(MenuUtils.getYesNo("Disable Addon Loader?", false)));
				AddonLoader.instance.saveSettings();
			}
		};
		debug = new MenuEntry("Debug", Icons.ICDebug){
			@Override
			public void onEntrySelected()
			{
				AddonLoader.instance.props.setProperty("addonloader.debug", String.valueOf(MenuUtils.getYesNo("Enable debug?", true)));
				AddonLoader.instance.saveSettings();
			}
		};
		AdvBootEntry.init();
	}

	@Override
	public void load()
	{
		menu.addToParent("Addon Loader", Icons.IC_ADDON);
		menu.addMenuEntries(addons, disable, debug);
		advboot.addMenuEntries(reboot, restart, exit);
		addonmenu.addMenuEntry(delete);
		MenuRegistry.boot_menu.addMenuEntry(advbootentry);
	}

	@Override
	public void finish(){}
}
