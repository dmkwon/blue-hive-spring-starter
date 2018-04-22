package blue.hive.spring.web.rest;

import java.util.List;

import blue.hive.spring.validation.BHiveFieldError;
import blue.hive.spring.web.rest.BHiveResponseEntity.REST_CALLBACK_CMD;
import blue.hive.spring.web.rest.BHiveResponseEntity.REST_RESPONSE_STATUS;

/**
 * Framework REST 공통 메시지 Builder
 *
 * @author DongMan Kwon <dmkwon@intellicode.co.kr>
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

	/** error로 응답하는 BHiveResponseEntity 빌더 획득 */
	public static BHiveResponseEntityBuilder error() {
		return new BHiveResponseEntityBuilder(REST_RESPONSE_STATUS.ERROR.toString());
	}

	/** fail로 응답하는 BHiveResponseEntity 빌더 획득 */
	public static BHiveResponseEntityBuilder fail() {
		return new BHiveResponseEntityBuilder(REST_RESPONSE_STATUS.FAIL.toString());
	}

	/** success로 응답하는 BHiveResponseEntity 빌더 획득 */
	public static BHiveResponseEntityBuilder success() {
		return new BHiveResponseEntityBuilder(REST_RESPONSE_STATUS.SUCCESS.toString());
	}

	/** 주어진 객체를 success로 응답하는 BHiveResponseEntity 획득 */
	public static <T> BHiveResponseEntity<T> success(T data) {
		return success().body(data);
	}
}
