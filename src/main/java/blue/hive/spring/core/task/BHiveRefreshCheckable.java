package blue.hive.spring.core.task;

/**
 * Refresh Check를 지원하는 Interface
 * 
 * @author DongMan Kwon <dmkwon@intellicode.co.kr>
 */
public interface BHiveRefreshCheckable {

	/**
	 * refresh를 체크 
	 */
	void refreshCheck();

	
	/**
	 * refresh 체크를 위한 Task를 시작 (AbleRefreshCheckTask) 
	 */
	void runRefreshCheckTask();
	
}
