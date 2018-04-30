package blue.hive.spring.core.task;

/**
 * Interval Job 프로세스 인터페이스
 * 
 * @author DongMan Kwon <a href="mailto:dmkwon@intellicode.co.kr">dmkwon@intellicode.co.kr</a>
 *
 */
public interface BHiveIntervalCallable extends BHiveJobCallable {

	/**
	 * Interval 로직 처리 
	 */
	void doIntervalProcess();
	
	/**
	 * refresh 체크를 위한 Task를 시작 (BHiveRefreshCheckTask) 
	 */
	void runIntervalProcessTask();
}
