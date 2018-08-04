package addonloader.util;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;

import lejos.hardware.BrickFinder;
import lejos.hardware.Button;
import lejos.hardware.lcd.Font;
import lejos.hardware.lcd.TextLCD;

public class Viewer {
	
	private TextLCD lcd;
	private int min_x_offset, min_y_offset, cap_x_offset, cap_y_offset;
	
	public Viewer(Font type, int x_off, int y_off)
	{
		this.min_x_offset = x_off;
		this.min_y_offset = y_off;
		lcd = BrickFinder.getDefault().getTextLCD(type);
		cap_x_offset = lcd.getTextWidth() - min_x_offset; 
		cap_y_offset = lcd.getTextHeight() - min_y_offset;
	}
	
	public Viewer(Font type)
	{
		this(type, 0, 2);
	}
	
	public void open(Reader input) throws IOException
	{
		ArrayList<String> lines = new ArrayList<>();
		//Fits roughly 30x16 characters
		StringBuilder line = new StringBuilder();
		int ch = input.read(), max_length = 0;
		while(ch > -1)
		{
			if((char)ch == '\n')
			{
				if(line.length() > max_length) max_length = line.length(); //Record longest line.
				lines.add(line.toString());
				line.setLength(0);
			}
			else line.append((char)ch);
			ch = input.read();
		}
		input.close();
		
		int x_off = 0, y_off = 0;
		
		int button = 0;
		String cline;
		while(button != Button.ID_ESCAPE)
		{
			lcd.clear();
			for(int index = 0; (index + y_off) < lines.size(); index++)
			{
				if(index > 16) break;
				cline = lines.get(index + y_off);
				if(cline.isEmpty() || x_off >= cline.length()) continue;
				// Computes maximum allowed line length. If the expected length exceeds screen, use real line length.
				int line_end = (x_off + 30 > cline.length() ? cline.length() : x_off + 30);
				lcd.drawString(cline.substring(x_off, line_end), min_x_offset, index + min_y_offset);
			}
			
			button = Button.waitForAnyPress();
			switch(button)
			{
			case Button.ID_RIGHT:
				if((x_off + cap_x_offset) < max_length) x_off++;
				break;
			case Button.ID_LEFT:
				if(x_off > 0) x_off--;
				break;
			case Button.ID_DOWN:
				if((y_off + cap_y_offset) < lines.size()) y_off++;
				break;
			case Button.ID_UP:
				if(y_off > 0) y_off--;
				break;
			default:
				break;
			}
		}
	}
}
