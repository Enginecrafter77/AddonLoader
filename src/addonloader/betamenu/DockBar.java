package addonloader.betamenu;

import addonloader.util.DataCarrier;
import lejos.hardware.BrickFinder;
import lejos.hardware.lcd.GraphicsLCD;
import lejos.utility.Delay;

public class DockBar extends Thread implements DataCarrier<String> {
	
	private String title;
	private GraphicsLCD lcd;
	private DockApplet[] items;
	private int refresh_period;
	private int places_occupied;
	
	public DockBar(int capacity, int refresh_period)
	{
		super();
		this.lcd = BrickFinder.getDefault().getGraphicsLCD();
		this.items = new DockApplet[capacity];
		this.refresh_period = refresh_period;
		this.places_occupied = 0;
		this.title = "EV3";
		this.setDaemon(true);
	}
	
	/**
	 * Called when changing the title to reload screen.
	 */
	@Override
	public void load_carrier(String extra)
	{
		this.title = extra;
		this.draw();
	}

	@Override
	public String fetch_carrier()
	{
		return this.title;
	}
	
	public synchronized void add_applet(DockApplet app)
	{
		if(!app.is_valid()) return; //We don't want invalid applets.
		else this.items[this.places_occupied++] = app;
	}
	
	public synchronized void clear()
	{
		lcd.bitBlt(null, lcd.getWidth(), 16, 0, 0, 0, 0, lcd.getWidth(), 16, GraphicsLCD.ROP_COPY); //Clear the bar
	}
	
	public synchronized void draw()
	{
		int last_x = 1; //1 to create small gap at the beginning.
		for(int index = 0; index < this.places_occupied; index++)
		{
			//Subtract the resulting width of object + 2 for gaps.
			int occupied = this.items[index].draw_icon(lcd, last_x, 0) + 2;
			last_x += occupied;
		}
		lcd.drawString(title, last_x, 0, GraphicsLCD.TOP | GraphicsLCD.LEFT);
	}
	
	@Override
	public void run()
	{
		while(!this.isInterrupted())
		{
			this.clear();
			this.draw();
			Delay.msDelay(refresh_period);
		}
	}
	
}
