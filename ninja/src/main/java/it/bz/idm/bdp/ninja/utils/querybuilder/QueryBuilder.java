package it.bz.idm.bdp.ninja.utils.querybuilder;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;

/**
 * Create a SQL string, depending on given conditions, a select list, an optional where-clause and
 * a {@link SelectExpansion} definition.
 *
 * @author Peter Moser
 */
public class QueryBuilder {

	private StringBuilder sql = new StringBuilder();
	private static SelectExpansion se;
	private Map<String, Object> parameters = new HashMap<String, Object>();

	public QueryBuilder(final String select, final String where, final boolean isDistinct, String... selectDefNames) {
		if (QueryBuilder.se == null) {
			throw new RuntimeException("Missing Select Expansion. Run QueryBuilder.setup before initialization.");
		}
		reset(select, where, isDistinct, selectDefNames);
	}

	public QueryBuilder reset(final String select, final String where, final boolean isDistinct, String... selectDefNames) {
		se.setWhereClause(where);
		se.setDistinct(isDistinct);
		se.expand(select, selectDefNames);
		return this;
	}

	/**
	 * Create a new {@link QueryBuilder} instance
	 *
	 * @see QueryBuilder#QueryBuilder(EntityManager)
	 *
	 * @param namedParameterJdbcTemplate {@link EntityManager}
	 */
	public static synchronized void setup(SelectExpansion selectExpansion) {
		if (selectExpansion == null) {
			throw new RuntimeException("No SelectExpansion defined!");
		}
		if (QueryBuilder.se != null) {
			throw new RuntimeException("QueryBuilder.setup can only be called once");
		}
		QueryBuilder.se = selectExpansion;
	}

	public static QueryBuilder init(final String select, final String where, final boolean isDistinct, String... selectDefNames) {
		return new QueryBuilder(select, where, isDistinct, selectDefNames);
	}

	public static QueryBuilder init(SelectExpansion selectExpansion, final String select, final String where, final boolean isDistinct, String... selectDefNames) {
		QueryBuilder.setup(selectExpansion);
		return QueryBuilder.init(select, where, isDistinct, selectDefNames);
	}

	/**
	 * Set a parameter with <code>name</code> and <code>value</code> and add
	 * <code>sqlPart</code> to the end of the SQL string, if the
	 * <code>condition</code> holds.
	 *
	 * @param name of the parameter
	 * @param value of the parameter
	 * @param sqlPart SQL string
	 * @param condition that must hold
	 * @return {@link QueryBuilder}
	 */
	public QueryBuilder setParameterIfNotNull(String name, Object value, String sqlPart) {
		return setParameterIfNotNullAnd(name, value, sqlPart, true);
	}

	public QueryBuilder setParameterIfNotNullAnd(String name, Object value, String sqlPart, boolean condition) {
		return setParameterIf(name, value, sqlPart, value != null && condition);
	}

	public QueryBuilder setParameterIfNotEmpty(String name, Object value, String sqlPart) {
		return setParameterIfNotEmptyAnd(name, value, sqlPart, true);
	}

	@SuppressWarnings("rawtypes")
	public QueryBuilder setParameterIfNotEmptyAnd(String name, Object value, String sqlPart, boolean condition) {
		return setParameterIf(name, value, sqlPart, value != null
													&& (value instanceof Collection)
													&& !((Collection)value).isEmpty()
													&& condition);
	}

	/**
	 * Set a parameter with <code>name</code> and <code>value</code> and add
	 * <code>sqlPart</code> to the end of the SQL string, if the
	 * <code>condition</code> holds.
	 *
	 * @param name of the parameter
	 * @param value of the parameter
	 * @param sqlPart SQL string
	 * @param condition that must hold
	 * @return {@link QueryBuilder}
	 */
	public QueryBuilder setParameterIf(String name, Object value, String sqlPart, boolean condition) {
		if (condition) {
			addSql(sqlPart);
			setParameter(name, value);
		}
		return this;
	}

	/**
	 * Set a parameter with <code>name</code> and <code>value</code>, if
	 * it is not null or empty.
	 *
	 * @param name of the parameter
	 * @param value of the parameter
	 * @return {@link QueryBuilder}
	 */
	public QueryBuilder setParameter(String name, Object value) {
		if (name != null && !name.isEmpty()) {
			parameters.put(name, value);
		}
		return this;
	}

	/**
	 * Append <code>sqlPart</code> to the end of the SQL string.
	 * @param sqlPart SQL string
	 * @return {@link QueryBuilder}
	 */
	public QueryBuilder addSql(String sqlPart) {
		if (sqlPart != null && !sqlPart.isEmpty()) {
			sql.append(" ");
			sql.append(sqlPart);
		}
		return this;
	}

