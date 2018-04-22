package blue.hive.spring.http.converter;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpOutputMessage;

/**
 * Implementation of {@link org.springframework.http.HttpOutputMessage} used
 * to write a MIME multipart.
 */
public class MultipartHttpOutputMessage implements HttpOutputMessage {

	private final OutputStream outputStream;

	private final HttpHeaders headers = new HttpHeaders();

	private boolean headersWritten = false;

	public MultipartHttpOutputMessage(OutputStream outputStream) {
		this.outputStream = outputStream;
	}

	public HttpHeaders getHeaders() {
		return (this.headersWritten ? HttpHeaders.readOnlyHttpHeaders(this.headers) : this.headers);
	}

	public OutputStream getBody() throws IOException {
		writeHeaders();
		return this.outputStream;
	}

	private void writeHeaders() throws IOException {
		if (!this.headersWritten) {
			for (Map.Entry<String, List<String>> entry : this.headers.entrySet()) {
				byte[] headerName = getAsciiBytes(entry.getKey());
				for (String headerValueString : entry.getValue()) {
					byte[] headerValue = getAsciiBytes(headerValueString);
					this.outputStream.write(headerName);
					this.outputStream.write(':');
					this.outputStream.write(' ');
					this.outputStream.write(headerValue);
					this.outputStream.write('\r');
					this.outputStream.write('\n');
				}
			}
			this.outputStream.write('\r');
			this.outputStream.write('\n');
			this.headersWritten = true;
		}
	}

	private byte[] getAsciiBytes(String name) {
		try {
			return name.getBytes("US-ASCII");
		}
		catch (UnsupportedEncodingException ex) {
			// Should not happen - US-ASCII is always supported.
			throw new IllegalStateException(ex);
		}
	}
}