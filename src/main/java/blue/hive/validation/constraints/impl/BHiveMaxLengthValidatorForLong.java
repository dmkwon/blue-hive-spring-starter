package blue.hive.validation.constraints.impl;

import javax.validation.ConstraintValidatorContext;

import blue.hive.validation.constraints.BHiveMaxLength;

/**
 * BHiveMaxLength Validator
 * 
 * @author DongMan Kwon <a href="mailto:dmkwon@intellicode.co.kr">dmkwon@intellicode.co.kr</a>
 */
public class BHiveMaxLengthValidatorForLong extends BHiveConstraintValidator<BHiveMaxLength, Long> {

	private int maxLength;
	
	@Override
	public void initialize(BHiveMaxLength constraintAnnotation) {
		maxLength = constraintAnnotation.value();
	}

	@Override
	public boolean isValid(Long value, ConstraintValidatorContext context) {
		if(value == null) {
			return true;
		}
		boolean result = (Long.toString(Math.abs(value)).length() <= maxLength);
		if(logger.isTraceEnabled()) {
			logger.trace("isValid({}) maxLength:{} => {}", value, maxLength, result);	
		}
		return result; 
	}

}