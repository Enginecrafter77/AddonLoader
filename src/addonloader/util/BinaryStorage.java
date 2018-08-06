package addonloader.util;

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
 * When retrieving data, as the data can have various length, the program iterates through the database
 * and counts separator characters, until the number of skipped separators is matched and the data begins to be read.
 * It means that elements cannot be accessed in random order, much like {@link java.util.LinkedList LinkedList}.
 * @param DATA The type of the stored data.
 * @author Enginecrafter77
 */
public abstract class BinaryStorage<DATA> {
	
	/**
	 * Data block separation sequence.
	 * Note that if you increase the blockStart length, there will be
	 * <b>less</b> chance of mismatch, but also <b>greater</b> disk space usage. <br><br>
	 * 
	 * So, Bigger block separator means: <br>
	 * <b><font color='green'>Less</font></b> probability of byte mismatch, eg. matching block start where it really isn't.<br>
	 * <b><font color='red'>More</font></b> disk space usage (barely noticeable) and performance impact (minor)
	 */
	private static final byte[] block_start = {0x5b, 0x1f, 0x5d};
	private final File stg_file;
	
	public BinaryStorage(File file)
	{
		this.stg_file = file;
	}
	
	public BinaryStorage(String path)
	{
		this.stg_file = new File(path);
	}
	
	/**
	 * Reads single data block from the binary storage, and constructs valid P instance out of the data.
	 * @param stream The stream to read from.
	 * @return Valid P instance constructed from read data.
	 * @throws IOException if something in FS wrecks up.
	 */
	protected abstract DATA readTag(DataInputStream stream) throws IOException;
	
	/**
	 * Writes P into single data block to the binary storage.
	 * @param stream The stream to write to.
	 * @throws IOException if something in FS wrecks up.
	 */
	protected abstract void writeTag(DATA content, DataOutputStream stream) throws IOException;
	
	/**
	 * Adds content to the database.
	 * @param content The content to be added
	 * @throws IOException if something in filesystem shits up.
	 */
	public final void write(DATA content) throws IOException
	{
		DataOutputStream file = new DataOutputStream(new FileOutputStream(stg_file, true));
		file.write(block_start);
		writeTag(content, file);
		file.close();
	}
	
	/**
	 * Adds content to the database.
	 * @param content The content to be added
	 * @throws IOException if something in filesystem shits up.
	 */
	public final void write(DATA[] content) throws IOException
	{
		DataOutputStream file = new DataOutputStream(new FileOutputStream(stg_file, true));
		for(DATA element : content)
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
	public final DATA read(int index) throws IOException
	{
		DataInputStream fr = new DataInputStream(new FileInputStream(stg_file));
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
		FileInputStream fr = new FileInputStream(stg_file);
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
	
	public final void exportFile(DATA content, File file) throws IOException
	{
		DataOutputStream fw = new DataOutputStream(new FileOutputStream(file));
		this.writeTag(content, fw);
		fw.close();
	}
	
	public final DATA importFile(File file) throws IOException
	{
		DataInputStream fr = new DataInputStream(new FileInputStream(file));
		DATA result = this.readTag(fr);
		fr.close();
		return result;
	}
}
