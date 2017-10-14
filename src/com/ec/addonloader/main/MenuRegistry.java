package com.ec.addonloader.main;

import static com.ec.addonloader.lib.Icons.*;

import com.ec.addonloader.lib.MenuUtils;
import com.ec.addonloader.menu.MappedMenu;
import com.ec.addonloader.menu.MenuEntry;
import lejos.ev3.startup.MainMenu;

/**
 * Class used to load all addons.
 * @author Enginecrafter77
 */
public final class MenuRegistry {
	
	/** The main menu */
	public static MappedMenu menu;
	/** The system menu */
	public static MappedMenu system;
	/** The sound menu */
	public static MappedMenu sound;
	/** The bluetooth menu */
	public static MappedMenu bluetooth;
	/** The generic file menu */
	public static MappedMenu file;
	/** The executable file menu */
	public static MappedMenu executable;
	/** Menu for bluetooth device */
	public static MappedMenu bluetooth_dev;
	/** The menu displayed when pressing escape in main menu.*/
	public static MappedMenu boot_menu;
	
	/**
	 * Called internally to initialize all the menus. Needed even when addonloader is not running.
	 */
	protected static void mainRegistry()
	{
		menu = new MappedMenu(
				new String[]{"Run Default", "Programs", "Samples", "Tools", "Bluetooth", "Wifi", "PAN", "Sound", "System", "Version"},
				new String[]{ICDefault,ICFiles,ICSamples,ICTools,ICBlue,ICWifi,ICPAN,ICSound,ICEV3,ICLeJOS});
		system = new MappedMenu(
				new String[]{"Delete Programs", "Auto Run", "Change Name", "NTP", "Suspend Menu", "Close IO", "Unset Default"},
				new String[]{ICFormat,ICAutoRun,IC_EDIT,ICDefault,ICSleep,ICNo,ICDefault});
		sound = new MappedMenu(new String[]{"", "", "", ""}, new String[]{ICSound,ICSound, ICSound, ICSound});
		bluetooth = new MappedMenu(
				new String[]{"Search/Pair", "Devices", "Visibility", "Change PIN", "Information"},
				new String[]{ICSearch,ICEV3,ICVisibility,ICPIN,ICInfo});
		file = new MappedMenu(new String[]{"View", "Delete"},new String[]{ICFiles, ICDelete});
		executable = new MappedMenu(new String[]{"Run", "Debug", "Set as Default", "Delete"},new String[]{ICProgram, ICDebug, ICDefault, ICDelete});
		bluetooth_dev = new MappedMenu(new String[]{"Remove"},new String[]{ICDelete});
		boot_menu = new MappedMenu(new String[]{"Shutdown", "Cancel"}, new String[]{IC_BOOT, ICNo});
		
		if(AddonLoader.isDisabled)
		{
			system.addMenuEntry(new MenuEntry("Enable AL", ICTools){
				@Override
				public void run()
				{
					if(MenuUtils.askConfirm("Enable Addon Loader?", false))
					{
						AddonLoader.instance.props.setProperty("addonloader.disabled", "false");
						AddonLoader.instance.saveSettings();
						MainMenu.self.restart();
					}
				}
			});
		}
	}
	
	public static void addMenuEntry(MappedMenu m, MenuEntry e)
	{
		m.addMenuEntry(e);
	}
	
	public static void addMenuEntries(MappedMenu m, MenuEntry... e)
	{
		m.addMenuEntries(e);
	}
	
	/**
	 * @return List of currently loaded addons.
	 * @see AddonLoader#getAddons()
	 */
	public static MenuAddon[] getAddons()
	{
		return MainMenu.al.getAddons();
	}
}
