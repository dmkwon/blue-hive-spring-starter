package blue.hive.spring.web.bind.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import blue.hive.spring.web.servlet.mvc.method.annotation.BHiveSecuredRequestResponseBodyMethodProcessor;

/**
 * {@link ResponseBody}와 같지만 Response Body를 송신전 복호화 처리를 해야하는 경우의 SecuredResponseBody Annotation
 * 
 * {@link RequestResponseBodyMethodProcessor}를 수정한 {@link BHiveSecuredRequestResponseBodyMethodProcessor}에서 처리
 * 
 * @author DongMan Kwon <a href="mailto:dmkwon@intellicode.co.kr">dmkwon@intellicode.co.kr</a>
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SecuredResponseBody {

}

