package blue.hive.constant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;

public class BHiveConstant {
	static Logger logger = LoggerFactory.getLogger(BHiveConstant.class);

	public final static String APPLICATION_JSON_VALUE = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8";
	public final static MediaType APPLICATION_JSON = MediaType.valueOf(APPLICATION_JSON_VALUE);
	public final static String TEXT_HTML_VALUE = MediaType.TEXT_HTML_VALUE + ";charset=utf-8";
	public final static MediaType TEXT_HTML = MediaType.valueOf(TEXT_HTML_VALUE);

	/** Spring REST API RequestMapping의 Produces 기본 값 */
	//public final static String API_PRODUCES = APPLICATION_JSON_VALUE; 
	//IE 9에서 json을 iframe transport로 전송시 파일로 저장하려는 버그 발생. APP은 무관
	public final static String API_PRODUCES = TEXT_HTML_VALUE;

	/** Spring REST API RequestMapping의 Produces MediaType 기본 값 */
	public final static MediaType API_PRODUCES_MEDIATYPE = MediaType.valueOf(API_PRODUCES);
}
