package blue.hive.spring.client;

import java.util.List;

import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

/**
 * HTTP API 연동을 위한 RestTemplate 확장
 *
 * @author DongMan Kwon <dmkwon@intellicode.co.kr>
 */
public class BHiveRestTemplate extends RestTemplate {

	public BHiveRestTemplate() {
		super();
	}
	public BHiveRestTemplate(ClientHttpRequestFactory requestFactory) {
		super(requestFactory);
	}
	public BHiveRestTemplate(List<HttpMessageConverter<?>> messageConverters) {
		super(messageConverters);
	}
}

