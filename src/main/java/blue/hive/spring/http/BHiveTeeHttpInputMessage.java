package blue.hive.spring.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.apache.commons.io.input.TeeInputStream;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.util.StringUtils;

/**
 * HttpInputMessage을 Tee처리하여 입력내용을
 * 내부 ByteArray에 저장하여 출력등에 사용할 수 있는 유틸 클래스
 *
 * @author DongMan Kwon <dmkwon@intellicode.co.kr>
 */
public class BHiveTeeHttpInputMessage implements HttpInputMessage {

	HttpInputMessage httpInputMessage;
	InputStream customBodyStream;
	ByteArrayOutputStream baos;

	public BHiveTeeHttpInputMessage(HttpInputMessage httpInputMessage) throws IOException {
		this.httpInputMessage = httpInputMessage;
		baos = new ByteArrayOutputStream();
		customBodyStream = new TeeInputStream(httpInputMessage.getBody(), baos);
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
		return customBodyStream;
	}

	/**
	 * @return 입력내용 Bytes
	 */
	public byte[] getTeeInputBytes() {
		return this.baos.toByteArray();
	}

	/**
	 * @return 입력내용 String (UTF-8)
	 * @throws UnsupportedEncodingException
	 */
	public String getTeeInputString() throws UnsupportedEncodingException {
		return StringUtils.trimTrailingWhitespace(this.baos.toString("UTF-8"));
	}

}
