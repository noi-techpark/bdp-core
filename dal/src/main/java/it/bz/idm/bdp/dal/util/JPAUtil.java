/**
 * BDP data - Data Access Layer for the Big Data Platform
 * Copyright © 2018 OpenDataHub (info@opendatahub.bz.it)
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
 * along with this program (see LICENSE/GPLv3). If not, see
 * <http://www.gnu.org/licenses/>.
 */
package it.bz.idm.bdp.dal.util;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.ManagedType;

public class JPAUtil {

	public static EntityManagerFactory emFactory;
	private static final Properties properties = new Properties();
	static {
		try {
			properties.load(JPAUtil.class.getClassLoader().getResourceAsStream("META-INF/persistence.properties"));
			emFactory = Persistence.createEntityManagerFactory(properties.getProperty("persistenceunit"));
		}catch(Throwable ex){
			System.err.println("Cannot create EntityManagerFactory.");
			throw new ExceptionInInitializerError(ex);
		}
	}

	public static EntityManager createEntityManager() {
		return emFactory.createEntityManager();
	}

	public static void close(){
		emFactory.close();
	}

	public static Object getInstanceByType(EntityManager em, String type)
			throws Exception {
		Set<ManagedType<?>> managedTypes = em.getEntityManagerFactory().getMetamodel().getManagedTypes();
		for (ManagedType<?> entity: managedTypes){
			if (entity.getJavaType().getSimpleName().equals(type)){
				return entity.getJavaType().newInstance();
			}
		}
		throw new Exception("ERROR: Cannot get any instance of type " + type + ". Type not found.");
	}

	public static String getEntityNameByObject(Object obj) throws Exception {
		for (EntityType<?> type: emFactory.getMetamodel().getEntities()) {
			if (obj.getClass().getTypeName().equals(type.getJavaType().getName()))
					return type.getName();
		}
		throw new Exception("ERROR: Cannot get any entity name for object "
				+ obj.getClass().getTypeName() + ". Class not found.");
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

	/**
	 * Execute all native SQL commands from a given file (InputStream). It assumes -- as
	 * comment marker, and ; as command separator. It will also automatically check, if it
	 * is a DDL (data definition language) or DML (data manipulation language) construct
	 * and calls either executeUpdate or getSingleResult.
	 *
	 * @param query
	 *            The query input stream
	 */
	public static void executeNativeQueries(final InputStream query) {
		List<String> commands = new ArrayList<String>();
		String finalSQL = "";

		// Remove all comments from SQL
		try (Scanner scanner = new Scanner(query)) {
			scanner.useDelimiter("\\n");

			while (scanner.hasNext()) {
				String line = scanner.next();
				int offset = line.indexOf("--");
				offset = (offset == -1) ? line.length() : offset;
				finalSQL += line.substring(0, offset) + " ";
			}
		}

		// Fill an array with found SQL commands
		try (Scanner scanner = new Scanner(finalSQL)) {
			scanner.useDelimiter(";");
			while (scanner.hasNext()) {
				String cmd = scanner.next();
				/*
				 * Colons must be escaped, because the would mark an injected variable.
				 * Clean up some whitespace overkill to make the queries human-readable.
				 */
				cmd = cmd.trim().replaceAll("\\s+", " ").replace(":", "\\:");
				if (cmd.length() > 0)
					commands.add(cmd + ";");
			}
		}

		// Execute all remaining commands
		EntityManager em = JPAUtil.createEntityManager();
		em.getTransaction().begin();
		for (String cmd : commands) {
			if (cmd.toLowerCase().startsWith("select")) {
				em.createNativeQuery(cmd).getSingleResult();
			} else {
				em.createNativeQuery(cmd).executeUpdate();
			}
			System.err.println("Execution from input stream successful: " + cmd);
		}
		em.getTransaction().commit();
	}
}
