package blue.hive.exception;

/**
 * 입력형식의 Format이 올바르지 않은 경우의 예외
 * 
 * @author DongMan Kwon <a href="mailto:dmkwon@intellicode.co.kr">dmkwon@intellicode.co.kr</a>
 */
public class BHiveInvalidFormatException extends BHiveRuntimeException {
	
	private static final long serialVersionUID = -432647972322788447L;

	public BHiveInvalidFormatException() {
		super();
	}

	public BHiveInvalidFormatException(String message, Throwable cause) {
		super(message, cause);
	}

	public BHiveInvalidFormatException(String message) {
		super(message);
	}

	public BHiveInvalidFormatException(Throwable cause) {
		super(cause);
	}	
	
}
