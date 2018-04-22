package blue.hive.spring.client;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter;
import org.springframework.util.MultiValueMap;

import blue.hive.crypto.AES256Crypto;
import blue.hive.spring.http.converter.BHiveMapHttpMessageConverter;
import blue.hive.spring.http.converter.BHiveSecuredHttpMessageConverter;
import blue.hive.spring.http.converter.BHiveTeeHttpMessageConverter;

/**
 * BHiveRestTemplate의 Builder
 *
 * @author DongMan Kwon <dmkwon@intellicode.co.kr>
 */
public class BHiveRestTemplateBuilder {
	static Logger logger = LoggerFactory.getLogger(BHiveRestTemplateBuilder.class);

	/**
	 * BHiveRestTemplate를 생성 (FORM API방식, 암호화 미적용) - POST(application/x-www-form-urlencoded)
	 */
	public static BHiveRestTemplate buildFormApiTemplate() {

		BHiveRestTemplate restTemplate = new BHiveRestTemplate();

		//MessageConverter 설정
		List<HttpMessageConverter<?>> messageConverters = getHttpMessageConverters(API_TYPE.REQUEST_FORM);
		restTemplate.setMessageConverters(messageConverters);

		//Logging을 위한 Interceptor 설정
		List<ClientHttpRequestInterceptor> interceptors = restTemplate.getInterceptors();
		interceptors.add(new BHiveLoggingClientHttpRequestInterceptor());
		restTemplate.setInterceptors(interceptors);

		//ResponseBody를 여러번 읽을 수 있도록 ClientHttpRequestFactory 처리
		ClientHttpRequestFactory requestFactoryInner = new SimpleClientHttpRequestFactory(); //SimpleClientHttpRequestFactory: 기본 JDK기능을 이용
		//ClientHttpRequestFactory requestFactoryInner = new HttpComponentsClientHttpRequestFactory(); //HttpComponentsClientHttpRequestFactory: Apache HttpComponent 4.3이상을 이용
		//Proxy 설정
		String proxySet = System.getProperty("proxySet");
		logger.debug("BHiveRestTemplateBuilder - proxySet.{}", proxySet);
		if("true".equals(proxySet)) {
			String proxyHost = System.getProperty("proxyHost");
			String proxyPort = System.getProperty("proxyPort");
			logger.debug("BHiveRestTemplateBuilder - proxyHost:{}, proxyPort:{}", proxyHost, proxyPort);
			if(!StringUtils.isEmpty(proxyHost) && !StringUtils.isEmpty(proxyPort)) {
				logger.debug("BHiveRestTemplateBuilder - Apply Proxy. {}:{}", proxyHost, proxyPort);
				int proxyPortInt = Integer.parseInt(proxyPort);
				SimpleClientHttpRequestFactory requestFactoryInnerTyped = (SimpleClientHttpRequestFactory)requestFactoryInner;
				InetSocketAddress address = new InetSocketAddress(proxyHost, proxyPortInt);
				Proxy proxy = new Proxy(Proxy.Type.HTTP, address);
				requestFactoryInnerTyped.setProxy(proxy);
			}
		}
		ClientHttpRequestFactory requestFactory = new BufferingClientHttpRequestFactory(requestFactoryInner);
		restTemplate.setRequestFactory(requestFactory);
		return restTemplate;		
	}

