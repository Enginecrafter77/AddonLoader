package com.ec.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

import lejos.ev3.startup.Utils;
import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.Font;
import lejos.hardware.lcd.TextLCD;

public class KeyStorage {
	
	public static final File KEY_STORAGE = new File("/home/root/lejos/config/stored_pass");
	public static LineReplacer setter;
	public static LineReplacer remover;
	
	private static String getPassUnhandled(String name) throws IOException
	{
		BufferedReader b = new BufferedReader(new FileReader(KEY_STORAGE));
		String res = null;
		String line = "";
		while(line != null)
		{
			line = b.readLine();
			if(line != null && line.contains("="))
			{
				if(Utils.before(line, "=").equals(name))
				{
					res = Utils.after(line, "=");
					break;
				}
			}
		}
		
		b.close();
		return res;
	}
	
	public static String getPass(String name)
	{
		try
		{
			return getPassUnhandled(name);
		}
		catch(Exception e)
		{
			return null;
		}
	}
	
	public static void add(String name, String pw)
	{
		try
		{
			PrintStream p = new PrintStream(new FileOutputStream(KEY_STORAGE, true));
			p.println(name + "=" + pw);
			p.close();
		}
		catch(IOException e){}
	}
	
	public static void remove(String name) throws IOException
	{
		System.out.println("Removing [" + name + "] from AP databse.");
		remover.setExtras(name);
		String buff = filterLines(KEY_STORAGE, remover);
		FileWriter fw = new FileWriter(KEY_STORAGE);
		fw.write(buff);
		fw.close();
	}
	
	public static void setPass(String name, String pw) throws IOException
	{
		setter.setExtras(name, pw);
		String buff = filterLines(KEY_STORAGE, setter);
		FileWriter fw = new FileWriter(KEY_STORAGE);
		fw.write(buff);
		fw.close();
	}
	
	/**
	 * Replaces matching lines in a file using a {@link LineReplacer} instance to match lines.
	 * @param f The file to work with.
	 * @param r Replace line filter.
	 * @return Modified file in string buffer.
	 * @throws FileNotFoundException When file is not found, oviously.
	 * @throws IOException When something else bad occurs.
	 */
	public static String filterLines(File f, LineReplacer r) throws FileNotFoundException, IOException
	{
		BufferedReader b = new BufferedReader(new FileReader(f));
		String buff = "";
		
		String line = b.readLine();
		while(line != null)
		{
			buff += r.lineMatches(line) ? r.onMatch(line) : r.notMatch(line);
			line = b.readLine();
		}
		
		b.close();
		return buff;
	}
	
	public static String[] getSSIDs() throws IOException
	{
		BufferedReader b = new BufferedReader(new FileReader(KEY_STORAGE));
		ArrayList<String> a = new ArrayList<String>();
		String line = b.readLine();
		while(line != null)
		{
			if(line.contains("="))
			{
				a.add(Utils.before(line, "="));
			}
			line = b.readLine();
		}
		b.close();
		
		String[] res = new String[a.size()];
		a.toArray(res);
		return res;
	}
	
	public static void viewInfo(String name) throws Exception
	{
		String pass = getPass(name);
		TextLCD t = LocalEV3.ev3.getTextLCD(Font.getSmallFont());
		t.drawString("AP Name: " + name, 1, 3);
		t.drawString("Password: " + pass, 1, 4);
		t.drawString("Security: " + (pass.isEmpty() ? "Open" : "Secured"), 1, 5);
		t.drawString("Press any key to exit", 1, 6);
		Button.waitForAnyPress();
		t.clear();
	}
	
	public static void init()
	{
		setter = new LineReplacer(){
			private String ssid;
			private String pwd;
			
			@Override
			public boolean lineMatches(String line)
			{
				return line.contains("=") && Utils.before(line, "=").equals(ssid);
			}

			@Override
			public String onMatch(String line)
			{
				System.out.println(line + " matches the setter; S: [" + ssid + "]; P: [" + pwd + "]");
				return ssid + '=' + pwd + System.lineSeparator();
			}

			@Override
			public String notMatch(String line)
			{
				System.out.println(line + " doesn't match the setter; S: [" + ssid + "]; P: [" + pwd + "]");
				return line + System.lineSeparator();
			}

			@Override
			public void setExtras(String... extras)
			{
				ssid = extras[0];
				pwd = extras[1];
			}
		};
		remover = new LineReplacer(){
			private String ssid;
			
			@Override
			public boolean lineMatches(String line)
			{
				return line.contains("=") && Utils.before(line, "=").equals(ssid);
			}

			@Override
			public String onMatch(String line)
			{
				System.out.println(line + " matches the remover; S: [" + ssid + "]");
				return "";
			}

			@Override
			public String notMatch(String line)
			{
				System.out.println(line + " doesn't match the remover; S: [" + ssid + "]");
				return line + System.lineSeparator();
			}

			@Override
			public void setExtras(String... extras)
			{
				ssid = extras[0];
			}
		};
	}
	
}
