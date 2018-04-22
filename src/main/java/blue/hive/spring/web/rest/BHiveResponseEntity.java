package blue.hive.spring.web.rest;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonView;

import blue.hive.spring.validation.BHiveFieldError;
import blue.hive.spring.web.rest.BHiveView.BaseView;

/**
 * Framework REST 공통 메시지
 *
 * @author DongMan Kwon <dmkwon@intellicode.co.kr>
 */
public class BHiveResponseEntity<T> {
	
	public enum REST_CALLBACK_CMD {
		LOGINREQIURED,
		REDIRECT,
		ALERT
	}
	
	public enum REST_RESPONSE_STATUS {
		SUCCESS, ERROR, FAIL
	}
	
	@JsonView(BaseView.class)
	protected String status = REST_RESPONSE_STATUS.SUCCESS.toString();
	@JsonView(BaseView.class)
	protected String msg = null;
	@JsonView(BaseView.class)
	protected String verboseMsg = null;
	@JsonView(BaseView.class)
	protected String cbCmd = null;
	@JsonView(BaseView.class)
	protected Object cbParam = null;
	@JsonView(BaseView.class)
	protected T body;
	@JsonView(BaseView.class)
	protected List<BHiveFieldError> fieldError = null;

	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getVerboseMsg() {
		return verboseMsg;
	}
	public void setVerboseMsg(String verboseMsg) {
		this.verboseMsg = verboseMsg;
	}
	public String getCbCmd() {
		return cbCmd;
	}
	public void setCbCmd(REST_CALLBACK_CMD cbCmd) {
		this.cbCmd = cbCmd.toString();
	}
	public Object getCbParam() {
		return cbParam;
	}
	public void setCbParam(Object cbParam) {
		this.cbParam = cbParam;
	}
	public T getBody() {
		return body;
	}
	public void setBody(T body) {
		this.body = body;
	}
	public List<BHiveFieldError> getFieldError() {
		return fieldError;
	}
	public void setFieldError(List<BHiveFieldError> fieldError) {
		this.fieldError = fieldError;
	}

	/**	성공 응답 생성 */
	public BHiveResponseEntity() {
		super();
	}

	/**	성공 응답 생성 with Body */
	public BHiveResponseEntity(T body) {
		super();
		this.body = body;
	}

	/**	응답 생성 with Status, Msg, cbCmd, cbParam */
	public BHiveResponseEntity(String status, String msg, REST_CALLBACK_CMD cbCmd, Object cbParam) {
		super();
		this.status = status;
		this.msg = msg;
		this.cbCmd = (cbCmd != null) ? cbCmd.toString().toLowerCase() : null;
		this.cbParam = cbParam;
	}

	/** 생성자 **/
	public BHiveResponseEntity(String status, String msg, REST_CALLBACK_CMD cbCmd, Object cbParam, List<BHiveFieldError> fieldError) {
		super();
		this.status = status;
		this.msg = msg;
		this.cbCmd = (cbCmd != null) ? cbCmd.toString().toLowerCase() : null;
		this.cbParam = cbParam;
		this.fieldError = fieldError;
	}

	/** 생성자 **/
	public BHiveResponseEntity(String status, String msg, REST_CALLBACK_CMD cbCmd, Object cbParam, T body, List<BHiveFieldError> fieldError) {
		super();
		this.status = status;
		this.msg = msg;
		this.cbCmd = (cbCmd != null) ? cbCmd.toString().toLowerCase() : null;
		this.cbParam = cbParam;
		this.body = body;
		this.fieldError = fieldError;
	}

	@Override
	public String toString() {
		return "BHiveResponseEntity [status=" + status + ", msg=" + msg + ", verboseMsg=" + verboseMsg + ", cbCmd=" + cbCmd + ", cbParam=" + cbParam + ", body=" + body + ", fieldError=" + fieldError + "]";
	}


}
