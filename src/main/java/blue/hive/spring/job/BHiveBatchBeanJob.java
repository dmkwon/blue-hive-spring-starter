package blue.hive.spring.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.task.TaskExecutor;

import blue.hive.exception.BHiveConfigurationException;
import blue.hive.spring.core.task.BHiveIntervalCallable;
import blue.hive.spring.core.task.BHiveIntervalTask;
import blue.hive.spring.core.task.BHiveJobCallable;
import blue.hive.util.BHiveHostingUtil;
import blue.hive.util.anyframe.StringUtil;

/**
 * Batch Bean을 주기적으로 호출하기 위한 Job
 * 
 * <pre>
 * [사용방법]
 * &lt;bean id="taskBatchJob1" class="bhive.spring.job.BHiveBatchBeanJob"&gt;
 * 	&lt;constructor-arg index="0" value="was1"/&gt;
 * 	&lt;constructor-arg index="1" ref="jobBeanName"/&gt;
 * 	&lt;constructor-arg index="2" ref="executor"/&gt;
 * &lt;/bean&gt;
 * </pre>
 * 
 * @author DongMan Kwon <a href="mailto:dmkwon@intellicode.co.kr">dmkwon@intellicode.co.kr</a>
 */
public class BHiveBatchBeanJob implements InitializingBean, BHiveIntervalCallable {

	protected final Logger logger = LoggerFactory.getLogger(BHiveBatchBeanJob.class);
	
	/** 배치를 실행할 InstanceName (없거나 null이여도 실행) */
	protected String instanceNameToRunning;
	
	/** 주기적으로 Job을 처리하는 빈객체 */
	protected BHiveJobCallable batchBean;
	
	/** 로직 반복실행에 사용할 TaskExecutor */
	protected TaskExecutor taskExecutor;

	/**
	 * 생성자
	 * @param instanceNameToRunning 실행할 서버 WasContainerName (설정시 해당 WasContainer의 인스턴스만 실행됨. WAS Pool에서 특정 WAS에서만 실행하는 기능) 
	 * @param batchBean 주기적으로 실행할 Bean
	 * @param taskExecutor Job실행에 사용할 taskExecutor
	 */
	public BHiveBatchBeanJob(String instanceNameToRunning, BHiveJobCallable batchBean, TaskExecutor taskExecutor) {
		super();
		this.instanceNameToRunning = instanceNameToRunning;
		this.batchBean = batchBean;
		this.taskExecutor = taskExecutor;
	}

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	public void afterPropertiesSet() {
		if(batchBean == null) {
			throw new BHiveConfigurationException("BHiveBatchBeanJob.batchBean is required");
		}
		if(this.taskExecutor == null) {
			throw new BHiveConfigurationException("BHiveBatchBeanJob.taskExecutor is required");
		}
	}
	
	/* (non-Javadoc)
	 * @see bhive.spring.core.task.BHiveIntervalCallable#runIntervalProcessTask()
	 */
	public void runIntervalProcessTask() {
		if(taskExecutor == null) {
			logger.debug("taskExecutor configuration required.");
			return;
		}
		
		String currentInstanceName = BHiveHostingUtil.getWasContainerName();
		if(!StringUtil.isEmptyTrimmed(this.instanceNameToRunning)) {
			if(currentInstanceName == null || !this.instanceNameToRunning.equals(currentInstanceName)) {
				logger.trace("#### SKIP BATCH #### instanceNameToRunning:{}, currentInstanceName:{}", instanceNameToRunning, currentInstanceName);
				return; 
			}
		}
		logger.trace("#### START BATCH #### instanceNameToRunning:{}, currentInstanceName:{}", instanceNameToRunning, currentInstanceName);
		
		taskExecutor.execute(new BHiveIntervalTask(this));
	}
	
	/**
	 * 주기적으로 배치 Bean의 Job을 실행
	 */
	public void doIntervalProcess() {
		batchBean.doIntervalProcess();
	}
	
}

