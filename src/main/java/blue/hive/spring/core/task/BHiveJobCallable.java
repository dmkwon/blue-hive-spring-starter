package blue.hive.spring.core.task;

/**
 * Job을 호출 할 수 있는 인터페이스
 * 
 * @author DongMan Kwon <a href="mailto:dmkwon@intellicode.co.kr">dmkwon@intellicode.co.kr</a>
 */
public interface BHiveJobCallable {

	/**
	 * Interval 로직 처리 
	 */
	void doIntervalProcess();
}