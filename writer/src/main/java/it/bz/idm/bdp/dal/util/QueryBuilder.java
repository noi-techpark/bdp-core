// Copyright © 2018 IDM Südtirol - Alto Adige (info@idm-suedtirol.com)
// Copyright © 2019 NOI Techpark - Südtirol / Alto Adige (info@opendatahub.com)
//
// SPDX-License-Identifier: GPL-3.0-only

package it.bz.idm.bdp.dal.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;

/**
 * Create an instance of TypedQuery for executing a Java Persistence query language statement.
 * This is a convenience class, that supports conditional query statements and emulates getSingleResult
 * without not-found or non-unique-result exceptions.
 *
 * @author Peter Moser
 */
public class QueryBuilder {
	private StringBuilder sql = new StringBuilder();
	private EntityManager em = null;
	private Map<String, Object> parameters = new HashMap<>();
	private boolean isNativeQuery = false;

	/**
	 * Create a new {@link QueryBuilder} instance
	 *
	 * @see QueryBuilder#QueryBuilder(EntityManager)
	 *
	 * @param em {@link EntityManager}
	 * @return {@link QueryBuilder}
	 */
	public static QueryBuilder init(EntityManager em) {
		return new QueryBuilder(em);
	}

	/**
	 * Create a new {@link QueryBuilder} instance
	 *
	 * @see QueryBuilder#init
	 *
	 * @param em {@link EntityManager}
	 * @return {@link QueryBuilder}
	 */
	public QueryBuilder(EntityManager em) {
		super();
		this.em = em;
	}

	public QueryBuilder nativeQuery() {
		this.isNativeQuery = true;
		return this;
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
		return setParameterIf(name, value, sqlPart, value != null);
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
	public QueryBuilder setParameterIfNotEmpty(String name, String value, String sqlPart) {
		return setParameterIf(name, value, sqlPart, value != null && !value.isEmpty());
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
	 * Create a {@link TypedQuery} with type <code>resultClass</code> and set
	 * all collected parameters.
	 *
	 * @param resultClass Type of the query result
	 * @return {@link TypedQuery} with type <code>resultClass</code>
	 */
	public <T> TypedQuery<T> build(Class<T> resultClass) {
		TypedQuery<T> query = em.createQuery(sql.toString(), resultClass);
		for (Entry<String, Object> entry : parameters.entrySet()) {
			query.setParameter(entry.getKey(), entry.getValue());
		}
		return query;
	}

	/**
	 * Create a {@link Query} with type <code>resultClass</code> and set
	 * all collected parameters.
	 *
	 * @return {@link Query} with type <code>resultClass</code>
	 */
	public Query buildNative() {
		Query query = em.createNativeQuery(sql.toString());
		for (Entry<String, Object> entry : parameters.entrySet()) {
			query.setParameter(entry.getKey(), entry.getValue());
		}
		return query;
	}

	/**
	 * Build the current query and execute {@link jakarta.persistence.TypedQuery#getResultList}
	 *
	 * @param resultClass Type of the query result
	 * @return List of <code>resultClass</code> objects
	 */
	@SuppressWarnings("unchecked")
	public <T> List<T> buildResultList(Class<T> resultClass) {
		if (isNativeQuery) {
			return buildNative().getResultList();
		}
		return build(resultClass).getResultList();
	}

	public <T> Stream<T> buildResultStream(Class<T> resultClass) {
		return buildResultList(resultClass).stream();
	}

	/**
	 * Emulate getSingleResult without not-found or non-unique-result exceptions. Simply
	 * return null, if {@link jakarta.persistence.TypedQuery#getResultList} has no results,
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
	 * return <code>alternative</code>, if {@link jakarta.persistence.TypedQuery#getResultList}
	 * has no results, and leave exceptions to proper errors.
	 *
	 * @param resultClass Type of the query result
	 * @param alternative to be returned, if {@link jakarta.persistence.TypedQuery#getResultList} does not return results
	 * @return topmost result or 'alternative' if not found
	 */
	@SuppressWarnings("unchecked")
	public <T> T buildSingleResultOrAlternative(Class<T> resultClass, T alternative) {
		Query query = isNativeQuery ? buildNative() : build(resultClass);
		query.setMaxResults(1);
		List<Object[]> list = null ;
		list = query.getResultList();
		if (list == null || list.isEmpty() || (isNativeQuery && list.get(0) == null)) {
			return alternative;
		}
		return (T) list.get(0);
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
