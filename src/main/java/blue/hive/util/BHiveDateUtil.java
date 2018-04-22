package blue.hive.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import blue.hive.exception.BHiveInvalidFormatException;
import blue.hive.util.anyframe.StringUtil;

/**
 * Joda DateTime, Java Date 관련 유틸
 *
 * @author DongMan Kwon <dmkwon@intellicode.co.kr>
 */
public class BHiveDateUtil {

	/**
	 * 문자열을 Joda DateTime으로 변환
	 * @param dateString yyyy/MM/dd, yyyy-MM-dd, yyyyMMdd, yyyyMMddHHmmss, yyyy-MM-dd HH:mm:ss, yyyy-MM-dd'T'HH:mm:ss.SSSZ
	 * @return Joda DateTime
	 * @throws FmsInvalidFormatException 요청된 DateTime 문자열의 형식이 올바르지 않은 경우 발생
	 */
	public static DateTime parseToDateTime(String dateString) {
		return parseToDateTime(dateString, true);
	}

	/**
	 * 문자열을 Joda DateTime으로 변환
	 * @param dateString yyyy-MM-dd, yyyy/MM/dd, yyyyMMdd, yyyyMMddHHmmss, yyyy-MM-dd HH:mm:ss, yyyy-MM-dd'T'HH:mm:ss.SSSZ
	 * @param throwException 처리중 예외가 발생하면 throw 할 것인지 여부
	 * @return Joda DateTime (throwException이 false이면 변환실패시 null반환)
	 * @throws FmsInvalidFormatException 요청된 DateTime 문자열의 형식이 올바르지 않은 경우 발생 (throwException이 true일때만...)
	 */
	public static DateTime parseToDateTime(String dateString, Boolean throwException) {
		if(StringUtil.isEmptyTrimmed(dateString)) {
			return null;
		}

		DateTimeFormatter dateFormatter = null;

		if(dateString.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
			dateFormatter = DateTimeFormat.forPattern("yyyy-MM-dd");
		} else if(dateString.matches("^\\d{4}/\\d{2}/\\d{2}$")) {
			dateFormatter = DateTimeFormat.forPattern("yyyy/MM/dd");
		} else if(dateString.matches("^\\d{8}$")) {
			dateFormatter = DateTimeFormat.forPattern("yyyyMMdd");
		} else if(dateString.matches("^\\d{4}\\d{2}\\d{2}\\d{2}\\d{2}\\d{2}$")) {
			dateFormatter = DateTimeFormat.forPattern("yyyyMMddHHmmss");
		} else if(dateString.matches("^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$")) {
			dateFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
		} else if(dateString.matches("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\x2E\\d{3}[+-]{1}\\d{2}:\\d{2}$")) {
			dateFormatter = ISODateTimeFormat.dateTime();
		} else {
			if(throwException) {
				throw new BHiveInvalidFormatException("Invalid DateTime Format. ['" + dateString + "']");
			} else {
				return null;
			}
		}
		return dateFormatter.parseDateTime(dateString);
	}
	
	/**
	 * 문자열을 Joda LocalDateTime으로 변환
	 * @param dateString yyyy-MM-dd, yyyy/MM/dd, yyyyMMdd, yyyyMMddHHmmss, yyyy-MM-dd HH:mm:ss, yyyy-MM-dd'T'HH:mm:ss, yyyy-MM-dd'T'HH:mm:ss.SSS
	 * @return Joda DateTime
	 * @throws FmsInvalidFormatException 요청된 DateTime 문자열의 형식이 올바르지 않은 경우 발생
	 */
	public static LocalDateTime parseToLocalDateTime(String dateString) {
		return parseToLocalDateTime(dateString, true);
	}

