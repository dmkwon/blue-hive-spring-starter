package blue.hive.exception;

/**
 * Framework Runtime Exception
 * 
 * @author DongMan Kwon <dmkwon@intellicode.co.kr>
 */
public class BHiveRuntimeException extends RuntimeException {

	private static final long serialVersionUID = -1811214606343796458L;
	
	public BHiveRuntimeException() {
		super();
	}

	public BHiveRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

	public BHiveRuntimeException(String message) {
		super(message);
	}

	public BHiveRuntimeException(Throwable cause) {
		super(cause);
	}

}
