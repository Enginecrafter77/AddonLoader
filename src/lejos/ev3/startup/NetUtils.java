package lejos.ev3.startup;

import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import com.ec.addonloader.main.MORegistry;

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
	public static void writeConfig(String ssid, String pwd, boolean hidden)
	{
		try
		{
			PrintWriter pw = new PrintWriter(WIFI_CONFIG);
			pw.println("ctrl_interface=/var/run/wpa_supplicant\n");
			pw.println("network={");
			pw.println("\tssid=\"" + ssid + '\"');
			pw.println("\tkey_mgmt=WPA-PSK NONE");
			if(!pwd.isEmpty())
			{
				pw.println("\tpsk=" + NetUtils.computePSK(ssid, pwd));
			}
			pw.println("\tscan_ssid=" + (hidden ? '1' : '0'));
			pw.println("}");
			pw.close();
		}
		catch(Exception e)
		{
			System.err.println("Failed to write wpa supplication configuration: " + e);
		}
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
		MORegistry.WIFI_CONNECT.setExtra(ap);
		if(MORegistry.WIFI_CONNECT.runMethodsB())
		{
			boolean hidden = false;
			if(ap.equals("[HIDDEN]"))
			{
				System.out.println("AP is hidden, requesting ESSID");
				ap = Keyboard.getString();
				hidden = true;
			}
			System.out.println("Access point is " + ap);
			
			String pwd = Keyboard.getString();
			if(pwd != null)
			{
			   	System.out.println("Password is " + (pwd.isEmpty() ? "empty" : pwd));
			   	WaitScreen.instance.begin("Restart\nWiFi\nServices");
			   	WaitScreen.instance.status("Write config");
			   	writeConfig(ap, pwd, hidden);
			   	WaitScreen.instance.status("Connecting...");
			   	connect();
			   	WaitScreen.instance.end();
			}
		}
		MORegistry.WIFI_CONNECT.runMethodsA();
	}
	
	private static String bytesToHex(byte[] buf)
	{
		int len = buf.length;
		char[] r = new char[len * 2];
		for(int i = 0; i < len; i++)
		{
			int v = buf[i];
			r[i * 2] = hexChars[(v >>> 4) & 0xF];
			r[i * 2 + 1] = hexChars[v & 0xF];
		}
		return new String(r);
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
		byte[] ss = ssid.getBytes("utf8");
		char[] pass = password.toCharArray();
		
		SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		KeySpec ks = new PBEKeySpec(pass, ss, 4096, 256);
		SecretKey s = f.generateSecret(ks);
		byte[] k = s.getEncoded();
		
		return bytesToHex(k);
	}
}
