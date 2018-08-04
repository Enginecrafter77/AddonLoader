package addonloader.util;


import lejos.Utils;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.GraphicsLCD;
import lejos.hardware.lcd.LCD;
import lejos.utility.Delay;

/**
 * Simple loading screen with progressbar,
 * indicating the current state of loading.
 * Displays also something like 'annoybar'
 * cycling dashes and spaces at the top of the screen,
 * used to indicate system activity => if the bar
 * is frozen, the system is so.
 * @author Enginecrafter77
 */
public class LoadingScreen{
	
	/** Progress of the progressbar displayed in percent */
	private int progress;
	private GraphicsLCD lcd;
	private AnnoyBar annoybar;
	
	/**
	 * Constructs the LoadingScreen
	 */
	public LoadingScreen()
	{
		this.progress = 0;
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
		annoybar = new AnnoyBar('=');
		annoybar.start();
	}
	
	/**
	 * Stops the loading bar and clears the screen.
	 */
	public void stop()
	{
		annoybar.interrupt();
		lcd.clear();
	}
	
	/**
	 * Updates the bar.
	 */
	private void redrawBar()
	{
		//We first remap the progress in new range <0;161>
		//and then draw the rectangle using widht from our mapped number.
		lcd.fillRect(9, 102, Utils.map(progress, 0, 100, 0, 161), 16);
	}
	
	/**
	 * Resets the bar to zero.
	 */
	public void reset()
	{
		this.progress = 0;
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
	private class AnnoyBar extends Thread
	{	
		private static final int dots_max = 12;
		private final char fill;
		private int dots;
		private boolean reverse;
		
		public AnnoyBar(char fill)
		{
			this.fill = fill;
			this.dots = -1;
			this.reverse = false;
		}
		
		@Override
		public void run()
		{
			LCD.drawChar('|', 1, 2); //Draws the 'boundaries'
			LCD.drawChar('|', 15, 2);
			while(!this.isInterrupted()) //Cycle while not interrupted
			{
				if(dots == dots_max) reverse = !reverse; //If the dots are at the end, start to erase
				dots = MenuUtils.cycleValue(dots, 0, dots_max, 1); //Cycle the position
				LCD.drawChar(reverse ? ' ' : fill, 2 + dots, 2); //If we are drawing in reverse, erase with ' ', else add fill
				Delay.msDelay(1000);
			}
		}
	}
	
}
