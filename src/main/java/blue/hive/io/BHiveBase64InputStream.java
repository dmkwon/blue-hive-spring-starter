package blue.hive.io;

import java.io.InputStream;

import org.apache.commons.codec.binary.Base64InputStream;

/**
 * {@link Base64InputStream}의 MIME_CHUNK_SIZE마다 CHUNK_SEPARATOR를 삽입하는 동작의 설정을 바꾼 BHiveBase64InputStream
 *
 * 그냥 암호화시 사용하기 위해 한줄로 쭉 출력
 *
 * @author DongMan Kwon <a href="mailto:dmkwon@intellicode.co.kr">dmkwon@intellicode.co.kr</a>
 */
public class BHiveBase64InputStream extends Base64InputStream {

    /**
     * Chunk separator per RFC 2045 section 2.1.
     *
     * <p>
     * N.B. The next major release may break compatibility and make this field private.
     * </p>
     *
     * @see <a href="http://www.ietf.org/rfc/rfc2045.txt">RFC 2045 section 2.1</a>
     */
    static final byte[] CHUNK_SEPARATOR = {'\r', '\n'};

    /**
     *  MIME chunk size per RFC 2045 section 6.8.
     *
     * <p>
     * The {@value} character limit does not count the trailing CRLF, but counts all other characters, including any
     * equal signs.
     * </p>
     *
     * @see <a href="http://www.ietf.org/rfc/rfc2045.txt">RFC 2045 section 6.8</a>
     */
    public static final int MIME_CHUNK_SIZE = 76;

	public BHiveBase64InputStream(InputStream in) {
		super(in,
				false, //doEncode
				Integer.MAX_VALUE, //lineLength - default: MIME_CHUNK_SIZE
				CHUNK_SEPARATOR //lineSeparator - default: CHUNK_SEPARATOR
		);
	}
}
