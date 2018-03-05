package lejos;

import addonloader.lib.Icon;
import lejos.hardware.Battery;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.GraphicsLCD;
import lejos.hardware.lcd.Image;
import lejos.hardware.lcd.TextLCD;

/**
 * Draws a battery and the name of the EV3.
 */
public class InfoBar
{
	private static final int ICON_BATTERY_BLINK = 4 * Reference.ANIMATION_DELAY;
	private static final Image wifiImage = Icon.WIFI_SMALL.loadIcon();
	private static final int ICON_X = 160;
	
	private String title;
	private boolean isOk;
	private int level;
	
	/** The wifi icon update mode.<br>1: update to true<br>0: no update<br>-1: update to false */
	private int wifi;
	
	private TextLCD lcd = LocalEV3.get().getTextLCD();
	private GraphicsLCD g = LocalEV3.get().getGraphicsLCD();
	
	public InfoBar(String default_title)
	{
		this.title = default_title;
		this.wifi = 0;
	}
	
	public void setWifi(boolean wifi)
	{
		this.wifi = wifi ? 1 : -1;
	}
	
	public void setTitle(String title)
	{
		if(this.title != null) this.title = title;
		else this.title = "EV3";
	}

	private void updateChargeLevel()
	{
		this.level = Battery.getVoltageMilliVolt();
		if(level > Reference.BATTERY_MAX) level = Reference.BATTERY_MAX;
		else if(level < Reference.BATTERY_MIN) level = Reference.BATTERY_MIN;
		this.isOk = level >= Reference.BATTERY_OK;
	}
	
	public void drawFormattedLevel(int x, int y)
	{
		lcd.drawString(String.valueOf(Utils.map(this.level, Reference.BATTERY_MIN, Reference.BATTERY_MAX, 5, 100)) + '%', x, y);
	}
	
	/**
	 * Display the battery icon and EV3 hostname.
	 */
	public synchronized void draw()
	{
		this.updateChargeLevel();
		lcd.drawString(title, 8 - (title.length() / 2), 0);
		if(isOk || (System.currentTimeMillis() % (2 * ICON_BATTERY_BLINK)) < ICON_BATTERY_BLINK)
		{
			this.drawFormattedLevel(0, 0);
		}
		else
		{
			lcd.drawString("   ", 0, 0);
		}
		
		if(wifi == 1)
		{
			g.drawRegion(wifiImage, 0, 0, 16, 16, 0, ICON_X, 0, 0);
			wifi = 0;
		}
		else if(wifi == -1)
		{
			g.drawRegionRop(null, 0, 0, 16, 16, 0, ICON_X, 0, 0, GraphicsLCD.ROP_CLEAR);
			wifi = 0;
		}
	}
}