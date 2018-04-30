package blue.hive.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import blue.hive.type.BHiveValueEnum;

/**
 * Enum Util
 * 
 * @author DongMan Kwon <a href="mailto:dmkwon@intellicode.co.kr">dmkwon@intellicode.co.kr</a>
 */
public class BHiveEnumUtil {

	protected static Logger logger = LoggerFactory.getLogger(BHiveEnumUtil.class);  
	
	//Enum 처리//////////////////////////////////////////////////////////////////////
	/**
	 * 주어진 Enum이 NOTSET인지 검사 (Convention) (예: NOT_SET(0), NOT_SET(null), NOTSET 등 Label이 NOTSET, NOT_SET인지 검사)
	 * @param e enum 값
	 * @return NOTSET인지 여부
	 */
	public static <E extends Enum<E>> Boolean isNotSet(E e) {
		String stringValue = e.toString();
		if(e instanceof BHiveValueEnum<?>) {
			BHiveValueEnum<?> et = (BHiveValueEnum<?>)e;
			String value = (et.getValue() == null) ? "" : et.getValue().toString();
			return value.equals("NOT_SET") || value.equals("NOTSET") || value.equals("") ||
					stringValue.equals("NOT_SET") || stringValue.equals("NOTSET") || stringValue.equals("");
		}
		return stringValue.equals("NOT_SET") || stringValue.equals("NOTSET") || stringValue.equals("");
	}
	
	/**
	 * Enum의 키 목록을 얻어온다. (예: ["A", "B"])
	 * @param elementType Enum의 타입
	 * @return Enum의 키값 목록
	 */
	public static <E extends Enum<E>> List<E> getEnumKeys(Class<E> elementType) {
		return getEnumKeys(elementType, true);
	}
	/**
	 * Enum의 키 목록을 얻어온다. (예: ["A", "B"])
	 * @param elementType Enum의 타입
	 * @param excludeNotSet NOTSET값 제외
	 * @return Enum의 키값 목록
	 */
	public static <E extends Enum<E>> List<E> getEnumKeys(Class<E> elementType, Boolean excludeNotSet) {
		List<E> result = new ArrayList<E>();
		Set<E> enumSet = EnumSet.allOf(elementType);
		for(E e : enumSet) {
			if(excludeNotSet && isNotSet(e)) {
				continue;
			}
			result.add(e);
		}
		return result;
	}	

	/**
	 * Enum의 키문자열 목록을 얻어온다. (예: [MyEnum.A, MyEnum.B] &ge; ["A", "B"])
	 * @param elementType Enum의 타입
	 * @return Enum의 키값 문자열 목록
	 */
	public static <E extends Enum<E>> List<String> getEnumKeyStrings(Class<E> elementType) {
		return getEnumKeyStrings(elementType, true);
	}
	/**
	 * Enum의 키문자열 목록을 얻어온다. (예: [MyEnum.A, MyEnum.B] &ge; ["A", "B"])
	 * @param elementType Enum의 타입
	 * @param excludeNotSet NOTSET값 제외
	 * @return Enum의 키값 문자열 목록
	 */
	public static <E extends Enum<E>> List<String> getEnumKeyStrings(Class<E> elementType, Boolean excludeNotSet) {
		List<String> result = new ArrayList<String>();
		Set<E> enumSet = EnumSet.allOf(elementType);
		for(E e : enumSet) {
			if(excludeNotSet && isNotSet(e)) {
				continue;
			}
			result.add(e.toString());
		}
		return result;
	}

	/**
	 * Enum, FmsEnum의 값 목록을 얻어온다. (예: [1, 2])
	 * @param elementType Enum, FmsEnum&lt;?&gt; 타입
	 * @return Enum, FmsEnum의 값 목록
	 */
	public static <E extends Enum<E>, T> List<T> getEnumValues(Class<E> elementType) {
		return getEnumValues(elementType, true);
	}
	/**
	 * Enum, FmsEnum의 값 목록을 얻어온다. (예: [1, 2])
	 * @param elementType Enum, FmsEnum&lt;?&gt; 타입
	 * @param excludeNotSet NOTSET값 제외
	 * @return Enum, FmsEnum의 값 목록
	 */
	public static <E extends Enum<E>, T> List<T> getEnumValues(Class<E> elementType, Boolean excludeNotSet) {
		return convertToEnumValues(Arrays.asList(elementType.getEnumConstants()), excludeNotSet);
	}
	/**
	 * Enum, FmsEnum의 값 목록을 얻어온다. (예: [A, B] &ge; [1, 2])
	 * @param enumArray enum 목록
	 * @param excludeNotSet NOTSET값 제외
	 * @return Enum, FmsEnum의 값 목록
	 */
	@SuppressWarnings("unchecked")
	public static <E extends Enum<E>, T> List<T> convertToEnumValues(Iterable<? extends E> enumArray, Boolean excludeNotSet) {
		List<T> result = new ArrayList<T>();
		for(E e : enumArray) {
			if(excludeNotSet && isNotSet(e)) {
				continue;
			}
			if(e instanceof BHiveValueEnum<?>) {
				BHiveValueEnum<?> et = (BHiveValueEnum<?>)e;
				result.add((T) et.getValue());
			} else {
				result.add((T) e);
			}
		}
		return result;
	}
	
