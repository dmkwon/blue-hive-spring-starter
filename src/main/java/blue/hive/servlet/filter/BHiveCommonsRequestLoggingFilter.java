package blue.hive.servlet.filter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.CommonsRequestLoggingFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import blue.hive.servlet.filter.wrap.BHiveTeeHttpServletResponse;
import blue.hive.util.BHiveHostingUtil;

/**
 * Spring의 CommonsRequestLoggingFilter를
 * Apache Common Logging 대신 Slf4J 로거를 이용하여 출력하도록 변경
 *
 * Servlet Filter Matching 보다 자세한 AntPathMatcher지원.
 * Response Payload Logging 추가
 *
 * @author DongMan Kwon <a href="mailto:dmkwon@intellicode.co.kr">dmkwon@intellicode.co.kr</a>
 */
public class BHiveCommonsRequestLoggingFilter extends CommonsRequestLoggingFilter {

	private String antPatterns = "";
	private List<String> patterns = new ArrayList<String>();

	private String beforeMessagePrefix = DEFAULT_BEFORE_MESSAGE_PREFIX;
	private String beforeMessageSuffix = DEFAULT_BEFORE_MESSAGE_SUFFIX;
	private String afterMessagePrefix = DEFAULT_AFTER_MESSAGE_PREFIX;
	private String afterMessageSuffix = DEFAULT_AFTER_MESSAGE_SUFFIX;

	private boolean includeResponsePayload = false;

	public String getAntPatterns() {
		return antPatterns;
	}
	public void setAntPatterns(String antPatterns) {
		this.antPatterns = antPatterns;
	}

	public void setBeforeMessagePrefix(String beforeMessagePrefix) {
		super.setBeforeMessagePrefix(beforeMessagePrefix);
		this.beforeMessagePrefix = beforeMessagePrefix;
	}
	public void setBeforeMessageSuffix(String beforeMessageSuffix) {
		super.setBeforeMessageSuffix( beforeMessageSuffix);
		this.beforeMessageSuffix = beforeMessageSuffix;
	}
	public void setAfterMessagePrefix(String afterMessagePrefix) {
		super.setAfterMessagePrefix(afterMessagePrefix);
		this.afterMessagePrefix = afterMessagePrefix;
	}
	public void setAfterMessageSuffix(String afterMessageSuffix) {
		super.setAfterMessageSuffix(afterMessageSuffix);
		this.afterMessageSuffix = afterMessageSuffix;
	}

	public boolean isIncludeResponsePayload() {
		return includeResponsePayload;
	}
	public void setIncludeResponsePayload(boolean includeResponsePayload) {
		this.includeResponsePayload = includeResponsePayload;
	}

	public BHiveCommonsRequestLoggingFilter() {
		this.setIncludeClientInfo(true);
		this.setIncludeQueryString(true);
		this.setIncludePayload(true);
		this.setMaxPayloadLength(1024); //default:50
		this.setIncludeResponsePayload(true);

		this.setBeforeMessagePrefix("\r\n##########################################################" + "\r\n#### START: ");
		this.setBeforeMessageSuffix("");
		this.setAfterMessagePrefix("\r\n#### END: ");
		this.setAfterMessageSuffix("\r\n##########################################################");
	}

	@Override
	protected void initFilterBean() throws ServletException {
		super.initFilterBean();
		this.antPatterns = getFilterConfig().getInitParameter("antPatterns");
		if(!StringUtils.isEmpty(this.antPatterns)) {
			String[] tokens = StringUtils.tokenizeToStringArray(this.antPatterns, ",");
			for (String pattern : tokens) {
				this.patterns.add(pattern);
			}
		}
		logger.debug("###### BHiveCommonsRequestLoggingFilter - patterns: " + this.patterns);
	}

	@Override
	protected boolean shouldLog(HttpServletRequest request) {
		if(logger.isDebugEnabled()) {
			if(this.patterns.size() > 0) {
				String servletPath = request.getServletPath();
				AntPathMatcher antPathMatcher = new AntPathMatcher();
				for (String pattern : this.patterns) {
					boolean isMatch = antPathMatcher.match(pattern, servletPath);
					if(isMatch == true) {
						return true;
					}
				}
			} 
				
			String path = request.getServletPath();
			path = path.toLowerCase();
			if(path.endsWith(".js") ||
				path.endsWith(".css") ||
				path.endsWith(".png") ||
				path.endsWith(".jpg") ||
				path.endsWith(".gif") ||
				path.endsWith(".map")
				) {
				return false;
			}
			return true; // has no patterns
		}
		return false; //logger debug disabled
	}

