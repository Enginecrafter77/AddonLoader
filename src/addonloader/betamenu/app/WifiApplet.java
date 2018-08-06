package addonloader.betamenu.app;

import java.io.IOException;

import addonloader.betamenu.DockApplet;
import addonloader.betamenu.net.ConnectionManager;
import addonloader.util.StockIcon;
import lejos.hardware.lcd.GraphicsLCD;
import lejos.hardware.lcd.Image;

public class WifiApplet implements DockApplet {

	private final ConnectionManager cnmg;
	private Image wifi;
	
	public WifiApplet(ConnectionManager cnmg)
	{
		this.cnmg = cnmg;
		
		try
		{
			this.wifi = StockIcon.WIFI_SMALL.call();
		}
		catch(IOException exc)
		{
			System.err.println("[ERROR] Could not load applet wifi icon.");
			this.wifi = null;
		}
	}
	
	@Override
	public int draw_icon(GraphicsLCD screen, int x, int y)
	{
		if(cnmg.wifi_connected() && wifi != null)
		{
			screen.drawImage(wifi, x, y, GraphicsLCD.TOP | GraphicsLCD.LEFT);
			return wifi.getWidth();
		}
		return -2; //Free up 2 spaces gap allocated for this applet.
	}

	@Override
	public String get_name()
	{
		return null;
	}

	@Override
	public boolean is_valid()
	{
		return this.wifi != null;
	}

}
