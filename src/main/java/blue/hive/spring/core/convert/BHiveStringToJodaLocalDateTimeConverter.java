package blue.hive.spring.core.convert;

import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;

import blue.hive.exception.BHiveConvertException;
import blue.hive.util.BHiveDateUtil;

/**
 * 문자열과 Joda DateTime형의 변환
 * 
 * 여러 DateTime형의 문자열을 DateTime으로 변환
 * 
 * @author DongMan Kwon <a href="mailto:dmkwon@intellicode.co.kr">dmkwon@intellicode.co.kr</a>
 */
public class BHiveStringToJodaLocalDateTimeConverter implements Converter<String, LocalDateTime> {

	private final Logger logger = LoggerFactory.getLogger(BHiveStringToJodaLocalDateTimeConverter.class);
	
	//DateTimeFormatter formatter = ISODateTimeFormat.dateTime(); //"yyyy-MM-dd'T'HH:mm:ss.SSSZ"
	
	/* (non-Javadoc)
	 * @see org.springframework.core.convert.converter.Converter#convert(java.lang.Object)
	 */
	public LocalDateTime convert(String dateString) {
		try {
			logger.trace("convert({})", dateString);
			return BHiveDateUtil.parseToLocalDateTime(dateString);
		} catch (Exception e) {
			logger.trace("convert({}) - Exception: {}", dateString, e.toString());
			throw new BHiveConvertException("Cannot convert dateString to Joda DateTime.", e) ;
		}
	}
}
