package addonloader.menu;

import addonloader.util.ui.Icon;

public class NoOpEntry extends SimpleMenuEntry {

	public NoOpEntry(String name, Icon icon)
	{
		super(name, icon);
	}

	@Override
	public void run()
	{
		System.err.println(String.format("[WARNING] No-OP Entry %s's code was run as Runnable.", this.getName()));
	}

}
