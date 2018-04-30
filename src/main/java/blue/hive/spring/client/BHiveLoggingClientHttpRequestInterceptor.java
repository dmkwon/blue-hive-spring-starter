package blue.hive.spring.client;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import blue.hive.util.BHiveConvertUtil;
import blue.hive.util.BHiveIOUtil;

/**
 * Request Interceptor for logging (request info)
 * 
 * @author DongMan Kwon <a href="mailto:dmkwon@intellicode.co.kr">dmkwon@intellicode.co.kr</a>
 */

public class BHiveLoggingClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {
	Logger logger = LoggerFactory.getLogger(this.getClass());

	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
		ClientHttpResponse response = null;
		try {
			response = execution.execute(request, body);
			if(logger.isDebugEnabled()) {
				String responseBody = BHiveIOUtil.readStringToEnd(response.getBody());
				logger.info(""
						+ "\r\n\t\t>>>> REQUEST: {} {}"
						+ "\r\n\t\t>>>>  - header: {}"
						+ "\r\n\t\t>>>>  - body: {} "
						+ "\r\n\t\t<<<< RESPONE: status {}, "
						+ "\r\n\t\t<<<<  - header{}, "
						+ "\r\n\t\t<<<<  - body:{}",
						request.getMethod(), request.getURI(),
						request.getHeaders(),
						BHiveConvertUtil.convertToString(body),
						response.getStatusCode(),
						response.getHeaders(),
						responseBody);
			}
		} catch (Exception ex) {
			if(logger.isDebugEnabled()) {
				HttpStatus responseStatus = null;
				HttpHeaders responseHeaders = null;
				String responseBody = null;
				if(response != null) {
					responseStatus = response.getStatusCode();
					responseHeaders = response.getHeaders();
					try {
						responseBody = BHiveIOUtil.readStringToEnd(response.getBody());
					} catch (IOException e) {
						responseBody = "<FAILED TO READ>";
					}
				}
				logger.warn("HTTP EXCEPTION: {}"
						+ "\r\n\t\t>>>> REQUEST: {} {}"
						+ "\r\n\t\t>>>>  - header: {}"
						+ "\r\n\t\t>>>>  - body: {} "
						+ "\r\n\t\t<<<< RESPONE: status: {}, "
						+ "\r\n\t\t<<<<  - header: {}, "
						+ "\r\n\t\t<<<<  - body:{}",
						ex.getMessage(),
						request.getMethod(), request.getURI(),
						request.getHeaders(),
						BHiveConvertUtil.convertToString(body),
						responseStatus,
						responseHeaders,
						responseBody);
			}
			throw new IOException(ex);
		}
		return response;
	}
}