	/**
	 * Append <code>sqlPart</code> to the end of the SQL string, if
	 * <code>condition</code> holds.
	 *
	 * @param sqlPart SQL string
	 * @return {@link QueryBuilder}
	 */
	public QueryBuilder addSqlIf(String sqlPart, boolean condition) {
		if (sqlPart != null && !sqlPart.isEmpty() && condition) {
			sql.append(" ");
			sql.append(sqlPart);
		}
		return this;
	}

	public QueryBuilder addSqlIfAlias(String sqlPart, String alias) {
		if (sqlPart != null && !sqlPart.isEmpty() && se.getUsedTargetNames().contains(alias)) {
			sql.append(" ");
			sql.append(sqlPart);
		}
		return this;
	}

	public QueryBuilder addSqlIfDefinition(String sqlPart, String selectDefName) {
		if (sqlPart != null && !sqlPart.isEmpty() && se.getUsedDefNames().contains(selectDefName)) {
			sql.append(" ");
			sql.append(sqlPart);
		}
		return this;
	}

	/**
	 * Append <code>sqlPart</code> to the end of the SQL string, if
	 * <code>object</code> is not null.
	 *
	 * @param sqlPart SQL string
	 * @return {@link QueryBuilder}
	 */
	public QueryBuilder addSqlIfNotNull(String sqlPart, Object object) {
		return addSqlIf(sqlPart, object != null);
	}

	/**
	 * Appends all <code>sqlPart</code> elements to the end of the SQL string.
	 *
	 * @param sqlPart SQL string array
	 * @return {@link QueryBuilder}
	 */
	public QueryBuilder addSql(String... sqlPart) {
		for (int i = 0; i < sqlPart.length; i++) {
			addSql(sqlPart[i]);
		}
		return this;
	}

	public QueryBuilder addLimit(long limit) {
		setParameterIf("limit", new Long(limit), "limit :limit", limit > 0);
		return this;
	}

	public QueryBuilder addOffset(long offset) {
		setParameterIf("offset", new Long(offset), "offset :offset", offset >= 0);
		return this;
	}

	public QueryBuilder expandSelect(final String... selectDef) {
		return expandSelectPrefix("", true, selectDef);
	}

	public QueryBuilder expandSelectPrefix(String prefix, boolean condition) {
		return expandSelectPrefix(prefix, condition, (String[]) null);
	}

	public QueryBuilder expandSelectPrefix(String prefix, boolean condition, final String... selectDef) {
		StringJoiner sj = new StringJoiner(", ");
		for (String expansion : se.getExpansion(selectDef).values()) {
			sj.add(expansion);
		}
		if (sj.length() > 0) {
			sql.append(" ");
			if (condition)
				sql.append(prefix);
			sql.append(sj.toString());
		}
		return this;
	}

	public QueryBuilder expandSelect() {
		return expandSelect((String[]) null);
	}

	public QueryBuilder expandSelectPrefix(String prefix) {
		return expandSelectPrefix(prefix, true, (String[]) null);
	}

	public QueryBuilder expandSelect(boolean condition, final String... selectDef) {
		if (condition) {
			expandSelect(selectDef);
		}
		return this;
	}

	public static Set<String> csvToSet(final String csv) {
		Set<String> resultSet = new HashSet<String>();
		for (String value : csv.split(",")) {
			value = value.trim();
			if (value.equals("*")) {
				resultSet.clear();
				resultSet.add(value);
				return resultSet;
			}
			resultSet.add(value);
		}
		return resultSet;
	}

	public String getSql() {
		return sql.toString();
	}

	public SelectExpansion getSelectExpansion() {
		return se;
	}

	public Map<String, Object> getParameters() {
		return parameters;
	}

	public QueryBuilder expandWhere() {
		String sqlWhere = se.getWhereSql();
		addSqlIfNotNull(" and " + sqlWhere, sqlWhere);
		if (se.getWhereParameters() != null) {
			parameters.putAll(se.getWhereParameters());
		}
		return this;
	}

	public QueryBuilder expandGroupBy() {
		return expandGroupByIf(null, true);
	}

	public QueryBuilder expandGroupByIf(final String optionalGroupings, boolean condition) {
		if (! se.hasFunctions()) {
			return this;
		}
		StringJoiner sj = new StringJoiner(", ");
		List<String> groupByTargetNames = se.getGroupByTargetNames();
		if (! groupByTargetNames.isEmpty()) {
			for (String targetName : groupByTargetNames) {
				TargetDef targetDef = se.getSchema().getTargetDefs(targetName).get(0);
				sj.add(targetDef.getColumn());
			}
		}
		if (optionalGroupings != null && condition) {
			sj.add(optionalGroupings);
		}
		if (sj.length() > 0) {
			addSql("group by " + sj);
		}
		return this;
	}

}
