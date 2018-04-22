package blue.hive.spring.http.converter;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import javax.mail.internet.MimeUtility;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.Assert;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;


/**
 * Spring의 기본 {@link org.springframework.http.converter.FormHttpMessageConverter} 계열이 MultiValueMap<String, ?>을 변환하여서
 * Map<String, ?>의 변환을 지원하기 위한 컨버터
 *
 * @author DongMan Kwon <dmkwon@intellicode.co.kr>
 *
 * @since 3.0
 * @see FormHttpMessageConverter
 */
public class BHiveMapHttpMessageConverter implements HttpMessageConverter<Map<String, ?>> {
	public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

	private static final byte[] BOUNDARY_CHARS =
			new byte[] {'-', '_', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', 'a', 'b', 'c', 'd', 'e', 'f', 'g',
					'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A',
					'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U',
					'V', 'W', 'X', 'Y', 'Z'};


	private Charset charset = DEFAULT_CHARSET;

	private Charset multipartCharset;

	private List<MediaType> supportedMediaTypes = new ArrayList<MediaType>();

	private List<HttpMessageConverter<?>> partConverters = new ArrayList<HttpMessageConverter<?>>();

	private final Random random = new Random();


	public BHiveMapHttpMessageConverter() {
		this.supportedMediaTypes.add(MediaType.APPLICATION_FORM_URLENCODED);
		this.supportedMediaTypes.add(MediaType.MULTIPART_FORM_DATA);

		this.partConverters.add(new ByteArrayHttpMessageConverter());
		StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter();
		stringHttpMessageConverter.setWriteAcceptCharset(false);
		this.partConverters.add(stringHttpMessageConverter);
		this.partConverters.add(new ResourceHttpMessageConverter());
	}


	/**
	 * Set the default character set to use for reading and writing form data when
	 * the request or response Content-Type header does not explicitly specify it.
	 * <p>By default this is set to "UTF-8".
	 */
	public void setCharset(Charset charset) {
		this.charset = charset;
	}

	/**
	 * Set the character set to use when writing multipart data to encode file
	 * names. Encoding is based on the encoded-word syntax defined in RFC 2047
	 * and relies on {@code MimeUtility} from "javax.mail".
	 * <p>If not set file names will be encoded as US-ASCII.
	 * @param multipartCharset the charset to use
	 * @since 4.1.1
	 * @see <a href="http://en.wikipedia.org/wiki/MIME#Encoded-Word">Encoded-Word</a>
	 */
	public void setMultipartCharset(Charset multipartCharset) {
		this.multipartCharset = multipartCharset;
	}

	/**
	 * Set the list of {@link MediaType} objects supported by this converter.
	 */
	public void setSupportedMediaTypes(List<MediaType> supportedMediaTypes) {
		this.supportedMediaTypes = supportedMediaTypes;
	}

	public List<MediaType> getSupportedMediaTypes() {
		return Collections.unmodifiableList(this.supportedMediaTypes);
	}

	/**
	 * Set the message body converters to use. These converters are used to
	 * convert objects to MIME parts.
	 */
	public void setPartConverters(List<HttpMessageConverter<?>> partConverters) {
		Assert.notEmpty(partConverters, "'partConverters' must not be empty");
		this.partConverters = partConverters;
	}

	/**
	 * Add a message body converter. Such a converter is used to convert objects
	 * to MIME parts.
	 */
	public void addPartConverter(HttpMessageConverter<?> partConverter) {
		Assert.notNull(partConverter, "'partConverter' must not be null");
		this.partConverters.add(partConverter);
	}


	public boolean canRead(Class<?> clazz, MediaType mediaType) {
		if (!Map.class.isAssignableFrom(clazz)) {
			return false;
		}
		if (mediaType == null) {
			return true;
		}
		for (MediaType supportedMediaType : getSupportedMediaTypes()) {
			// We can't read multipart....
			if (!supportedMediaType.equals(MediaType.MULTIPART_FORM_DATA) && supportedMediaType.includes(mediaType)) {
				return true;
			}
		}
		return false;
	}

