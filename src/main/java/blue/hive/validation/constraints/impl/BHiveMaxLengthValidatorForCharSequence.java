package blue.hive.validation.constraints.impl;

import javax.validation.ConstraintValidatorContext;

import blue.hive.validation.constraints.BHiveMaxLength;

/**
 * BHiveMaxLength Validator
 * 
 * @author DongMan Kwon <a href="mailto:dmkwon@intellicode.co.kr">dmkwon@intellicode.co.kr</a>
 */
public class BHiveMaxLengthValidatorForCharSequence extends BHiveConstraintValidator<BHiveMaxLength, CharSequence> {

	private int maxLength;
	
	public void initialize(BHiveMaxLength constraintAnnotation) {
		maxLength = constraintAnnotation.value();
	}

	public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
		if(value == null) {
			return true;
		}
		if(logger.isTraceEnabled()) {
			logger.trace("isValid({}) maxLength:{} => {}", value, maxLength, (value.length() > maxLength));
		}
		return (value.length() <= maxLength); 
	}

}