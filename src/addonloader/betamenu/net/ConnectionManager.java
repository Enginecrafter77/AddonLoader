package addonloader.betamenu.net;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Random;

import addonloader.main.Reference;
import lejos.hardware.Brick;
import lejos.hardware.BrickFinder;
import lejos.hardware.LocalBTDevice;
import lejos.hardware.LocalWifiDevice;
import lejos.hardware.RemoteBTDevice;

public class ConnectionManager {
	
	public static final String WIFI_CONFIG="/home/root/lejos/config/wpa_supplicant.conf";
	
	public final LocalBTDevice bluetooth;
	public final LocalWifiDevice wifi;
	public final Brick brick;
	private final String bluetooth_pin_format;
	private final Random rng;
	
	private HashMap<String, InetAddress> interface_adresses; 
	
	private String bluetooth_pin;
	private boolean bluetooth_random_pin;
	private boolean wifi_connected;
	
	public ConnectionManager(String btpinformat)
	{
		this.bluetooth_pin_format = btpinformat;
		this.interface_adresses = new HashMap<String, InetAddress>();
		this.brick = BrickFinder.getDefault();
		this.bluetooth = brick.getBluetoothDevice();
		this.wifi = brick.getWifiDevice();
		this.rng = new Random();
	}
	
	public void update_interfaces()
	{
		try
		{
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
			interface_adresses.clear();
			
			NetworkInterface current;
			Enumeration<InetAddress> adresses;
			while(interfaces.hasMoreElements())
			{
				current = interfaces.nextElement();
				adresses = current.getInetAddresses();
				
				if(!current.isUp() || current.isLoopback() || current.isVirtual()) continue;
				while(adresses.hasMoreElements()) this.interface_adresses.put(current.getName(), adresses.nextElement());
			}
			
			this.wifi_connected = this.interface_adresses.containsKey(Reference.WLAN_INTERFACE);
		}
		catch(IOException exc)
		{
			System.err.println("[ERROR] Couldn't update interface info: " + exc);
		}
	}
	
	public AccessPoint[] search_wifi()
	{
		String[] ap_names = this.wifi.getAccessPointNames();
		AccessPoint[] ap_objs = new AccessPoint[ap_names.length];
		for(int index = 0; index < ap_names.length; index++) ap_objs[index] = new AccessPoint(ap_names[index], true);
		return ap_objs;
	}
	
	public RemoteBTDevice[] search_bluetooth()
	{
		Collection<RemoteBTDevice> search_result = this.bluetooth.search();
		RemoteBTDevice[] result = new RemoteBTDevice[search_result.size()];
		search_result.toArray(result);
		return result;
	}
	
	public void configure_wpa(AccessPoint ap, String psk) throws IOException
	{
		PrintWriter wpa_supplicant = new PrintWriter(WIFI_CONFIG);
		wpa_supplicant.println("ctrl_interface=/var/run/wpa_supplicant\n");
		wpa_supplicant.println("network={");
		wpa_supplicant.println("\tssid=\"" + ap.essid + '\"');
		wpa_supplicant.println("\tkey_mgmt=WPA-PSK NONE");
		if(psk != null) wpa_supplicant.println("\tpsk=" + psk);
		wpa_supplicant.println("\tscan_ssid=" + (ap.visible ? '0' : '1'));
		wpa_supplicant.println("}");
		wpa_supplicant.close();
	}
	
	public Process wifi_connect() throws IOException, InterruptedException
	{
		return Runtime.getRuntime().exec(Reference.START_WLAN);
	}
	
	public boolean wifi_connected()
	{
		return this.wifi_connected;
	}
	
	public void bluetooth_pair(RemoteBTDevice dev)
	{
		this.bluetooth.authenticate(dev.getAddress(), this.bluetooth_random_pin ? this.generate_bluetooth_pin() : this.bluetooth_pin);
	}
	
	private String generate_bluetooth_pin()
	{
		StringBuilder result = new StringBuilder();
		
		Object insert;
		for(char current : this.bluetooth_pin_format.toCharArray())
		{
			switch(current)
			{
			case '#': //Generate single digit
				insert = rng.nextInt(10);
				break;
			default: //Insert the template character
				insert = current; 
				break;
			}
			result.append(insert);
		}
		
		return result.toString();
	}
}
