package blue.hive.mybatis.typehandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;

/**
 * MyBatis Type Handler
 * 
 * org.joda.time.DateTime - DB Date
 * 
 * [설정방법] sqlSessionFactory 빈에 typeHandlersPackage 프라퍼티등으로 설정
 * 
 * @author DongMan Kwon <dmkwon@intellicode.co.kr>
 */
public class JodaLocalDateTimeTypeHandler extends BaseTypeHandler<LocalDateTime> {

	@Override
	public LocalDateTime getNullableResult(ResultSet rs, String columnName) throws SQLException {
		Timestamp timeStamp = rs.getTimestamp(columnName);
		if (timeStamp == null) {
			return null;
		}
		return new LocalDateTime(timeStamp);
	}

	@Override
	public LocalDateTime getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		Timestamp timeStamp = rs.getTimestamp(columnIndex);
		if (timeStamp == null) {
			return null;
		}
		return new LocalDateTime(timeStamp);
	}

	@Override
	public LocalDateTime getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		Timestamp timeStamp = cs.getTimestamp(columnIndex);
		if (timeStamp == null) {
			return null;
		}
		return new LocalDateTime(timeStamp);
	}

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, LocalDateTime parameter, JdbcType jdbcType)
			throws SQLException {
		if (parameter == null) {
			ps.setTimestamp(i, null);
		} else {
			DateTime dateTime = parameter.toDateTime(DateTimeZone.UTC);
			switch (jdbcType) {
			case DATE:
				ps.setDate(i, new java.sql.Date(dateTime.getMillis()));
				break;
			default:
				ps.setTimestamp(i, new Timestamp(dateTime.getMillis()));
				break;
			}
		}
	}

}
