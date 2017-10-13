package com.ec.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public interface LineReplacer {
	
	public boolean lineMatches(String line);
	public String onMatch(String line);
	public String notMatch(String line);
	public void setExtras(String... extras);
	
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
	
}
