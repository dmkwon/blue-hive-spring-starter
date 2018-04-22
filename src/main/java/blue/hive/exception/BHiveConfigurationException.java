package blue.hive.exception;

/**
 * 설정관련 오류
 * 
 * @author DongMan Kwon <dmkwon@intellicode.co.kr>
 */
public class BHiveConfigurationException extends BHiveRuntimeException {

	private static final long serialVersionUID = 7112528976573188127L;

	public BHiveConfigurationException() {
		super();
	}

	public BHiveConfigurationException(String message, Throwable cause) {
		super(message, cause);
	}

	public BHiveConfigurationException(String message) {
		super(message);
	}

	public BHiveConfigurationException(Throwable cause) {
		super(cause);
	}
	
}
