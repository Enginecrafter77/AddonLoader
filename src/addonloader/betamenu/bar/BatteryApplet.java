package addonloader.betamenu.bar;

import addonloader.main.Reference;
import addonloader.util.MenuUtils;
import lejos.hardware.Battery;
import lejos.hardware.lcd.GraphicsLCD;

public class BatteryApplet implements DockApplet {
	
	public int get_level()
	{
		int level = Battery.getVoltageMilliVolt();
		if(level > Reference.BATTERY_MAX) level = Reference.BATTERY_MAX;
		if(level < Reference.BATTERY_MIN) level = Reference.BATTERY_MIN;
		return level;
	}
	
	@Override
	public int draw_icon(GraphicsLCD screen, int x, int y)
	{
		int width = MenuUtils.map(this.get_level(), Reference.BATTERY_MIN, Reference.BATTERY_MAX, 0, 12);
		screen.fillRect(x, y + 6, 2, 4); //Draw the battery button.
		screen.drawRect(x + 2, y + 3, 15, 9);
		//Start 2 pixels offseted to make small gap, and subtract it from width.
		screen.fillRect(x + 4, y + 5, width, 6);
		return 17;
	}

	@Override
	public String get_name()
	{
		return "battery";
	}

	@Override
	public boolean is_valid()
	{
		return Battery.getVoltageMilliVolt() > Reference.BATTERY_MIN;
	}

}