	/**
	 * Enum, FmsEnum의 값문자열 목록을 얻어온다. (예: ["1", "2"])
	 * @param elementType Enum, FmsEnum&lt;?&gt; 타입
	 * @return Enum, FmsEnum의 값 목록
	 */
	public static <E extends Enum<E>> List<String> getEnumValueStrings(Class<E> elementType) {
		return getEnumValueStrings(elementType, true);
	}
	/**
	 * Enum, FmsEnum의 값문자열 목록을 얻어온다. (예: ["1", "2"])
	 * @param elementType Enum, FmsEnum&lt;?&gt; 타입
	 * @param excludeNotSet NOTSET값 제외
	 * @return Enum, FmsEnum의 값 목록
	 */
	public static <E extends Enum<E>> List<String> getEnumValueStrings(Class<E> elementType, Boolean excludeNotSet) {
		return convertToEnumValueStrings(Arrays.asList(elementType.getEnumConstants()), excludeNotSet);
	}
	/**
	 * Enum, FmsEnum의 값문자열 목록을 얻어온다. (예: [A, B] &ge; ["1", "2"])
	 * @param enumArray enum 목록
	 * @param excludeNotSet NOTSET값 제외
	 * @return Enum, FmsEnum의 값 목록
	 */
	public static <E extends Enum<E>> List<String> convertToEnumValueStrings(Iterable<? extends E> enumArray, Boolean excludeNotSet) {
		List<String> result = new ArrayList<String>();
		for(E e : enumArray) {
			if(excludeNotSet && isNotSet(e)) {
				continue;
			}
			if(e instanceof BHiveValueEnum<?>) {
				BHiveValueEnum<?> et = (BHiveValueEnum<?>)e;
				result.add(et.getValue().toString());
			} else {
				result.add(e.toString());
			}
		}
		return result;
	}
	
	/**
	 * Enum, FmsEnum의 값문자열 목록을 얻어온다. (예: [A, B] &ge; ["1", "2"])
	 * @param enumArray enum 목록
	 * @param excludeNotSet NOTSET값 제외
	 * @return Enum, FmsEnum의 값 목록
	 */
	public static <E extends Enum<E>> List<String> convertToEnumKeyStrings(Iterable<? extends E> enumArray, Boolean excludeNotSet) {
		List<String> result = new ArrayList<String>();
		for(E e : enumArray) {
			if(excludeNotSet && isNotSet(e)) {
				continue;
			}
			result.add(e.toString());
		}
		return result;
	}
	
	/**
	 * Enum의 키-값 Map을 얻는다. (예: [MyEnum.Blue:1, MyEnum.Red:2] )
	 * @param elementType Enum의 타입
	 * @return Enum의 키문자열-값 Map
	 */
	public static <E extends Enum<E>, T> Map<E, T> getEnumKeyValueMap(Class<E> elementType) {
		return getEnumKeyValueMap(elementType, true);
	}
	/**
	 * Enum의 키-값 Map을 얻는다. (예: [MyEnum.Blue:1, MyEnum.Red:2] )
	 * @param elementType Enum의 타입
	 * @param excludeNotSet NOTSET값 제외 
	 * @return Enum의 키문자열-값 Map
	 */
	public static <E extends Enum<E>, T> Map<E, T> getEnumKeyValueMap(Class<E> elementType, Boolean excludeNotSet) {
		return convertToEnumKeyValueMap(Arrays.asList(elementType.getEnumConstants()), excludeNotSet);
	}
	/**
	 * Enum의 키-값 Map을 얻는다. (예: [MyEnum.Blue, MyEnum.Red] &ge; [MyEnum.Blue:1, MyEnum.Red:2] )
	 * @param enumArray enum 목록
	 * @param excludeNotSet NOTSET값 제외 
	 * @return Enum의 키문자열-값 Map
	 */
	@SuppressWarnings("unchecked")
	public static <E extends Enum<E>, T> Map<E, T> convertToEnumKeyValueMap(Iterable<? extends E> enumArray, Boolean excludeNotSet) {
		Map<E, T> result = new LinkedHashMap<E, T>();
		for(E e : enumArray) {
			if(excludeNotSet && isNotSet(e)) {
				continue;
			}
			if(e instanceof BHiveValueEnum<?>) {
				BHiveValueEnum<?> et = (BHiveValueEnum<?>)e;
				result.put(e, (T) et.getValue());
			}
			else
			{
				result.put(e, (T) e);
			}
		}
		return result;
	}
	
