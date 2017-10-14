package lejos.ev3.startup;

import java.text.DecimalFormat;

import lejos.hardware.Battery;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.GraphicsLCD;
import lejos.hardware.lcd.Image;
import lejos.hardware.lcd.TextLCD;

/**
 * Draws a battery and the name of the EV3.
 */
public class BatteryIndicator
{
	// Battery state information
	private static final int STD_MIN = 6100;
	private static final int STD_OK = 6500;
	private static final int STD_MAX = 8000;
	private static final int RECHARGE_MIN = 7100;
	private static final int RECHARGE_OK = 7200;
	private static final int RECHARGE_MAX = 8200;
	
	private static final String ICIWifi = "\u0000\u0000\u0000\u0000\u00e0\u0007\u00f8\u001f\u001e\u0078\u0007\u00e0\u0003\u00c0\u00f0\u000f\u0078\u001e\u0018\u0018\u0000\u0000\u0080\u0001\u0080\u0001\u0000\u0000\u0000\u0000\u0000\u0000";
	private static final Image wifiImage = new Image(16,16,Utils.stringToBytes8(ICIWifi));
	
	private static final int ICON_X = 160;
	
	private int levelMin;
	private int levelOk;
	private int levelHigh;
	private boolean isOk;
	
	private float level;
	private byte[] title;
	private byte[] default_title;
	private String titleString;
	
	private boolean rechargeable = false;
	private boolean wifi = false;
	
	private TextLCD lcd = LocalEV3.get().getTextLCD();
	private GraphicsLCD g = LocalEV3.get().getGraphicsLCD();
	
	public BatteryIndicator()
	{
		if(rechargeable)
		{
			this.levelMin = RECHARGE_MIN;
			this.levelOk = RECHARGE_OK;
			this.levelHigh = RECHARGE_MAX;
		}
		else
		{
			this.levelMin = STD_MIN;
			this.levelOk = STD_OK;
			this.levelHigh = STD_MAX;
		}
	}
	
	public void setWifi(boolean wifi) {
		this.wifi = wifi;
	}
	
	public synchronized void setDefaultTitle(String title)
	{
		titleString = "";
		byte[] o = this.default_title;
		byte[] b = Utils.textToBytes(title);
		this.default_title = b;
		if (this.title == o)
		{
			this.title = b;
		}
	}
	
	public synchronized void setTitle(String title)
	{
		titleString = title;
   		this.title = (title == null) ? default_title : Utils.textToBytes(title);
	}

	private void checkLevels()
	{
		if(level > levelHigh)
		{
			level = levelHigh;
		}
		else if(level < levelMin)
		{
			level = levelMin;
		}
		this.isOk = level >= levelOk;
	}
	
	public void drawFormattedLevel(int x, int y)
	{
		DecimalFormat d = new DecimalFormat("#.0");
		lcd.drawString(d.format(this.level / 1000), x, y);
	}
	
	/**
	 * Display the battery icon.
	 */
	public synchronized void draw()
	{
		this.level = Battery.getVoltageMilliVolt();
		this.checkLevels();
		if(titleString == null){titleString = "";}
		lcd.drawString(titleString, 8 - (titleString.length()/2), 0);
		if(isOk || (System.currentTimeMillis() % (2*Reference.ICON_BATTERY_BLINK)) < Reference.ICON_BATTERY_BLINK)
		{
			this.drawFormattedLevel(0, 0);
		}
		else
		{
			lcd.drawString("   ", 0, 0);
		}
		
		if(wifi)
		{
			g.drawRegion(wifiImage, 0, 0, 16, 16, 0, ICON_X, 0, 0);
		}
		else
		{
			g.drawRegionRop(null, 0, 0, 16, 16, 0, ICON_X, 0, 0, GraphicsLCD.ROP_CLEAR);
		}
	}
}