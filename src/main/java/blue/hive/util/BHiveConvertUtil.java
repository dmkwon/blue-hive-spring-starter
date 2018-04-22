package blue.hive.util;

import java.nio.charset.Charset;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

/**
 * 형변환 관련 유틸
 *
 * @author DongMan Kwon <dmkwon@intellicode.co.kr>
 */
public class BHiveConvertUtil {
	protected static Logger logger = LoggerFactory.getLogger(BHiveConvertUtil.class);

	/**
	 * 버퍼를 UTF-8 문자열로 변환 (Trim처리)
	 */
	public static String convertToString(byte[] buf) {
		String s = new String(buf, Charset.forName("UTF-8"));
		return StringUtils.trimTrailingWhitespace(s);
	}


	/**
	 * 일반 Map을 RestTemplate의 FormHttpMessageConverter가 변환할 수 있는 MultiValueMap으로 변환
	 * @param map
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static MultiValueMap<String, Object> convertToMultiValueMap(Map<String, ?> map) {
		MultiValueMap<String, Object> result = new LinkedMultiValueMap<String, Object>();

		if (result.getClass().isAssignableFrom(map.getClass())) {
			return (MultiValueMap<String, Object>) map;
		}
	
		for (Entry<String, ?> key : map.entrySet()) {
			result.add(key.getKey(), key.getValue());
		}
		return result;
	}

}
