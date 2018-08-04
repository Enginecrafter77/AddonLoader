package lejos;

import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.CommonLCD;
import lejos.hardware.lcd.GraphicsLCD;
import lejos.hardware.lcd.Image;
import lejos.hardware.lcd.TextLCD;
import lejos.utility.Delay;
import lejos.utility.TextMenu;

/**
 * Displays a list of items using icons (32x32).  The select() method allows the user to scroll the list using the right and left keys to scroll forward and backward 
 * through the list. The location of fClear area
 * the list , and an optional title can be specified to an extent.
 * 
 * The layout of a GraphicMenu is really basic.  It consists of a scrolling arc 
 * of 5 icons underneath the label.  The position of the label determines where 
 * the icons are drawn and can be defined with the method setYLocation().  Other 
 * than the label location, nothing can be changed about the location of the menu.
 * 
 * @deprecated by AddonLoader project, because more suitable {@link addonloader.util.ui.MenuCircular alternative} has been made.
 * 
 * @author Abram Early <br>
 * Modified by Lawrie griffiths for the EV3 <br>
 * Further modified by Enginecrafter77 for the AddonLoader project.
 */
@Deprecated
public class GraphicMenu extends TextMenu {
	private static final byte X_AREA = 0; // x of Menu Area
	private static final byte X_WIDTH = 35; // Distance between icon IDs on x axis
	private static final byte Y_WIDTH = 8; // " on y axis
	private static final byte X_OFFSET = 1;
	private static final byte Y_OFFSET = 4;
	
	private static final int INTERVAL = 16; // Time between animation frames in milliseconds (1000ms per 1s)
	private static final int TICKCOUNT = 10; // Number of animation frames used
	
	protected int label_line = 4;
	protected int title_line;
	private int menu_line;
	
	/*
	 * Icon Database
	 */
	protected Image[] icons;
	
	protected GraphicsLCD g = LocalEV3.get().getGraphicsLCD();
	protected TextLCD lcd = LocalEV3.get().getTextLCD();
	
	/**
	 * This constructor sets the location of the menu to the parameter line
	 */
	public GraphicMenu(String[] items, Image[] icons, int line)
	{
		this(items, icons, line, null, 1);
	}
	
	/**
	 * This constructor allows the specification of a title (of up to 16 characters) <br>
	 * The title is displayed in the row above the item list.
	 * @param items - string array containing the menu items. No items beyond the first null will be displayed.
	 * @param icons - string array containing the icon data in the form of a string instead of a byte[].
	 */	
	public GraphicMenu(String[] items, Image[] icons, int line, String title, int titleLine)
	{
		super(items,line,title);
		this.title_line = titleLine;
		this.setItems(items,icons);
		label_line = line;
		menu_line = (line+1)*16;
	}
	
	/**
	 * set the array of items to be displayed
	 * @param items
	 */
	public void setItems(String[] items, Image[] newicons)
	{
		super.setItems(items);
		icons = newicons;
	}
	
	@Override
	public int select(int selectedIndex, int timeout) 
	{ 
		if (selectedIndex >= _length) selectedIndex = _length -1; //might result in -1
		if (selectedIndex < 0) selectedIndex = 0;
		
		_quit = false;
		resetTimeout();

		if (_topIndex > selectedIndex) _topIndex = selectedIndex;
		if (_topIndex > _length - _height) _topIndex = _length - _height;			
		display(selectedIndex, 0,0);
		while(true)
		{
			int button;
			do
			{				
				if (_quit) return -2; // quit by another thread
				if (timeout > 0 && System.currentTimeMillis() - _startTime >= timeout)  return -3; // timeout
                button = Button.waitForAnyPress(BUTTON_POLL_INTERVAL);
			} while (button == 0);
			
			if(button == Button.ID_ENTER && selectedIndex >= 0 && selectedIndex < _length)
			{
				clearArea();
				return selectedIndex;
			}
			if(button == Button.ID_ESCAPE) return -1; //Escape
			int temp = selectedIndex;
			int dir = 0;
			if(button == Button.ID_RIGHT && (!(_length <= 2 && selectedIndex > 0) || get2IconMode())) // scroll forward
			{
				selectedIndex++;
				if(selectedIndex >= _length) // check for index out of bounds
				{
					selectedIndex = 0;
					_topIndex = 0;
				}
				else if(selectedIndex >= _topIndex + _height) _topIndex = selectedIndex - _height + 1;
				dir = -1;

			}
			if(button == Button.ID_LEFT && (!(_length <= 2 && selectedIndex < _length-1) || get2IconMode())) // scroll backward
			{
				selectedIndex--;
				if(selectedIndex < 0) // check for index out of bounds
				{			
					selectedIndex  = _length - 1;
					_topIndex = _length - _height;
				}
				else if(selectedIndex < _topIndex) _topIndex = selectedIndex;
				dir = 1;

			}
			if (_length > 1) animate(temp,selectedIndex,dir);
		}
	}
	
