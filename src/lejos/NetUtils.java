package lejos;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import addonloader.menu.HookRegistry;
import addonloader.menu.InputMethod;

/**
 * Class written to store various network-related methods.
 * @author Enginecrafter77
 */
public class NetUtils {
	
	public static final String WIFI_CONFIG="/home/root/lejos/config/wpa_supplicant.conf";
	private static final char[] hexChars = "0123456789abcdef".toCharArray();
	
	/**
	 * Writes SSID and password converted to PSK to WPA supplicant file.
	 * @param ssid The name of the network (SSID)
	 * @param pwd The password of the network, if any
	 */
	public static void writeConfig(String ssid, String psk, boolean hidden) throws IOException
	{
		PrintWriter pw = new PrintWriter(WIFI_CONFIG);
		pw.println("ctrl_interface=/var/run/wpa_supplicant\n");
		pw.println("network={");
		pw.println("\tssid=\"" + ssid + '\"');
		pw.println("\tkey_mgmt=WPA-PSK NONE");
		if(psk != null) pw.println("\tpsk=" + psk);
		pw.println("\tscan_ssid=" + (hidden ? '1' : '0'));
		pw.println("}");
		pw.close();
	}
	
	/**
	 * Restarts the network, thus connecting using wpa_supplicant file.
	 * Simply wrapper for MainMenu.self.startNetwrok(Reference.START_WLAN, true);
	 */
	public static void connect()
	{
		MainMenu.self.startNetwork(Reference.START_WLAN, true);
	}
	
	/**
	 * Used internally to connect to wifi.
	 * @param ap
	 */
	protected static void connectAP(String ap)
	{
		HookRegistry.WIFI_CONNECT.loadCarrier(ap);
		if(HookRegistry.WIFI_CONNECT.runHooks(0) > 0) return;
		HookRegistry.WIFI_CONNECT.runHooks(1);
		boolean hidden = false;
		if(ap.equals("[HIDDEN]"))
		{
			System.out.println("AP is hidden, requesting ESSID");
			ap = InputMethod.current.call();
			hidden = true;
		}
		System.out.println("Access point is " + ap);
		
		String pwd = InputMethod.current.call();
		if(pwd != null)
		{
			System.out.println("Password is " + (pwd.isEmpty() ? "empty" : pwd));
		   	WaitScreen.instance.begin("Restart\nWiFi\nServices");
		   	WaitScreen.instance.status("Write config");
		   	try
		   	{
		   		writeConfig(ap, computePSK(ap, pwd), hidden);
		   	}
		   	catch(Exception exc)
		   	{
		   		System.err.println(String.format("[ERROR] Failed writing WPA configuration. (%s)", exc.getClass().getName()));
		   		return;
		   	}
		   	WaitScreen.instance.status("Connecting...");
		   	connect();
		   	WaitScreen.instance.end();
		}
		HookRegistry.WIFI_CONNECT.runHooks(2);
	}
	
	/**
	 * Computes PSK from SSID and password. Used when writing configuration to wpa_supplicant.
	 * @param ssid The network name (SSID)
	 * @param password Password
	 * @return Computed PSK ready to be written in the config.
	 * @throws UnsupportedEncodingException IDK why
	 * @throws NoSuchAlgorithmException IDK why
	 * @throws InvalidKeySpecException IDK why
	 */
	public static String computePSK(String ssid, String password) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeySpecException
	{
		byte[] ssid_bytes = ssid.getBytes("utf8");
		char[] pass = password.toCharArray();
		
		SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		KeySpec ks = new PBEKeySpec(pass, ssid_bytes, 4096, 256);
		SecretKey s = f.generateSecret(ks);
		byte[] buf = s.getEncoded();
		
		int len = buf.length;
		char[] result = new char[len * 2];
		for(int i = 0; i < len; i++)
		{
			int v = buf[i];
			result[i * 2] = hexChars[(v >>> 4) & 0xF];
			result[i * 2 + 1] = hexChars[v & 0xF];
		}
		return new String(result);
	}
}
