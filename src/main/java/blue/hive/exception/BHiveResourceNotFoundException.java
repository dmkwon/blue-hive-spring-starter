package blue.hive.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author DongMan Kwon <dmkwon@intellicode.co.kr>
 */

@ResponseStatus(value= HttpStatus.NOT_FOUND)
public class BHiveResourceNotFoundException extends BHiveRuntimeException {

	private static final long serialVersionUID = 9098427096055189463L;

	public BHiveResourceNotFoundException() {
		super();
	}

	public BHiveResourceNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public BHiveResourceNotFoundException(String message) {
		super(message);
	}

	public BHiveResourceNotFoundException(Throwable cause) {
		super(cause);
	}
	
}
