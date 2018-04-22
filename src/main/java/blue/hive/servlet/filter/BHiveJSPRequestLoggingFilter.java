package blue.hive.servlet.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.WebUtils;

/**
 * Logging Filter for all JSP request
 *
 * @author DongMan Kwon <dmkwon@intellicode.co.kr>
 */
public class BHiveJSPRequestLoggingFilter implements Filter {

	/** Logger available to subclasses */
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	//protected FilterConfig config;

	public void init(FilterConfig config) throws ServletException {
		//this.config = config;
	}

	public void destroy() {}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		try{
			ServletResponse responseWrapper = response;

			if(logger.isDebugEnabled()) {
				logger.debug("\r\n\t##################################################"
						+ "\r\n\t#### START JSP - " + getServletPathName(request)
		                + "\r\n\t\trequest parameter: " + getLoggableRequestMap(request));
			}

			chain.doFilter(request, response);

			if(logger.isDebugEnabled()) {
				logger.debug("\r\n\t#### END JSP - " + getServletPathName(request)
						+ "\r\n\t##################################################"
		        		+ getLoggableResponseBody(responseWrapper));
			}

		} catch(Exception ex) {
			if(logger.isDebugEnabled()) {
	    		logger.error("{} FAILED.\r\nex message: {}", getServletPathName(request), ex.toString(), ex);
	    	}
		}
	}

	private String getServletPathName(ServletRequest request) {
		if(request instanceof HttpServletRequest) {
			HttpServletRequest httpRequest = (HttpServletRequest)request;
			return httpRequest.getServletPath();
		}
		return request.toString();
	}

	private String getLoggableResponseBody(ServletResponse response) {
		HashMap<String, String> map = new HashMap<String, String>();

		String contentType = response.getContentType();
		map.put("contentType", contentType);

		HttpServletResponse httpRequest = WebUtils.getNativeResponse(response, HttpServletResponse.class);
		if(httpRequest != null) {
			int status = httpRequest.getStatus();
			map.put("StatusCode", String.valueOf(status));
		}

		StringBuilder sb = new StringBuilder();
		for(Entry<String, String> item : map.entrySet()) {
			sb.append("\r\n\t\t- " + item.getKey() + ": " + item.getValue());
		}
		return sb.toString();
	}

	/**
	 * 요청객체의 파라미터를 로깅이 가능하도록 Map<String, String> 형태로 반환
	 *
	 * request.getParameterMap()은 Map<String, String[]>인데 Map<String, String>으로 로깅이 가능하게 변환하여 반환
	 *
	 * @param request 요청 객체
	 * @return Map<String, String> 형태의 요청 파라미터맵
	 */
	public static Map<String, String> getLoggableRequestMap(ServletRequest request) {
		Map<String, String> result = new HashMap<String, String>();
		Map<String, String[]> parameterMap = request.getParameterMap();
		String key;
		String[] value;
		for(Entry<String, String[]> item : parameterMap.entrySet()) {
			key = item.getKey();
			value = item.getValue();
			result.put(key, Arrays.asList(value).toString());
		}
		return result;
	}
}

