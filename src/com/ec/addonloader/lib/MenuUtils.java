package com.ec.addonloader.lib;

import static com.ec.addonloader.util.Icons.ICNo;
import static com.ec.addonloader.util.Icons.ICYes;

import java.io.File;

import com.ec.addonloader.lib.ResourceLocation.DataSize;

import lejos.ev3.startup.GraphicMenu;
import lejos.ev3.startup.MainMenu;
import lejos.ev3.startup.Utils;
import lejos.hardware.Button;

/**
 * Class used to store random functions used whereever in the code to keep the code neat.
 * @author Enginecrafter77
 */
public class MenuUtils {
	
	/**
	 * Appends <b>diff</b> to <b>num</b> until <b>num</b> is equal to <b>max</b>. Then sets <b>num</b> to <b>min</b>.
	 * @param num
	 * @param min
	 * @param max
	 * @param diff
	 * @return
	 */
	public static int cycleValue(int num, int min, int max, int diff)
	{
		int counted = num + diff;
		if(counted > max)
		{
			counted = min;
		}
		return counted;
	}
	
	/**
	 * Removes al files from directory with little more care on IO bandwidth.
	 * @param ct The directory to empty.
	 */
	public static void removeContent(String ct)
	{
		File[] files = new File(ct).listFiles();
		for(File f : files)
		{
			f.delete();
		}
	}
	
	public static String getFreeRam()
	{
		return "Free RAM: " + DataSize.formatDataSize(Runtime.getRuntime().freeMemory(), DataSize.BYTE);
	}
	
	/**
	 * Returns executable type based on jar location.
	 * @param f The File of jar executable.
	 * @return Executable type.
	 */
	public static int getExecType(File f)
	{
		String path = f.getAbsolutePath();
		if(path.startsWith(MainMenu.SAMPLES_DIRECTORY))
		{
			return MainMenu.TYPE_SAMPLE;
		}
		else if(path.startsWith(MainMenu.TOOLS_DIRECTORY))
		{
			return MainMenu.TYPE_TOOL;
		}
		else
		{
			return MainMenu.TYPE_PROGRAM;
		}
	}
	
	/**
	 * Adds or subtracts value until point is reached, then set it to opposite value.
	 * @param num Working subject
	 * @param max Maximal number
	 * @param min Minimal number
	 * @param forward Add? Then true.
	 * @return Modified num value.
	 */
	public static int moveNumber(int num, int max, int min, boolean forward)
	{
		if(forward)
		{
			if(num < max)
			{
				num++;
			}
			else
			{
				num = min;
			}
		}
		else
		{
			if(num > min)
			{
				num--;
			}
			else
			{
				num = max;
			}
		}
		return num;
	}
	
	/**
	 * Ask the user for confirmation of an action.
	 * @param prompt A description of the action about to be performed
	 * @param def A default selection.
	 * @return True if pressed yes.
	 */
	public static boolean getYesNo(String prompt, boolean def)
	{
		GraphicMenu menu = new GraphicMenu(new String[]{"No", "Yes"},new String[]{ICNo,ICYes}, 4, prompt, 3);
		return menu.getSelection(def ? 1 : 0) == 1;
	}
	
	/**
	 * Sets up a new screen for entering number. You can load default value into {@code buff}
	 * parameter.
	 * @param title The screen title.
	 * @param digits Number of digits.
	 * @param buff The number buffer. Loads values into this parameter.
	 * @param xoffset X offset from 0
	 * @param line Line on which to display.
	 * @return If set was commited(ENTER), returns true.
	 */
	public static boolean enterNumber(String title, int digits, int[] buff, int xoffset, int line)
	{
		if(buff == null)
		{
			buff = new int[digits];
		}
		else
		{
			digits = buff.length;
		}
		
		MainMenu.self.newScreen(title);
		int selection = 0;
		while(true)
		{
			MainMenu.self.newScreen();
			StringBuilder s = new StringBuilder();
			for(int i = 0; i < digits; i++)
			{
				s.append(buff[i]);
				s.append(' ');
			}
			MainMenu.lcd.drawString(s.toString(), xoffset, line);
			
			Utils.drawRect(selection * 20 + xoffset * 10 - 5, line * 16 - 3, 20, 20);
			int ret = Button.waitForAnyPress();
			switch(ret)
			{
				case Button.ID_ENTER:
					return true;
				case Button.ID_DOWN:
					buff[selection] = MenuUtils.moveNumber(buff[selection], 9, 0, false);
					break;
				case Button.ID_UP:
					buff[selection] = MenuUtils.moveNumber(buff[selection], 9, 0, true);
					break;
				case Button.ID_LEFT:
					selection = MenuUtils.moveNumber(selection, digits - 1, 0, false);
					break;
				case Button.ID_RIGHT:
					selection = MenuUtils.moveNumber(selection, digits - 1, 0, true);
					break;
				case Button.ID_ESCAPE:
					return false;
			}
		}
	}
	
	/**
	 * Customized ersion of {@link #enterNumber(String, int, int[], int, int)} specialized for 4-digit pin.
	 * @param title Screen title.
	 * @return Pin digits in int array.
	 */
	public static int[] enterPin(String title)
	{
		int[] res = new int[4];
		if(enterNumber(title, res.length, res, 2, 3))
		{
			return res;
		}
		else
		{
			return null;
		}
	}
	
}
