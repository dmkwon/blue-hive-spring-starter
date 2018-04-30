package blue.hive.spring.context;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

import com.google.common.collect.Lists;

/**
 * Spring Application Context Initializer
 *
 * 서버기동시 재기동 기록 WARN 로깅
 * 서버기동시 Spring.Profile등 환경변수 INFO 로깅
 * spring.profiles.active를 Properties로 설정한 경우와 Environment로 설정한 경우에 대해 모두 지원하기 위한 Sync 작업
 *
 * @author DongMan Kwon <a href="mailto:dmkwon@intellicode.co.kr">dmkwon@intellicode.co.kr</a>
 */
public class BHiveApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

	private static final Logger logger = LoggerFactory.getLogger(BHiveApplicationContextInitializer.class);

	/* (non-Javadoc)
	 * @see org.springframework.context.ApplicationContextInitializer#initialize(org.springframework.context.ConfigurableApplicationContext)
	 */
	public void initialize(ConfigurableApplicationContext applicationContext) {
		ApplicationContextHolder.setApplicationContext(applicationContext);
		logger.warn("\r\n##########################################\r\n"
				+ "Application Starting...\r\n"
				+ "Application Name: {}\r\n"
				+ "##########################################",
				applicationContext.getApplicationName());
		logSpringActiveProfile(applicationContext);
	}

	/**
	 * Spring active profiles를 출력
	 * @param applicationContext
	 */
	private void logSpringActiveProfile(ConfigurableApplicationContext applicationContext) {
		Environment environment = applicationContext.getEnvironment();
		List<String> defaultProfiles = Lists.newArrayList(environment.getDefaultProfiles());
		List<String> activeProfiles = Lists.newArrayList(environment.getActiveProfiles());
		logger.info("");
		logger.info("## StartUp");
		logger.info("## Init Application Context.....");
		logger.info("");
		logger.info("############################################################");
		logger.info(" DefaultProfiles: {}", defaultProfiles);
		logger.info(" ActiveProfiles: {}", activeProfiles);
		logger.info("############################################################");
		logger.info("");
	}
}
