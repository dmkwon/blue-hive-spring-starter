package blue.hive.exception;

/**
 * IO 처리 관련 예외
 * 
 * @author DongMan Kwon <dmkwon@intellicode.co.kr>
 */
public class BHiveIOException extends BHiveRuntimeException {

	private static final long serialVersionUID = 5632244228482978109L;

	public BHiveIOException() {
		super();
	}

	public BHiveIOException(String message, Throwable cause) {
		super(message, cause);
	}

	public BHiveIOException(String message) {
		super(message);
	}

	public BHiveIOException(Throwable cause) {
		super(cause);
	}	
}
