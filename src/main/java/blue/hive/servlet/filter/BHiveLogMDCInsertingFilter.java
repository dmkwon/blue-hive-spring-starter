package blue.hive.servlet.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.MDC;

/**
 * slf4j MDC로 주요 관심사를 처리할 수 있게 연동하는 Servlet Filter
 * 
 * client.accessIP: 요청 Client IP (X-FORWARDED-FOR 대응, Localhost IPv6대응)
 * client.accessIP.prefix: client.accessIP IPv4의 맨앞쪽 Segment (127.0.0.1 => 127)
 * client.httpMethod: 요청 HTTP METHOD  
 * 
 * @author DongMan Kwon <dmkwon@intellicode.co.kr>
 */
public class BHiveLogMDCInsertingFilter implements Filter {

	private final static String MDC_ACCESS_IP = "client.accessIP";
	private final static String MDC_ACCESS_IP_PREFIX = "client.accessIP.prefix";
	private final static String MDC_CLIENT_METHOD = "client.httpMethod";

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	public void init(FilterConfig arg0) throws ServletException {
	}
	
	/* (non-Javadoc)
	 * @see javax.servlet.Filter#destroy()
	 */
	public void destroy() {
	}
	
	/* (non-Javadoc)
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		try{
			insertIntoMDC(request);
			chain.doFilter(request, response);
		} finally {
			clearMDC();
		}
	}

	
	/**
	 * 요청객체에서 필요한 값을 식별하여 MDC에 설정
	 * @param request 요청객체
	 */
	private void insertIntoMDC(ServletRequest request) {
		String ipAddress = request.getRemoteAddr();
		if (request instanceof HttpServletRequest) {
			HttpServletRequest httpServletRequest = (HttpServletRequest) request;
			String xForwardedFor = httpServletRequest.getHeader("X-FORWARDED-FOR"); //WEB을 거쳐서 WAS에 온경우 Origin IP
			if(xForwardedFor!=null && xForwardedFor.length()>0) {
				ipAddress = xForwardedFor;
			}
			MDC.put(MDC_CLIENT_METHOD, httpServletRequest.getMethod());
		}
		
		if(ipAddress!=null && ipAddress.length()>0) {
			if(ipAddress.equals("0:0:0:0:0:0:0:1")) {
				ipAddress = "127.0.0.1";
			}
			
			MDC.put(MDC_ACCESS_IP, ipAddress);
			MDC.put(MDC_ACCESS_IP_PREFIX, getClientIPPrefix(ipAddress));
		}		
	}
	
	/**
	 * IP주소에서 맨 앞 Segment 획득 (127.0.0.1 => 127)
	 * @param ipAddress IPv4주소
	 * @return IPv4주소의 맨앞 Segement
	 */
	private String getClientIPPrefix(String ipAddress) {
		if(ipAddress == null || ipAddress.equals("")) {
			return "";
		}
		int posSeperator = ipAddress.indexOf('.');
		if(posSeperator == -1) {
			return "";
		}
		String prefix = ipAddress.substring(0, posSeperator);
		return prefix;
	}

	/**
	 * 설정했던 MDC에 대해 초기화 (요청처리 종료시 호출)
	 */
	private void clearMDC() {
		MDC.remove(MDC_ACCESS_IP);
		MDC.remove(MDC_ACCESS_IP_PREFIX);
		MDC.remove(MDC_CLIENT_METHOD);
	}
}
