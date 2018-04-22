package blue.hive.spring.validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.ObjectError;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * <code>org.springframework.validation.ObjectError</code>의 응답용 Wrapper DT
 *
 * @author DongMan Kwon <dmkwon@intellicode.co.kr>
 */
@JsonPropertyOrder({"objectName", "code", "message"})
public class BHiveObjectError {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	protected String objectName;
	protected String code;
	protected String message;

	public String getObjectName() {
		return objectName;
	}

	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public BHiveObjectError() {
		super();
	}

	public BHiveObjectError(ObjectError error) {
		this.objectName = error.getObjectName();
		this.code = error.getCode();
		this.message = error.getDefaultMessage();
	}

	@Override
	public String toString() {
		return "BHiveObjectError [objectName=" + objectName + ", code=" + code + ", message=" + message + "]";
	}

}
