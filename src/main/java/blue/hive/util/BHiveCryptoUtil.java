package blue.hive.util;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 암복호화 관련 유틸
 *
 * @author DongMan Kwon <dmkwon@intellicode.co.kr>
 */
public class BHiveCryptoUtil {
	protected static Logger logger = LoggerFactory.getLogger(BHiveCryptoUtil.class);

	private static String key = "1234567890123456";
	protected final static String initialVector = "1234567890123456";

	public static String encode(byte[] data) {
		if (data == null) {	
			return null;
		}
		byte[] result = null;
		String iv = initialVector;
		Cipher cipher = null;
		SecretKeySpec keyspec = null;
		IvParameterSpec ivspec = null;
		try {
			keyspec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
			ivspec  = new IvParameterSpec(iv.getBytes("UTF-8"));
			cipher  = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);
			result = cipher.doFinal(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String resStr = Base64.encodeBase64String(result);
		return resStr;
	}

	public static byte[] decode(byte[] data, String input_key) {
		if (data.length == 0) {
			return null;
		}
		byte[] result = null;
		String iv = initialVector;
		Cipher cipher = null;
		SecretKeySpec keyspec = null;
		IvParameterSpec ivspec = null;
		try {
			data = Base64.decodeBase64(data);
			keyspec = new SecretKeySpec(input_key.getBytes("UTF-8"), "AES");
			ivspec  = new IvParameterSpec(iv.getBytes("UTF-8"));
			cipher  = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE,  keyspec, ivspec);
			result = cipher.doFinal(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