	/**
	 * BHiveRestTemplate를 생성 (REST API방식, 암호화 미적용) - POST(application/json)
	 */
	public static BHiveRestTemplate buildRestApiTemplate() {
		
		BHiveRestTemplate restTemplate = new BHiveRestTemplate();

		//MessageConverter 설정
		List<HttpMessageConverter<?>> messageConverters = getHttpMessageConverters(API_TYPE.REQUEST_BODY_JSON);
		restTemplate.setMessageConverters(messageConverters);

		//Logging을 위한 Interceptor 설정
		List<ClientHttpRequestInterceptor> interceptors = restTemplate.getInterceptors();
		interceptors.add(new BHiveLoggingClientHttpRequestInterceptor());
		restTemplate.setInterceptors(interceptors);

		//ResponseBody를 여러번 읽을 수 있도록 ClientHttpRequestFactory 처리
		ClientHttpRequestFactory requestFactoryInner = new SimpleClientHttpRequestFactory(); //SimpleClientHttpRequestFactory: 기본 JDK기능을 이용
		//ClientHttpRequestFactory requestFactoryInner = new HttpComponentsClientHttpRequestFactory(); //HttpComponentsClientHttpRequestFactory: Apache HttpComponent 4.3이상을 이용
		//Proxy 설정
		String proxySet = System.getProperty("proxySet");
		logger.debug("BHiveRestTemplateBuilder - proxySet.{}", proxySet);
		if("true".equals(proxySet)) {
			String proxyHost = System.getProperty("proxyHost");
			String proxyPort = System.getProperty("proxyPort");
			logger.debug("BHiveRestTemplateBuilder - proxyHost:{}, proxyPort:{}", proxyHost, proxyPort);
			if(!StringUtils.isEmpty(proxyHost) && !StringUtils.isEmpty(proxyPort)) {
				logger.debug("BHiveRestTemplateBuilder - Apply Proxy. {}:{}", proxyHost, proxyPort);
				int proxyPortInt = Integer.parseInt(proxyPort);
				SimpleClientHttpRequestFactory requestFactoryInnerTyped = (SimpleClientHttpRequestFactory)requestFactoryInner;
				InetSocketAddress address = new InetSocketAddress(proxyHost, proxyPortInt);
				Proxy proxy = new Proxy(Proxy.Type.HTTP, address);
				requestFactoryInnerTyped.setProxy(proxy);
			}
		}
		ClientHttpRequestFactory requestFactory = new BufferingClientHttpRequestFactory(requestFactoryInner);
		restTemplate.setRequestFactory(requestFactory);
		return restTemplate;	
	}

	/**
	 * BHiveRestTemplate를 생성 (REST API방식, 암호화 적용, 파라미터에 따라 실서버용/개발용 암호화키 사용) - POST(application/json)
	 * @param secretKey 암호화시 사용할 키 (API_TYPE.REQUEST_BODY_SECURED_JSON에만 사용)
	 */
	public static BHiveRestTemplate buildSecureRestApiTemplate(String secretKey) {
		BHiveRestTemplate restTemplate = buildRestTemplate(API_TYPE.REQUEST_BODY_SECURED_JSON, secretKey);
		return restTemplate;
	}

	/**
	 * 내부용 - 실제로 BHiveRestTemplate을 환경에 맞춰 생성
	 * @param apiType api의 통신 방식
	 * @param secretKey 암호화시 사용할 키 (API_TYPE.REQUEST_BODY_SECURED_JSON에만 사용)
	 */
	private static BHiveRestTemplate buildRestTemplate(API_TYPE apiType, String secretKey) {
		BHiveRestTemplate restTemplate = new BHiveRestTemplate();

		//MessageConverter 설정
		List<HttpMessageConverter<?>> messageConverters = getHttpMessageConverters(apiType, secretKey);
		restTemplate.setMessageConverters(messageConverters);

		//Logging을 위한 Interceptor 설정
		List<ClientHttpRequestInterceptor> interceptors = restTemplate.getInterceptors();
		interceptors.add(new BHiveLoggingClientHttpRequestInterceptor());
		restTemplate.setInterceptors(interceptors);

		//ResponseBody를 여러번 읽을 수 있도록 ClientHttpRequestFactory 처리
		ClientHttpRequestFactory requestFactoryInner = new SimpleClientHttpRequestFactory(); //SimpleClientHttpRequestFactory: 기본 JDK기능을 이용
		//ClientHttpRequestFactory requestFactoryInner = new HttpComponentsClientHttpRequestFactory(); //HttpComponentsClientHttpRequestFactory: Apache HttpComponent 4.3이상을 이용
		//Proxy 설정
		String proxySet = System.getProperty("proxySet");
		logger.debug("BHiveRestTemplateBuilder - proxySet.{}", proxySet);
		if("true".equals(proxySet)) {
			String proxyHost = System.getProperty("proxyHost");
			String proxyPort = System.getProperty("proxyPort");
			logger.debug("BHiveRestTemplateBuilder - proxyHost:{}, proxyPort:{}", proxyHost, proxyPort);
			if(!StringUtils.isEmpty(proxyHost) && !StringUtils.isEmpty(proxyPort)) {
				logger.debug("BHiveRestTemplateBuilder - Apply Proxy. {}:{}", proxyHost, proxyPort);
				int proxyPortInt = Integer.parseInt(proxyPort);
				SimpleClientHttpRequestFactory requestFactoryInnerTyped = (SimpleClientHttpRequestFactory)requestFactoryInner;
				InetSocketAddress address = new InetSocketAddress(proxyHost, proxyPortInt);
				Proxy proxy = new Proxy(Proxy.Type.HTTP, address);
				requestFactoryInnerTyped.setProxy(proxy);
			}
		}
		ClientHttpRequestFactory requestFactory = new BufferingClientHttpRequestFactory(requestFactoryInner);
		restTemplate.setRequestFactory(requestFactory);


		return restTemplate;
	}


