package lejos.ev3.startup;

import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;

import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.Font;
import lejos.hardware.lcd.TextLCD;
import lejos.utility.Delay;

public class ExceptionHandler implements UncaughtExceptionHandler {

	@Override
	public void uncaughtException(Thread th, Throwable t)  {
		Sound.buzz();
	    TextLCD lcd = LocalEV3.get().getTextLCD(Font.getSmallFont());
	    int offset = 0;
	    while (true)
	    {
    		lcd.clear();
    		lcd.drawString("Uncaught exception:", offset, 0);
    		lcd.drawString(t.getClass().getName(), offset, 2);
    		if (t.getMessage() != null) lcd.drawString(t.getMessage(), offset, 3);		
    		
    		if (t.getCause() != null) {
    			lcd.drawString("Caused by:", offset, 5);
    			lcd.drawString(t.getCause().toString(), offset, 6);
    		}
    		
    		StackTraceElement[] trace = t.getStackTrace();
    		for(int i=0;i<7 && i < trace.length ;i++) lcd.drawString(trace[i].toString(), offset, 8+i);
    		
    		lcd.refresh();
    		int id = Button.waitForAnyEvent();
    		if (id == Button.ID_ESCAPE) break;
    		if (id == Button.ID_LEFT) offset += 5;
    		if (id == Button.ID_RIGHT)offset -= 5;
    		if (offset > 0) offset = 0;
	    }
	    
	    // Shutdown the EV3
    	try {
			Runtime.getRuntime().exec("init 0");
		} catch (IOException e) {
			// Ignore
		}
	    System.exit(1);
	}
	
	public static class DebugHandler implements UncaughtExceptionHandler
	{
		@Override
		public void uncaughtException(Thread th, Throwable e)
		{
			Sound.buzz();
			TextLCD lcd = LocalEV3.get().getTextLCD(Font.getLargeFont());
			lcd.clear();
			lcd.drawString("ERROR", 1, 2);
			e.printStackTrace(System.err);
			Delay.msDelay(500);
			System.exit(0);
		}
	}
}
