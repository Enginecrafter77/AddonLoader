package com.ec.addonloader.lib;

import java.net.URL;

public class ResourceLocation{
	
	private ResourceType res;
	private String path;
	
	public ResourceLocation(ResourceType res, String path)
	{
		this.res = res;
		if(path.endsWith(res.getFileExtension()))
		{
			this.path = res.getRawFilePath(path);
		}
		else
		{
			this.path = path;
		}
	}
	
	public URL getFile()
	{
		return this.getClass().getClassLoader().getResource(res.signFile(path));
	}
	
	public static enum ResourceType
	{
		TEXT(".txt"),
		IMAGE(".png"),
		XML(".xml"),
		CONFIGURATON(".conf"),
		DATA_FILE(".dat");
		
		private final String attr;
		
		private ResourceType(String attr)
		{
			this.attr = attr;
		}
		
		public String getFileExtension()
		{
			return attr;
		}
		
		public int getFALength()
		{
			return attr.length();
		}
		
		public String getRawFilePath(String path)
		{
			String res = path;
			int pathLastChar = path.length();
			res = path.substring(0, pathLastChar - this.getFALength());
			return res;
		}
		
		public String signFile(String fileName)
		{
			return fileName + attr;
		}
		
		public boolean testAttribute(ResourceType right)
		{
			if(this.attr.equals(right.getFileExtension()))
			{
				return true;
			}
			else
			{
				return false;
			}
		}
	}
    
    public static enum DataSize
    {    	
    	BYTE("B", 1),
    	KILOBYTE("KiB", 1024),
    	MEGABYTE("MiB", 1048576),
    	GIGABYTE("GiB", 1073741824);
    	
    	private final String id;
    	public final int size;
    	
    	private DataSize(String id, int size)
    	{
    		this.id = id;
    		this.size = size;
    	}
    	
    	public static DataSize fitDataSize(long size)
    	{
    		size = Math.abs(size);
    		DataSize[] val = DataSize.values();
    		int res = 0;
    		for(int i = val.length - 1; i > -1; i--)
    		{
    			if(size >= val[i].size)
    			{
    				res = i;
    				break;
    			}
    		}
    		return val[res];
    	}
    	
    	public static String formatDataSize(long size, DataSize from)
    	{
    		DataSize d = fitDataSize(size * from.size);
    		return convert(from, d, (int)size) + " " + d.getName();
    	}
    	
    	public long convert(DataSize to, long subject)
    	{
    		return convert(this, to, (int)subject);
    	}
    	
    	public String getName()
    	{
    		return this.id;
    	}
    	
    	public static long convert(DataSize from, DataSize to, long subject)
    	{
    		return (subject * from.size) / to.size;
    	}
    }
}
