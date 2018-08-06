package addonloader.betamenu;

import addonloader.menu.SimpleMenuEntry;
import addonloader.util.ui.StockIcon;
import lejos.hardware.Button;
import lejos.hardware.Key;
import lejos.hardware.Sound;
import lejos.hardware.lcd.LCD;

public class SoundEntry extends SimpleMenuEntry {

	private static final String[] prop_names = {Sound.VOL_SETTING, Button.VOL_SETTING, Button.FREQ_SETTING, Button.LEN_SETTING};
	private int prop_id;
	
	public SoundEntry(String name, int prop_id)
	{
		super("Set " + name, StockIcon.SOUND);
		this.prop_id = prop_id;
	}

	@Override
	public void run()
	{
		int factor = (int) Math.round(Math.pow(10, Math.round(prop_id / 2 + 0.5)));
		int value = Integer.parseInt(BetaMenu.settings.getProperty(prop_names[prop_id], "0")) / factor;
		int button = 0;
		
		while(button != Button.ID_ENTER)
		{
			LCD.clear(3);
			LCD.drawString(String.format("+ <%d> -", value), 5, 3);
			button = Button.waitForAnyPress();
			switch(button)
			{
			case Button.ID_LEFT:
				if(value < 10) value++;
				break;
			case Button.ID_RIGHT:
				if(value > 0) value--;
				break;
			case Button.ID_ESCAPE:
				return;
			}
			
			switch(this.prop_id)
			{
			case 0:
				if(Button.getKeyClickVolume() == 0) Sound.playTone(500, 500);
				Sound.setVolume(value * factor);
				break;
			case 1:
				Button.setKeyClickVolume(value * factor);
			case 2:
				Button.setKeyClickTone(Key.ENTER, value * factor);
				break;
			case 3:
				Button.setKeyClickLength(value * factor);
				break;
			}
		}
		
		BetaMenu.settings.setProperty(prop_names[prop_id], String.valueOf(value * factor));
	}

}
