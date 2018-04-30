package blue.hive.spring.web.servlet.mvc.method.annotation;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.lang.reflect.Type;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.Conventions;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.AbstractMessageConverterMethodProcessor;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;

import blue.hive.crypto.AES256Crypto;
import blue.hive.io.BHiveTeeInputStreamWrapper;
import blue.hive.io.BHiveTeeOutputStreamWrapper;
import blue.hive.spring.http.BHiveTeeHttpInputMessage;
import blue.hive.spring.http.BHiveTeeHttpOutputMessage;
import blue.hive.spring.web.bind.annotation.SecuredRequestBody;
import blue.hive.spring.web.bind.annotation.SecuredResponseBody;

/**
 * {@link RequestBody}, {@link ResponseBody}를 처리하는 {@link RequestResponseBodyMethodProcessor}와 유사하지만,
 * {@link SecuredRequestBody}, {@link SecuredResponseBody}를 암복호화하여 처리
 *
 * 상속등으로 사용하고, secretKey를 서비스에 맞춰서 설정하면 된다.
 *
 * Resolves method arguments annotated with {@code @SecuredRequestBody} and
 * handles return values from methods annotated with
 * {@code @SecuredResponseBody} by reading and writing to the body of the
 * request or response with an {@link HttpMessageConverter}.
 *
 * <p>
 * An {@code @SecuredRequestBody} method argument is also validated if it is
 * annotated with {@code @javax.validation.Valid}. In case of validation
 * failure, {@link MethodArgumentNotValidException} is raised and results in a
 * 400 response status code if {@link DefaultHandlerExceptionResolver} is
 * configured.
 *
 * @author DongMan Kwon <a href="mailto:dmkwon@intellicode.co.kr">dmkwon@intellicode.co.kr</a>
 * @author Arjen Poutsma
 * @author Rossen Stoyanchev
 * @since 3.1
 */
public abstract class BHiveSecuredRequestResponseBodyMethodProcessor extends AbstractMessageConverterMethodProcessor implements InitializingBean {

