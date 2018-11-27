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

	public QueryBuilder(EntityManager em) {
		super();
		this.em = em;
	}

	public QueryBuilder(EntityManager em, String sqlPart) {
		this(em);
		addSql(sqlPart);
	}

	public QueryBuilder(EntityManager em, String... sqlPart) {
		this(em);
		addSql(sqlPart);
	}


	public QueryBuilder setParameterIfNotNull(String name, Object value, String sqlPart) {
		if (value != null) {
			addSql(sqlPart);
			setParameter(name, value);
		}
		return this;
	}

	public QueryBuilder setParameter(String name, Object value) {
		if (name != null && !name.isEmpty()) {
			parameters.put(name, value);
		}
		return this;
	}

	public <T> TypedQuery<T> build(Class<T> resultClass) {
		TypedQuery<T> query = em.createQuery(sql.toString(), resultClass);
		for (Entry<String, Object> entry : parameters.entrySet()) {
			query.setParameter(entry.getKey(), entry.getValue());
		}
		return query;
	}

	public <T> List<T> buildResultList(Class<T> resultClass) {
		return build(resultClass).getResultList();
	}

	/**
	 * Emulates getSingleResult without not-found or non-unique-result exceptions. It
	 * simply returns null on no-results. Leaves exceptions to proper errors.
	 *
	 * @param query
	 * @return topmost result or null if not found
	 */
	public <T> T buildSingleResultOrNull(Class<T> resultClass) {
		return buildSingleResultOrAlternative(resultClass, null);
	}

	/**
	 * Emulates getSingleResult without not-found or non-unique-result exceptions. It
	 * simply returns 'alternative' on no-results. Leaves exceptions to proper errors.
	 *
	 * @param query
	 * @param alternative
	 * @return topmost result or 'alternative' if not found
	 */
	public <T> T buildSingleResultOrAlternative(Class<T> resultClass, T alternative) {
		TypedQuery<T> query = build(resultClass);
		return getSingleResultOrAlternative(query, alternative);
	}

	/**
	 * Emulates getSingleResult without not-found or non-unique-result exceptions. It
	 * simply returns null on no-results. Leaves exceptions to proper errors.
	 *
	 * @param query
	 * @return topmost result or null if not found
	 */
	public static <T> T getSingleResultOrNull(TypedQuery<T> query) {
		return getSingleResultOrAlternative(query, null);
	}

	/**
	 * Emulates getSingleResult without not-found or non-unique-result exceptions. It
	 * simply returns 'alternative' on no-results. Leaves exceptions to proper errors.
	 *
	 * @param query
	 * @param alternative
	 * @return topmost result or 'alternative' if not found
	 */
	public static <T> T getSingleResultOrAlternative(TypedQuery<T> query, T alternative) {
		query.setMaxResults(1);
		List<T> list = query.getResultList();
		if (list == null || list.isEmpty()) {
			return alternative;
		}
		return list.get(0);
	}

	public QueryBuilder addSql(String sqlPart) {
		if (sqlPart != null && !sqlPart.isEmpty()) {
			sql.append(" ");
			sql.append(sqlPart);
		}
		return this;
	}

	public QueryBuilder addSql(String... sqlPart) {
		for (int i = 0; i < sqlPart.length; i++) {
			addSql(sqlPart[i]);
		}
		return this;
	}
}
