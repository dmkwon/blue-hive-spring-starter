package blue.hive.spring.http;

import java.io.IOException;
import java.io.OutputStream;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMessage;
import org.springframework.http.HttpOutputMessage;

/**
 * HttpOutputMessage의 Body를 래핑 처리할 수 있는 클래스
 *
 * @author DongMan Kwon <a href="mailto:dmkwon@intellicode.co.kr">dmkwon@intellicode.co.kr</a>
 */
public class BHiveHttpOutputMessage implements HttpMessage, HttpOutputMessage {

	private HttpOutputMessage httpOutputMessage;
	private OutputStream customBody;

	/**
	 * 생성자
	 * @param httpOutputMessage 원본 {@link HttpOutputMessage}
	 * @param customBody 원본 HttpOutputMessage의 Body를 래핑하여 변경처리할 커스텀 Body
	 */
	public BHiveHttpOutputMessage(HttpOutputMessage httpOutputMessage, OutputStream customBody) {
		this.httpOutputMessage = httpOutputMessage;
		this.customBody = customBody;
	}

	/* (non-Javadoc)
	 * @see org.springframework.http.HttpMessage#getHeaders()
	 */
	public HttpHeaders getHeaders() {
		return httpOutputMessage.getHeaders();
	}

	/* (non-Javadoc)
	 * @see org.springframework.http.HttpOutputMessage#getBody()
	 */
	public OutputStream getBody() throws IOException {
		if(customBody == null) {
			return httpOutputMessage.getBody();
		}
		return customBody;
	}

	/**
	 * 사용자가 임의로 변경한 OutputStream body를 설정 (예. OutputStream을 CipherOutputStream으로 래핑)
	 * @param customBody OutputStream object
	 */
	public void setBody(OutputStream customBody) {
		this.customBody = customBody;
	}

}