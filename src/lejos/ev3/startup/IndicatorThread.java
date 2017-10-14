package lejos.ev3.startup;

/**
 * Manage the top line of the display.
 * The top line of the display shows battery state, menu titles, and I/O
 * activity.
 */
public class IndicatorThread implements Runnable
{
	private int displayState = 0;
	private int savedState = 0;
	private Thread thread;
	
	public IndicatorThread()
	{
		thread = new Thread(this);
		thread.setDaemon(true);
	}

	public void start()
	{
		thread.start();
	}
	
	@Override
	public synchronized void run()
	{
		try
		{
			int updateIPCountdown = 0;
			while (true)
			{
				if (displayState >= Reference.IND_NORMAL)
				{
					if (updateIPCountdown <= 0)
					{
						if (MainMenu.self.updateIPAddresses())
						{
							System.out.println("Address changed");
							MainMenu.self.startNetworkServices();
						}
						updateIPCountdown = Reference.IP_UPDATE;
					}
					MainMenu.self.indiBA.setWifi(MainMenu.wlanAddress != null);
					MainMenu.self.indiBA.draw();
					if (displayState >= Reference.IND_FULL)
					{
						MainMenu.lcd.clear(1);
						MainMenu.lcd.clear(2);
						int row = 1;
						for(String ip: MainMenu.ips)
						{
							MainMenu.lcd.drawString(ip,8 - ip.length()/2,row++);
						}							
					}
					MainMenu.lcd.refresh();
					// wait until next tick
					long time = System.currentTimeMillis();
					this.wait(Reference.ANIM_DELAY - (time % Reference.ANIM_DELAY));
					updateIPCountdown -= Reference.ANIM_DELAY;
				}
				else
				{
					this.wait();
				}
			}
		}
		catch (InterruptedException e){}
	}
	
	/**
	 * Update the indicators
	 */
	public synchronized void updateNow()
	{
		this.notifyAll();
	}
	
	public void setDisplayState(int state)
	{
		if(displayState != Reference.IND_SUSPEND)
		{
			displayState = state;
			updateNow();
		}
		else
			savedState = state;
	}
	
	public void suspend()
	{
		savedState = displayState;
		displayState = Reference.IND_SUSPEND;
		updateNow();			
	}
	
	public void resume()
	{
		displayState = savedState;
		updateNow();
	}
}
