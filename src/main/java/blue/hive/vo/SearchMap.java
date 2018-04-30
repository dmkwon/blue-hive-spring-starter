package blue.hive.vo;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

/**
 * 검색조건 맵 (Prefix: search)
 * 
 * put("name", value) &le; searchName으로 저장
 * get("name") 또는 get("searchName")으로 조회 가능
 * 
 * @author DongMan Kwon <a href="mailto:dmkwon@intellicode.co.kr">dmkwon@intellicode.co.kr</a>
 */
public class SearchMap extends HashMap<String, Object> {

	private static final long serialVersionUID = -7116224054358578695L;

	@Override
	public Object put(String key, Object value) {
//		if (key == null) {
//			return super.put(key, value);
//		}
		key = StringUtils.removeStart(key, "search");
		key = "search" + WordUtils.capitalize(key);
		return super.put(key, value);
	}

	@Override
	public Object get(Object keyObj) {
//		if (keyObj == null) {
//			return super.get(keyObj);
//		}
		String key = (String) keyObj;
		if(!containsKey(key)) {
			key = "search" + WordUtils.capitalize(key);
		}
		return super.get(key);
	}
	
	public SearchMap add(String key, Object value) {
		this.put(key, value);
		return this;
	}

	public SearchMap(Map<? extends String, ? extends Object> map) {
		for (Map.Entry<? extends String, ? extends Object> entry : map.entrySet()) {
			if( entry.getValue() instanceof String[] ) {
				String[] values = (String[]) entry.getValue();
				if( values.length == 0 ) {
					continue;
				} else if( values.length == 1 ) {
					this.put(entry.getKey(), decodeURIComponent(values[0]));
					continue;
				}
				for(String value: values) {
					value = decodeURIComponent(value);
				}
				this.put(entry.getKey(), Arrays.asList(values));
				continue;
			}
			if(entry.getValue() instanceof String) {
				this.put(entry.getKey(), decodeURIComponent((String)entry.getValue()));
				continue;
			}
			this.put(entry.getKey(), entry.getValue());
		}
	}
	
	private String decodeURIComponent(String value) {
		try {
			return URLDecoder.decode(value, "UTF-8");
		} catch(UnsupportedEncodingException e) {
			return value;
		}
	}
	
	public SearchMap() {
		super();
	}
	public SearchMap(ServletRequest request) {
		this(request.getParameterMap());
	}
	public static SearchMap buildFrom(Map<? extends String, ? extends Object> map) {
		return new SearchMap(map);
	}
	public static SearchMap buildFrom(ServletRequest request) {
		return new SearchMap(request);
	}
}
