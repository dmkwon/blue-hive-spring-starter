package blue.hive.spring.job;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.task.TaskExecutor;

import blue.hive.exception.BHiveConfigurationException;
import blue.hive.mybatis.support.BHiveSqlSessionDaoSupport;
import blue.hive.spring.core.task.BHiveIntervalCallable;
import blue.hive.spring.core.task.BHiveIntervalTask;
import blue.hive.util.BHiveHostingUtil;
import blue.hive.util.anyframe.StringUtil;



/**
 * MyBatis의 프로시져를 주기적으로 호출하기 위한 Job
 * 
 * <pre>
 * [실행방법]
 * 
 * 특정 MyBatis 구문을 주기적으로 실행. 주로 DB Procedure의 Batch 실행
 * 
 * &lt;bean id="taskBatchJob1" class="bhive.spring.job.BHiveMyBatisProcedureJob"&gt;
 *  &lt;constructor-arg name="0" value="was1"/&gt;
 *  &lt;constructor-arg name="1" value="persistence.BatchJobDAO.eslrpcDummyTest"/&gt;
 *  &lt;constructor-arg name="2" ref="executor"/&gt;
 * &lt;/bean&gt;
 * </pre>
 * 
 * @author DongMan Kwon <a href="mailto:dmkwon@intellicode.co.kr">dmkwon@intellicode.co.kr</a>
 */
public class BHiveMyBatisProcedureJob extends BHiveSqlSessionDaoSupport implements InitializingBean, BHiveIntervalCallable {

	
	/** 배치를 실행할 InstanceName (없거나 null이여도 실행) */
	protected String instanceNameToRunning;
	
	/** 실행할 MyBatis Statement */
	private String statement;
	
	/** 로직 반복실행에 사용할 TaskExecutor */
	protected TaskExecutor taskExecutor;

	/**
	 * 생성자
	 * @param instanceNameToRunning 실행할 서버 WasContainerName (설정시 해당 WasContainer의 인스턴스만 실행됨. WAS Pool에서 특정 WAS에서만 실행하는 기능) 
	 * @param statement 주기적으로 실행할 MyBatis Statement Name
	 * @param taskExecutor Job실행에 사용할 taskExecutor
	 */
	public BHiveMyBatisProcedureJob(String instanceNameToRunning, String statement, TaskExecutor taskExecutor) {
		super();
		this.instanceNameToRunning = instanceNameToRunning;
		this.statement = statement;
		this.taskExecutor = taskExecutor;
	}

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	public void afterPropertiesSet() {
		super.afterPropertiesSet();
		if(StringUtil.isEmpty(this.statement)) {
			throw new BHiveConfigurationException("BHiveMyBatisProcedureJob.statement is required");
		}
		if(this.taskExecutor == null) {
			throw new BHiveConfigurationException("BHiveMyBatisProcedureJob.taskExecutor is required");
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
	 * 설정된 MyBatis statement를 실행
	 * @see blue.hive.spring.core.task.BHiveIntervalCallable#doIntervalProcess()
	 */
	public void doIntervalProcess() {
		if(logger.isDebugEnabled()) { logger.debug("SQL ===>[" + statement + "]"); }
		Map<String, Object> map = new HashMap<String, Object>();
		int res = getSqlSession().update(this.statement, map);
		if(res > 0) {
			logger.debug("SQL update ===>[" + res + "]");
		}
	}
	
}
