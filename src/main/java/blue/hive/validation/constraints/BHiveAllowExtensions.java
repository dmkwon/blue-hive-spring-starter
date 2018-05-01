package blue.hive.validation.constraints;
import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;

import blue.hive.validation.constraints.impl.BHiveAllowExtensionsForListOfMultipartFile;
import blue.hive.validation.constraints.impl.BHiveAllowExtensionsForMultipartFile;

/**
 * 어노테이션된 멀티파트 파일의 확장자를 제한 ({@code null} or empty인 경우 검사안함) 
 * @author DongMan Kwon <a href="mailto:dmkwon@intellicode.co.kr">dmkwon@intellicode.co.kr</a>
 */
@Documented
@Constraint(validatedBy = { BHiveAllowExtensionsForMultipartFile.class, BHiveAllowExtensionsForListOfMultipartFile.class})
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
@Retention(RUNTIME)
@ReportAsSingleViolation
public @interface BHiveAllowExtensions {

	/** 
	 * 허용 확장자 목록 (comma-seperated) 
	 * @return 허용 확장자 목록
	 */
	String value(); //required property
	
	String message() default "{bhive.validation.constraint.BHiveallowextensions.message}";
	//String message() default "값은 {value}자 이하로 입력해야 합니다.";
	Class<?>[] groups() default { };
	Class<? extends Payload>[] payload() default { };	
	
	@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
	@Retention(RUNTIME)
	@Documented
	@interface List {
		BHiveAllowExtensions[] value();
	}
}
