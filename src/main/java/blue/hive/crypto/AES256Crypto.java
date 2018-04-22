package blue.hive.crypto;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import blue.hive.exception.BHiveRuntimeException;
import blue.hive.io.BHiveBase64InputStream;
import blue.hive.io.BHiveBase64OutputStream;

/**
 * 암호화 처리기 (AES256)
 *
 * @author DongMan Kwon <dmkwon@intellicode.co.kr>
 */
public class AES256Crypto {
	Logger logger = LoggerFactory.getLogger(this.getClass());

	//DEFAULTS
	private static final Charset UTF8 = Charset.forName("UTF-8");
	private static final String DEFAULT_CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";
	private static byte[] DEFAULT_IV = new byte[16];
	static {
		Arrays.fill(DEFAULT_IV, (byte)0);
	}

	//Cipher Init Parameters
	String cipherAlgorithm = null;
	String secretKey;
	byte[] iv;

	//Cipher
	SecretKeySpec secretSpec = null;
	IvParameterSpec ivSpec = null;

	public String getSecretKey() {
		return secretKey;
	}
	public void setSecretKey(String secretKey) {
		if(secretKey.length() > 24) {
			this.secretKey = org.apache.commons.lang3.StringUtils.rightPad(secretKey, 32, "\0");	
		} else if(secretKey.length() > 16) {
			this.secretKey = org.apache.commons.lang3.StringUtils.rightPad(secretKey, 24, "\0");	
		} else if(secretKey.length() > 0) {
			this.secretKey = org.apache.commons.lang3.StringUtils.rightPad(secretKey, 16, "\0");	
		}
	}

	public AES256Crypto() {
		this(DEFAULT_CIPHER_ALGORITHM, "", DEFAULT_IV);
	}

	public AES256Crypto(String secretKey) {
		this(DEFAULT_CIPHER_ALGORITHM, secretKey, DEFAULT_IV);
	}

	public AES256Crypto(String cipherAlgorithm, String secretKey) {
		this(cipherAlgorithm, secretKey, DEFAULT_IV);
	}
	
	public AES256Crypto(String cipherAlgorithm, String secretKey, byte[] iv) {
		this.cipherAlgorithm = cipherAlgorithm;
		setSecretKey(secretKey);
		this.iv = Arrays.copyOf(iv, iv.length);
	}

	public Cipher getEncryptChiper() throws Exception {
		try {
			//Init Secret
			this.secretSpec = new SecretKeySpec(this.secretKey.getBytes("UTF-8"), "AES");
			//Init IV
			this.ivSpec = new IvParameterSpec(this.iv);
			Cipher cipher = Cipher.getInstance(this.cipherAlgorithm);
			if(cipherAlgorithm.contains("ECB")) {
				cipher.init(Cipher.ENCRYPT_MODE, this.secretSpec);
			} else {
				cipher.init(Cipher.ENCRYPT_MODE, this.secretSpec, this.ivSpec);
			}
			return cipher;
		} catch (Exception ex) {
			logger.warn("getEncryptChiper FAILED. cipherAlgorithm:{}, secretKey:{}, iv:{} - EXCEPTION: {} - {}",
					cipherAlgorithm, secretKey, iv,
					ex.getClass().getName(), ex.getMessage());
			throw ex;
		}
	}
	public Cipher getDecryptChiper() throws Exception {
		try {
			//Init Secret
			this.secretSpec = new SecretKeySpec(this.secretKey.getBytes("UTF-8"), "AES");
			//Init IV
			this.ivSpec = new IvParameterSpec(this.iv);
			Cipher cipher = Cipher.getInstance(this.cipherAlgorithm);
			if(cipherAlgorithm.contains("ECB")) {
				cipher.init(Cipher.DECRYPT_MODE, this.secretSpec);
			} else {
				cipher.init(Cipher.DECRYPT_MODE, this.secretSpec, this.ivSpec);
			}
			return cipher;
		} catch (Exception ex) {
			logger.warn("getDecryptChiper FAILED. cipherAlgorithm:{}, secretKey:{}, iv:{} - EXCEPTION: {} - {}",
					cipherAlgorithm, secretKey, iv,
					ex.getClass().getName(), ex.getMessage());
			throw ex;
		}
	}

	/** 주어진 문자열을 기본(UTF-8)인코딩으로 암호화된 바이트배열 획득 */
	public byte[] encryptString(String plainString) {
		return encryptString(plainString, UTF8);
	}
	/** 주어진 문자열을 주어진 인코딩으로 암호화된 바이트배열 획득 */
	public byte[] encryptString(String plainString, Charset charset) {
		if(StringUtils.isEmpty(plainString)) {
			logger.warn("encryptString FAILED. -> plainString is empty.");
			return null;
		}
		byte[] cipherBytes = null;
		try {
			Cipher cipher = getEncryptChiper();
			byte[] plainBytes = plainString.getBytes(charset);
			if(cipherAlgorithm.contains("ECB") && plainBytes.length%16 != 0) {
				//to multiple of 16 bytes
				int size = plainBytes.length + (16 - plainBytes.length % 16);
				byte[] plainBytesPadded = new byte[size];
				Arrays.fill(plainBytesPadded, (byte) 0);
				System.arraycopy(plainBytes, 0, plainBytesPadded, 0, plainBytes.length);
				plainBytes = plainBytesPadded;
			}
			
			cipherBytes = cipher.doFinal(plainBytes);
		} catch (Exception ex) {
			logger.warn("encryptString FAILED. plainString: {} - EXCEPTION: {} - {}", plainString,
					ex.getClass().getName(), ex.getMessage());
		}
		return cipherBytes;
	}

