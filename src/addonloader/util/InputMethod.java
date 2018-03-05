package addonloader.util;

import java.util.concurrent.Callable;

/**
 * Class specifing the method used to enter text on the screen.
 * @author Enginecrafter77
 */
public abstract class InputMethod implements Callable<String> {
	
	public static InputMethod current;
	protected final StringBuilder buffer;
	
	public InputMethod()
	{
		this.buffer = new StringBuilder();
	}
	
	public void setString(String text)
	{
		buffer.setLength(0);
		buffer.append(text);
	}
	
	public String getString()
	{
		return buffer.toString();
	}
}
