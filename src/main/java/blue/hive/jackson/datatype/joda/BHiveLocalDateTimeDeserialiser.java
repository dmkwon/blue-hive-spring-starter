package blue.hive.jackson.datatype.joda;

import java.io.IOException;

import org.joda.time.LocalDateTime;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonTokenId;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.datatype.joda.deser.LocalDateTimeDeserializer;

import blue.hive.util.BHiveDateUtil;

/**
 * Ablecoms Joda LocalDateTime Deserializer
 * 
 * http://wiki.fasterxml.com/JacksonFAQDateHandling
 * http://www.baeldung.com/jackson-serialize-dates
 * https://raymondhlee.wordpress.com/2015/01/24/custom-json-serializer-and-deserializer-for-joda-datetime-objects/
 * https://dzone.com/articles/how-serialize-javautildate
 * http://stackoverflow.com/questions/3269459/how-to-serialize-joda-datetime-with-jackson-json-processer
 * 
 * @author DongMan Kwon <a href="mailto:dmkwon@intellicode.co.kr">dmkwon@intellicode.co.kr</a>
 */
public class BHiveLocalDateTimeDeserialiser extends LocalDateTimeDeserializer {
	private static final long serialVersionUID = -565820334558847688L;

	@Override
	public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
		try {
			switch (p.getCurrentTokenId()) {
			case JsonTokenId.ID_STRING:
	            {
	                String str = StringUtils.trimWhitespace(p.getText());
	                if(str.length() == 0) {
	                	return null;
	                }
	                LocalDateTime result = BHiveDateUtil.parseToLocalDateTime(str, false);
	                if(result != null) {
	                	return result;
	                }
	            }
	            default:
			 }
		} catch (IOException e) {
		}
		return super.deserialize(p, ctxt);
	}
}
