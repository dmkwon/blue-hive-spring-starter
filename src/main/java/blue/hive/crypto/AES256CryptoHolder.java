package blue.hive.crypto;

/**
 * 암호화 처리기 기본 인스턴스 Holder
 *
 * @author DongMan Kwon <a href="mailto:dmkwon@intellicode.co.kr">dmkwon@intellicode.co.kr</a>
 */
public class AES256CryptoHolder {
	
	private static AES256Crypto instance;

	public static AES256Crypto getCrypto() {
		return instance;
	}
	
	public static void setCrypto(AES256Crypto instance) {
		AES256CryptoHolder.instance = instance;
	}

}