	/**
	 * Obtain a menu item selection
	 * Allow the user to make a selection from the specified menu item. If a
	 * power off timeout has been specified and no choice is made within this
	 * time, the os will shut down. This method is a replacement for static method
	 * used for this purpose.
	 * @author Enginecrafter77
	 * @param menu Menu to display.
	 * @param cur Initial item to select.
	 * @return Selected item or -1 for escape.
	 */
	public int getSelection(int cur)
	{
		int selection = -2;
		MainMenu.self.setCurrentMenu(this);
		while(selection == -2)
		{
			selection = this.select(cur, MainMenu.self.timeout*60000);
			if(MainMenu.self.isSuspended()) MainMenu.self.waitResume();
		}
		if(selection == -3) MainMenu.self.shutdown();
		
		return selection;
	}
	
	/**
	 * 
	 * @param selectedIndex
	 * @param finalIndex
	 * @param animateDirection -1=right 1=left
	 */
	protected void animate(int selectedIndex, int finalIndex,int animateDirection){
		int count = 1;
		while (count < TICKCOUNT)
		{
			display(selectedIndex,animateDirection,(int) ((10.0/TICKCOUNT)*count));
			Delay.msDelay(INTERVAL);
			count++;
		}
		display(finalIndex,0,0);
	}
	
	/**
	 * Displays the Graphic Menu at the index provided with the animation details provided.
	 * @param selectedIndex
	 * @param animateDirection #-1 to 1 (-1=Left 0=None 1=Right)
	 * @param tick #0-10 (0 = No change 10 = Full move)
	 */
	protected void display(int selectedIndex, int animateDirection, int tick)
	{
		if(_title != null) lcd.drawString(_title, 0, title_line);
		clearArea();
		//Prepare Index Locations
		int length = _length;
		int[] index = new int[5];
		for (int i = 0; i < 5; i++)
        {
			index[i] = (selectedIndex + (i-2)) % length;
            if (index[i] < 0) index[i] += length;
        }

		if (length > 4) drawIconAtTick(icons[((index[0]<0)?length+index[0]:index[0])],0,0+animateDirection,tick);

		if (length > 1 && !(length == 2 && index[1] == (length-1))) drawIconAtTick(icons[((index[1]<0)?length+index[1]:index[1])],1,1+animateDirection,tick);

		//Middle Icon
		drawIconAtTick(icons[index[2]],2,2+animateDirection,tick);

		if (length > 1 && !(length == 2 && index[3] == 0)) drawIconAtTick(icons[((index[3]>=length)?index[3]-length:index[3])],3,3+animateDirection,tick);

		if (length > 3) drawIconAtTick(icons[((index[4]>=length)?index[4]-length:index[4])],4,4+animateDirection,tick);
		// Draw Label
		lcd.clear(label_line);
		if (_items[index[2]].length()>16) lcd.drawString(_items[index[2]],0, label_line);
		else lcd.drawString(_items[index[2]], 8-(_items[index[2]].length()/2), label_line);
		
		lcd.refresh();
	}
	
	public void clearArea()
	{
		lcd.bitBlt(null, 178, 64, 0, 0, 0, menu_line, 178, 64, CommonLCD.ROP_CLEAR);
	}
	
	/**
	 * Helper method to draw a menu icon at a variable location (determined by tick) between two icon positions.
	 * @param sID -1 to 6
	 * @param eID -1 to 6
	 * @param tick #0-10
	 */
	protected void drawIconAtTick(Image iconImage,int sID, int eID,int tick){
		// Determine sID Coordinates
		int fx = X_AREA + X_OFFSET+sID*X_WIDTH;
		int fy = menu_line + Y_OFFSET+(Math.abs(sID-2)*Y_WIDTH);
		// Determine eID Coordinates
		int sx = X_AREA + X_OFFSET+eID*X_WIDTH;
		int sy = menu_line + Y_OFFSET+(Math.abs(eID-2)*Y_WIDTH);
		// Determine Icon Offset from sID
		int ix = (int) (((sx-fx)/10.0)*tick);
		int iy = (int) (((sy-fy)/10.0)*tick);
		// Paint Icon
		g.drawRegion(iconImage, 0, 0, 32, 32, 0,fx+ix,fy+iy,0);
	}
	
	/**
	 * @return Wrap with 2 Icons?
	 */
	protected boolean get2IconMode()
	{
		return false;
	}
}