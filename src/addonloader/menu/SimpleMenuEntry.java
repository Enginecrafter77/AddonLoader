package addonloader.menu;

import addonloader.util.ui.Icon;

public abstract class SimpleMenuEntry implements MenuEntry {
	
	private final String title;
	private final Icon icon;
	protected MappedMenu parent;
	
	public SimpleMenuEntry(String name, Icon icon)
	{
		this.title = name;
		this.icon = icon;
	}

	@Override
	public Icon getIcon()
	{
		return this.icon;
	}

	@Override
	public String getName()
	{
		return this.title;
	}
	
	@Override
	public void setParent(MappedMenu menu)
	{
		this.parent = menu;
	}
	
}
