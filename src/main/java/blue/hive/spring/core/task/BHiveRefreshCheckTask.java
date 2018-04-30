package blue.hive.spring.core.task;

/**
 * Refresh를 체크하는 Runnable.
 * 
 * BHiveRefreshCheckable 객체에 체크를 위임
 * 
 * @author DongMan Kwon <a href="mailto:dmkwon@intellicode.co.kr">dmkwon@intellicode.co.kr</a>
 *
 */
public class BHiveRefreshCheckTask implements Runnable {
	
	/**
	 * refresh를 체크할 객체
	 */
	protected BHiveRefreshCheckable checkTargetBean;
	
	/**
	 * 생성자
	 * @param checkTargetBean refresh를 체크할 객체
	 */
	public BHiveRefreshCheckTask(BHiveRefreshCheckable checkTargetBean) {
		this.checkTargetBean = checkTargetBean;
	}
	
	/**
	 * BHiveRefreshCheckable 객체에 Refresh 체크를 위임
	 */
	public void run() {
		checkTargetBean.refreshCheck();			
	}
}
