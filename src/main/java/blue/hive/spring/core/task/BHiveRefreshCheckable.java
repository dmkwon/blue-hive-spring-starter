package blue.hive.spring.core.task;

/**
 * Refresh Check를 지원하는 Interface
 * 
 * @author DongMan Kwon <a href="mailto:dmkwon@intellicode.co.kr">dmkwon@intellicode.co.kr</a>
 */
public interface BHiveRefreshCheckable {

	/**
	 * refresh를 체크 
	 */
	void refreshCheck();

	
	/**
	 * refresh 체크를 위한 Task를 시작 (BHiveRefreshCheckTask) 
	 */
	void runRefreshCheckTask();
	
}