	public boolean canWrite(Class<?> clazz, MediaType mediaType) {
		if (!Map.class.isAssignableFrom(clazz)) {
			return false;
		}
		if (mediaType == null || MediaType.ALL.equals(mediaType)) {
			return true;
		}
		for (MediaType supportedMediaType : getSupportedMediaTypes()) {
			if (supportedMediaType.isCompatibleWith(mediaType)) {
				return true;
			}
		}
		return false;
	}

	public Map<String, String> read(Class<? extends Map<String, ?>> clazz,
			HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {

		MediaType contentType = inputMessage.getHeaders().getContentType();
		Charset charset = (contentType.getCharset() != null ? contentType.getCharset() : this.charset);
		String body = StreamUtils.copyToString(inputMessage.getBody(), charset);

		String[] pairs = StringUtils.tokenizeToStringArray(body, "&");
		Map<String, String> result = new HashMap<String, String>(pairs.length);
		for (String pair : pairs) {
			int idx = pair.indexOf('=');
			if (idx == -1) {
				result.put(URLDecoder.decode(pair, charset.name()), null);
			}
			else {
				String name = URLDecoder.decode(pair.substring(0, idx), charset.name());
				String value = URLDecoder.decode(pair.substring(idx + 1), charset.name());
				result.put(name, value);
			}
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public void write(Map<String, ?> map, MediaType contentType, HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException {

		if (!isMultipart(map, contentType)) {
			writeForm((Map<String, String>) map, contentType, outputMessage);
		}
		else {
			writeMultipart((Map<String, Object>) map, outputMessage);
		}
	}


	private boolean isMultipart(Map<String, ?> map, MediaType contentType) {
		if (contentType != null) {
			return MediaType.MULTIPART_FORM_DATA.includes(contentType);
		}
					
		for (Map.Entry<String, ?> name : map.entrySet()) {
			Object value = name.getValue();
			if (value != null && !(value instanceof String)) {
				return true;
			}
		}
		return false;
	}

	private void writeForm(Map<String, String> form, MediaType contentType, HttpOutputMessage outputMessage)
			throws IOException {

		Charset charset;
		if (contentType != null) {
			outputMessage.getHeaders().setContentType(contentType);
			charset = contentType.getCharset() != null ? contentType.getCharset() : this.charset;
		}
		else {
			outputMessage.getHeaders().setContentType(MediaType.APPLICATION_FORM_URLENCODED);
			charset = this.charset;
		}
		StringBuilder builder = new StringBuilder();
		for(Iterator<Entry<String, String>> nameIterator = form.entrySet().iterator(); nameIterator.hasNext();) {
			Entry<String, String> name = nameIterator.next();
			String value = name.getValue();
			builder.append(URLEncoder.encode(name.getValue(), charset.name()));
			if (value != null) {
				builder.append('=');
				builder.append(URLEncoder.encode(value, charset.name()));
			}
			if (nameIterator.hasNext()) {
				builder.append('&');
			}
		}

		byte[] bytes = builder.toString().getBytes(charset.name());
		outputMessage.getHeaders().setContentLength(bytes.length);
		StreamUtils.copy(bytes, outputMessage.getBody());
	}

	private void writeMultipart(Map<String, Object> parts, HttpOutputMessage outputMessage) throws IOException {
		byte[] boundary = generateMultipartBoundary();
		Map<String, String> parameters = Collections.singletonMap("boundary", new String(boundary, "US-ASCII"));

		MediaType contentType = new MediaType(MediaType.MULTIPART_FORM_DATA, parameters);
		outputMessage.getHeaders().setContentType(contentType);

		writeParts(outputMessage.getBody(), parts, boundary);
		writeEnd(outputMessage.getBody(), boundary);
	}

	private void writeParts(OutputStream os, Map<String, Object> parts, byte[] boundary) throws IOException {
		for (Map.Entry<String, Object> entry : parts.entrySet()) {
			String name = entry.getKey();
			Object part = entry.getValue();
			if (part != null) {
				writeBoundary(os, boundary);
				writePart(name, getHttpEntity(part), os);
				writeNewLine(os);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void writePart(String name, HttpEntity<?> partEntity, OutputStream os) throws IOException {
		Object partBody = partEntity.getBody();
		Class<?> partType = partBody.getClass();
		HttpHeaders partHeaders = partEntity.getHeaders();
		MediaType partContentType = partHeaders.getContentType();
		for (HttpMessageConverter<?> messageConverter : this.partConverters) {
			if (messageConverter.canWrite(partType, partContentType)) {
				HttpOutputMessage multipartMessage = new MultipartHttpOutputMessage(os);
				multipartMessage.getHeaders().setContentDispositionFormData(name, getFilename(partBody));
				if (!partHeaders.isEmpty()) {
					multipartMessage.getHeaders().putAll(partHeaders);
				}
				((HttpMessageConverter<Object>) messageConverter).write(partBody, partContentType, multipartMessage);
				return;
			}
		}
		throw new HttpMessageNotWritableException("Could not write request: no suitable HttpMessageConverter " +
				"found for request type [" + partType.getName() + "]");
	}


	/**
	 * Generate a multipart boundary.
	 * <p>The default implementation returns a random boundary.
	 * Can be overridden in subclasses.
	 */
	protected byte[] generateMultipartBoundary() {
		byte[] boundary = new byte[this.random.nextInt(11) + 30];
		for (int i = 0; i < boundary.length; i++) {
			boundary[i] = BOUNDARY_CHARS[this.random.nextInt(BOUNDARY_CHARS.length)];
		}
		return boundary;
	}

	/**
	 * Return an {@link HttpEntity} for the given part Object.
	 * @param part the part to return an {@link HttpEntity} for
	 * @return the part Object itself it is an {@link HttpEntity},
	 * or a newly built {@link HttpEntity} wrapper for that part
	 */
	protected HttpEntity<?> getHttpEntity(Object part) {
		if (part instanceof HttpEntity) {
			return (HttpEntity<?>) part;
		}
		else {
			return new HttpEntity<Object>(part);
		}
	}

	/**
	 * Return the filename of the given multipart part. This value will be used for the
	 * {@code Content-Disposition} header.
	 * <p>The default implementation returns {@link Resource#getFilename()} if the part is a
	 * {@code Resource}, and {@code null} in other cases. Can be overridden in subclasses.
	 * @param part the part to determine the file name for
	 * @return the filename, or {@code null} if not known
	 */
	protected String getFilename(Object part) {
		if (part instanceof Resource) {
			Resource resource = (Resource) part;
			String filename = resource.getFilename();
			if (this.multipartCharset != null) {
				filename = MimeDelegate.encode(filename, this.multipartCharset.name());
			}
			return filename;
		}
		else {
			return null;
		}
	}


	private void writeBoundary(OutputStream os, byte[] boundary) throws IOException {
		os.write('-');
		os.write('-');
		os.write(boundary);
		writeNewLine(os);
	}

	private void writeEnd(OutputStream os, byte[] boundary) throws IOException {
		os.write('-');
		os.write('-');
		os.write(boundary);
		os.write('-');
		os.write('-');
		writeNewLine(os);
	}

	private void writeNewLine(OutputStream os) throws IOException {
		os.write('\r');
		os.write('\n');
	}

	/**
	 * Inner class to avoid a hard dependency on the JavaMail API.
	 */
	private static class MimeDelegate {

		public static String encode(String value, String charset) {
			try {
				return MimeUtility.encodeText(value, charset, null);
			}
			catch (UnsupportedEncodingException ex) {
				throw new IllegalStateException(ex);
			}
		}
	}
}
