package lejos.ev3.startup;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import lejos.utility.Delay;

public class PipeReader extends Thread {
	
	@Override
	public synchronized void run()
	{
		try
		{
			InputStream is = new FileInputStream(MainMenu.MENU_DIRECTORY + "/menufifo");
			while(true)
			{
				try
				{
					int c = is.read();
					if(c < 0)
					{
						Delay.msDelay(200);
					}
					else
					{
						System.out.println("Read from fifo: " + c + " " + ((char) c));
						
						if(c == 's')
						{
							MainMenu.self.suspend();
							System.out.println("Menu suspended");
						}
						else if(c == 'r')
						{
							MainMenu.self.resume();
							System.out.println("Menu resumed");
						}
					}
				}
				catch(IOException e)
				{
					e.printStackTrace();
					break;
				}
			}
			is.close();
		}
		catch (IOException e)
		{
			System.err.println("Failed to read from fifo: " + e);
			return;
		}
	}	
}