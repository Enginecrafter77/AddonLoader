package addonloader.main;

import addonloader.lib.Icon;
import addonloader.menu.MappedMenu;
import addonloader.menu.MenuEntry;
import addonloader.util.MenuUtils;
import lejos.MainMenu;

/**
 * This class contains all the orignal menu's remapped
 * and wrapped inside the {@link MappedMenu} system.
 * @author Enginecrafter77
 */
public final class DefaultMenus {
	
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
		menu = new MappedMenu(new String[]{"Run Default", "Programs", "Samples", "Tools", "Bluetooth", "Wifi", "PAN", "Sound", "System", "Version"}, new Icon[]{Icon.DEFAULT,Icon.PROGRAMS,Icon.SAMPLES,Icon.TOOLS,Icon.BLUETOOTH,Icon.WIFI,Icon.NETWORK,Icon.SOUND,Icon.EV3BRICK,Icon.LEJOS});
		system = new MappedMenu(new String[]{"Delete Programs", "Auto Run", "Change Name", "NTP", "Suspend Menu", "Close IO", "Unset Default"}, new Icon[]{Icon.FORMAT,Icon.AUTORUN,Icon.EDIT,Icon.JAVA,Icon.SLEEP,Icon.NO,Icon.JAVA});
		sound = new MappedMenu(new String[]{"", "", "", ""}, new Icon[]{Icon.SOUND,Icon.SOUND,Icon.SOUND,Icon.SOUND});
		bluetooth = new MappedMenu(new String[]{"Search/Pair", "Devices", "Visibility", "Change PIN", "Information"}, new Icon[]{Icon.SEARCH, Icon.EV3BRICK, Icon.EYE, Icon.KEY, Icon.INFO});
		file = new MappedMenu(new String[]{"View", "Delete"},new Icon[]{Icon.DIRECTORY, Icon.DELETE});
		executable = new MappedMenu(new String[]{"Run", "Debug", "Set as Default", "Delete"},new Icon[]{Icon.JAVA, Icon.DEBUG, Icon.JAVA, Icon.DELETE});
		bluetooth_dev = new MappedMenu(new String[]{"Remove"}, new Icon[]{Icon.DELETE});
		boot_menu = new MappedMenu(new String[]{"Shutdown", "Cancel"}, new Icon[]{Icon.POWER, Icon.NO});
		
		if(!Boolean.parseBoolean(MainMenu.addon_loader.props.getProperty("enabled", "true")))
		{
			system.add(new MenuEntry("Enable AL", Icon.TOOLS){
				@Override
				public void run()
				{
					if(MenuUtils.askConfirm("Enable Addon Loader?", false))
					{
						MainMenu.addon_loader.props.setProperty("enabled", "true");
						MainMenu.addon_loader.props.store("AddonLoader config");
						MainMenu.self.restart();
					}
				}
			});
		}
	}
}
