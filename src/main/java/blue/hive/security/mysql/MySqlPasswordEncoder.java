package blue.hive.security.mysql;

import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;

import org.apache.commons.codec.binary.Hex;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * MySQL의 password() function과 같은 Password Encoder
 * 
 * select password('qwer1234') &gt; "*D75CC763C5551A420D28A227AC294FADE26A2FF2"
 * 
 * @author DongMan Kwon <a href="mailto:dmkwon@intellicode.co.kr">dmkwon@intellicode.co.kr</a>
 */
public class MySqlPasswordEncoder implements PasswordEncoder {
	
	public String encode(CharSequence rawPassword) {
		if (rawPassword == null) {
			throw new NullPointerException();
		}
		
		try {
			byte[] rawPasswordBytes = rawPassword.toString().getBytes(Charset.forName("ASCII"));
			//sha256 해시 2pass 적용
			MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
			byte[] hashBytes = sha256.digest(rawPasswordBytes); // stage1
			hashBytes = sha256.digest(hashBytes); // stage2
			//'*' + hex로 변환
			char[] hashHexArray = Hex.encodeHex(hashBytes, false/*toLowerCase*/);
			String result = "*" + new String(hashHexArray);
			return result;
		} catch (GeneralSecurityException e) {
			throw new RuntimeException(e);
		}
	}

	public boolean matches(CharSequence rawPassword, String encodedPassword) {
		if (encodedPassword == null || rawPassword == null) {
			return false;
		}
		if (!encodedPassword.equals(encode(rawPassword))) {
			return false;
		}
		return true;
	}	

}
