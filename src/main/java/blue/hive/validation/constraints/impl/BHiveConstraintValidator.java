package blue.hive.validation.constraints.impl;

import java.lang.annotation.Annotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Constraint Validator Base Class
 * 
 * @author DongMan Kwon <dmkwon@intellicode.co.kr>
 */
public abstract class BHiveConstraintValidator<A extends Annotation, T> implements ConstraintValidator<A, T> {
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * Initialize the validator in preparation for isValid calls.
	 * The constraint annotation for a given constraint declaration
	 * is passed.
	 * <p/>
	 * This method is guaranteed to be called before any use of this instance for
	 * validation.
	 *
	 * @param constraintAnnotation annotation instance for a given constraint declaration
	 */
	public abstract void initialize(A constraintAnnotation);

	/**
	 * Implement the validation logic.
	 * The state of <code>value</code> must not be altered.
	 *
	 * This method can be accessed concurrently, thread-safety must be ensured
	 * by the implementation.
	 *
	 * @param value object to validate
	 * @param context context in which the constraint is evaluated
	 *
	 * @return false if <code>value</code> does not pass the constraint
	 */
	public abstract boolean isValid(T value, ConstraintValidatorContext context);
}