	protected String secretKey = "";
	public String getSecretKey() {
		return secretKey;
	}
	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if(StringUtils.isEmpty(secretKey)) {
			logger.info("!!!!!!!!! Secret Key is empty !!!!!!!!! => check set secretKey property.");
		}
	}

	public BHiveSecuredRequestResponseBodyMethodProcessor(List<HttpMessageConverter<?>> messageConverters) {
		super(messageConverters);
	}

	public BHiveSecuredRequestResponseBodyMethodProcessor(List<HttpMessageConverter<?>> messageConverters, ContentNegotiationManager contentNegotiationManager) {
		super(messageConverters, contentNegotiationManager);
	}

	public BHiveSecuredRequestResponseBodyMethodProcessor(List<HttpMessageConverter<?>> messageConverters, ContentNegotiationManager contentNegotiationManager, List<Object> responseBodyAdvice) {
		super(messageConverters, contentNegotiationManager, responseBodyAdvice);
	}

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		// dmkwon - SecuredRequestBody로 변경
		return parameter.hasParameterAnnotation(SecuredRequestBody.class);
	}

	@Override
	public boolean supportsReturnType(MethodParameter returnType) {
		// dmkwon - SecuredResponseBody로 변경
		return (AnnotationUtils.findAnnotation(returnType.getContainingClass(), SecuredResponseBody.class) != null || returnType.getMethodAnnotation(SecuredResponseBody.class) != null);
	}

	/**
	 * Throws MethodArgumentNotValidException if validation fails.
	 * @throws HttpMessageNotReadableException if {@link RequestBody#required()}
	 * is {@code true} and there is no body content or if there is no suitable
	 * converter to read the content with.
	 */
	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

		Object arg = readWithMessageConverters(webRequest, parameter, parameter.getGenericParameterType());
		String name = Conventions.getVariableNameForParameter(parameter);
		WebDataBinder binder = binderFactory.createBinder(webRequest, arg, name);
		if (arg != null) {
			validateIfApplicable(binder, parameter);
			if (binder.getBindingResult().hasErrors() && isBindExceptionRequired(binder, parameter)) {
				throw new MethodArgumentNotValidException(parameter, binder.getBindingResult());
			}
		}
		mavContainer.addAttribute(BindingResult.MODEL_KEY_PREFIX + name, binder.getBindingResult());
		return arg;
	}

	@Override
	protected <T> Object readWithMessageConverters(NativeWebRequest webRequest, MethodParameter methodParam, Type paramType) throws IOException, HttpMediaTypeNotSupportedException {

		final HttpServletRequest servletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
		HttpInputMessage inputMessage = new ServletServerHttpRequest(servletRequest);

		InputStream inputStream = inputMessage.getBody();
		if (inputStream == null) {
			return handleEmptyBody(methodParam);
		} else if (inputStream.markSupported()) {
			inputStream.mark(1);
			if (inputStream.read() == -1) {
				return handleEmptyBody(methodParam);
			}
			inputStream.reset();
		} else {
			final PushbackInputStream pushbackInputStream = new PushbackInputStream(inputStream);
			int b = pushbackInputStream.read();
			if (b == -1) {
				return handleEmptyBody(methodParam);
			} else {
				pushbackInputStream.unread(b);
			}

			//dmkwon remark
			//inputMessage = new ServletServerHttpRequest(servletRequest) {
			//	@Override
			//	public InputStream getBody() throws IOException {
			//		// Form POST should not get here
			//		return pushbackInputStream;
			//	}
			//};

			//dmkwon add start
			AES256Crypto crypto = new AES256Crypto(secretKey);
			BHiveTeeInputStreamWrapper teeInputStream = new BHiveTeeInputStreamWrapper(pushbackInputStream); //원본 요청메시지를 Tee처리
			final InputStream cipherInputStream = crypto.getDecryptChiperInputStream(teeInputStream.getInputStream());
			inputMessage = new ServletServerHttpRequest(servletRequest) {
				@Override
				public InputStream getBody() throws IOException {
					// Form POST should not get here
					return cipherInputStream;
				}
			};
			BHiveTeeHttpInputMessage teeDecryptedHttpInputMessage = new BHiveTeeHttpInputMessage(inputMessage); //복호화된 요청메시지를 Tee처리

			try {
				Object result = super.readWithMessageConverters(teeDecryptedHttpInputMessage, methodParam, paramType);
				cipherInputStream.close();

				String requestBody = teeInputStream.getTeeInputString();
				String decryptedRequestBody = teeDecryptedHttpInputMessage.getTeeInputString(); //!!!!!!!!!!!!
				logger.debug("\r\n  >>>> READ " + requestBody
						+ "\r\n  >>>>      => " + decryptedRequestBody
						+ "\r\n  >>>>      => " + result);
				return result;
			} catch (Exception ex) {
				String requestBody = teeInputStream.getTeeInputString();
				logger.debug("\r\n  >>>> READ {}" + requestBody
						+ "\r\n  >>>>      => FAILED TO READ CRYPTO REQUEST BODY!! exception: " + ex.getMessage());
				throw new HttpMessageNotReadableException("FAILED TO READ CRYPTO REQUEST BODY!! requestBody: " + requestBody, ex);
			}
			//dmkwon add end
		}

		return super.readWithMessageConverters(inputMessage, methodParam, paramType);
	}

	private Object handleEmptyBody(MethodParameter param) {
		//dmkwon - SecuredRequestBody로 변경
		SecuredRequestBody body = param.getParameterAnnotation(SecuredRequestBody.class);
		if(body != null && body.required()) {
			throw new HttpMessageNotReadableException("Required request body content is missing: " + param);
		}
		return null;
	}

	@Override
	public void handleReturnValue(Object returnValue, MethodParameter returnType,
			ModelAndViewContainer mavContainer, NativeWebRequest webRequest)
					throws IOException, HttpMediaTypeNotAcceptableException {

		mavContainer.setRequestHandled(true);

		// Try even with null return value. ResponseBodyAdvice could get involved.
		writeWithMessageConverters(returnValue, returnType, webRequest);
	}

	@Override
	protected ServletServerHttpResponse createOutputMessage(NativeWebRequest webRequest) {
		HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);
		return new ServletServerHttpResponse(response);

		//		//dmkwon add start
		//		AbleTeeHttpServletResponse teeResponse = new AbleTeeHttpServletResponse(response); //원본 응답을 가로채기위한 Tee처리
		//		return new ServletServerHttpResponse(teeResponse);
		//		//dmkwon add end
	}

	@Override
	protected <T> void writeWithMessageConverters(T returnValue, MethodParameter returnType, NativeWebRequest webRequest)
			throws IOException, HttpMediaTypeNotAcceptableException {

		ServletServerHttpRequest inputMessage = createInputMessage(webRequest);
		ServletServerHttpResponse outputMessage = createOutputMessage(webRequest);
		//writeWithMessageConverters(returnValue, returnType, inputMessage, outputMessage);

		//dmkwon add start
		BHiveTeeHttpOutputMessage teeHttpOutputMessage = new BHiveTeeHttpOutputMessage(outputMessage); //최종 응답메시지를 Tee처리
		AES256Crypto crypto = new AES256Crypto(secretKey);
		OutputStream cipherOutputStream = crypto.getEncryptChiperOutputStream(teeHttpOutputMessage.getBody());
		final BHiveTeeOutputStreamWrapper teeOutputStream = new BHiveTeeOutputStreamWrapper(cipherOutputStream); //Plain 응답메시지를 Tee처리
		HttpServletResponse response = outputMessage.getServletResponse();
		ServletServerHttpResponse outputMessageWrap = new ServletServerHttpResponse(response) {
			@Override
			public OutputStream getBody() throws IOException {
				super.getBody(); //writeHeaders() 처리위임
				return teeOutputStream.getOutputStream();
			}
		};

		try {
			writeWithMessageConverters(returnValue, returnType, inputMessage, outputMessageWrap);
			cipherOutputStream.close();
			String responseBodyPlain = teeOutputStream.getTeeOutputString();
			String responseBody = teeHttpOutputMessage.getTeeOutputString();
			logger.debug("\r\n  >>>> WRITE " + responseBodyPlain
					+ "\r\n  >>>>      => " + responseBody);
		} catch (Exception ex) {
			String responseBody = teeHttpOutputMessage.getTeeOutputString();
			logger.debug("\r\n  >>>> WRITE {}" + responseBody
					+ "\r\n  >>>>      => FAILED TO WRITE CRYPTO RESPONSE BODY!! exception: " + ex.getMessage());
			throw new HttpMessageNotWritableException("FAILED TO WRITE CRYPTO RESPONSE BODY!!", ex);
		}
		//dmkwon add end
	}

}