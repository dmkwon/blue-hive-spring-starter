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

import blue.hive.validation.constraints.impl.BHiveMaxLengthValidatorForCharSequence;
import blue.hive.validation.constraints.impl.BHiveMaxLengthValidatorForInteger;
import blue.hive.validation.constraints.impl.BHiveMaxLengthValidatorForLong;

/**
 * 어노테이션된 문자열의 길이를 제한 ({@code null} or empty인 경우 검사안함) 
 * @author DongMan Kwon <dmkwon@intellicode.co.kr>
 */
@Documented
@Constraint(validatedBy = { BHiveMaxLengthValidatorForCharSequence.class, BHiveMaxLengthValidatorForInteger.class, BHiveMaxLengthValidatorForLong.class })
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
@Retention(RUNTIME)
@ReportAsSingleViolation
public @interface BHiveMaxLength {

	//int value() default Integer.MAX_VALUE;
	int value(); //required property
	
	String message() default "{bhive.validation.constraint.BHiveMaxLength.message}";
	//String message() default "값은 {value}자 이하로 입력해야 합니다.";
	Class<?>[] groups() default { };
	Class<? extends Payload>[] payload() default { };
	
	@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
	@Retention(RUNTIME)
	@Documented
	@interface List {
		BHiveMaxLength[] value();
	}	
}

