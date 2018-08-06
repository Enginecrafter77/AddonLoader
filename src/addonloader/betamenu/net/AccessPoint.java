package addonloader.betamenu.net;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class AccessPoint implements Serializable {
	
	private static final long serialVersionUID = 6884833936552476063L;
	private static final char[] hex_chars = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
	
	public final String essid;
	public final boolean visible;
	
	/**
	 * Constructs instance of classic AP, either visible or not.
	 * Should be only used by {@link ConnectionManager}.
	 * @param essid ESSID of the AP
	 * @param hidden If the wifi dev should scan for the AP.
	 */
	protected AccessPoint(String essid, boolean visible)
	{
		this.essid = essid;
		this.visible = visible;
	}
	
	/**
	 * Constructs instance of hidden AP.
	 * @param essid ESSID of the AP
	 */
	public AccessPoint(String essid)
	{
		this.essid = essid;
		this.visible = false;
	}
	
	public String compute_psk(String password) throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeySpecException
	{
		byte[] ssid_bytes = this.essid.getBytes("utf8");
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
			result[i * 2] = hex_chars[(v >>> 4) & 0xF];
			result[i * 2 + 1] = hex_chars[v & 0xF];
		}
		return new String(result);
	}
	
}
