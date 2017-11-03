package addonloader.menu;


import addonloader.util.MenuUtils;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.GraphicsLCD;
import lejos.hardware.lcd.LCD;
import lejos.utility.Delay;

public class LoadingAnimation{
	
	private int progress;
	private GraphicsLCD lcd;
	private int lastPixel;
	private Immitator t;
	
	public LoadingAnimation()
	{
		this.progress = 0;
		this.lastPixel = 9;
		this.lcd = LocalEV3.ev3.getGraphicsLCD();
	}
	
	/**
	 * Sets the progress of bar.
	 * @param progress - Percentual progress.
	 */
	public void setProgress(int progress)
	{
		this.progress = progress;
		this.redrawBar();
	}
	
	/**
	 * Sets the progress of bar.
	 * @param progress - Percentual progress.
	 */
	public void addProgress(int progress)
	{
		this.progress += progress;
		this.redrawBar();
	}
	
	/**
	 * Sets the title of the bar.
	 * @param text
	 */
	public void setText(String text)
	{
		LCD.clear(4);
		LCD.drawString(text, 1, 4);
	}
	
	/**
	 * Sets the both text and progress at once.
	 * @param text
	 * @param progress
	 */
	public void setState(String text, int progress)
	{
		this.setText(text);
		this.setProgress(progress);
	}
	
	/**
	 * Starts the loading bar.
	 */
	public void start(String title)
	{
		lcd.clear();
		lcd.setStrokeStyle(GraphicsLCD.SOLID);
		lcd.drawRect(7, 100, 163, 19);
		lcd.drawString(title, 85, 10, GraphicsLCD.TOP | GraphicsLCD.HCENTER);
		t = new Immitator();
		t.start();
	}
	
	/**
	 * Stops the loading bar and clears the screen.
	 */
	public void stop()
	{
		t.interrupt();
		lcd.clear();
	}
	
	/**
	 * Updates the bar.
	 */
	private void redrawBar()
	{
		int pg = (int)(progress / 0.625) + 9; //9 is the starting value, so it shifts the whole row.
		lcd.fillRect(lastPixel, 102, pg - lastPixel, 16);
		lastPixel = pg;
	}
	
	/**
	 * Resets the bar to zero.
	 */
	public void reset()
	{
		lcd.setColor(GraphicsLCD.WHITE);
		lcd.fillRect(9, 102, 161, 16);
		lcd.setColor(GraphicsLCD.BLACK);
	}
	
	/**
	 * Class that does only one thing. Traverses characters from
	 * one end of the line to the other and so on, indicating background activity.
	 * May seem useless, but it is really useful to detect system freeze.
	 * @author Enginecrafter77
	 */
	private class Immitator extends Thread
	{	
		private static final int dots_max = 12;
		private static final char filler = '=';
		private int dots;
		private boolean reverse;
		
		public Immitator()
		{
			this.dots = -1;
			this.reverse = false;
		}
		
		@Override
		public void run()
		{			
			LCD.drawChar('|', 1, 2);
			LCD.drawChar('|', 15, 2);
			while(!this.isInterrupted())
			{
				if(dots == dots_max)
				{
					reverse = !reverse;
				}
				dots = MenuUtils.cycleValue(dots, 0, dots_max, 1);
				LCD.drawChar(reverse ? ' ' : filler, 2 + dots, 2);
				Delay.msDelay(1000);
			}
		}
	}
	
}
