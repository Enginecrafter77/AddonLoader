package addonloader.lib;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Binary storage is class used to store various data inside binary serialized from.
 * Data is written using the following scheme: <i>n[BlockStart+DATA]</i>. That means every block of data is prefixed by
 * <b>{@link #block_start Block Start}</b> bytes to dentote start of the block.
 * When retrieving data, as the data can have verious length, the program iterates through the database
 * and counts separator characters, until the number of skipped separators is matched and the data begins to be read.
 * It means that elements cannot be accessed in random order, much like {@link java.util.LinkedList LinkedList}.
 * @param <P> The type of the stored data.
 * @author Enginecrafter77
 */
public abstract class BinaryStorage<P>{
	
	/**
	 * Data block separation sequence.
	 * Note that if you increase the blockStart length, there will be
	 * <b>less</b> chance of mismatch, but also <b>greater</b> disk space usage. <br><br>
	 * 
	 * So, Bigger block separator means: <br>
	 * <b><font color='green'>Less</font></b> conflict probability<br>
	 * <b><font color='red'>More</font></b> disk space usage and performance impact
	 */
	private static final byte[] block_start = {0x5b, 0x1f, 0x5d};
	private final File f;
	
	public BinaryStorage(File file)
	{
		this.f = file;
	}
	
	public BinaryStorage(String path)
	{
		this.f = new File(path);
	}
	
	/**
	 * Reads single data block from the binary storage, and constructs valid P instance out of the data.
	 * @param stream The stream to read from.
	 * @return Valid P instance constructed from read data.
	 * @throws IOException if something in FS wrecks up.
	 */
	protected abstract P readTag(DataInputStream stream) throws IOException;
	
	/**
	 * Writes P into single data block to the binary storage.
	 * @param stream The stream to write to.
	 * @throws IOException if something in FS wrecks up.
	 */
	protected abstract void writeTag(P content, DataOutputStream stream) throws IOException;
	
	/**
	 * Adds content to the database.
	 * @param content The content to be added
	 * @throws IOException if something in filesystem shits up.
	 */
	public final void write(P content) throws IOException
	{
		DataOutputStream file = new DataOutputStream(new FileOutputStream(f, true));
		file.write(block_start);
		writeTag(content, file);
		file.close();
	}
	
	/**
	 * Adds content to the database.
	 * @param content The content to be added
	 * @throws IOException if something in filesystem shits up.
	 */
	public final void write(P[] content) throws IOException
	{
		DataOutputStream file = new DataOutputStream(new FileOutputStream(f, true));
		for(P element : content)
		{
			file.write(block_start);
			writeTag(element, file);
		}
		file.close();
	}
	
	/**
	 * Retrieves content back from the database.
	 * @param index the index of the element to be retreieved
	 * @return Retrieved content on index
	 * @throws IOException if something in filesystem shits up.
	 */
	public final P read(int index) throws IOException
	{
		DataInputStream fr = new DataInputStream(new FileInputStream(f));
		int i = -1;
		int matched = 0;
		while(i < index)
		{
			if((byte)fr.read() == block_start[matched]) matched++;
			if(matched >= block_start.length)
			{
				i++;
				matched = 0;
			}
		}
		return this.readTag(fr);
	}
	
	/**
	 * Counts blocks stored in the storage,
	 * and returns estimated number of blocks in the storage.
	 * @return Number of all blocks in storage.
	 * @throws IOException
	 */
	public final int length() throws IOException
	{
		FileInputStream fr = new FileInputStream(f);
		int i = 0;
		int matched = 0;
		while(fr.available() > 0)
		{
			if((byte)fr.read() == block_start[matched]) matched++;
			if(matched >= block_start.length)
			{
				i++;
				matched = 0;
			}
		}
		fr.close();
		return i;
	}
	
	public final void exportFile(P content, File file) throws IOException
	{
		DataOutputStream fw = new DataOutputStream(new FileOutputStream(file));
		this.writeTag(content, fw);
		fw.close();
	}
	
	public final P importFile(File file) throws IOException
	{
		DataInputStream fr = new DataInputStream(new FileInputStream(file));
		P result = this.readTag(fr);
		fr.close();
		return result;
	}
}
