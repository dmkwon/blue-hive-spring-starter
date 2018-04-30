package blue.hive.mybatis.typehandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MyBatis Type Handler
 * 
 * Java boolean - DB Y/N
 * 
 * [설정방법]
 * sqlSessionFactory 빈에 typeHandlersPackage 프라퍼티등으로 설정
 *   
 * @author DongMan Kwon <a href="mailto:dmkwon@intellicode.co.kr">dmkwon@intellicode.co.kr</a>
 */
public class BooleanYNTypeHandler extends BaseTypeHandler<Boolean> {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private Boolean parseBoolean(String value) throws SQLException {
		Boolean result;
		if("Y".equalsIgnoreCase(value) || "T".equalsIgnoreCase(value)) {
			result = Boolean.valueOf(true);
		} else if("N".equalsIgnoreCase(value) || "F".equalsIgnoreCase(value)) {
			result = Boolean.valueOf(false);
		} else {
			result = null;
		}
		if(logger.isTraceEnabled()) {
			logger.trace("\"" + value + "\" -> " + result);
		}
		return result;
	}

	private String convertToString(Boolean value) {
		String result = (value != null && value.booleanValue() == true) ? "Y" : "N";
		if (logger.isTraceEnabled()) {
			logger.trace(value + " -> " + "\"" + result + "\"");
		}
		return result;
	}

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, Boolean parameter, JdbcType jdbcType) throws SQLException {
		ps.setString(i, convertToString(parameter));
	}

	@Override
	public Boolean getNullableResult(ResultSet rs, String columnName) throws SQLException {
		return parseBoolean(rs.getString(columnName));
	}

	@Override
	public Boolean getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		return parseBoolean(rs.getString(columnIndex));
	}

	@Override
	public Boolean getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		return parseBoolean(cs.getString(columnIndex));
	}

}

