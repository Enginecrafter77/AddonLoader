package lejos.ev3.startup;

import static com.ec.addonloader.util.Icons.IMG_HOURGLASS;
import static com.ec.addonloader.util.Icons.stringToBytes8;

import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.Font;
import lejos.hardware.lcd.GraphicsLCD;
import lejos.hardware.lcd.Image;
import lejos.hardware.lcd.LCD;

public class WaitScreen
{
	public static WaitScreen instance;
	public static final Image hourglass = new Image(64, 64, stringToBytes8(IMG_HOURGLASS));
	final GraphicsLCD g = LocalEV3.get().getGraphicsLCD();
	final int scrWidth;
	final int scrHeight;
	final int chHeight;
	final int basePos;
	final int statusPos;
	
	public WaitScreen()
	{
		g.setFont(Font.getDefaultFont());
		scrWidth = g.getWidth();
		scrHeight = g.getHeight();
		chHeight = g.getFont().getHeight();
		basePos = scrHeight/3;
		statusPos = basePos*2;
	}
	
	public static void init()
	{
		WaitScreen.instance = new WaitScreen();
	}

	public void begin(String title)
	{
		System.out.println("Start wait");
		MainMenu.self.suspend();
		g.clear();
		g.drawRegion(hourglass, 0, 0, hourglass.getWidth(), hourglass.getHeight(), GraphicsLCD.TRANS_NONE, 50, 50, GraphicsLCD.HCENTER | GraphicsLCD.VCENTER);
		int x = LCD.SCREEN_WIDTH/2;
		String[] strings = title.split("\n");
		int y = basePos - (strings.length/2)*chHeight;
		for(String s : strings)
		{
			g.drawString(s, x, y, 0);
			y += chHeight;
		}
		g.refresh();				
	}
	
	public void end()
	{
		g.clear();
		MainMenu.self.resume();
	}
	
	public void status(String msg)
	{
		g.bitBlt(null, scrWidth, chHeight, 0, 0, 0, statusPos, scrWidth, chHeight, GraphicsLCD.ROP_CLEAR);
		g.drawString(msg, 0, statusPos, 0);
		g.refresh();			
	}
	
	public static void drawLaunchScreen()
	{
		GraphicsLCD g = LocalEV3.get().getGraphicsLCD();
		g.setFont(Font.getDefaultFont());
		g.drawRegion(hourglass, 0, 0, hourglass.getWidth(), hourglass.getHeight(), GraphicsLCD.TRANS_NONE, 50, 65, GraphicsLCD.HCENTER | GraphicsLCD.VCENTER);
		int x = LCD.SCREEN_WIDTH/2;
		g.drawString("Wait", x, 40, 0);
		g.drawString("a", x, 55, 0);
		g.drawString("second...", x, 70, 0);
		g.refresh(); // TODO: Needed?
	}
}
