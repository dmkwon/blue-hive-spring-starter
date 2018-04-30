package blue.hive.spring.web.multipart;


import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;

/**
 * 특정 URL패턴을 제외하는 Delegating Multipart Resolver
 * 
 * @author DongMan Kwon <a href="mailto:dmkwon@intellicode.co.kr">dmkwon@intellicode.co.kr</a>
 */
public class BHiveDelegatingMultipartResolver implements MultipartResolver, InitializingBean {
	final static Logger logger = LoggerFactory.getLogger(BHiveDelegatingMultipartResolver.class);
	
	/** Multipart 처리에서 제외할 패턴 목록 */
	private List<String> excludePatterns = new ArrayList<String>();
	
	/** 제외할 요청 경로 검사기 (기본값: AntPathMatcher) */
	private PathMatcher pathMatcher = new AntPathMatcher();
	
	/** 위임할 원본 Multipart Resolver */
	private MultipartResolver multipartResolver = null;
	
	
	/** 
	 * Multipart 처리에서 제외할 패턴 목록 
	 * @return List exclude pattern list to use
	 */
	public List<String> getExcludePatterns() {
		return excludePatterns;
	}
	/** 
	 * Multipart 처리에서 제외할 패턴 목록
	 * @param excludePatterns exclude pattern list to use
	 */
	public void setExcludePatterns(List<String> excludePatterns) {
		this.excludePatterns = excludePatterns;
	}

	/** 
	 * 제외할 요청 경로 검사기 (기본값: AntPathMatcher) 
	 * @return pathMatcher PathMatcher object
	 */
	public PathMatcher getPathMatcher() {
		return pathMatcher;
	}
	/**
	 *  제외할 요청 경로 검사기 (기본값: AntPathMatcher) 
	 *  @param pathMatcher path matcher to user
	 */
	public void setPathMatcher(PathMatcher pathMatcher) {
		this.pathMatcher = pathMatcher;
	}

	/** 
	 * 위임할 원본 Multipart Resolver
	 * @return MultipartResolver MultipartResolver object
	 */
	public MultipartResolver getMultipartResolver() {
		return multipartResolver;
	}
	/** 
	 * 위임할 원본 Multipart Resolver 
	 * @param multipartResolver MultipartResolver object to use
	 */
	public void setMultipartResolver(MultipartResolver multipartResolver) {
		this.multipartResolver = multipartResolver;
	}

	/**
	 * 기본 생성자
	 */
	public BHiveDelegatingMultipartResolver() {
		super();
	}
	
	/**
	 * 생성자
	 * @param multipartResolver 위임대상 Spring Multipart Resolver 지정
	 */
	public BHiveDelegatingMultipartResolver(MultipartResolver multipartResolver) {
		super();
		this.multipartResolver = multipartResolver;
	}
	
	/**
	 * 빈 초기화 - 설정값 검사 
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		if(multipartResolver == null) {
			logger.error("위임할 MultipartResolver가 지정되어 있지 않습니다.");
			throw new RuntimeException("위임할 MultipartResolver가 지정되어 있지 않습니다.");
		}
	}
	
	/**
	 * Multipart 처리에서 제외할 요청인지 검사 - true이면 Spring MultipartResolver에서 처리하지 않음. 
	 * InnoDS등의 솔루션에서는 해당 솔루션으로 직접 처리
	 * @param request 검사대상 요청
	 * @return MultipartResolver에서 처리를 제외할지 여부
	 */
	protected boolean isExcludeUrlPattern(HttpServletRequest request) {
		if(excludePatterns == null || excludePatterns.size() == 0) {
			return false;
		}
		String servletPath = request.getServletPath();
		for (String excludePattern : excludePatterns) {
			boolean isMatch = pathMatcher.match(excludePattern, servletPath);
			logger.trace("isExcludeUrlPattern => {}, {} => {}", excludePattern, servletPath, isMatch);
			if(isMatch) {
				logger.trace("isExcludeUrlPattern => true");
				return true;
			}
		}
		logger.trace("isExcludeUrlPattern => false");
		return false;
	}
	
	/**
	 * Determine if the given request contains multipart content.
	 * <p>Will typically check for content type "multipart/form-data", but the actually
	 * accepted requests might depend on the capabilities of the resolver implementation.
	 * @param request the servlet request to be evaluated
	 * @return whether the request contains multipart content
	 */
	@Override
	public boolean isMultipart(HttpServletRequest request) {
		if(isExcludeUrlPattern(request)) {
			return false; //Multipart가 아닌것으로 처리. Spring MultipartResolver에서 처리되지 않으므로 별도의 커스텀 처리필요 (예: InnoDS 전송모듈)
		}
		return multipartResolver.isMultipart(request);
	}

	/**
	 * Parse the given HTTP request into multipart files and parameters,
	 * and wrap the request inside a
	 * {@link org.springframework.web.multipart.MultipartHttpServletRequest}
	 * object that provides access to file descriptors and makes contained
	 * parameters accessible via the standard ServletRequest methods.
	 * @param request the servlet request to wrap (must be of a multipart content type)
	 * @return the wrapped servlet request
	 * @throws MultipartException if the servlet request is not multipart, or if
	 * implementation-specific problems are encountered (such as exceeding file size limits)
	 * @see MultipartHttpServletRequest#getFile
	 * @see MultipartHttpServletRequest#getFileNames
	 * @see MultipartHttpServletRequest#getFileMap
	 * @see javax.servlet.http.HttpServletRequest#getParameter
	 * @see javax.servlet.http.HttpServletRequest#getParameterNames
	 * @see javax.servlet.http.HttpServletRequest#getParameterMap
	 */
	@Override
	public MultipartHttpServletRequest resolveMultipart(HttpServletRequest request) throws MultipartException {
		if(isExcludeUrlPattern(request)) {
			return null;
		}
		return multipartResolver.resolveMultipart(request);
	}

	/**
	 * Cleanup any resources used for the multipart handling,
	 * like a storage for the uploaded files.
	 * @param request the request to cleanup resources for
	 */
	@Override
	public void cleanupMultipart(MultipartHttpServletRequest request) {
		if(isExcludeUrlPattern(request)) {
			return;
		}
		multipartResolver.cleanupMultipart(request);
	}

	
}