	/**
	 * Enum의 키문자열-값 Map을 얻는다. (예: ["Blue":1, "Red":2])
	 * @param elementType Enum의 타입
	 * @return Enum의 키문자열-값 Map
	 */
	public static <E extends Enum<E>, T> Map<String, T> getEnumKeyStringValueMap(Class<E> elementType) {
		return getEnumKeyStringValueMap(elementType, true);
	}	
	/**
	 * Enum의 키문자열-값 Map을 얻는다. (예: ["Blue":1, "Red":2])
	 * @param elementType Enum의 타입
	 * @param excludeNotSet NOTSET값 제외
	 * @return Enum의 키문자열-값 Map
	 */
	public static <E extends Enum<E>, T> Map<String, T> getEnumKeyStringValueMap(Class<E> elementType, Boolean excludeNotSet) {
		return convertToEnumKeyStringValueMap(Arrays.asList(elementType.getEnumConstants()), excludeNotSet);
	}
	/**
	 * Enum의 키문자열-값 Map을 얻는다. (예: ["Blue":1, "Red":2])
	 * @param enumArray enum 목록
	 * @param excludeNotSet NOTSET값 제외
	 * @return Enum의 키문자열-값 Map
	 */
	@SuppressWarnings("unchecked")
	public static <E extends Enum<E>, T> Map<String, T> convertToEnumKeyStringValueMap(Iterable<? extends E> enumArray, Boolean excludeNotSet) {
		Map<String, T> result = new LinkedHashMap<String, T>();
		for(E e : enumArray) {
			if(excludeNotSet && isNotSet(e)) {
				continue;
			}
			if(e instanceof BHiveValueEnum<?>) {
				BHiveValueEnum<?> et = (BHiveValueEnum<?>)e;
				result.put(e.toString(), (T) et.getValue());
			} else {
				result.put(e.toString(), (T) e);
			}
		}
		return result;
	}
	
	/**
	 * Enum의 키문자열-값문자열 Map을 얻는다. (예: ["Blue":"1", "Red":"2"])
	 * @param elementType Enum의 타입
	 * @return Enum의 키문자열-값문자열 Map
	 */
	public static <E extends Enum<E>> Map<String, String> getEnumKeyStringValueStringMap(Class<E> elementType) {
		return getEnumKeyStringValueStringMap(elementType, true);
	}	
	/**
	 * Enum의 키문자열-값 Map을 얻는다. (예: ["Blue":"1", "Red":"2"])
	 * @param elementType Enum의 타입
	 * @param excludeNotSet NOTSET값 제외
	 * @return Enum의 키문자열-값문자열 Map
	 */
	public static <E extends Enum<E>> Map<String, String> getEnumKeyStringValueStringMap(Class<E> elementType, Boolean excludeNotSet) {
		return convertToEnumKeyStringValueStringMap(Arrays.asList(elementType.getEnumConstants()), excludeNotSet);
	}
	/**
	 * Enum의 키문자열-값 Map을 얻는다. (예: ["Blue":"1", "Red":"2"])
	 * @param enumArray enum 목록
	 * @param excludeNotSet NOTSET값 제외
	 * @return Enum의 키문자열-값문자열 Map
	 */
	public static <E extends Enum<E>> Map<String, String> convertToEnumKeyStringValueStringMap(Iterable<? extends E> enumArray, Boolean excludeNotSet) {
		Map<String, String> result = new LinkedHashMap<String, String>();
		for(E e : enumArray) {
			if(excludeNotSet && isNotSet(e)) {
				continue;
			}
			if(e instanceof BHiveValueEnum<?>) {
				BHiveValueEnum<?> et = (BHiveValueEnum<?>)e;
				result.put(e.toString(), (et.getValue()==null)?"":et.getValue().toString());
			} else {
				result.put(e.toString(), e.toString());
			}
		}
		return result;
	}
	
