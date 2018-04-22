package blue.hive.jackson;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import org.apache.tiles.request.ApplicationContext;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ClassUtils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;

import blue.hive.jackson.datatype.joda.BHiveLocalDateTimeDeserialiser;

/**
 * 공통 Jackson2 ObjectMapper
 * 
 *  - JodaModule을 등록
 * 
 * @see http://stackoverflow.com/questions/13700853/jackson2-json-iso-8601-date-from-jodatime-in-spring-3-2rc1
 * 
 * @author DongMan Kwon <dmkwon@intellicode.co.kr> 
 */
public class BHiveObjectMapper extends ObjectMapper implements Externalizable  {
	private static final long serialVersionUID = 9103711369247336228L;

	protected static final Logger logger = LoggerFactory.getLogger(BHiveObjectMapper.class);

	private ClassLoader moduleClassLoader = getClass().getClassLoader();

	private static String DEFAULT_DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

	public BHiveObjectMapper() {
		this(null, DEFAULT_DATETIME_FORMAT);
	}

	public BHiveObjectMapper(ApplicationContext applicationContext) {
		this(applicationContext, DEFAULT_DATETIME_FORMAT);
	}

	public BHiveObjectMapper(String simpleDateTimeFormat) {
		this(null, simpleDateTimeFormat);
	}

	public BHiveObjectMapper(ApplicationContext applicationContext, String simpleDateTimeFormat) {
		//Register Java8 DateTime, Joda DateTime Module, Hibernate4 Module
		registerWellKnownModulesIfAvailable(this);

		//Features 설정
		disable(SerializationFeature.INDENT_OUTPUT); //Pretty Print
		disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

		// @see org.springframework.http.converter.json.Jackson2ObjectMapperBuilder.customizeDefaultFeatures(ObjectMapper)
		disable(MapperFeature.DEFAULT_VIEW_INCLUSION); //@JsonView의 사용을 위해 기본포함기능을 비활성화
		disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES); //Deserialization에서 알려지지 않은 속성에 대해 오류 방지

		//TimeZone 설정 (Jackson은 기본적으로 GMT를 사용함)
		//http://wiki.fasterxml.com/JacksonFAQDateHandling
		//How come this time is off by 9 hours? (5 hours, 3 hours etc)
		//You may be thinking in terms of your local time zone. Remember that Jackson defaults to using GMT (Greenwich time, one hour behind central European timezone; multiple hours ahead of US time zones).
		setTimeZone(TimeZone.getDefault()); //Java Default Time
		//setTimeZone(DateTimeZone.getDefault()); //Joda Default Time

		//기본 DateTime Format 지정
		if(simpleDateTimeFormat != null) {
			disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
			DateFormat dateFormat = new SimpleDateFormat(simpleDateTimeFormat);
			setDateFormat(dateFormat);
		}

		//기타 features는 다음 소스참고: org.springframework.http.converter.json.Jackson2ObjectMapperFactoryBean

		registerCustomModules();
	}

	private void registerCustomModules() {
		//SimpleModule module = new SimpleModule("bhive HTML XSS Serializer", new Version(1, 0, 0, "FINAL"));
		SimpleModule module = new SimpleModule("bhive HTML XSS Serializer");

		//JS에 Json을 내려보낼때 JS가 깨지지 않게 포매팅
		//module.addSerializer(new AbleJsonHtmlXssSerializer());

		//Joda LocalDateTime Deserializer - 대부분의 날짜형식을 다 받아줌...
		module.addDeserializer(LocalDateTime.class, new BHiveLocalDateTimeDeserialiser());

		registerModule(module);
	}

	@SuppressWarnings("unchecked")
	private void registerWellKnownModulesIfAvailable(ObjectMapper mapper) {
		// Java 8 java.time package present?
		if (ClassUtils.isPresent("java.time.LocalDate", this.moduleClassLoader)) {
			try {
				Class<? extends Module> jsr310Module = (Class<? extends Module>)
						ClassUtils.forName("com.fasterxml.jackson.datatype.jsr310.JSR310Module", this.moduleClassLoader);
				mapper.registerModule(BeanUtils.instantiateClass(jsr310Module));
			}
			catch (ClassNotFoundException ex) {
				// jackson-datatype-jsr310 not available
			}
		}
		// Joda-Time present?
		if (ClassUtils.isPresent("org.joda.time.LocalDate", this.moduleClassLoader)) {
			try {
				Class<? extends Module> jodaModule = (Class<? extends Module>)
						ClassUtils.forName("com.fasterxml.jackson.datatype.joda.JodaModule", this.moduleClassLoader);
				mapper.registerModule(BeanUtils.instantiateClass(jodaModule));
			}
			catch (ClassNotFoundException ex) {
				// jackson-datatype-joda not available
			}
		}
		//Hibernate present? 
		if (ClassUtils.isPresent("com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module", this.moduleClassLoader)) {
			try {
				Class<? extends Module> hibernate4Module = (Class<? extends Module>)
						ClassUtils.forName("com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module", this.moduleClassLoader);
				mapper.registerModule(BeanUtils.instantiateClass(hibernate4Module));
			}
			catch (ClassNotFoundException ex) {
				// jackson-datatype-hibernate4 not available
			}
		}
	}

	public void writeExternal(ObjectOutput out) throws IOException {
		// TODO Auto-generated method stub
		
	}

	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		// TODO Auto-generated method stub
		
	}

}
