package addonloader.menu;

import java.util.concurrent.Callable;

/**
 * Class specifing the method used to enter text on the screen.
 * @author Enginecrafter77
 */
public abstract class InputMethod implements Callable<String>,Runnable {
	
	public static InputMethod current;
	
	protected final StringBuilder buffer;
	protected boolean invalid;
	
	public InputMethod()
	{
		this.buffer = new StringBuilder();
		this.invalid = false;
	}
	
	public void setText(String text)
	{
		buffer.setLength(0);
		buffer.append(text);
	}
	
	public void reset()
	{
		this.invalid = false;
		this.buffer.setLength(0);
	}
	
	public String call()
	{
		this.reset();
		this.run();
		return buffer.toString();
	}
	
	public boolean isInvalid()
	{
		return this.invalid;
	}
}

