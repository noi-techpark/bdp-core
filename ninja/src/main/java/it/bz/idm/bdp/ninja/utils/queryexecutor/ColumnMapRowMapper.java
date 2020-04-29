package it.bz.idm.bdp.ninja.utils.queryexecutor;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Map;

import org.postgis.PGgeometry;
import org.postgresql.util.PGobject;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.LinkedCaseInsensitiveMap;

import com.jsoniter.JsonIterator;

public class ColumnMapRowMapper implements RowMapper<Map<String, Object>> {

	private static boolean ignoreNull = false;
	private static Map<String, String> targetDefNameToAliasMap = null;

	public static synchronized void setIgnoreNull(boolean ignoreNull) {
		ColumnMapRowMapper.ignoreNull = ignoreNull;
	}

	public static synchronized void setTargetDefNameToAliasMap(Map<String, String> map) {
		ColumnMapRowMapper.targetDefNameToAliasMap = map;
	}

	@Override
	public Map<String, Object> mapRow(ResultSet rs, int rowNum) throws SQLException {
		ResultSetMetaData rsmd = rs.getMetaData();
		int columnCount = rsmd.getColumnCount();
		Map<String, Object> mapOfColumnValues = createColumnMap(columnCount);
		for (int i = 1; i <= columnCount; i++) {
			Object newValue = getColumnValue(rs, i);
			if (ColumnMapRowMapper.ignoreNull && newValue == null)
				continue;

			String column = JdbcUtils.lookupColumnName(rsmd, i);
			String replacementColumn = targetDefNameToAliasMap.get(column);

			if (replacementColumn == null) {
				mapOfColumnValues.put(column, newValue);
			} else {
				if (mapOfColumnValues.containsKey(replacementColumn)) {
					Object oldValue = mapOfColumnValues.get(replacementColumn);
					if (oldValue == null && newValue != null) {
						mapOfColumnValues.put(replacementColumn, newValue);
					}
				} else {
					mapOfColumnValues.put(replacementColumn, newValue);
				}
			}
		}
		return mapOfColumnValues.isEmpty() ? null : mapOfColumnValues;
	}

	/**
	 * Create a Map instance to be used as column map.
	 * <p>By default, a linked case-insensitive Map will be created.
	 * @param columnCount the column count, to be used as initial
	 * capacity for the Map
	 * @return the new Map instance
	 * @see org.springframework.util.LinkedCaseInsensitiveMap
	 */
	protected Map<String, Object> createColumnMap(int columnCount) {
		return new LinkedCaseInsensitiveMap<>(columnCount);
	}

	private static String cleanPostgresType(String type) {
		int dotPosition = type.indexOf(".");
		if (dotPosition > 0) {
			type = type.substring(dotPosition + 1);
		}
		return type.replace("\"", "").toLowerCase().trim();
	}

	/**
	 * Retrieve a JDBC object value for the specified column.
	 * <p>The default implementation uses the {@code getObject} method.
	 * Additionally, this implementation includes a "hack" to get around Oracle
	 * returning a non standard object for their TIMESTAMP datatype.
	 * @param rs is the ResultSet holding the data
	 * @param index is the column index
	 * @return the Object returned
	 * @see org.springframework.jdbc.support.JdbcUtils#getResultSetValue
	 */
	@Nullable
	protected Object getColumnValue(ResultSet rs, int index) throws SQLException {
		Object obj = rs.getObject(index);
		if (obj != null && obj instanceof PGobject) {
			PGobject pgObj = (PGobject) obj;
			String pgObjType = cleanPostgresType(pgObj.getType());

			switch (pgObjType) {
				case "geometry":
					return PGgeometry.geomFromString(pgObj.getValue());
				case "jsonb":
					// FIXME Return a proper map
					/* This is a proper JSON null value, since a string would be ""null"" instead. */
					if (pgObj.getValue().equalsIgnoreCase("null")) {
						return null;
					}
					return JsonIterator.deserialize(pgObj.getValue());
				default:
					throw new RuntimeException("PGobject type " + pgObjType + " not supported!");
			}
		}

		return JdbcUtils.getResultSetValue(rs, index);
	}

}
