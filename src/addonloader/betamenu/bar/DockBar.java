package addonloader.betamenu.bar;

import addonloader.util.DataCarrier;
import lejos.hardware.BrickFinder;
import lejos.hardware.lcd.GraphicsLCD;
import lejos.utility.Delay;

public class DockBar extends Thread implements DataCarrier<String> {
	
	private String title;
	private GraphicsLCD lcd;
	private DockApplet[] items;
	
	/** The delay between display updates */
	protected final int refresh_period;
	
	/** Places occupied by applets */
	protected int places_occupied;
	
	/** Cancels the next automatic display update */
	protected boolean skip_update;
	
	public DockBar(int capacity, int refresh_period)
	{
		super();
		this.lcd = BrickFinder.getDefault().getGraphicsLCD();
		this.items = new DockApplet[capacity];
		this.refresh_period = refresh_period;
		this.places_occupied = 0;
		this.title = "EV3";
		this.skip_update = false;
		this.setDaemon(true);
	}
	
	/**
	 * Called when changing the title to reload screen.
	 */
	@Override
	public void load_carrier(String extra)
	{
		this.title = extra;
		this.skip_update = true; //Skip automatic update
		this.draw(); //We can assume we are drawing on clear surface.
	}

	/**
	 * Returns the currently displayed title.
	 */
	@Override
	public String fetch_carrier()
	{
		return this.title;
	}
	
	/**
	 * Adds applet to the bar. If the bar cannot hold any more applets,
	 * or the applet is corrupted, the method returns false. 
	 * @param app The applet to add to the bar.
	 * @return True if the operation was sucessful.
	 */
	public synchronized boolean add_applet(DockApplet app)
	{
		if(!app.is_valid() || this.places_occupied >= this.items.length) return false; //We don't want invalid applets, and IndexOutOfBounds-es neither.
		
		this.items[this.places_occupied++] = app;
		return true;
	}
	
	/**
	 * Clears the bar surface, to prepare clean place for the next update.
	 */
	public synchronized void clear()
	{
		lcd.bitBlt(null, lcd.getWidth(), 16, 0, 0, 0, 0, lcd.getWidth(), 16, GraphicsLCD.ROP_COPY);
	}
	
	/**
	 * Draws the applets, together with the title on the bar surface.
	 */
	public synchronized void draw()
	{
		int last_x = 1; //1 to create small gap at the beginning.
		for(int index = 0; index < this.places_occupied; index++)
		{
			//Subtract the resulting width of object + 2 for gaps.
			int occupied = this.items[index].draw_icon(lcd, last_x, 0) + 2;
			last_x += occupied;
		}
		
		// half = lcd.getWidth() / 2 = 89
		lcd.drawString(title, 89, 0, GraphicsLCD.TOP | GraphicsLCD.HCENTER);
	}
	
	@Override
	public void run()
	{
		while(!this.isInterrupted())
		{
			if(this.skip_update) this.skip_update = false;
			else
			{
				this.clear();
				this.draw();
			} 
			Delay.msDelay(refresh_period);
		}
	}
	
}
