package com.ec.addonloader.lib;


import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.GraphicsLCD;
import lejos.hardware.lcd.LCD;
import lejos.utility.Delay;

public class LoadingAnimation{
	
	private int progress;
	private GraphicsLCD lcd;
	private int lastProgress;
	private TheThing t;
	
	public LoadingAnimation()
	{
		this.progress = 0;
		this.lastProgress = 9;
		this.lcd = LocalEV3.ev3.getGraphicsLCD();
	}
	
	/**
	 * Sets the progress of bar.
	 * @param progress - Percentual progress.
	 */
	public void setProgress(int progress)
	{
		this.progress = (int)(progress / 0.625);
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
		t = new TheThing();
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
		int pg = progress + 9;
		lcd.fillRect(lastProgress, 102, pg - lastProgress, 16);
		lastProgress = pg;
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
	
	private class TheThing extends Thread
	{	
		private final int dots_max;
		private final char filler;
		private int dots;
		private boolean reverse;
		
		public TheThing()
		{
			this.dots_max = 12;
			this.dots = -1;
			this.reverse = false;
			this.filler = '-';
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
