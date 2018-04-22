package blue.hive.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletRequest;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.ConsoleAppender;

/**
 * 로그관련 유틸
 * 
 * SLF4J, Logback classic, 로깅 헬퍼
 * 
 * @author DongMan Kwon <dmkwon@intellicode.co.kr>
 */
public class BHiveLogUtil {

	/**
	 * 전체 Appender 목록 획득
	 * @param isStartedOnly 시작된 Appender 목록만 획득
	 * @return 전체 Appender 목록
	 */
	public static List<Appender<ILoggingEvent>> getAppenders(boolean isStartedOnly) {
		List<Appender<ILoggingEvent>> appenders = new ArrayList<Appender<ILoggingEvent>>();
		LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
		for (ch.qos.logback.classic.Logger logger : context.getLoggerList()) {
			Iterator<Appender<ILoggingEvent>> appenderIterator = logger.iteratorForAppenders();
			while (appenderIterator.hasNext()) {
				Appender<ILoggingEvent> appender = (Appender<ILoggingEvent>) appenderIterator.next();
				if(isStartedOnly && !appender.isStarted()) {
					continue;
				}
				if(!appenders.contains(appender)) {
					appenders.add(appender);
				}
			}	
		}
		return appenders;
	}
	
	/**
	 * 전체 Appender 목록 획득
	 * @param isStartedOnly 시작된 Appender 목록만 획득
	 * @return 전체 Appender 목록
	 */
	public static List<Appender<ILoggingEvent>> getConsoleAppenders(boolean isStartedOnly) {
		List<Appender<ILoggingEvent>> appenders = new ArrayList<Appender<ILoggingEvent>>();
		List<Appender<ILoggingEvent>> allAppenders = getAppenders(isStartedOnly);
		for (Appender<ILoggingEvent> appender : allAppenders) {
			if(appender instanceof ConsoleAppender) {
				appenders.add(appender);	
			}
		}
		return appenders;
	}
	
	/**
	 * 주어진 appender를 모두 중지
	 * @param appenders 중지할 appender 목록
	 */
	public static void stopAllAppenders(List<Appender<ILoggingEvent>> appenders) {
		for (Appender<ILoggingEvent> appender : appenders) {
			if(appender.isStarted()) {
				appender.stop();	
			}
		}
	}
	
	/**
	 * 주어진 appender를 모두 시작
	 * @param appenders 시작할 appender 목록
	 */
	public static void startAllAppenders(List<Appender<ILoggingEvent>> appenders) {
		for (Appender<ILoggingEvent> appender : appenders) {
			if(!appender.isStarted()) {
				appender.start();	
			}
		}
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
	
	/**
	 * 주어진 Exception이 의도한 예외인지 판별. {"code":"NNNN","msg":"NNNN"}
	 * 
	 * 기존 MobilePop의 예외처리에서 정상적인 오류응답을 Exception에 담아 처리하면서
	 * 진짜 Exception과 구분해주는 처리
	 * 
	 * 정규식으로 {"code":"NNNN..","msg":"XXXXXXX..." 로 시작하면 Graceful한 Exception으로 처리
	 *  
	 * @param ex 예외 객체
	 */
	public static boolean isGracefulException(Exception ex) {
		try {
			//String text = "{\"code\":\"NNNN\",\"msg\":\"NNNN\"}";
			String text = ex.getMessage();
			String regex = "^\\x7B\\x22code\\x22:\\x22.*\\x22\\x2C\\x22msg\\x22:\\x22.*\\x22";
			Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(text);
			boolean matched = matcher.find();
			return matched;	
		} catch(Exception e) {
		}
		return false;
	}
	
}
