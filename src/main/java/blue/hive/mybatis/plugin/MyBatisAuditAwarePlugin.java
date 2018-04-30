package blue.hive.mybatis.plugin;

import java.util.Properties;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;
import org.joda.time.DateTime;
import org.springframework.data.domain.Auditable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * MyBatis용 Auditable 데이터객체에 Audit정보 처리
 * @author DongMan Kwon <a href="mailto:dmkwon@intellicode.co.kr">dmkwon@intellicode.co.kr</a>
 */
@Intercepts({ @Signature(type = Executor.class, method = "update", args = { MappedStatement.class, Object.class }) })
public class MyBatisAuditAwarePlugin implements Interceptor {

	public Object intercept(Invocation invocation) throws Throwable {
		return invocation.proceed();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Object plugin(Object target) {
		if (target instanceof DefaultParameterHandler) {
			DefaultParameterHandler paramHandler = (DefaultParameterHandler) target;
			Object obj = paramHandler.getParameterObject();
			if (obj != null) {
				if (obj instanceof Auditable) {
					DateTime currentTimeStamp = new DateTime();
					String currentUser = getCurrentLoginUser();
					Auditable auditable = (Auditable) obj;
					//등록일이 없으면 등록일 설정
					if (auditable.getCreatedDate() == null) {
						auditable.setCreatedDate(currentTimeStamp);
						auditable.setCreatedBy(currentUser);
					}
					//수정일이 없으면 수정일 설정
					auditable.setLastModifiedBy(currentUser);
					auditable.setLastModifiedDate(currentTimeStamp);
				}
			}
		}
		return target;
	}
	
	/**
	 * 현재 로그인 유저정보 획득
	 */
	private String getCurrentLoginUser() {
		SecurityContext context = SecurityContextHolder.getContext();
		Authentication authentication = context.getAuthentication();
		if(authentication == null) {
			return null;
		}
		if(authentication instanceof UserDetails) {
			UserDetails userDetails= (UserDetails)authentication;
			return userDetails.getUsername();
		}
		return authentication.getName();
	}

	public void setProperties(Properties properties) {
	}

}
