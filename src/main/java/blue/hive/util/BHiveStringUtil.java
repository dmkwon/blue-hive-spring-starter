package blue.hive.util;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import blue.hive.util.anyframe.StringUtil;

/**
 * String 관련 유틸
 * 
 * @author DongMan Kwon <a href="mailto:dmkwon@intellicode.co.kr">dmkwon@intellicode.co.kr</a>
 */
public class BHiveStringUtil {

	private final static Logger logger = LoggerFactory.getLogger(BHiveStringUtil.class);

	/**
	 * 파일의 bytes 크기를 읽기좋게 변경 ("B", "KB", "MB", "GB", "TB") 
	 * @param size 파일 크기
	 * @return 파일 크기 표시용 문자열
	 */
	public static String readableFileSize(long size) {
		if(size <= 0) return "0";
		final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
		int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
		return new DecimalFormat("#,##0.#").format(size/Math.pow(1024, digitGroups)) + " " + units[digitGroups];
	}

	/**
	 *  br 태그를 제외한 모든 태그를 escape처리
	 */
	public static String htmlEscapeExcludeBR(String text) {
		if(StringUtil.isEmpty(text)) {
			return text;
		}
		return StringUtil.htmlEscape(text.replace("\n", "<br/>\n")).replace("&lt;br/&gt;", "<br/>");
	}


	/**
	 * 문자열에 대문자 포함여부
	 */
	public static boolean isPatternUpperCaseInclude(String str) {
		Pattern p = Pattern.compile(".*[A-Z].*", Pattern.UNICODE_CASE);
		Matcher m = p.matcher(str);
		return m.matches();
	}

	/**
	 * 문자열에 소문자 포함여부
	 */
	public static boolean isPatternLowerCaseInclude(String str) {
		Pattern p = Pattern.compile(".*[a-z].*", Pattern.UNICODE_CASE);
		Matcher m = p.matcher(str);
		return m.matches();
	}

	/** 연속된 숫자, 반목된 문자(숫자) 비허용 체크 */
	public static boolean validateBadSequenceLength(String pw, Integer badSequenceLength/* 3 */) {
		if(badSequenceLength == null) {
			badSequenceLength = 3;
		}
		String numbers = "01234567890";
		int start = badSequenceLength - 1;
		String seq = "_" + pw.substring(0, start);
		for (int i = start; i < pw.length(); i++) {
			seq = seq.substring(1) + pw.charAt(i);
			logger.debug("seq: {}", seq);
			if (numbers.indexOf(seq) > -1) {
				logger.debug("sequencial pw invalid.");
				return false; // sequencial
			}
			if (seq.replace(seq.substring(0, 1), "").equals("")) {
				logger.debug("equivalant pw invalid.");
				return false; // equivalant
			}
		}
		return true;
	};	
}
