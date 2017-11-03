package addonloader.util;

import java.io.FileNotFoundException;
import java.io.PrintStream;

public class PrefixedStream extends PrintStream{

	private String prefix;
	
	public PrefixedStream(String fileName, String prefix) throws FileNotFoundException
	{
		super(fileName);
		this.prefix = prefix;
	}
	
	@Override
	public void println(Object o)
	{
		super.println(prefix + o);
	}
	
	@Override
	public void println(String s)
	{
		super.println(prefix + s);
	}
	
	@Override
	public void println(int num)
	{
		super.println(prefix + num);
	}
	
	@Override
	public void println(float num)
	{
		super.println(prefix + num);
	}
	
	@Override
	public void println(double num)
	{
		super.println(prefix + num);
	}
	
	@Override
	public void println(long num)
	{
		super.println(prefix + num);
	}
	
	@Override
	public void println(boolean tf)
	{
		super.println(prefix + tf);
	}
	
	@Override
	public void println(char c)
	{
		super.println(prefix + c);
	}
	
	@Override
	public void println(char[] cc)
	{
		super.println(prefix + new String(cc));
	}
}
