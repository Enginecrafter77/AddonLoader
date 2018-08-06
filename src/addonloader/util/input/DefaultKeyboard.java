package addonloader.util.input;

import addonloader.util.ui.StockIcon;
import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.Font;
import lejos.hardware.lcd.GraphicsLCD;
import lejos.hardware.lcd.Image;

public class DefaultKeyboard extends InputMethod
{	
	private static GraphicsLCD lcd = LocalEV3.get().getGraphicsLCD();
	
	private static String[][] keyboards = {
			{ "1234567890?", 
			   "qwertyuiop<", 
			   "asdfghjkl.>", 
			   "^zxcvbnm  ^" },
			{ "1234567890?", 
			   "QWERTYUIOP<", 
			   "ASDFGHJKL,>", 
			   "^ZXCVBNM  ^" },
			{ "[]{}#%^*+=@", 
			  "-\\|:;()$@\"<", 
			  "~/_`&.,?!'>", 
			  "^	 <>  ^" } };

	public String call()
	{
		StringBuilder buffer = new StringBuilder();
		Image ic_ok, ic_symbol, ic_del, ic_shift, ic_shifton;
		try
		{
			ic_ok = StockIcon.KEY_OK.call();
			ic_symbol = StockIcon.KEY_SYMBOLS.call();
			ic_del = StockIcon.KEY_DEL.call();
			ic_shift = StockIcon.KEY_SHIFT.call();
			ic_shifton = StockIcon.KEY_SHIFTON.call();
		}
		catch(Exception exc)
		{
			exc.printStackTrace();
			return this.invalidate();
		}
		
		int sx = 0, sy = 0, keyboard = 0;

		lcd.setFont(Font.getDefaultFont());
		int chWidth = lcd.getFont().width;
		int chHeight = lcd.getFont().getHeight();
		int maxChars = 160 / chWidth - 2;
		lcd.clear();
		do
		{
			lcd.setColor(GraphicsLCD.WHITE);
			lcd.fillRect(8, 20, 162, 98);
			lcd.setColor(GraphicsLCD.BLACK);
			lcd.drawRect(7, 19, 164, 100);
			lcd.drawLine(6, 20, 6, 118);
			lcd.drawLine(172, 20, 172, 118);

			lcd.setStrokeStyle(GraphicsLCD.DOTTED);
			lcd.setFont(Font.getDefaultFont());

			String substr = buffer.toString();
			if(buffer.length() > maxChars)
			{
				substr = buffer.substring(buffer.length() - maxChars);
				lcd.drawString("<", 16, 38, GraphicsLCD.BOTTOM
						| GraphicsLCD.RIGHT);
			}
			lcd.drawString(substr, 15, 38, GraphicsLCD.BOTTOM);
			lcd.drawString(" ", 15 + lcd.getFont().stringWidth(substr), 38,
					GraphicsLCD.BOTTOM, true);

			lcd.drawLine(15, 40, 163, 40);

			for (int yi = 0; yi < 4; yi++)
			{
				for (int xi = 0; xi < 11; xi++)
				{
					int x = xi * (chWidth + 4) + 16;
					int y = yi * (lcd.getFont().getHeight() + 3) + 43;

					/* Draw Key Character */
					lcd.drawChar(keyboards[keyboard][yi].charAt(xi), x, y, 0);

					/* Draw Finish Key */
					if (yi == 1 && xi == 10)
						lcd.drawImage(ic_ok, x - 1, y, 0);

					/* Draw Symbol Key */
					if (yi == 2 && xi == 10)
						lcd.drawImage(ic_symbol, x - 1, y, 0);

					/* Draw Del Key */
					if (yi == 0 && xi == 10)
						lcd.drawImage(ic_del, x - 1, y, 0);

					/* Draw Shift Key */
					if (yi == 3 && (xi == 0 || xi == 10))
						lcd.drawImage((keyboard == 1) ? ic_shifton : ic_shift,
								x - 1, y, 0);

					/* Draw Space Bar */
					if (yi == 3 && xi == 8)
					{
						lcd.drawRect(x, y + 1, chWidth * 2 + 4, chHeight - 2);
						if (sy == 3 && sx >= 8 && sx <= 9)
							lcd.fillRect(x - 1, y, chWidth * 2 + 6, chHeight);
					}

					/* Invert Key if Selected */
					if (sx == xi && sy == yi
							&& !(sy == 3 && sx >= 8 && sx <= 9))
						lcd.drawRegionRop(null, x - 1, y, chWidth + 2,
								chHeight, x - 1, y, 0,
								GraphicsLCD.ROP_COPYINVERTED);
				}
			}
			lcd.refresh();
			switch (Button.waitForAnyPress())
			{
			case Button.ID_RIGHT:
				if (sy == 3 && sx == 8)
					sx++;
				if (sx < 10)
					sx++;
				else
					sx = 0;
				break;
			case Button.ID_LEFT:
				if (sy == 3 && sx == 9)
					sx--;
				if (sx > 0)
					sx--;
				else
					sx = 10;
				break;
			case Button.ID_UP:
				if (sy > 0)
					sy--;
				else
					sy = 3;
				break;
			case Button.ID_DOWN:
				if (sy < 3)
					sy++;
				else
					sy = 0;
				break;
			case Button.ID_ENTER:
				if (sy == 3 && (sx == 0 || sx == 10))
				{ // Shift Key Pressed
					if (keyboard == 0)
						keyboard = 1;
					else if (keyboard == 1)
						keyboard = 0;
				}
				else if (sy == 2 && sx == 10)
				{ // Symbols Key Pressed
					if (keyboard != 2)
						keyboard = 2;
					else
						keyboard = 0;
				}
				else if(sy == 0 && sx == 10)
				{
					// Backspace Key Pressed
					if(buffer.length() > 0) buffer.setLength(buffer.length() - 1);
				}
				else if(sy == 1 && sx == 10)
				{
					// Finish Key Pressed
					return buffer.toString();
				}
				else
					// Character Key Pressed
					buffer.append(keyboards[keyboard][sy].substring(sx, sx + 1));
				break;
			case Button.ID_ESCAPE:
				return this.invalidate();
			}
			lcd.refresh();

		} while (true);
	}

	@Override
	public boolean ready()
	{
		return true; //We are always ready.
	}

}
