package blue.hive.exception;


import org.springframework.validation.Errors;

/**
 * ablecoms framework validation exception
 *
 * BindingResult Error로 예외 발생을 위한 클래스
 *
 * @author DongMan Kwon <a href="mailto:dmkwon@intellicode.co.kr">dmkwon@intellicode.co.kr</a>
 */
public class BHiveValidationException extends BHiveRuntimeException {

	private static final long serialVersionUID = 4015703672117080731L;

	protected Errors errors;

	public Errors getErrors() {
		return errors;
	}

	public BHiveValidationException(Errors errors) {
		super();
		this.errors = errors;
	}

	public BHiveValidationException(String message) {
		super(message);
	}

	public BHiveValidationException(String message, Errors errors) {
		super(message);
		this.errors = errors;
	}

	@Override
	public String toString() {
		return "BHiveValidationException [message=" + getMessage() + ", errors=" + errors + "]";
	}
}
