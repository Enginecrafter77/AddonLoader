package addonloader.util.input;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import lejos.hardware.lcd.Font;
import lejos.hardware.lcd.GraphicsLCD;

public class PipeInput extends InputMethod {

	private File pipe;
	
	public PipeInput(String pipe)
	{
		this.pipe = new File(pipe);
	}
	
	public static void attach_std()
	{
		try
		{
			System.setOut(new PrintStream("/var/log/menu.log"));
			System.setErr(new PrintStream("/var/log/menu.err"));
		}
		catch(IOException exc)
		{
			exc.printStackTrace();
		}
	}
	
	@Override
	public boolean ready()
	{
		return pipe.canRead();
	}

	@Override
	public String call()
	{
		try
		{
			StringBuilder str = new StringBuilder();
			Font font = screen.getFont();
			screen.clear();
			screen.drawString("Waiting for", font.width, font.height, GraphicsLCD.TOP | GraphicsLCD.LEFT);
			screen.drawString("piped input", font.width * 2, font.height * 2, GraphicsLCD.TOP | GraphicsLCD.LEFT);
			
			Reader rd = new FileReader(pipe);
			int cch = 0;
			while(cch > -1)
			{
				cch = rd.read();
				str.append((char)cch);
			}
			rd.close();
			
			return str.toString();
		}
		catch(IOException exc)
		{
			exc.printStackTrace();
			return this.invalidate();
		}
	}

}
