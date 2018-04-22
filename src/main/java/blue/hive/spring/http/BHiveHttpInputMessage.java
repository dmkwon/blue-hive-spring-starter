package blue.hive.spring.http;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpMessage;

/**
 * HttpInputMessage의 Body를 래핑 처리할 수 있는 클래스
 *
 * @author DongMan Kwon <dmkwon@intellicode.co.kr>
 * @see {@link HttpMessage}, {@link HttpInputMessage}
 */
public class BHiveHttpInputMessage implements HttpMessage, HttpInputMessage {

	private HttpInputMessage httpInputMessage;
	private InputStream customBody;

	/**
	 * 생성자
	 * @param httpInputMessage 원본 {@link HttpInputMessage}
	 * @param customBody 원본 HttpInputMessage의 Body를 래핑하여 변경처리할 커스텀 Body
	 */
	public BHiveHttpInputMessage(HttpInputMessage httpInputMessage, InputStream customBody) {
		this.httpInputMessage = httpInputMessage;
		this.customBody = customBody;
	}

	/* (non-Javadoc)
	 * @see org.springframework.http.HttpMessage#getHeaders()
	 */
	public HttpHeaders getHeaders() {
		return httpInputMessage.getHeaders();
	}

	/* (non-Javadoc)
	 * @see org.springframework.http.HttpInputMessage#getBody()
	 */
	public InputStream getBody() throws IOException {
		if(customBody == null) {
			return httpInputMessage.getBody();
		}
		return customBody;
	}

	/**
	 * 사용자가 임의로 변경한 InputStream body를 설정 (예. InputStream을 CipherInputStream으로 래핑)
	 */
	public void setBody(InputStream customBody) {
		this.customBody = customBody;
	}

}
