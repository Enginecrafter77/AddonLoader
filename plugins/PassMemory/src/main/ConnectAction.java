package com.ec.main;

import java.io.IOException;

import addonloader.menu.ActionHook;
import addonloader.menu.HookRegistry;
import addonloader.menu.InputMethod;
import lejos.NetUtils;

public class ConnectAction extends ActionHook {

	public ConnectAction()
	{
		super(HookRegistry.WIFI_CONNECT, HookType.OVERRIDE);
	}

	@Override
	public void run()
	{
		String ap_ssid = this.parent.fetchCarrier();		
		if(ap_ssid.equals("[HIDDEN]")) ap_ssid = InputMethod.current.call();
		String psk = Main.key_storage.getProperty(ap_ssid);
		
		if(psk == null)
		{
			try
			{
				String password = InputMethod.current.call();
				if(InputMethod.current.isInvalid()) psk = "";
				else psk = NetUtils.computePSK(ap_ssid, password);
				Main.key_storage.setProperty(ap_ssid, psk);
			}
			catch(Exception exc)
			{
				System.err.println("[ERROR] Failed computing PSK for AP " + ap_ssid);
			}
		}
		
		try
		{
			NetUtils.writeConfig(ap_ssid, psk, ap_ssid.equals("[HIDDEN]"));
		}
		catch(IOException exc)
		{
			System.err.println("[ERROR] Failed writing wpa_supplicant config.");
		}
	}

}
