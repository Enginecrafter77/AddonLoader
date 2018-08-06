package addonloader.menu;

import addonloader.util.ui.Icon;

/**
 * Interface representing entry in MappedMenu. MenuEntry extends Runnable,
 * so when the user selects this menu, some code is run.
 * 
 * MenuEntry consists of {@link #getName() name} and {@link #getIcon() icon}
 * @author Enginecrafter77
 */
public interface MenuEntry extends Runnable{
	
	/** The name of the menu entry */
	public Icon getIcon();
	/** The icon of the menu entry */
	public String getName();
	/** Sets the owning parent */
	public void setParent(MappedMenu menu);
}
