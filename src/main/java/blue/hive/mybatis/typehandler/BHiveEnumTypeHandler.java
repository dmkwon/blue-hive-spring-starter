package blue.hive.mybatis.typehandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import blue.hive.type.BHiveValueEnum;
import blue.hive.util.BHiveEnumUtil;

/**
 * MyBatis Enum Type Handler
 * 
 * @author DongMan Kwon <dmkwon@intellicode.co.kr>
 * @param <E> Enum 타입
 */
public class BHiveEnumTypeHandler<E extends Enum<E>> extends BaseTypeHandler<E> {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private Class<E> type;
	private final E[] enums;

	public BHiveEnumTypeHandler(Class<E> type) {
		this.type = type;
		this.enums = type.getEnumConstants();
		if (this.enums == null) {
			throw new IllegalArgumentException(type.getSimpleName() + " does not represent an enum type.");
		}
	}

	private E parseToValue(String value) throws SQLException {
		return BHiveEnumUtil.parseEnumValueOf(type, value);
	}

	private String convertToString(Enum<E> param) {
		String result = (param == null) ? "" : param.toString();
		if(param instanceof BHiveValueEnum<?>) {
			BHiveValueEnum<?> typedParam = (BHiveValueEnum<?>)param;
			result = typedParam.getValue().toString();
		}
		if (logger.isTraceEnabled()) {
			logger.trace(param + " -> " + "\"" + result + "\"");
		}
		return result;
	}

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, E parameter, JdbcType jdbcType) throws SQLException {
		ps.setString(i, convertToString(parameter));
	}

	@Override
	public E getNullableResult(ResultSet rs, String columnName) throws SQLException {
		return parseToValue(rs.getString(columnName));
	}

	@Override
	public E getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		return parseToValue(rs.getString(columnIndex));
	}

	@Override
	public E getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		return parseToValue(cs.getString(columnIndex));
	}


}
