package blue.hive.spring.http.converter;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import blue.hive.spring.http.BHiveTeeHttpInputMessage;
import blue.hive.spring.http.BHiveTeeHttpOutputMessage;

/**
 * {@link HttpMessageConverter}를 래핑하여 입출력 내용을 Tee하여 로깅하는 클래스
 *
 * @author DongMan Kwon <a href="mailto:dmkwon@intellicode.co.kr">dmkwon@intellicode.co.kr</a>
 */
public class BHiveTeeHttpMessageConverter<T> implements HttpMessageConverter<T> {
	Logger logger = LoggerFactory.getLogger(this.getClass());

	HttpMessageConverter<T> httpConverter = null;

	public BHiveTeeHttpMessageConverter(HttpMessageConverter<T> httpConverter) {
		this.httpConverter = httpConverter;
	}

	/* (non-Javadoc)
	 * @see org.springframework.http.converter.HttpMessageConverter#canRead(java.lang.Class, org.springframework.http.MediaType)
	 */
	public boolean canRead(Class<?> clazz, MediaType mediaType) {
		return httpConverter.canRead(clazz, mediaType);
	}

	/* (non-Javadoc)
	 * @see org.springframework.http.converter.HttpMessageConverter#canWrite(java.lang.Class, org.springframework.http.MediaType)
	 */
	public boolean canWrite(Class<?> clazz, MediaType mediaType) {
		return httpConverter.canWrite(clazz, mediaType);
	}

	/* (non-Javadoc)
	 * @see org.springframework.http.converter.HttpMessageConverter#getSupportedMediaTypes()
	 */
	public List<MediaType> getSupportedMediaTypes() {
		return httpConverter.getSupportedMediaTypes();
	}

	public T read(Class<? extends T> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
		BHiveTeeHttpInputMessage teeHttpInputMessage = new BHiveTeeHttpInputMessage(inputMessage);
		T result = httpConverter.read(clazz, teeHttpInputMessage);
		logger.debug("\r\n  >>>> READ: {}", teeHttpInputMessage.getTeeInputString());
		return result;
	}

	public void write(T t, MediaType contentType, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
		BHiveTeeHttpOutputMessage teeHttpOutputMessage = new BHiveTeeHttpOutputMessage(outputMessage);
		httpConverter.write(t, contentType, teeHttpOutputMessage);
		logger.debug("\r\n  >>>> WRITE: {}", teeHttpOutputMessage.getTeeOutputString());
	}

}