	/**
	 * RestTemplate에 설정할 MessageConverter 목록을 생성하여 획득 (JSON용 Converter 반환)
	 * @param apiType api의 통신 방식
	 */
	private static List<HttpMessageConverter<?>> getHttpMessageConverters(API_TYPE apiType) {
		List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
		if(apiType.equals(API_TYPE.REQUEST_BODY_JSON)) {
			messageConverters.add(new BHiveTeeHttpMessageConverter<Object>(getNonSecuredJsonConverter()));
		} else if(apiType.equals(API_TYPE.REQUEST_FORM)) {
			messageConverters.add(new BHiveTeeHttpMessageConverter<MultiValueMap<String, ?>>(new AllEncompassingFormHttpMessageConverter()));
			messageConverters.add(new BHiveTeeHttpMessageConverter<Map<String, ?>>(new BHiveMapHttpMessageConverter()));
			messageConverters.add(new BHiveTeeHttpMessageConverter<String>(new StringHttpMessageConverter()));
			messageConverters.add(new BHiveTeeHttpMessageConverter<Object>(getNonSecuredJsonConverter()));
		}
		return messageConverters;
	}

	/**
	 * RestTemplate에 설정할 MessageConverter 목록을 생성하여 획득 (JSON용 Converter 반환)
	 * @param apiType api의 통신 방식
	 * @param secretKey 암호화시 사용할 키 (API_TYPE.REQUEST_BODY_SECURED_JSON에만 사용)
	 */
	private static List<HttpMessageConverter<?>> getHttpMessageConverters(API_TYPE apiType, String secretKey) {
		List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
		if(apiType.equals(API_TYPE.REQUEST_BODY_SECURED_JSON)) {
			HttpMessageConverter<?> converter = getSecuredJsonConverter(secretKey);
			messageConverters.add(converter);
		}
		return messageConverters;
	}

	private static HttpMessageConverter<?> getSecuredJsonConverter(String secretKey) {
		MappingJackson2HttpMessageConverter httpConverter = new MappingJackson2HttpMessageConverter();
		List<MediaType> mediaTypes = new ArrayList<MediaType>();
		mediaTypes.add(new MediaType("application", "secured+json", Charset.forName("UTF-8")));
		httpConverter.setSupportedMediaTypes(mediaTypes);
		AES256Crypto crypto = new AES256Crypto(secretKey);
		HttpMessageConverter<?> converter = new BHiveSecuredHttpMessageConverter<Object>(crypto, httpConverter);
		return converter;
	}

	private static MappingJackson2HttpMessageConverter getNonSecuredJsonConverter() {
		MappingJackson2HttpMessageConverter httpConverter = new MappingJackson2HttpMessageConverter();
		List<MediaType> mediaTypes = new ArrayList<MediaType>();
		mediaTypes.add(new MediaType("application", "json", Charset.forName("UTF-8")));
		mediaTypes.add(new MediaType("application", "*+json", Charset.forName("UTF-8")));
		httpConverter.setSupportedMediaTypes(mediaTypes);
		return httpConverter;
	}
}
