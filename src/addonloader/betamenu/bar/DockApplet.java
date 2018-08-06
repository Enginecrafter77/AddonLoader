package addonloader.betamenu.bar;

import lejos.hardware.lcd.GraphicsLCD;

public interface DockApplet {
	
	/**
	 * Called to draw DockApplet's icon.
	 * Icon is anchored at top left corner.
	 * By convention, the icons must not be
	 * taller than 16 pixels.
	 * @param screen Graphical Screen to draw stuff onto.
	 * @param x The left most pixel
	 * @param y The top most pixel
	 * @return The width of the drawed object.
	 */
	public abstract int draw_icon(GraphicsLCD screen, int x, int y);
	
	/**
	 * @return Applet's name.
	 */
	public abstract String get_name();
	
	/**
	 * Called when adding applet to the bar.
	 * If the applet is invalid, it will
	 * get rejected.
	 * @return True if applet is valid.
	 */
	public abstract boolean is_valid();
	
}
