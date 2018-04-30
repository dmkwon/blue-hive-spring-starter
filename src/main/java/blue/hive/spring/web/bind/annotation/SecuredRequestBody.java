package blue.hive.spring.web.bind.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import blue.hive.spring.web.servlet.mvc.method.annotation.BHiveSecuredRequestResponseBodyMethodProcessor;

/**
 * {@link RequestBody}와 같지만 Request Body를 수신전 복호화 처리를 해야하는 경우의 SecuredRequestBody Annotation
 * 
 * {@link RequestResponseBodyMethodProcessor}를 수정한 {@link BHiveSecuredRequestResponseBodyMethodProcessor}에서 처리
 * 
 * @author DongMan Kwon <a href="mailto:dmkwon@intellicode.co.kr">dmkwon@intellicode.co.kr</a>
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SecuredRequestBody {

	/**
	 * Whether body content is required.
	 * <p>Default is {@code true}, leading to an exception thrown in case
	 * there is no body content. Switch this to {@code false} if you prefer
	 * {@code null} to be passed when the body content is {@code null}.
	 * @return boolean required 여부 
	 */
	boolean required() default true;

}
