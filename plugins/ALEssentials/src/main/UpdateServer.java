package main;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

import lejos.ev3.startup.MainMenu;

public class UpdateServer extends Thread {
	
	public UpdateServer()
	{
		this.setDaemon(true);
	}
	
	public static final int port = 8003;
	
	@Override
	public void run()
	{
		try
		{
			ServerSocket ss = new ServerSocket(port);
			Socket s;
			
			while(!this.isInterrupted())
			{
				try
				{
					s = ss.accept();
					InputStream is = s.getInputStream();
					UpdateType u = UpdateType.parseFromStream(is);
					u.copyFrom(is);
					s.close();
					if(u.equals(UpdateType.MENU))
					{
						MainMenu.lcd.clear();
						MainMenu.self.restart();
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			ss.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
			this.interrupt();
		}
	}
	
}
