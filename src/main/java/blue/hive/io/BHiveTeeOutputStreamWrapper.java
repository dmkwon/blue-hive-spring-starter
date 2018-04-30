package blue.hive.io;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import org.apache.commons.io.output.TeeOutputStream;
import org.springframework.util.StringUtils;

/**
 * {@link TeeOutputStream}을 이용하여 OutputStream의 쓴 내용을 Tee처리하는 Wrapper
 *
 * @author DongMan Kwon <a href="mailto:dmkwon@intellicode.co.kr">dmkwon@intellicode.co.kr</a>
 */
public class BHiveTeeOutputStreamWrapper {

	OutputStream teeStream;
	ByteArrayOutputStream baos;

	public BHiveTeeOutputStreamWrapper(OutputStream outputStream) {
		baos = new ByteArrayOutputStream();
		this.teeStream = new TeeOutputStream(outputStream, baos);
	}

	public OutputStream getOutputStream() {
		return teeStream;
	}

	/**
	 * @return 입력내용 Bytes
	 */
	public byte[] getTeeOutputBytes() {
		return this.baos.toByteArray();
	}

	/**
	 * @return 입력내용 String (UTF-8)
	 * @throws UnsupportedEncodingException  unsupported encoding exception
	 */
	public String getTeeOutputString() throws UnsupportedEncodingException {
		return StringUtils.trimTrailingWhitespace(this.baos.toString("UTF-8"));
	}

}
