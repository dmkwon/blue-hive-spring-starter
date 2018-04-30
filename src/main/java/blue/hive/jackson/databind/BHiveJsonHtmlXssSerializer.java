package blue.hive.jackson.databind;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * fasterxml jackson JsonSerializer
 * 
 * Html에 String을 내려줄때 XSS 처리 (현재 미사용) 
 * 
 * @author DongMan Kwon <a href="mailto:dmkwon@intellicode.co.kr">dmkwon@intellicode.co.kr</a>
 */
public class BHiveJsonHtmlXssSerializer extends JsonSerializer<String> {

	@Override
	public Class<String> handledType() {
		return String.class;
	}
	
	@Override
	public void serialize(String value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
		if(value != null) {
			String encodedValue = encodeHtmlForBlockingXSS(value);
			jgen.writeString(encodedValue);
		}
	}

	private String encodeHtmlForBlockingXSS(String value) {
		//Json Encode Table from StringEscapeUtils.escapeJson
		//{"\"", "\\\""}
		//{"\\", "\\\\"}
		//{"/", "\\/"}
		return value; //.replace("/", "\\/"); 
	}

}
