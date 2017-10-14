package lejos.ev3.startup;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import com.ec.addonloader.lib.ResourceLocation;
import com.ec.addonloader.main.MORegistry;

public class NetUtils {
	
	public static ResourceLocation wpastub;
	public static final String WIFI_CONFIG="/home/root/lejos/config/wpa_supplicant.conf";
	
	protected static void init()
	{
		wpastub = new ResourceLocation("resources/wpa_stub.txt");
	}
	
	protected static void connectAP(String ap)
	{
		MORegistry m = MORegistry.getRegistry(MORegistry.Type.WIFI_CONNECT);
		m.setExtra(ap);
		if(m.runMethodsB())
		{
			if(ap.equals("[HIDDEN]"))
			{
				System.out.println("AP is hidden, requesting ESSID");
				ap = Keyboard.getString();
			}
			System.out.println("Access point is " + ap);
			
			String pwd = Keyboard.getString();
			if(pwd != null)
			{
			   	System.out.println("Password is " + (pwd.isEmpty() ? "empty" : pwd));
			   	WaitScreen.instance.begin("Restart\nWiFi\nServices");
			   	WaitScreen.instance.status("Save configuration");
			   	connect(ap, pwd);
			   	WaitScreen.instance.end();
			}
		}
		m.runMethodsA();
	}
	
	protected static final char[] hexChars = "0123456789abcdef".toCharArray();
	
	public static void connect(String ssid, String pwd)
	{
		try
		{
			BufferedReader br = new BufferedReader(new InputStreamReader(wpastub.address().openStream()));
			PrintWriter pw = new PrintWriter(WIFI_CONFIG);
			StringBuilder buff = new StringBuilder();
			String line = new String();
			while((line = br.readLine()) != null)
			{
				buff.setLength(0);
				buff.append(line);
				addToLine(buff, "ssid=", '"' + ssid + '"');
				addToLine(buff, "key_mgmt=", pwd.isEmpty() ? "NONE" : "WPA-PSK");
				if(matchPSK(buff, pwd.isEmpty(), ssid, pwd))
				{
					continue;
				}
				
				pw.println(buff.toString());
			}
			pw.close();
			br.close();
		}
		catch(Exception e)
		{
			System.err.println("Failed to write wpa supplication configuration: " + e);
		}
		MainMenu.self.startNetwork(Reference.START_WLAN, true);
	}
	
	private static void addToLine(StringBuilder line, String search, String append)
	{
		if(line.indexOf(search) >= 0)
		{
			line.append(append);
		}
	}
	
	private static boolean matchPSK(StringBuilder line, boolean isNoPWD, String ssid, String pwd) throws Exception
	{
		if(line.indexOf("psk=") >= 0)
		{
			if(isNoPWD)
			{
				return true;
			}
			else
			{
				line.append(computePSK(ssid, pwd));
				return false;
			}
		}
		else
		{
			return false;
		}
	}
	
	public static String bytesToHex(byte[] buf)
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
	
	 private static String computePSK(String ssid, String password) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeySpecException
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
