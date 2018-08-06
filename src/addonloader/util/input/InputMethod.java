package addonloader.util.input;

import java.util.ArrayList;
import java.util.concurrent.Callable;

import lejos.hardware.BrickFinder;
import lejos.hardware.lcd.GraphicsLCD;

/**
 * Class specifing the method used to enter text on the screen.
 * @author Enginecrafter77
 */
public abstract class InputMethod implements Callable<String> {
	
	/**
	 * A sorted list containing input method, sorted by priority. 0 is fallback, while the last is default.
	 */
	private static final ArrayList<InputMethod> options = new ArrayList<InputMethod>(1);
	
	protected final GraphicsLCD screen;
	private boolean invalid;
	
	public InputMethod()
	{
		this.screen = BrickFinder.getDefault().getGraphicsLCD();
		this.invalid = false;
	}
	
	public abstract boolean ready();
	
	@Override
	public abstract String call();
	
	protected String invalidate()
	{
		this.invalid = true;
		return null;
	}
	
	public static void set_fallback(InputMethod fallback)
	{
		if(options.size() > 0) options.set(0, fallback);
		else options.add(fallback);
	}
	
	public static int add(InputMethod option)
	{
		int next_index = options.size();
		options.add(option);
		return next_index;
	}
	
	public static String enter()
	{
		String result;
		InputMethod current;
		for(int index = options.size() - 1; index > -1; index--)
		{
			current = options.get(index);
			if(current.ready())
			{
				result = current.call();
				if(!current.invalid) return result;
			}
		}
		
		return null;
	}
}

