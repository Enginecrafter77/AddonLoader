package addonloader.menu;

import addonloader.util.ui.Icon;
import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;

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
	public Icon get_icon()
	{
		return this.icon;
	}

	@Override
	public String get_name()
	{
		return this.title;
	}
	
	@Override
	public void set_parent(MappedMenu menu)
	{
		this.parent = menu;
	}
	
	protected void textflash(String text)
	{
		LCD.drawString(text, (18 - text.length()) / 2, 3);
		Button.waitForAnyPress();
	}
	
}
