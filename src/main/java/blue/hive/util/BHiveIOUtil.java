package blue.hive.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.springframework.util.StringUtils;

/**
 * IO 관련 유틸
 *
 * @author DongMan Kwon <a href="mailto:dmkwon@intellicode.co.kr">dmkwon@intellicode.co.kr</a>
 */
public class BHiveIOUtil {

	/**
	 * InputStream에서 문자열을 끝까지 읽는다. (UTF-8 인코딩 적용)
	 * @param is Inputstream 객체
	 * @return String 읽은 문자열
	 * @throws IOException throws IO Exception
	 */
	public static String readStringToEnd(InputStream is) throws IOException {
		return readStringToEnd(is, "UTF-8");
	}

	/**
	 * InputStream에서 문자열을 끝까지 읽는다. (주어진 인코딩 적용)
	 * @param is InputStream 객체
	 * @param charsetName character set 값
	 * @return 읽은 문자열
	 * @throws IOException throws io exception
	 */
	public static String readStringToEnd(InputStream is, String charsetName) throws IOException {
		String body = new String(readBytesToEnd(is), Charset.forName(charsetName));
		return StringUtils.trimTrailingWhitespace(body);
	}

	/**
	 * InputStream에서 Byte[]을 끝까지 읽는다.
	 * @param is inputStream 객체
	 * @return byte[] array of byte
	 * @throws IOException throws io exception
	 */
	public static byte[] readBytesToEnd(InputStream is) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int length = 0;
		while((length = is.read(buffer)) != -1) {
			baos.write(buffer, 0, length);
		}
		return baos.toByteArray();
	}

	/**
	 * File 객체가 directory 인지 체크
	 * @param file File 객체
	 * @return boolean is plain Directory's check
	 * @throws IOException throws io exception
	 */
	public static boolean isPlainDir(File file) throws IOException {
		return file.isDirectory() && !isSymbolicLink(file);
	}

	/**
	 * File 객체가 symbolic link 인지 체크
	 * @param file File 객체
	 * @return boolean is symbolic link's check
	 * @throws IOException throws io exception
	 */
	public static boolean isSymbolicLink(File file) throws IOException {
		if (file == null) {
			throw new NullPointerException("File must not be null");
		}
		File canon = null;
		if (file.getParent() == null) {
			canon = file;
		} else {
			File parentFile = file.getParentFile();
			if(parentFile != null) {
				File canonDir = parentFile.getCanonicalFile();
				canon = new File(canonDir, file.getName());
			} else {
				throw new NullPointerException("File must not be null");
			}
		}
		return !canon.getCanonicalFile().equals(canon.getAbsoluteFile());
	}
}
