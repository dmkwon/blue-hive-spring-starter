package blue.hive.spring.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import org.apache.commons.io.output.TeeOutputStream;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpOutputMessage;
import org.springframework.util.StringUtils;

/**
 * HttpOutputMessage을 Tee처리하여 출력내용을
 * 내부 ByteArray에 저장하여 출력등에 사용할 수 있는 유틸 클래스
 *
 * @author DongMan Kwon <dmkwon@intellicode.co.kr>
 */
public class BHiveTeeHttpOutputMessage implements HttpOutputMessage {

	HttpOutputMessage httpOutputMessage;
	OutputStream customBodyStream;
	ByteArrayOutputStream baos;

	public BHiveTeeHttpOutputMessage(HttpOutputMessage httpOutputMessage) throws IOException {
		this.httpOutputMessage = httpOutputMessage;
		baos = new ByteArrayOutputStream();
		customBodyStream = new TeeOutputStream(httpOutputMessage.getBody(), baos);
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
		return customBodyStream;
	}

	/**
	 * @return 출력내용 Bytes
	 */
	public byte[] getTeeOutputBytes() {
		return this.baos.toByteArray();
	}

	/**
	 * @return 출력내용 String (UTF-8)
	 * @throws UnsupportedEncodingException
	 */
	public String getTeeOutputString() throws UnsupportedEncodingException {
		return StringUtils.trimTrailingWhitespace(this.baos.toString("UTF-8"));
	}

}