	/**
	 * Writes a log message before the request is processed.
	 */
	@Override
	protected void beforeRequest(HttpServletRequest request, String message) {
		logger.debug(message);
	}

	/**
	 * Writes a log message after the request is processed.
	 */
	@Override
	protected void afterRequest(HttpServletRequest request, String message) {
		logger.debug(message);
	}

	///////////////////////////////////////////////////////////////////////
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		boolean isFirstRequest = !isAsyncDispatch(request);
		HttpServletRequest requestToUse = request;

		if (isIncludePayload() && isFirstRequest && !(request instanceof ContentCachingRequestWrapper)) {
			requestToUse = new ContentCachingRequestWrapper(request);
		}

		//dmkwon start
		HttpServletResponse responseToUse = response;
		if (isIncludeResponsePayload() && isFirstRequest && !(response instanceof BHiveTeeHttpServletResponse)) {
			responseToUse = new BHiveTeeHttpServletResponse(response);
		}
		//dmkwon end

		boolean shouldLog = shouldLog(requestToUse);
		if (shouldLog && isFirstRequest) {
			beforeRequest(requestToUse, getBeforeMessage(requestToUse));
		}
		try {
			filterChain.doFilter(requestToUse, responseToUse);
		}
		finally {
			if (shouldLog && !isAsyncStarted(requestToUse)) {
				afterRequest(requestToUse, getAfterMessage(requestToUse, responseToUse));
			}
		}
	}

	private String getBeforeMessage(HttpServletRequest request) throws IOException {
		return createMessage(request, null, this.beforeMessagePrefix, this.beforeMessageSuffix);
	}

	private String getAfterMessage(HttpServletRequest request, HttpServletResponse response) throws IOException {
		return createMessage(request, response, this.afterMessagePrefix, this.afterMessageSuffix);
	}

	protected String createMessage(HttpServletRequest request, HttpServletResponse response, String prefix, String suffix) throws IOException {
		StringBuilder msg = new StringBuilder();
		msg.append(prefix);
		msg.append("uri: ").append(request.getRequestURI());
		if (isIncludeQueryString()) {
			if(!StringUtils.isEmpty(request.getQueryString())) {
				msg.append('?').append(request.getQueryString());
			}
		}
		if (isIncludeClientInfo()) {
			//String client = request.getRemoteAddr();
			String client = BHiveHostingUtil.getClientIP(request);
			if (StringUtils.hasLength(client)) {
				msg.append("\r\n#### - client: ").append(client);
			}
			HttpSession session = request.getSession(false);
			if (session != null) {
				msg.append("\r\n#### - session: ").append(session.getId());
			}
			String user = request.getRemoteUser();
			if (user != null) {
				msg.append("\r\n#### - user: ").append(user);
			}
		}
		if (isIncludePayload() && request instanceof ContentCachingRequestWrapper) {
			ContentCachingRequestWrapper wrapper = (ContentCachingRequestWrapper) request;
			byte[] buf = wrapper.getContentAsByteArray();
			if (buf.length > 0) {
				int length = Math.min(buf.length, getMaxPayloadLength());
				String payload;
				try {
					payload = new String(buf, 0, length, wrapper.getCharacterEncoding());
				}
				catch (UnsupportedEncodingException e) {
					payload = "[unknown]";
				}
				msg.append("\r\n#### - payload: ").append(StringUtils.trimWhitespace(payload));
			}
		}

		if(isIncludeResponsePayload() && response != null && response instanceof BHiveTeeHttpServletResponse) {
			BHiveTeeHttpServletResponse wrapper = (BHiveTeeHttpServletResponse) response;
			wrapper.finish();
			String contentType = wrapper.getContentType();
			if(contentType != null) {
				msg.append("\r\n#### - responsecontenttype: ").append(contentType);
				if(contentType.contains("json") /*|| contentType.contains("html")*/) {
					byte[] buf = wrapper.getOutputBuffer();
					if (buf != null && buf.length > 0) {
						int length = Math.min(buf.length, getMaxPayloadLength());
						String payload;
						try {
							payload = new String(buf, 0, length, wrapper.getCharacterEncoding());
						}
						catch (UnsupportedEncodingException e) {
							payload = "[unknown]";
						}
						msg.append("\r\n#### - responsepayload: ").append(StringUtils.trimWhitespace(payload));
					}
				}
			}
		}

		msg.append(suffix);
		return msg.toString();
	}
}



