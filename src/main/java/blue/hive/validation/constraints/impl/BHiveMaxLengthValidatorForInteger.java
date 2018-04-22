package blue.hive.validation.constraints.impl;

import javax.validation.ConstraintValidatorContext;

import blue.hive.validation.constraints.BHiveMaxLength;

/**
 * BHiveMaxLength Validator
 * 
 * @author DongMan Kwon <dmkwon@intellicode.co.kr>
 */
public class BHiveMaxLengthValidatorForInteger extends BHiveConstraintValidator<BHiveMaxLength, Integer> {

	private int maxLength;
	
	@Override
	public void initialize(BHiveMaxLength constraintAnnotation) {
		maxLength = constraintAnnotation.value();
	}

	@Override
	public boolean isValid(Integer value, ConstraintValidatorContext context) {
		if(value == null) {
			return true;
		}
		boolean result = (Integer.toString(Math.abs(value)).length() <= maxLength);
		if(logger.isTraceEnabled()) {
			logger.trace("isValid({}) maxLength:{} => {}", value, maxLength, result);	
		}
		return result; 
	}

}