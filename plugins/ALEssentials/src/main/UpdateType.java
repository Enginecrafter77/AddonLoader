package main;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public enum UpdateType implements Serializable
{	
	ADDON("/home/root/lejos/addons", true),
	PROGRAM("/home/lejos/programs", true),
	TOOL("/home/root/lejos/tools", true),
	SAMPLE("/home/root/lejos/samples", true),
	MENU("/home/root/lejos/menu/Menu.jar", false);
	
	private static final char separator = '|';
	private Path path;
	private final boolean dir;
	private UpdateType(String path, boolean dir)
	{
		this.path = new File(path).toPath();
		this.dir = dir;
	}
	
	@Override
	public String toString()
	{
		StringBuilder name = new StringBuilder(this.name().toLowerCase());
		char cp = Character.toUpperCase(name.charAt(0));
		name.setCharAt(0, cp);
		return name.toString();
	}
	
	public void copyFrom(InputStream is) throws IOException
	{
		Path path = this.path;
		if(this.dir)
		{
			path = path.resolve(readUntil(is, separator));
		}
		
		Files.copy(is, path, StandardCopyOption.REPLACE_EXISTING);
	}
	
	public void copyTo(OutputStream os, Path file) throws IOException
	{
		os.write(this.name().getBytes());
		os.write(separator);
		if(this.dir)
		{
			os.write((file.toFile().getName() + separator).getBytes());
		}
		
		Files.copy(file, os);
	}
	
	public static String readUntil(InputStream is, char until) throws IOException
	{
		StringBuilder res = new StringBuilder();
		char c = (char)is.read();
		while(c != until)
		{
			res.append(c);
			c = (char)is.read();
		}
		return res.toString();
	}
	
	public static UpdateType parseFromStream(InputStream is) throws IOException
	{
		return parse(readUntil(is, separator));
	}
	
	public static UpdateType parse(String id)
	{
		String cap = id.toUpperCase();
		for(UpdateType u : UpdateType.values())
		{
			if(u.name().equals(cap))
			{
				return u;
			}
		}
		return null;
	}
}