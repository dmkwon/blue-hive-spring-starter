package blue.hive.servlet.filter.wrap;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.util.StringUtils;

/**
 * Blue Hive TeeHttpServletResponse. Logback Access TeeHttpServletResponse 소스 참고
 *
 * @author DongMan Kwon <a href="mailto:dmkwon@intellicode.co.kr">dmkwon@intellicode.co.kr</a>
 */
public class BHiveTeeHttpServletResponse extends HttpServletResponseWrapper {

	BHiveTeeServletOutputStream teeServletOutputStream;
	PrintWriter teeWriter;

	public BHiveTeeHttpServletResponse(HttpServletResponse httpServletResponse) {
		super(httpServletResponse);
	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		if (teeServletOutputStream == null) {
			teeServletOutputStream = new BHiveTeeServletOutputStream(this.getResponse());
		}
		return teeServletOutputStream;
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		if (this.teeWriter == null) {
			this.teeWriter = new PrintWriter(new OutputStreamWriter(getOutputStream(), "UTF-8"), true);
		}
		return this.teeWriter;
	}

	@Override
	public void flushBuffer() {
		if (this.teeWriter != null) {
			this.teeWriter.flush();
		}
	}

	/**
	 * Tee로 저장된 내용을 획득
	 * @return byte[] byte array
	 */
	public byte[] getOutputBuffer() {
		// teeServletOutputStream can be null if the getOutputStream method is
		// never
		// called.
		if (teeServletOutputStream != null) {
			return teeServletOutputStream.getOutputStreamAsByteArray();
		} else {
			return null;
		}
	}

	public void finish() throws IOException {
		if (this.teeWriter != null) {
			this.teeWriter.close();
		}
		if (this.teeServletOutputStream != null) {
			this.teeServletOutputStream.close();
		}
	}

	/**
	 * Tee로 저장된 byte[]를 UTF-8 문자열로 획득 (Trim처리)
	 * @return String Tee output String value
	 */
	public String getTeeOutputString() {
		byte[] buffer = getOutputBuffer();
		if(ArrayUtils.isEmpty(buffer)) {
			return "";
		}
		return StringUtils.trimTrailingWhitespace(new String(buffer, Charset.forName("UTF-8")));
	}
}
