package addonloader.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import addonloader.lib.ResourceLocation;
import lejos.Reference;
import lejos.Utils;
import lejos.hardware.lcd.LCDOutputStream;
import lejos.utility.Delay;

/**
 * Installer does all the job of modifing the lejos home
 * directory to suit addonloader's needs.
 * It basically reads contents of patch.zip file, and makes
 * sure the system files are the same.
 * @author Enginecrafter77
 */
public class Installer implements Runnable {
	
	@Override
	public void run()
	{
		checkInstall(Reference.LEJOS_HOME);
	}
	
	/**
	 * Checks the files, and if the menu is missing, runs the installer.
	 */
	public static void checkInstall(String path)
	{
		File menu = new File(path + "/menu/Menu.jar");
		if(menu.exists()) return; //If the menu exists, the installation is supposed to be OK
		
		try
		{			
			LCDOutputStream lcd = new LCDOutputStream();
			PrintStream log = new PrintStream(lcd);
			PrintStream err = new PrintStream("/var/log/menu_install.err");
			System.setOut(log);
			System.setErr(err);
			
			System.out.println("Patching files...");
			ResourceLocation patch_zip = new ResourceLocation("addonloader/resources/patch.zip");
			ZipInputStream zip = new ZipInputStream(patch_zip.openStream());
			ZipEntry current_entry = zip.getNextEntry();
			while(current_entry != null)
			{
				checkFile(current_entry, zip, path);
				current_entry = zip.getNextEntry();
			}
			zip.close();
			
			System.out.println("Install menu...");
			InputStream is = Installer.class.getProtectionDomain().getCodeSource().getLocation().openStream();
			Files.copy(is, menu.toPath());
			is.close();
			
			System.out.println("Done!");
			System.out.println("Rebooting...");
			Delay.msDelay(500);
			Runtime.getRuntime().exec("reboot");
		}
		catch(Exception exc)
		{
			exc.printStackTrace();
		}
		System.exit(0);
	}
	
	/**
	 * Checks file specified by ZipEntry relative to filesystem position
	 * @param zip ZipEntry specifing relative file and it's contents
	 * @param from InputStream from which to read ZipEntry's contents.
	 * @param path The path against which the relatives will be matched.
	 * @throws IOException If filesystem error occurs.
	 */
	public static void checkFile(ZipEntry zip, InputStream from, String path) throws IOException
	{
		File real = new File(path + '/' + zip.getName());
		System.err.println("File " + real.getAbsolutePath());
		if(!real.exists())
		{
			if(zip.isDirectory()) real.mkdir();
			else real.createNewFile();
		}
		
		if(!zip.isDirectory()) //If the file exists, overwrite it.
		{
			byte[] buffer = new byte[(int)zip.getSize()];
			from.read(buffer);
			FileOutputStream fs = new FileOutputStream(real);
			fs.write(buffer);
			fs.close();
		}
	}
	
	/**
	 * Stops all other JVM instances.
	 */
	public static void stopJVMs() throws IOException, InterruptedException
	{
		String procid = ManagementFactory.getRuntimeMXBean().getName();
		String pid = Utils.before(procid, "@");
		Process proc = Runtime.getRuntime().exec("pidof java");
		proc.waitFor();
		Scanner scan = new Scanner(proc.getInputStream());
		String[] out = scan.nextLine().split(" ");
		scan.close();
		
		for(String cpid : out)
		{
			if(!cpid.equals(pid)) Runtime.getRuntime().exec("kill " + cpid).waitFor();
		}
	}

}
