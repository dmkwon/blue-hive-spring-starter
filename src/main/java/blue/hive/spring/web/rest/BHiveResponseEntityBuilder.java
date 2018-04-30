package blue.hive.spring.web.rest;

import java.util.List;

import blue.hive.spring.validation.BHiveFieldError;
import blue.hive.spring.web.rest.BHiveResponseEntity.REST_CALLBACK_CMD;
import blue.hive.spring.web.rest.BHiveResponseEntity.REST_RESPONSE_STATUS;

/**
 * Framework REST 공통 메시지 Builder
 *
 * @author DongMan Kwon <a href="mailto:dmkwon@intellicode.co.kr">dmkwon@intellicode.co.kr</a>
 */
public class BHiveResponseEntityBuilder {

	protected String status = REST_RESPONSE_STATUS.SUCCESS.toString();
	protected String msg = null;
	protected REST_CALLBACK_CMD cbCmd = null;
	protected Object cbParam = null;
	protected Object body = null;
	protected List<BHiveFieldError> fieldError = null;

	public BHiveResponseEntityBuilder() {
		super();
	}

	public BHiveResponseEntityBuilder(String status) {
		super();
		this.status = status;
	}

	public BHiveResponseEntityBuilder result(String status) {
		this.status = status;
		return this;
	}

	public BHiveResponseEntityBuilder msg(String msg) {
		this.msg = msg;
		return this;
	}

	public BHiveResponseEntityBuilder callback(REST_CALLBACK_CMD cbCmd, String cbParam) {
		this.cbCmd = cbCmd;
		this.cbParam = cbParam;
		return this;
	}

	public BHiveResponseEntityBuilder fieldError(List<BHiveFieldError> fieldError) {
		this.fieldError = fieldError;
		return this;
	}

	public BHiveResponseEntity<Object> build() {
		return new BHiveResponseEntity<Object>(this.status, this.msg, this.cbCmd, this.cbParam, this.body, this.fieldError);
	}

	public <T> BHiveResponseEntity<T> body(T body) {
		this.body = body;
		return new BHiveResponseEntity<T>(this.status, this.msg, this.cbCmd, this.cbParam, body, this.fieldError);
	}

	public static BHiveResponseEntityBuilder error() {
		return new BHiveResponseEntityBuilder(REST_RESPONSE_STATUS.ERROR.toString());
	}

	public static BHiveResponseEntityBuilder fail() {
		return new BHiveResponseEntityBuilder(REST_RESPONSE_STATUS.FAIL.toString());
	}

	public static BHiveResponseEntityBuilder success() {
		return new BHiveResponseEntityBuilder(REST_RESPONSE_STATUS.SUCCESS.toString());
	}

	public static <T> BHiveResponseEntity<T> success(T data) {
		return success().body(data);
	}
}
