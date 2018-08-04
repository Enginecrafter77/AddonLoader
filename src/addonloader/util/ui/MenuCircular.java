package addonloader.util.ui;

import java.util.LinkedList;
import lejos.hardware.BrickFinder;
import lejos.hardware.Button;
import lejos.hardware.lcd.GraphicsLCD;
import lejos.hardware.lcd.Image;

/**
 * Circular menu is a type of graphical menu, much similar to {@link lejos.GraphicMenu GraphicMenu}.
 * This menu provides dynamic entry mechanism, using {@link LinkedList} implementation.
 * The behaviour as well as look can be easily customized by the user by subclassing
 * the Circular Menu. Hovewer, the menu lacks animations, like in {@link lejos.GraphicMenu GraphicMenu},
 * but that's probably doesn't matter. If you would like to implement animations, good luck!
 * @param ENT_TYPE
 * @author Enginecrafter77(Michal Hutira)
 */
public abstract class MenuCircular<ENT_TYPE> extends LinkedList<ENT_TYPE>
{
	private static final long serialVersionUID = -2373662527546708172L;
	
	private final GraphicsLCD lcd;
	private int label_ycord, menu_ycord;
	
	public MenuCircular()
	{
		super();
		this.lcd = BrickFinder.getLocal().getGraphicsLCD();
		this.label_ycord = lcd.getFont().getHeight() * 3;
		this.menu_ycord = this.label_ycord + lcd.getFont().getHeight();
	}
	
	/**
	 * Called everytime to load an icon from the icon {@link #icons List}.
	 * @param index The index of the processed element.
	 * @return Image extracted from the icon, ready to be drawn.
	 */
	protected abstract Image load_icon(int index);
	/**
	 * Called everytime to load a label from the icon {@link #icons List}.
	 * @param index The index of the processed element.
	 * @return String extracted from the entry, ready to be drawn.
	 */
	protected abstract String load_label(int index);
	
	/**
	 * Opens the menu.
	 * @return Selected item index.
	 */
	public int open()
	{
		int selection = 0, button = 0;
		
		while(button != Button.ID_ENTER)
		{
			this.draw(selection);
			
			button = Button.waitForAnyPress();
			if(button == Button.ID_ESCAPE) return -1;
			selection = this.on_button_press(button, selection);
		}
		return selection;
	}
	
	/**
	 * (Re)Draws the menu contents on the screen.
	 * At first, it clears {@link #label_ycord line},
	 * and then draws the {@link #load_label(Object) label}
	 * on it. After that, maximum icon bounds are specified
	 * to max the circle, at maximum 2 for each side.
	 * The loop then continues to compute coordinates
	 * for each icon, calling {@link #compute_coords(char, int) this} function.
	 * @param selection
	 */
	protected void draw(int selection)
	{
		int cur_index;
		int max_bound = this.size() > 5 ? 2 : this.size() / 2;
		//Clear & draw the label line.
		lcd.bitBlt(null, lcd.getWidth(), lcd.getFont().getHeight(), 0, 0, 0, this.label_ycord, lcd.getWidth(), lcd.getFont().getHeight(), GraphicsLCD.ROP_CLEAR);
		lcd.drawString(this.load_label(selection), lcd.getWidth() / 2, this.label_ycord, GraphicsLCD.HCENTER);
		
		for(int prindex = -max_bound; prindex <= max_bound; prindex++)
		{
			cur_index = this.get_peripheral_index(selection, prindex);
			lcd.drawImage(this.load_icon(cur_index), this.compute_coords('x', prindex), this.compute_coords('y', prindex), GraphicsLCD.HCENTER);
		}
	}
	
	/**
	 * Computes coordinates for the given axis location.
	 * @param coord_index Can be 'x' of 'y', depending on the requested data.
	 * @param icon_num Index of the icon drawn, from -2 to 2.
	 * @return Coordinates for the axis given by coord_index.
	 */
	protected int compute_coords(char coord_index, int icon_num)
	{
		//Default X move factor = 36;
		//Default Y move factor = 8;
		if(coord_index == 'x') return lcd.getWidth() / 2 + (icon_num * 36); //X Coordination
		else return this.menu_ycord + Math.abs(icon_num * 8); //Y Coordination
	}
	
	/**
	 * Peripheral indexes are indexes used when circulating values.
	 * It's much like if you had hourglass, and covered it's half by paper.
	 * Then, when you rotate it either way, you must had came to an end,
	 * where the values wrap to the other end. And that's exactly
	 * what this function does. Wrap values to the other end.
	 * @param index The current index
	 * @param way Negative numbers indicate backwards, while positive forwards.
	 * @return New index moven exactly &quot;{@code way}&quot; places.
	 */
	protected int get_peripheral_index(int index, int way)
	{
		index += way;
		if(way > 0 && index >= this.size()) index -= this.size();
		else if(way < 0 && index < 0) index += this.size();
		return index;
	}

	/**
	 * Method that is responsible for moving
	 * the selection back and forth.
	 * @param button The button being pressed.
	 * @param selection The current selection.
	 * @return New selection moved the way the button did induce.
	 */
	protected int on_button_press(int button, int selection)
	{
		switch(button)
		{
		case Button.ID_RIGHT:
			selection = this.get_peripheral_index(selection, 1);
			break;
		case Button.ID_LEFT:
			selection = this.get_peripheral_index(selection, -1);
			break;
		}
		return selection;
	}
}