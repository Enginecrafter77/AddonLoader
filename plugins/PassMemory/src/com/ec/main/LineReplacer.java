package com.ec.main;

public interface LineReplacer {
	
	public boolean lineMatches(String line);
	public String onMatch(String line);
	public String notMatch(String line);
	public void setExtras(String... extras);
	
}
