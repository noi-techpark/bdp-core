package it.bz.opendatahub.reader2.utils;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.jsoniter.output.JsonStream;

/**
 * Create an instance of TypedQuery for executing a Java Persistence query language statement.
 * This is a convenience class, that supports conditional query statements and emulates getSingleResult
 * without not-found or non-unique-result exceptions.
 *
 * @author Peter Moser
 */
public class QueryBuilder {
	private StringBuilder sql = new StringBuilder();
	private static NamedParameterJdbcTemplate npjt;
	MapSqlParameterSource parameters = new MapSqlParameterSource();

	/**
	 * Create a new {@link QueryBuilder} instance
	 *
	 * @see QueryBuilder#QueryBuilder(EntityManager)
	 *
	 * @param namedParameterJdbcTemplate {@link EntityManager}
	 */
	public static synchronized void setup(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
		if (QueryBuilder.npjt != null) {
			throw new RuntimeException("QueryBuilder.setup can only be called once");
		}
		QueryBuilder.npjt = namedParameterJdbcTemplate;
	}

	public static QueryBuilder init() {
		if (QueryBuilder.npjt == null) {
			throw new RuntimeException("Missing JDBC template. Run QueryBuilder.setup before initialization.");
		}
		return new QueryBuilder();
	}

	public static QueryBuilder init(NamedParameterJdbcTemplate namedParameterJdbcTemplate)  {
		QueryBuilder.setup(namedParameterJdbcTemplate);
		return QueryBuilder.init();
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
			parameters.addValue(name, value);
		}
		return this;
	}

	/**
	 * Build the current query and execute it via {@link NamedParameterJdbcTemplate#query}
	 *
	 * @return A list of (key, value) pairs
	 */
	public List<Map<String, Object>> build() {
		return npjt.query(sql.toString(), parameters, new ColumnMapRowMapper());
	}

	public <T> List<T> build(Class<T> resultClass) {
		return npjt.queryForList(sql.toString(), parameters, resultClass);
	}

	/**
	 * Build the current query and execute it via {@link NamedParameterJdbcTemplate#query},
	 * finally serialize it into a JSON string
	 *
	 * @return List of <code>resultClass</code> objects
	 */
	public String buildJson() {
		return JsonStream.serialize(build());
	}

	public <T> String buildJson(Class<T> resultClass) {
		return JsonStream.serialize(build(resultClass));
	}

	/**
	 * Emulate getSingleResult without not-found or non-unique-result exceptions. Simply
	 * return null, if {@link javax.persistence.TypedQuery#getResultList} has no results,
	 * and leave exceptions to proper errors.
	 *
	 * @param resultClass Type of the query result
	 * @return topmost result or null if not found
	 */
	public <T> T buildSingleResultOrNull(Class<T> resultClass) {
		return buildSingleResultOrAlternative(resultClass, null);
	}

	/**
	 * Emulate getSingleResult without not-found or non-unique-result exceptions. Simply
	 * return <code>alternative</code>, if {@link javax.persistence.TypedQuery#getResultList}
	 * has no results, and leave exceptions to proper errors.
	 *
	 * @param resultClass Type of the query result
	 * @param alternative to be returned, if {@link javax.persistence.TypedQuery#getResultList} does not return results
	 * @return topmost result or 'alternative' if not found
	 */
	public <T> T buildSingleResultOrAlternative(Class<T> resultClass, T alternative) {
		List<T> list = build(resultClass);
		if (list == null || list.isEmpty()) {
			return alternative;
		}
		return list.get(0);
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
}
