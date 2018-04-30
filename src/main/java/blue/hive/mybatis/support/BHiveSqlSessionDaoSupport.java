package blue.hive.mybatis.support;

import static org.springframework.util.Assert.notNull;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.executor.BatchResult;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * SqlSessionDaoSupport를 참조하여 sqlSession이 Autowired되게 만든 기본 DAO 클래스
 * 
 * DAO 작성시 MyBatis 관련 공통기능 자동 제공 
 * 
 * @author DongMan Kwon <a href="mailto:dmkwon@intellicode.co.kr">dmkwon@intellicode.co.kr</a>
 * 
 */
public class BHiveSqlSessionDaoSupport implements InitializingBean {

	/** Logger available to subclasses */
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	private SqlSession sqlSession;

	private boolean externalSqlSession;

	@Autowired
	public void setSqlSession(SqlSession sqlSession) {
		this.sqlSession = sqlSession;
	}

	protected void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
		if (!this.externalSqlSession) {
			this.sqlSession = new SqlSessionTemplate(sqlSessionFactory);
		}
	}

	protected void setSqlSessionTemplate(SqlSessionTemplate sqlSessionTemplate) {
		this.sqlSession = sqlSessionTemplate;
		this.externalSqlSession = true;
	}

	public void afterPropertiesSet() throws IllegalArgumentException,
			BeanInitializationException {
		// Let abstract subclasses check their configuration.
		checkDaoConfig();

		// Let concrete implementations initialize themselves.
		try {
			initDao();
		} catch (Exception ex) {
			throw new BeanInitializationException(
					"Initialization of DAO failed", ex);
		}
	}

	/**
	 * Abstract subclasses can override this to check their configuration.
	 * <p>
	 * Implementors should be marked as {@code final} if concrete subclasses are
	 * not supposed to override this template method themselves.
	 * 
	 * @throws IllegalArgumentException
	 *             in case of illegal configuration
	 */
	protected void checkDaoConfig() {
		notNull(this.sqlSession,
				"Property 'sqlSessionFactory' or 'sqlSessionTemplate' are required");
	}

	/**
	 * Concrete subclasses can override this for custom initialization behavior.
	 * Gets called after population of this instance's bean properties.
	 * 
	 * @throws Exception
	 *             if DAO initialization fails (will be rethrown as a
	 *             BeanInitializationException)
	 * @see org.springframework.beans.factory.BeanInitializationException
	 */
	protected void initDao() throws Exception {
	}

	/**
	 * Users should use this method to get a SqlSession to call its statement
	 * methods This is SqlSession is managed by spring. Users should not
	 * commit/rollback/close it because it will be automatically done.
	 *
	 * @return Spring managed thread safe SqlSession
	 */
	protected SqlSession getSqlSession() {
		return this.sqlSession;
	}

	/**
	 * Retrieve a single row mapped from the statement key
	 * 
	 * @param <T>
	 *            the returned object type
	 * @param statement 
	 * 			  Unique identifier matching the statement to use.
	 * @return Mapped object
	 */
	protected <T> T selectOne(String statement) {
		if(logger.isDebugEnabled()) { logger.debug("SQL ===>[" + statement + "]"); }
		return sqlSession.selectOne(statement);
	}

	/**
	 * Retrieve a single row mapped from the statement key and parameter.
	 * 
	 * @param <T>
	 *            the returned object type
	 * @param statement
	 *            Unique identifier matching the statement to use.
	 * @param parameter
	 *            A parameter object to pass to the statement.
	 * @return Mapped object
	 */
	protected <T> T selectOne(String statement, Object parameter) {
		if(logger.isDebugEnabled()) { logger.debug("SQL ===>[" + statement + "]"); }
		return sqlSession.selectOne(statement, parameter);
	}

	/**
	 * Retrieve a list of mapped objects from the statement key and parameter.
	 * 
	 * @param <E>
	 *            the returned list element type
	 * @param statement
	 *            Unique identifier matching the statement to use.
	 * @return List of mapped object
	 */
	protected <E> List<E> selectList(String statement) {
		if(logger.isDebugEnabled()) { logger.debug("SQL ===>[" + statement + "]"); }
		return sqlSession.selectList(statement);
	}

	/**
	 * Retrieve a list of mapped objects from the statement key and parameter.
	 * 
	 * @param <E>
	 *            the returned list element type
	 * @param statement
	 *            Unique identifier matching the statement to use.
	 * @param parameter
	 *            A parameter object to pass to the statement.
	 * @return List of mapped object
	 */
	protected <E> List<E> selectList(String statement, Object parameter) {
		if(logger.isDebugEnabled()) { logger.debug("SQL ===>[" + statement + "]"); }
		if(logger.isDebugEnabled()) { logger.debug("PARAM =>[" + parameter + "]"); }
		return sqlSession.selectList(statement, parameter);
	}

	/**
	 * Retrieve a list of mapped objects from the statement key and parameter,
	 * within the specified row bounds.
	 * 
	 * @param <E>
	 *            the returned list element type
	 * @param statement
	 *            Unique identifier matching the statement to use.
	 * @param parameter
	 *            A parameter object to pass to the statement.
	 * @param rowBounds
	 *            Bounds to limit object retrieval
	 * @return List of mapped object
	 */
	protected <E> List<E> selectList(String statement, Object parameter, RowBounds rowBounds) {
		if(logger.isDebugEnabled()) { logger.debug("SQL ===>[" + statement + "]"); }
		return sqlSession.selectList(statement, parameter, rowBounds);
	}

	/**
	 * The selectMap is a special case in that it is designed to convert a list
	 * of results into a Map based on one of the properties in the resulting
	 * objects. Eg. Return a of Map[Integer,Author] for
	 * selectMap("selectAuthors","id")
	 * 
	 * @param <K>
	 *            the returned Map keys type
	 * @param <V>
	 *            the returned Map values type
	 * @param statement
	 *            Unique identifier matching the statement to use.
	 * @param mapKey
	 *            The property to use as key for each value in the list.
	 * @return Map containing key pair data.
	 */
	protected <K, V> Map<K, V> selectMap(String statement, String mapKey) {
		if(logger.isDebugEnabled()) { logger.debug("SQL ===>[" + statement + "]"); }
		return sqlSession.selectMap(statement, mapKey);
	}

	/**
	 * The selectMap is a special case in that it is designed to convert a list
	 * of results into a Map based on one of the properties in the resulting
	 * objects.
	 * 
	 * @param <K>
	 *            the returned Map keys type
	 * @param <V>
	 *            the returned Map values type
	 * @param statement
	 *            Unique identifier matching the statement to use.
	 * @param parameter
	 *            A parameter object to pass to the statement.
	 * @param mapKey
	 *            The property to use as key for each value in the list.
	 * @return Map containing key pair data.
	 */
	protected <K, V> Map<K, V> selectMap(String statement, Object parameter, String mapKey) {
		if(logger.isDebugEnabled()) { logger.debug("SQL ===>[" + statement + "]"); }
		return sqlSession.selectMap(statement, parameter, mapKey);
	}

	/**
	 * The selectMap is a special case in that it is designed to convert a list
	 * of results into a Map based on one of the properties in the resulting
	 * objects.
	 * 
	 * @param <K>
	 *            the returned Map keys type
	 * @param <V>
	 *            the returned Map values type
	 * @param statement
	 *            Unique identifier matching the statement to use.
	 * @param parameter
	 *            A parameter object to pass to the statement.
	 * @param mapKey
	 *            The property to use as key for each value in the list.
	 * @param rowBounds
	 *            Bounds to limit object retrieval
	 * @return Map containing key pair data.
	 */
	protected <K, V> Map<K, V> selectMap(String statement, Object parameter, String mapKey, RowBounds rowBounds) {
		if(logger.isDebugEnabled()) { logger.debug("SQL ===>[" + statement + "]"); }
		return sqlSession.selectMap(statement, parameter, mapKey, rowBounds);
	}

	/**
	 * Retrieve a single row mapped from the statement key and parameter using a
	 * {@code ResultHandler}.
	 * 
	 * @param statement
	 *            Unique identifier matching the statement to use.
	 * @param parameter
	 *            A parameter object to pass to the statement.
	 * @param handler
	 *            ResultHandler that will handle each retrieved row
	 */
	protected void select(String statement, Object parameter, ResultHandler<?> handler) {
		if(logger.isDebugEnabled()) { logger.debug("SQL ===>[" + statement + "]"); }
		sqlSession.select(statement, parameter, handler);
	}

	/**
	 * Retrieve a single row mapped from the statement using a
	 * {@code ResultHandler}.
	 * 
	 * @param statement
	 *            Unique identifier matching the statement to use.
	 * @param handler
	 *            ResultHandler that will handle each retrieved row
	 */
	protected void select(String statement, ResultHandler<?> handler){
		if(logger.isDebugEnabled()) { logger.debug("SQL ===>[" + statement + "]"); }
		sqlSession.select(statement, handler);
	}
	/**
	 * Retrieve a single row mapped from the statement key and parameter using a
	 * {@code ResultHandler} and {@code RowBounds}
	 * 
	 * @param statement
	 *            Unique identifier matching the statement to use.
	 * @param parameter
	 *            A parameter object to pass to the statement.
	 * @param rowBounds
	 *            RowBound instance to limit the query results
	 * @param handler
	 *            ResultHandler that will handle each retrieved row
	 */
	protected void select(String statement, Object parameter, RowBounds rowBounds, ResultHandler<?> handler) {
		if(logger.isDebugEnabled()) { logger.debug("SQL ===>[" + statement + "]"); }
		sqlSession.select(statement, parameter, rowBounds, handler);
	}
	

	/**
	 * Execute an insert statement.
	 * 
	 * @param statement
	 *            Unique identifier matching the statement to execute.
	 * @return int The number of rows affected by the insert.
	 */
	protected int insert(String statement) {
		if(logger.isDebugEnabled()) { logger.debug("SQL ===>[" + statement + "]"); }
		return sqlSession.insert(statement);
	}

	/**
	 * Execute an insert statement with the given parameter object. Any
	 * generated autoincrement values or selectKey entries will modify the given
	 * parameter object properties. Only the number of rows affected will be
	 * returned.
	 * 
	 * @param statement
	 *            Unique identifier matching the statement to execute.
	 * @param parameter
	 *            A parameter object to pass to the statement.
	 * @return int The number of rows affected by the insert.
	 */
	protected int insert(String statement, Object parameter) {
		if(logger.isDebugEnabled()) { logger.debug("SQL ===>[" + statement + "]"); }
		return sqlSession.insert(statement, parameter);
	}

	/**
	 * Execute an update statement. The number of rows affected will be
	 * returned.
	 * 
	 * @param statement
	 *            Unique identifier matching the statement to execute.
	 * @return int The number of rows affected by the update.
	 */
	protected int update(String statement) {
		if(logger.isDebugEnabled()) { logger.debug("SQL ===>[" + statement + "]"); }
		return sqlSession.update(statement);
	}

	/**
	 * Execute an update statement. The number of rows affected will be
	 * returned.
	 * 
	 * @param statement
	 *            Unique identifier matching the statement to execute.
	 * @param parameter
	 *            A parameter object to pass to the statement.
	 * @return int The number of rows affected by the update.
	 */
	protected int update(String statement, Object parameter) {
		if(logger.isDebugEnabled()) { logger.debug("SQL ===>[" + statement + "]"); }
		return sqlSession.update(statement, parameter);
	}

	/**
	 * Execute a delete statement. The number of rows affected will be returned.
	 * 
	 * @param statement
	 *            Unique identifier matching the statement to execute.
	 * @return int The number of rows affected by the delete.
	 */
	protected int delete(String statement) {
		if(logger.isDebugEnabled()) { logger.debug("SQL ===>[" + statement + "]"); }
		return sqlSession.delete(statement);
	}

	/**
	 * Execute a delete statement. The number of rows affected will be returned.
	 * 
	 * @param statement
	 *            Unique identifier matching the statement to execute.
	 * @param parameter
	 *            A parameter object to pass to the statement.
	 * @return int The number of rows affected by the delete.
	 */
	protected int delete(String statement, Object parameter) {
		if(logger.isDebugEnabled()) { logger.debug("SQL ===>[" + statement + "]"); }
		return sqlSession.delete(statement, parameter);
	}

	/**
	 * Flushes batch statements and commits database connection. Note that
	 * database connection will not be committed if no updates/deletes/inserts
	 * were called. To force the commit call {@link SqlSession#commit(boolean)}
	 */
	protected void commit() {
		sqlSession.commit();
	}

	/**
	 * Flushes batch statements and commits database connection.
	 * 
	 * @param force
	 *            forces connection commit
	 */
	protected void commit(boolean force) {
		sqlSession.commit(force);
	}

	/**
	 * Discards pending batch statements and rolls database connection back.
	 * Note that database connection will not be rolled back if no
	 * updates/deletes/inserts were called. To force the rollback call
	 * {@link SqlSession#rollback(boolean)}
	 */
	protected void rollback() {
		sqlSession.rollback();
	}

	/**
	 * Discards pending batch statements and rolls database connection back.
	 * Note that database connection will not be rolled back if no
	 * updates/deletes/inserts were called.
	 * 
	 * @param force
	 *            forces connection rollback
	 */
	protected void rollback(boolean force) {
		sqlSession.rollback(force);
	}

	/**
	 * Flushes batch statements.
	 * 
	 * @return BatchResult list of updated records
	 * @since 3.0.6
	 */
	protected List<BatchResult> flushStatements() {
		return sqlSession.flushStatements();
	}

	/**
	 * Closes the session
	 */
	protected void close() {
		sqlSession.close();
	}

	/**
	 * Clears local session cache
	 */
	protected void clearCache() {
		sqlSession.clearCache();
	}

	/**
	 * Retrieves current configuration
	 * 
	 * @return Configuration
	 */
	protected Configuration getConfiguration() {
		return sqlSession.getConfiguration();
	}

	/**
	 * Retrieves a mapper.
	 * 
	 * @param <T>
	 *            the mapper type
	 * @param type
	 *            Mapper interface class
	 * @return a mapper bound to this SqlSession
	 */
	protected <T> T getMapper(Class<T> type) {
		return sqlSession.getMapper(type);
	}

	/**
	 * Retrieves inner database connection
	 * 
	 * @return Connection
	 */
	protected Connection getConnection() {
		return sqlSession.getConnection();
	}

	/**
	 * Retrieves current method name
	 * @return current method name
	 */
	public static String getCurrentMethodName() {
		StackTraceElement[] stacks = Thread.currentThread().getStackTrace();
		int depth = 0;
		for (StackTraceElement stack : stacks) {
			if(stack.getClassName().equals("java.lang.Thread") && (stack.getMethodName().equals("getStackTraceImpl") || stack.getMethodName().equals("getStackTrace"))) {
				continue;
			}
			if(depth == 1) {
				return stack.getClassName() + "." + stack.getMethodName(); 
			}
			depth++;
		}
		throw new RuntimeException("#### FAILED to getCurrentMethodName ####");
    }
}
