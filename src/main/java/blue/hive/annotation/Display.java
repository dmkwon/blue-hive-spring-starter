package blue.hive.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 특정 속성에 대한 필드명 Annotation
 * 
 * @see http://tutorials.jenkov.com/java-reflection/annotations.html
 * @see http://blog.softwaregeeks.org/archives/642
 * 
 * @author DongMan Kwon <dmkwon@intellicode.co.kr>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD})
public @interface Display {
	
	/** @return 필드명 */
	public String name();
	
	/** @return 노출순서 */
	public int order() default Integer.MAX_VALUE;
	
	/** @return 노출여부 */
	public boolean visible() default true;
}
