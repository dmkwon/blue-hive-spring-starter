package blue.hive.util;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;

import blue.hive.annotation.Display;
import blue.hive.annotation.ExcelColumn;
import blue.hive.exception.BHiveRuntimeException;
import blue.hive.util.anyframe.StringUtil;

/**
 * VO관련 Util 클래스
 *
 * @author DongMan Kwon <a href="mailto:dmkwon@intellicode.co.kr">dmkwon@intellicode.co.kr</a>
 */
public class BHiveVOUtil {

	protected static Logger logger = LoggerFactory.getLogger(BHiveVOUtil.class);

	/**
	 * Display Annotation의 order 속성으로 정렬된 필드-순서 맵을 획득
	 * @param voClass 대상 VO
	 * @return Display Annotation의 order 속성으로 정렬된 필드-순서 맵
	 */
	public static Map<String, Integer> getDisplayOrderMap(Class<?> voClass) {
		Field[] fields = voClass.getDeclaredFields();
		Map<String, Integer> voFieldOrderMap = new HashMap<String, Integer>();
		for(Field field : fields) {
			String fieldName = field.getName();
			//노출 순서
			int displayOrder = Integer.MAX_VALUE;
			Display displayAnnotation = field.getAnnotation(Display.class);
			if(displayAnnotation != null) {
				displayOrder = displayAnnotation.order();
			}
			voFieldOrderMap.put(fieldName, displayOrder);
		}
		voFieldOrderMap = BHiveCollectionUtil.sortByValues(voFieldOrderMap);
		return voFieldOrderMap;
	}

	/**
	 * Display Annotation의 name 속성으로 필드-필드명 맵을 획득 (기본로케일)
	 * @param voClass 대상 VO
	 * @return Display Annotation의 name 속성으로 필드-필드명 맵
	 */
	public static Map<String, String> getDisplayNameMap(Class<?> voClass) {
		return getDisplayNameMap(voClass, null, null, Locale.getDefault());
	}

	/**
	 * Display Annotation의 name 속성으로 필드-필드명 맵을 획득
	 * @param voClass 대상 VO
	 * @param messageSource 메시지 소스
	 * @param prefix 메시지소스로 메시지 변환처리시 필드 앞에 붙일 Prefix (예: com.col.)
	 * @param locale 메시지 적용 로케일
	 * @return Display Annotation의 name 속성으로 필드-필드명 맵
	 */
	public static Map<String, String> getDisplayNameMap(Class<?> voClass, MessageSource messageSource, String prefix, Locale locale) {
		Field[] fields = voClass.getDeclaredFields();
		Map<String, String> voFieldNameMap = new HashMap<String, String>();
		for(Field field : fields) {
			//기본 필드명
			String fieldName = field.getName();

			//@DisplayName
			String displayName = null;
			Display displayAnnotation = field.getAnnotation(Display.class);

			if(displayAnnotation != null) {
				displayName = displayAnnotation.name();
			} else {
				displayName = fieldName;
				if(StringUtil.isNotEmpty(prefix)) {
					displayName = prefix + displayName;
	        	}
				displayName = displayName.toLowerCase();
			}

			if(messageSource != null) {
				displayName = messageSource.getMessage(displayName, null, locale);
			}
			voFieldNameMap.put(fieldName, displayName);
		}
		return voFieldNameMap;
	}

	/**
	 * Display Annotation의 name 속성으로 필드명을 획득 (기본로케일)
	 * @param voClass 대상 VO
	 * @param declaredFieldName 선언된 필드명
	 * @return Display Annotation의 name 속성으로 필드명
	 */
	public static String getDisplayName(Class<?> voClass, String declaredFieldName) {
		return getDisplayName(voClass, declaredFieldName, null, null, Locale.getDefault());
	}

	/**
	 * Display Annotation의 name 속성으로 필드명을 획득
	 * @param voClass 대상 VO
	 * @param declaredFieldName 선언된 필드명
	 * @param messageSource 메시지 소스
	 * @param prefix 메시지소스로 메시지 변환처리시 필드 앞에 붙일 Prefix (예: com.col.)
	 * @param locale 메시지 적용 로케일
	 * @return Display Annotation의 name 속성으로 필드명
	 */
	public static String getDisplayName(Class<?> voClass, String declaredFieldName, MessageSource messageSource, String prefix, Locale locale) {
		Field field;
		try {
			field = voClass.getDeclaredField(declaredFieldName);
		} catch (Exception e) {
			throw new BHiveRuntimeException("Failed to get declared field.", e);
		}

		//기본 필드명
		String fieldName = field.getName();

		//@DisplayName
		String displayName = null;
		Display displayAnnotation = field.getAnnotation(Display.class);

		if(displayAnnotation != null) {
			displayName = displayAnnotation.name();
		} else {
			displayName = fieldName;
			if(StringUtil.isNotEmpty(prefix)) {
				displayName = prefix + displayName;
        	}
		}

		if(messageSource != null) {
			displayName = messageSource.getMessage(displayName, null, locale);
		}
		return displayName;
	}

