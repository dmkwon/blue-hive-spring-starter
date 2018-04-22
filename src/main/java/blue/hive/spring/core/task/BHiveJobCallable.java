package blue.hive.spring.core.task;

/**
 * Job을 호출 할 수 있는 인터페이스
 * 
 * @author DongMan Kwon <dmkwon@intellicode.co.kr>
 */
public interface BHiveJobCallable {

	/**
	 * Interval 로직 처리 
	 */
	void doIntervalProcess();
}