package blue.hive.spring.web.log;


import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;
import org.springframework.util.StopWatch.TaskInfo;

/**
 * Logging Aspect for every invokation annotated methods in @Repository annotated beans
 *
 * @author DongMan Kwon <a href="mailto:dmkwon@intellicode.co.kr">dmkwon@intellicode.co.kr</a>
 */
@Aspect
public class BHiveRepositoryLoggingAspect {

	/** Logger available to subclasses */
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@Pointcut("within(@org.springframework.stereotype.Repository *)")
	public void repository() {}

	@Pointcut("execution(* *(..))")
	public void methodPointcut() {}

	@Before("repository() && methodPointcut()")
	public void beforeServiceMethod(JoinPoint joinPoint) throws Throwable {
		if(logger.isDebugEnabled()) {
			logger.debug("\r\n\t##################################################"
					+ "\r\n\t#### START " + niceNameForStart(joinPoint));
		}
	}

	@AfterReturning(pointcut = "repository() && methodPointcut()", returning="retVal")
	public void afterServiceMethod(JoinPoint joinPoint, Object retVal) {
		if(logger.isDebugEnabled()) {
			logger.debug("\r\n\t#### END " + niceNameForEnd(joinPoint, retVal)
			+ "\r\n\t##################################################");
		}
	}

	@AfterThrowing(pointcut = "repository() && methodPointcut()", throwing = "ex")
	public void afterThrowing(JoinPoint joinPoint, Throwable ex) {
		if(logger.isDebugEnabled()) {
			logger.debug("\r\n\t#### EXCEPTION AT " + niceNameForException(joinPoint, ex)
			+ "\r\n\t##################################################");
		}
	}

	@Around("repository() && methodPointcut()")
	public Object around(ProceedingJoinPoint pjp) throws Throwable {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start(pjp.toShortString());
		boolean isExceptionThrown = false;
		try {
			// execute the profiled method
			return pjp.proceed();
		} catch (RuntimeException e) {
			isExceptionThrown = true;
			throw e;
		} finally {
			stopWatch.stop();
			TaskInfo taskInfo = stopWatch.getLastTaskInfo();
			// Log the method's profiling result
			String profileMessage = taskInfo.getTimeMillis() + " ms" +
					(isExceptionThrown ? " (thrown Exception)" : "");
			if(logger.isDebugEnabled()) {
				logger.debug("\r\n\t#### ELAPSED TIME : " + profileMessage);
			}	
		}
	}

	/**
	 * joinPoint의 Before 로깅내용 획득
	 */
	private static String niceNameForStart(JoinPoint joinPoint) {
		return joinPoint.getTarget().getClass().getSimpleName()
				+ "#" + joinPoint.getSignature().getName();
				//+ argsToString(joinPoint.getArgs());
	}

	/**
	 * Method Arguments의 로깅내용 획득
	 * @param args Arguments
	 * @return Arguments 로깅 문자열
	 */
	private static String argsToString(Object[] args) {
		StringBuffer sb = new StringBuffer();
		if(args == null || args.length == 0) {
			return "\t\tno arguments.";
		}
		for (int i = 0; i < args.length; i++) {
			sb.append(String.format("\r%n\t####  - args[%d]: %s", i, argToString(args[i])));
		}
		return sb.toString();
	}

	private static String argToString(Object arg) {
		if(arg == null) {
			return "<null>";
		}
		return arg.toString();
	}

	/**
	 * joinPoint의 AfterReturning 로깅내용 획득
	 */
	private static String niceNameForEnd(JoinPoint joinPoint, Object retVal) {
		return joinPoint.getTarget().getClass().getSimpleName()
				+ "#" + joinPoint.getSignature().getName()
				//+ argsToString(joinPoint.getArgs())
				+ retValToString(retVal);
	}

	private static String retValToString(Object retVal) {
		return "\r\n\t####  - return: " + ((retVal == null) ? "" : retVal.toString());
	}

	/**
	 * joinPoint의 AfterThrowing 로깅내용 획득
	 */
	private String niceNameForException(JoinPoint joinPoint, Throwable ex) {
		return joinPoint.getTarget().getClass().getSimpleName()
				+ "#" + joinPoint.getSignature().getName()
				+ argsToString(joinPoint.getArgs())
				+ exceptionToString(ex);
	}

	private String exceptionToString(Throwable ex) {
		return "\r\n\t####  - EXCEPTION : " + ex.toString();
	}
}
