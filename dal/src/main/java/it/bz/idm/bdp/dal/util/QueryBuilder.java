/**
 * BDP data - Data Access Layer for the Big Data Platform
 * Copyright © 2018 IDM Südtirol - Alto Adige (info@idm-suedtirol.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program (see LICENSES/GPL-3.0.txt). If not, see
 * <http://www.gnu.org/licenses/>.
 *
 * SPDX-License-Identifier: GPL-3.0
 */
package it.bz.idm.bdp.dal.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

public class QueryBuilder {
	private StringBuilder sql = new StringBuilder();
	private EntityManager em = null;
	private Map<String, Object> parameters = new HashMap<String, Object>();

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
	 * Build the current query and execute {@link javax.persistence.TypedQuery#getResultList}
	 *
	 * @param resultClass Type of the query result
	 * @return List of <code>resultClass</code> objects
	 */
	public <T> List<T> buildResultList(Class<T> resultClass) {
		return build(resultClass).getResultList();
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
		TypedQuery<T> query = build(resultClass);
		query.setMaxResults(1);
		List<T> list = query.getResultList();
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
