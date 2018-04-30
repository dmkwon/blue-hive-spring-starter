package blue.hive.exception;

/**
 * 변환 관련 오류
 * 
 * @author DongMan Kwon <a href="mailto:dmkwon@intellicode.co.kr">dmkwon@intellicode.co.kr</a>
 */
public class BHiveConvertException extends BHiveRuntimeException {

	private static final long serialVersionUID = 5476430130572956410L;

	public BHiveConvertException() {
		super();
	}

	public BHiveConvertException(String message, Throwable cause) {
		super(message, cause);
	}

	public BHiveConvertException(String message) {
		super(message);
	}

	public BHiveConvertException(Throwable cause) {
		super(cause);
	}	
}