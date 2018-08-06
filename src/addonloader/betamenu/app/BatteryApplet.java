package addonloader.betamenu.app;

import addonloader.betamenu.DockApplet;
import addonloader.main.Reference;
import addonloader.util.MenuUtils;
import lejos.hardware.Battery;
import lejos.hardware.lcd.GraphicsLCD;

public class BatteryApplet implements DockApplet {

	@Override
	public int draw_icon(GraphicsLCD screen, int x, int y)
	{
		int width = MenuUtils.map(Battery.getVoltageMilliVolt(), Reference.BATTERY_MIN, Reference.BATTERY_MAX, 0, 18);
		screen.fillRect(x, y + 4, 2, 8); //Draw the battery button.
		screen.drawRect(x + 2, y + 1, 22, 13); //Draw the frame | 13 should be 14, but IDK why it behaves like that.
		//Start 2 pixels offseted to make small gap, and subtract it from width.
		screen.fillRect(x + 4, y + 3, width, 10);
		return 24;
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