	/**
	 * map의 값으로 VO의 필드값을 설정
	 * @param voClass 필드값을 설정한 VO
	 * @param map 키-값 맵
	 */
	public static void setVOProperties(Object voClass, Map<String, Object> map){
		try {
			for(Field field: voClass.getClass().getDeclaredFields()){
				if (field.getType().equals(DateTime.class)) {
					field.set(voClass, BHiveDateUtil.parseToDateTime(map.get(field.getName().toString()).toString()));
				} else if (field.getType().equals(Integer.class)) {
					field.set(voClass, map.get(field.getName()));
				} else {
					field.set(voClass, map.get(field.getName().toString()));
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	////////////////////////////////////////////////////////////////////////////////////
	/**
	 * VO클래스에서 PropertyDescriptor의 Name과 같은 Field나 getter의 ExcelColumn Annotaion 획득
	 * @param voClass VO 클래스
	 * @param descriptor PropertyDescriptor
	 * @return VO클래스에서 PropertyDescriptor의 Name과 같은 Field나 getter의 ExcelColumn Annotaion
	 */
	public static ExcelColumn getExcelColumnAnnotation(Class<?> voClass, PropertyDescriptor descriptor) {
		String propName = descriptor.getName();
		ExcelColumn annotation = null;
		//1. from Field
		try {
			Field field = voClass.getDeclaredField(propName);
			annotation = field.getAnnotation(ExcelColumn.class);
			if(annotation != null) {
				return annotation;
			}
		} catch (SecurityException e) {
		} catch (NoSuchFieldException e) {
		}
		//2. getter Method
		Method method = PropertyUtils.getReadMethod(descriptor);
		if(method != null) {
			annotation = method.getAnnotation(ExcelColumn.class);
			if(annotation != null) {
				return annotation;
			}
		}
		return null;
	}
	/**
	 * ExcelColumn Annotation의 order 속성으로 정렬된 필드-순서 맵을 획득
	 * @param voClass 대상 VO
	 * @return ExcelCoumn Annotation의 order 속성으로 정렬된 필드-순서 맵
	 */
	public static Map<String, Integer> getExcelColumnOrderMap(Class<?> voClass) {
		Map<String, Integer> voFieldOrderMap = new HashMap<String, Integer>();

		PropertyDescriptor[] descriptors = PropertyUtils.getPropertyDescriptors(voClass);
		for (PropertyDescriptor descriptor : descriptors) {
			ExcelColumn excelColumnAnnotation = getExcelColumnAnnotation(voClass, descriptor);
			if(excelColumnAnnotation == null || excelColumnAnnotation.ignore()) {
				continue;
			}

			String columnName = descriptor.getName();
			//노출 순서
			int excelColumnOrder = Integer.MAX_VALUE;
			excelColumnOrder = excelColumnAnnotation.order();

			voFieldOrderMap.put(columnName, excelColumnOrder);
		}

		voFieldOrderMap = BHiveCollectionUtil.sortByValues(voFieldOrderMap);
		return voFieldOrderMap;
	}

	/**
	 * ExcelColumn Annotation의 groupname 속성으로 필드-필드그룹명 맵을 획득 (기본로케일)
	 * @param voClass 대상 VO
	 * @return ExcelColumn Annotation의 groupname 속성으로 필드-필드그룹명 맵
	 */
	public static Map<String, String> getExcelColumnGroupNameMap(Class<?> voClass) {
		return getExcelColumnGroupNameMap(voClass, null, null, Locale.getDefault());
	}

	/**
	 * ExcelColumn Annotation의 groupname 속성으로 필드-필드그룹명 맵을 획득
	 * @param voClass 대상 VO
	 * @param messageSource 메시지 소스
	 * @param prefix 메시지소스로 메시지 변환처리시 필드 앞에 붙일 Prefix (예: com.col.)
	 * @param locale 메시지 적용 로케일
	 * @return ExcelColumn Annotation의 groupname 속성으로 필드-필드그룹명 맵
	 */
	public static Map<String, String> getExcelColumnGroupNameMap(Class<?> voClass, MessageSource messageSource, String prefix, Locale locale) {
		Map<String, String> voFieldNameMap = new HashMap<String, String>();

		PropertyDescriptor[] descriptors = PropertyUtils.getPropertyDescriptors(voClass);
		for (PropertyDescriptor descriptor : descriptors) {
			ExcelColumn excelColumnAnnotation = getExcelColumnAnnotation(voClass, descriptor);
			if(excelColumnAnnotation == null || excelColumnAnnotation.ignore()) {
				continue;
			}

			//기본 필드명
			String columnName = descriptor.getName();
			//@ExcelColumn.groupname
			String excelColumnName = null;
			excelColumnName = excelColumnAnnotation.groupname();
			if(excelColumnName == null) {
				excelColumnName = columnName;
				if(StringUtil.isNotEmpty(prefix)) {
					excelColumnName = prefix + excelColumnName;
	        	}
				excelColumnName = excelColumnName.toLowerCase();
			}
			if(messageSource != null) {
				excelColumnName = messageSource.getMessage(excelColumnName, null, locale);
			}
			voFieldNameMap.put(columnName, excelColumnName);
		}

		return voFieldNameMap;
	}

	/**
	 * ExcelColumn Annotation의 name 속성으로 필드-필드명 맵을 획득 (기본로케일)
	 * @param voClass 대상 VO
	 * @return ExcelColumn Annotation의 name 속성으로 필드-필드명 맵
	 */
	public static Map<String, String> getExcelColumnNameMap(Class<?> voClass) {
		return getExcelColumnNameMap(voClass, null, null, Locale.getDefault());
	}

	/**
	 * ExcelColumn Annotation의 name 속성으로 필드-필드명 맵을 획득
	 * @param voClass 대상 VO
	 * @param messageSource 메시지 소스
	 * @param prefix 메시지소스로 메시지 변환처리시 필드 앞에 붙일 Prefix (예: com.col.)
	 * @param locale 메시지 적용 로케일
	 * @return ExcelColumn Annotation의 name 속성으로 필드-필드명 맵
	 */
	public static Map<String, String> getExcelColumnNameMap(Class<?> voClass, MessageSource messageSource, String prefix, Locale locale) {
		Map<String, String> voFieldNameMap = new HashMap<String, String>();

		PropertyDescriptor[] descriptors = PropertyUtils.getPropertyDescriptors(voClass);
		for (PropertyDescriptor descriptor : descriptors) {
			ExcelColumn excelColumnAnnotation = getExcelColumnAnnotation(voClass, descriptor);
			if(excelColumnAnnotation == null || excelColumnAnnotation.ignore()) {
				continue;
			}

			//기본 필드명
			String columnName = descriptor.getName();
			//@ExcelColumn.name
			String excelColumnName = null;
			excelColumnName = excelColumnAnnotation.name();
			if(StringUtil.isEmpty(excelColumnName)) {
				excelColumnName = columnName;
				if(StringUtil.isNotEmpty(prefix)) {
					excelColumnName = prefix + excelColumnName;
	        	}
				excelColumnName = excelColumnName.toLowerCase();
			}
			if(messageSource != null) {
				excelColumnName = messageSource.getMessage(excelColumnName, null, locale);
			}
			voFieldNameMap.put(columnName, excelColumnName);
		}

		return voFieldNameMap;
	}

	/**
	 * ExcelColumn Annotation의 필드-Annotation 맵을 획득
	 * @param voClass 대상 VO
	 * @return ExcelCoumn Annotation의 필드-Annotation 맵
	 */
	public static Map<String, ExcelColumn> getExcelColumnAnnotationMap(Class<?> voClass) {
		Map<String, ExcelColumn> voFieldOrderMap = new HashMap<String, ExcelColumn>();

		PropertyDescriptor[] descriptors = PropertyUtils.getPropertyDescriptors(voClass);
		for (PropertyDescriptor descriptor : descriptors) {
			ExcelColumn excelColumnAnnotation = getExcelColumnAnnotation(voClass, descriptor);
			if(excelColumnAnnotation == null || excelColumnAnnotation.ignore()) {
				continue;
			}

			String columnName = descriptor.getName();
			voFieldOrderMap.put(columnName, excelColumnAnnotation);
		}
		return voFieldOrderMap;
	}
}
