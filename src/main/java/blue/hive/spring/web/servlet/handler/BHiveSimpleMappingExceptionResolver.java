package blue.hive.spring.web.servlet.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

/**
 * SimpleMappingExceptionResolver의 확장 클래스
 * 
 * 로깅 기능 추가
 * 
 * @author DongMan Kwon <dmkwon@intellicode.co.kr>
 */
public class BHiveSimpleMappingExceptionResolver extends SimpleMappingExceptionResolver implements MessageSourceAware {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	//protected MessageSource messageSource;

	@Override
	public void setMessageSource(MessageSource messageSource) {
		//this.messageSource = messageSource;
	}

	@Override
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
		if (logger.isErrorEnabled()) {
			logger.error("URL: {}, ex: {}", request.getRequestURI(), ex.getMessage(), ex);
		}
		return super.resolveException(request, response, handler, ex);
	}

	@Override
	protected ModelAndView getModelAndView(String viewName, Exception ex, HttpServletRequest request) {
		ModelAndView model = super.getModelAndView(viewName, ex, request);
		
		//NOTE: 여기에서 Exception 종류에 따른 분기처리 - 이 파일을 참고하여 각각의 프로젝트에 구현 필요
		
		return model;
	}

}
