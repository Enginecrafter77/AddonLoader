package addonloader.util.input;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.net.ServerSocket;
import java.net.Socket;

import lejos.hardware.lcd.Font;
import lejos.hardware.lcd.GraphicsLCD;

public class TerminalServer extends InputMethod {

	public static ServerSocket server;
	public static Socket client;
	
	public static void attach_std()
	{
		try
		{
			server = new ServerSocket(8765);
			client = server.accept();
			PrintStream out_wrapper = new PrintStream(client.getOutputStream());
			
			System.setIn(client.getInputStream());
			System.setOut(out_wrapper);
			System.setErr(out_wrapper);
		}
		catch(IOException exc)
		{
			System.err.println("[ERROR] Redirecting stream failed (socket not connected)");
		}
	}

	@Override
	public String call()
	{
		try
		{
			StringBuilder str = new StringBuilder();
			Reader rd = new InputStreamReader(System.in);
			Font font = screen.getFont();
			screen.clear();
			screen.drawString("Waiting for", font.width, font.height, GraphicsLCD.TOP | GraphicsLCD.LEFT);
			screen.drawString("remote input", font.width * 2, font.height * 2, GraphicsLCD.TOP | GraphicsLCD.LEFT);
			
			int cch = 0;
			while(cch > -1)
			{
				cch = rd.read();
				if((char)cch == '\n') break;
				str.append((char)cch);
			}
			rd.close();
			
			return str.toString();
		}
		catch(IOException exc)
		{
			exc.printStackTrace();
			return this.invalidate();
		}
	}

	@Override
	public boolean ready()
	{
		return client != null;
	}

}
