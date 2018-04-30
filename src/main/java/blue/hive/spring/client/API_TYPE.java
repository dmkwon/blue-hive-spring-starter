package blue.hive.spring.client;

/**
 * 레거시 호출을 위한 RestTemplate을 사용할 때 Legacy의 통신방식
 *
 * @author DongMan Kwon <a href="mailto:dmkwon@intellicode.co.kr">dmkwon@intellicode.co.kr</a>
 */
public enum API_TYPE {

	/**
	 * 일반적인 GET이나, POST(application/x-www-form-urlencoded) 전송으로 통신, 응답은 문자열
	 */
	REQUEST_FORM,


	/**
	 * REST API 방식의 통신 application/json ContentType 사용
	 */
	REQUEST_BODY_JSON,

	/**
	 * REST API 방식의 통신 application/secured+json ContentType 사용
	 */
	REQUEST_BODY_SECURED_JSON
}