	/**
	 * 문자열을 Joda LocalDateTime으로 변환
	 * @param dateString yyyy-MM-dd, yyyy/MM/dd, yyyyMMdd, yyyyMMddHHmmss, yyyy-MM-dd HH:mm:ss, yyyy-MM-dd'T'HH:mm:ss, yyyy-MM-dd'T'HH:mm:ss.SSS
	 * @param throwException 처리중 예외가 발생하면 throw 할 것인지 여부
	 * @return Joda DateTime (throwException이 false이면 변환실패시 null반환)
	 * @throws FmsInvalidFormatException 요청된 DateTime 문자열의 형식이 올바르지 않은 경우 발생 (throwException이 true일때만...)
	 */
	public static LocalDateTime parseToLocalDateTime(String dateString, Boolean throwException) {
		if(StringUtil.isEmptyTrimmed(dateString)) {
			return null;
		}

		DateTimeFormatter dateFormatter = null;

		if(dateString.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
			dateFormatter = DateTimeFormat.forPattern("yyyy-MM-dd");
		} else if(dateString.matches("^\\d{4}/\\d{2}/\\d{2}$")) {
			dateFormatter = DateTimeFormat.forPattern("yyyy/MM/dd");
		} else if(dateString.matches("^\\d{8}$")) {
			dateFormatter = DateTimeFormat.forPattern("yyyyMMdd");
		} else if(dateString.matches("^\\d{4}\\d{2}\\d{2}\\d{2}\\d{2}\\d{2}$")) {
			dateFormatter = DateTimeFormat.forPattern("yyyyMMddHHmmss");
		} else if(dateString.matches("^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$")) {
			dateFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
		} else if(dateString.matches("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\x2E\\d{3}$")) {
			dateFormatter = ISODateTimeFormat.dateHourMinuteSecondMillis();
		} else {
			if(throwException) {
				throw new BHiveInvalidFormatException("Invalid DateTime Format. ['" + dateString + "']");
			} else {
				return null;
			}
		}
		return dateFormatter.parseLocalDateTime(dateString);
	}
	
	
	/**
	 * 문자열을 Joda LocalDateTime으로 변환
	 * @param dateString
	 * @param format date Format
	 * @return Joda DateTime (throwException이 false이면 변환실패시 null반환)
	 * @throws FmsInvalidFormatException 요청된 DateTime 문자열의 형식이 올바르지 않은 경우 발생 (throwException이 true일때만...)
	 */
	public static LocalDateTime parseToLocalDateTime(String dateString, String format) {
		if(StringUtil.isEmptyTrimmed(dateString)) {
			return null;
		}

		DateTimeFormatter dateFormatter = DateTimeFormat.forPattern(format);
		return dateFormatter.parseLocalDateTime(dateString);
	}
	
	/**
	 * Date객체를 주어진 포맷 문자열로 획득
	 * @param date Date 객체
	 * @param formatString 포맷 문자열
	 * @return 주어진 Date객체를 주어진 포맷 문자열로 획득
	 */
	public static String formatDate(Date date, String formatString) {
		SimpleDateFormat formatter = new SimpleDateFormat(formatString);
		return formatter.format(date);
	}

	/** yyyyMMdd로 포매팅 */
	public static String yyyyMMdd(Date date) { return formatDate(date, "yyyyMMdd"); }
	/** yyyyMMddHHmmss로 포매팅 */
	public static String yyyyMMddHHmmss(Date date) { return formatDate(date, "yyyyMMddHHmmss"); }
	/** yyyyMMddHHmmssSSSS로 포매팅 */
	public static String yyyyMMddHHmmssSSSS(Date date) { return formatDate(date, "yyyyMMddHHmmssSSSS"); }

	/**
	 * 현재 Date를 주어진 포맷 문자열로 획득
	 * @param formatString 포맷 문자열
	 * @return 현재 Date를 주어진 포맷 문자열로 획득
	 */
	public static String formatNowDate(String formatString) {
		return formatDate(new Date(), formatString);
	}

//	/** 현재 HH */
//	public static String HH() { return formatNowDate("HH"); }
	/** 현재 yyyyMM */
	public static String yyyy() { return formatNowDate("yyyy"); }
	/** 현재 yyyyMM */
	public static String yyyyMM() { return formatNowDate("yyyyMM"); }
	/** 현재 yyyyMMdd */
	public static String yyyyMMdd() { return formatNowDate("yyyyMMdd"); }
	/** 현재 yyyyMMddHHmmss */
	public static String yyyyMMddHHmmss() { return formatNowDate("yyyyMMddHHmmss"); }
	/** 현재 yyyyMMddHHmmssSSSS */
	public static String yyyyMMddHHmmssSSSS() { return formatNowDate("yyyyMMddHHmmssSSSS"); }
	/** 현재 yyyyMMddHypenHHmmss */
	public static String yyyyMMddHypenHHmmss() { return formatNowDate("yyyyMMdd-HHmmss"); }

	/** 현재 Date의 주어진 날짜만큼 이전/다음 날을 획득 */
	private static Date getDate(int dayAmount) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, dayAmount);
		Date date = cal.getTime();
		return date;
	}

	/** 현재 Date의 주어진 날짜만큼 이전/다음 날의 yyyyMMdd를 획득 */
	public static String yyyyMMdd(int dayAmount) { return yyyyMMdd(getDate(dayAmount));	}
	
	/** LocalDateTime을 yyyy-MM-dd HH:mm:ss 포맷의 문자열로 반환 */
	public static String convertLocalDateTimeToString(LocalDateTime date) {
		String dateToString = "";
		if( null != date ) {
			dateToString = date.toString("yyyy-MM-dd HH:mm:ss");
		}
		return dateToString;
	}
	
	public static String convertLocalDateTimeToString(LocalDateTime date, String format) {
		String dateToString = "";
		if( null != date ) {
			dateToString = date.toString(format);
		}
		return dateToString;
	}
	
	// 두 날짜 사이 차이 
	public static int diffDate(LocalDateTime start, LocalDateTime end) {
		return Days.daysBetween(start.toLocalDate(), end.toLocalDate()).getDays();
	}
}
