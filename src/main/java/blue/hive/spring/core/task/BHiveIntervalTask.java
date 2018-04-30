package blue.hive.spring.core.task;

/**
 * Interval 프로세스를 실행하는 Runnable Task
 * 
 * @author DongMan Kwon <a href="mailto:dmkwon@intellicode.co.kr">dmkwon@intellicode.co.kr</a>
 *
 */
public class BHiveIntervalTask implements Runnable {

	/**
	 * 주기적으로 로직을 처리하기 위한 객체
	 */
	protected BHiveIntervalCallable intervalProcessBean;

	/**
	 * 생성자 
	 * @param intervalProcessBean 주기적으로 로직을 처리하기 위한 객체
	 */
	public BHiveIntervalTask(BHiveIntervalCallable intervalProcessBean) {
		this.intervalProcessBean = intervalProcessBean;
	}

	/**
	 * AbleIntervalCallable객체의 처리 로직을 실행
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		intervalProcessBean.doIntervalProcess();
	}
	
}