	/**
	 * Enum의 keyValue EntrySet Array을 얻는다. (예: [{key:"Blue", value:"1"}, {key:"Red", value:"2"}])
	 * @param elementType Enum의 타입
	 * @return Enum의 keyValue EntrySet Array
	 */
	public static <E extends Enum<E>> Set<Entry<String, String>> getEnumKeyValueEntrySet(Class<E> elementType) {
		return getEnumKeyValueEntrySet(elementType, true);
	}	
	/**
	 * Enum의 키문자열-값문자열 keyValue EntrySet Array을 얻는다. (예: [{key:"Blue", value:"1"}, {key:"Red", value:"2"}])
	 * @param elementType Enum의 타입
	 * @param excludeNotSet NOTSET값 제외
	 * @return Enum의 keyValue EntrySet Array
	 */
	public static <E extends Enum<E>> Set<Entry<String, String>> getEnumKeyValueEntrySet(Class<E> elementType, Boolean excludeNotSet) {
		return convertToEnumKeyValueEntrySet(Arrays.asList(elementType.getEnumConstants()), excludeNotSet);
	}
	/**
	 * Enum의 키문자열-값문자열 keyValue EntrySet Array을 얻는다. (예: [{key:"Blue", value:"1"}, {key:"Red", value:"2"}])
	 * @param enumArray enum 목록
	 * @param excludeNotSet NOTSET값 제외
	 * @return Enum의 keyValue EntrySet Array
	 */
	public static <E extends Enum<E>> Set<Entry<String, String>> convertToEnumKeyValueEntrySet(Iterable<? extends E> enumArray, Boolean excludeNotSet) {
		Map<String, String> result = new LinkedHashMap<String, String>();
		for(E e : enumArray) {
			if(excludeNotSet && isNotSet(e)) {
				continue;
			}
			if(e instanceof BHiveValueEnum<?>) {
				BHiveValueEnum<?> et = (BHiveValueEnum<?>)e;
				result.put(e.toString(), (et.getValue()==null)?"":et.getValue().toString());
			} else {
				result.put(e.toString(), e.toString());
			}
		}
		return result.entrySet();
	}
	
	/**
	 * Enum의 value값에서 Enum 값을 얻는다 
	 * (예: [MyEnum.Blue:1, MyEnum.Red:2]일때: 2, "2", "Red" &gt; MyEnum.Red 반환)
	 * 반대로 MyEnum.Red &ge; 2로 변환은 MyEnum.Red.getValue()로 획득. AbleValueEnum&lt;T&gt;에 구현
	 * @param elementType Enum의 타입
	 * @param value 변환할 Enum값
	 * @return 변환된 Enum 값. Parse에 실패하면 null
	 */
	public static <E extends Enum<E>, T> E parseEnumValueOf(Class<E> elementType, T value) {
		for(E e : elementType.getEnumConstants()) {
			if(e instanceof BHiveValueEnum<?>) {
				BHiveValueEnum<?> et = (BHiveValueEnum<?>)e;
				if( et.getValue() == value || (et.getValue() != null && et.getValue().toString().equals(value.toString())) ) {
					return e;
				}
			} 
			if( e.toString().equals(value.toString()) ) {
				return e;
			}
		}
		return null;
	}
	
	/**
	 * Enum의 value 값을 얻는다 
	 * (예: [MyEnum.Blue:1, MyEnum.Red:2]일때: 2, "2", "Red" &gt; "2" 반환)
	 * @param elementType Enum의 타입
	 * @param value 변환할 Enum값
	 * @return 변환된 Enum 값의 value 값. Parse에 실패하면 null
	 */
	public static <E extends Enum<E>, T> Object convertToCodeValue(Class<E> elementType, T value) {
		E e = parseEnumValueOf(elementType, value);
		if(e instanceof BHiveValueEnum<?>) {
			BHiveValueEnum<?> et = (BHiveValueEnum<?>)e;
			return et.getValue();
		}
		return null;
	}
	
	/**
	 * Enum의 value 값을 얻는다 
	 * (예: [MyEnum.Blue:1, MyEnum.Red:2]일때: 2, "2", "Red" &gt; "Red" 반환)
	 * @param elementType Enum의 타입
	 * @param value 변환할 Enum값
	 * @return 변환된 Enum 값의 value 값. Parse에 실패하면 null
	 */
	public static <E extends Enum<E>, T> String convertToCodeKeyString(Class<E> elementType, T value) {
		E e = parseEnumValueOf(elementType, value);
		if(e instanceof BHiveValueEnum<?>) {
			BHiveValueEnum<?> et = (BHiveValueEnum<?>)e;
			return et.toString();
		}
		return null;
	}
	
}