	/** 주어진 문자열을 기본(UTF-8)인코딩으로 암호화된 바이트배열의 Base64인코딩 문자열 획득 */
	public String encryptStringToBase64(String plainString) {
		return encryptStringToBase64(plainString, UTF8);
	}
	/** 주어진 문자열을 주어진 인코딩으로 암호화된 바이트배열의 Base64인코딩 문자열 획득 */
	public String encryptStringToBase64(String plainString, Charset charset) {
		byte[] cipherBytes = encryptString(plainString, charset);
		if(cipherBytes != null) {
			logger.trace("cipherBytes: " + Arrays.toString(cipherBytes));
			byte[] cipherBytesBase64Encoded = Base64.encodeBase64(cipherBytes);
			logger.trace("cipherBytesBase64Encoded: " + Arrays.toString(cipherBytesBase64Encoded));
			return new String(cipherBytesBase64Encoded, charset);			
		}
		return plainString;
	}

	/** 주어진 OutputStream을 암호화하는 OutputStream을 생성 (Base64Encoding 적용) */
	public OutputStream getEncryptChiperOutputStream(OutputStream os) {
		return getEncryptChiperOutputStream(os, true);
	}
	/** 주어진 OutputStream을 암호화하는 OutputStream을 생성 (Base64Encoding 옵션 적용) */
	public OutputStream getEncryptChiperOutputStream(OutputStream os, boolean useBase64) {
		if(os == null) {
			throw new IllegalArgumentException("OutputStream 'os' parameter cannot be null.");
		}
		try {
			//Plain -> Chiper(Encrypt) -> Base64Encode -> OutputStream
			if(useBase64) {
				os = new BHiveBase64OutputStream(os);
			}
			OutputStream osEncrypted = new CipherOutputStream(os, getEncryptChiper());
			return osEncrypted;
		} catch (Exception ex) {
			logger.warn("getEncryptChiperOutputStream FAILED. EXCEPTION: {}", ex.getMessage(), ex);
			throw new BHiveRuntimeException("FAILED TO CREATE ENCRYPT OUTPUTSTREAM.", ex);
		}
	}

	/** 주어진 암호화된 바이트배열을 복호화하여 기본(UTF-8)인코딩의 문자열로 획득 */
	public String decryptBytes(byte[] chiperBytes) {
		return decryptBytes(chiperBytes, UTF8);
	}
	/** 주어진 암호화된 바이트배열을 복호화하여 주어진 인코딩의 문자열로 획득 */
	public String decryptBytes(byte[] chiperBytes, Charset charset) {
		if(chiperBytes == null || chiperBytes.length == 0) {
			logger.warn("decryptBytes FAILED. -> chiperBytes is empty.");
			return null;
		}
		String plainString = null;
		try {
			Cipher cipher = getDecryptChiper();
			byte[] plainBytes = cipher.doFinal(chiperBytes);
			plainString = new String(plainBytes, charset);
		} catch (Exception ex) {
			logger.warn("decryptBytes FAILED. - EXCEPTION: {} - {}", ex.getClass().getName(), ex.getMessage());
		}
		return plainString;
	}

	/** 주어진 암호화된 바이트배열을 Base64Encoding된 문자열을 Base64Decoding과 복호화하여 기본(UTF-8)인코딩 문자열로 획득 */
	public String decryptBase64String(String cipherBase64String) {
		return decryptBase64String(cipherBase64String, UTF8);
	}
	
	public String decryptBase64String(String cipherBase64String, Charset charset) {
		if(StringUtils.isEmpty(cipherBase64String)) {
			logger.warn("decryptBase64String FAILED -> cipherBase64String is empty.");
			return null;
		}
		byte[] cipherBytes = Base64.decodeBase64(cipherBase64String);
		String plainString = decryptBytes(cipherBytes, charset);
		plainString = StringUtils.trimTrailingWhitespace(plainString);
		return StringUtils.trimWhitespace(plainString);
	}

	/** 주어진 InputStream을 복호화하는 InputStream 생성 (Base64Decoding 적용) */
	public InputStream getDecryptChiperInputStream(InputStream is) {
		return getDecryptChiperInputStream(is, true);
	}
	/** 주어진 InputStream을 복호화하는 InputStream 생성 (Base64Decoding 옵션) */
	public InputStream getDecryptChiperInputStream(InputStream is, boolean useBase64) {
		if(is == null) {
			throw new IllegalArgumentException("InputStream 'is' parameter cannot be null.");
		}
		try {
			//InputStream -> Base64Decode -> Chiper(Decrypt) -> Plain
			if(useBase64) {
				is = new BHiveBase64InputStream(is);
			}
			InputStream isDecrypted = new CipherInputStream(is, getDecryptChiper());
			return isDecrypted;
		} catch (Exception ex) {
			logger.warn("getDecryptChiperInputStream FAILED. EXCEPTION: {} - {}", ex.getClass().getName(), ex.getMessage());
			throw new BHiveRuntimeException("FAILED TO CREATE DECRYPT INPUTSTREAM.", ex);
		}

	}

}
