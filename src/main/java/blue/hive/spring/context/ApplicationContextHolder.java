package blue.hive.spring.context;

import org.springframework.context.ApplicationContext;

/**
 * Application Context Holder: 스프링 Root ApplicationContext를 정적으로 보관하고 코드 어디서나 접근하기 위한 Bean 
 * 
 * @author DongMan Kwon <dmkwon@intellicode.co.kr>
 */
public class ApplicationContextHolder {

	private static ApplicationContext applicationContext;
	
	/**
	 * @return 현재 Spring Root Context
	 */
	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}
	
	/**
	 * ApplicationContext를 정적으로 보관처리
	 * @param applicationContext
	 */
	public static void setApplicationContext(ApplicationContext applicationContext) {
		ApplicationContextHolder.applicationContext = applicationContext;
	}

}
