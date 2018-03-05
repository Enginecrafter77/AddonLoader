package addonloader.lib;

/**
 * Classic binary prefix data size formatter
 * @author Enginecrafter77
 */
public enum DataSize
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
