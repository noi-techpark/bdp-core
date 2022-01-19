/**
 * BDP data - Data Access Layer for the Big Data Platform
 *
 * Copyright © 2018 IDM Südtirol - Alto Adige (info@idm-suedtirol.com)
 * Copyright © 2019 NOI Techpark - Südtirol / Alto Adige (info@opendatahub.bz.it)
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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.ManagedType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * <p>
 * Utility class to access the entity manager and retrieve information
 * from JPA. A static initialization defines the persistence unit, which should be used.
 * Configuration will be done through META-INF/persistence.xml and META-INF/persistence.properties
 * </p>
 *
 * @author Patrick Bertolla
 * @author Peter Moser
 *
 */
public class JPAUtil {

	private static final Logger LOG = LoggerFactory.getLogger(JPAUtil.class);
	private static EntityManagerFactory emFactory;

	private JPAUtil() {}

	static {
		try {
			PropertiesWithEnv properties = PropertiesWithEnv.fromActiveSpringProfile();
			emFactory =
				Persistence.createEntityManagerFactory(
					"jpa-persistence", // This must correspond to the persistence.xml persistence-unit tag
					properties.getStringMap()
				);
		} catch (Exception ex) {
			LOG.error("Cannot create EntityManagerFactory. Exception is " + ex);
			throw new ExceptionInInitializerError(ex);
		}
	}

	public static EntityManager createEntityManager() {
		return emFactory.createEntityManager();
	}

	public static void close() {
		emFactory.close();
	}

	/**
	 * @param em entitymanager
	 * @return names of all existing jpa entities
	 */
	public static List<String> getInstanceTypes(EntityManager em) {
		Set<ManagedType<?>> managedTypes = em
			.getEntityManagerFactory()
			.getMetamodel()
			.getManagedTypes();
		List<String> types = new ArrayList<>();
		for (ManagedType<?> entity : managedTypes) {
			types.add(entity.getJavaType().getSimpleName());
		}
		return types;
	}

	/**
	 * @param obj any object which could be a instance of a jpa entity
	 * @return name of the jpa entity/class of the given object
	 */
	public static String getEntityNameByObject(Object obj) {
		for (EntityType<?> type : emFactory.getMetamodel().getEntities()) {
			if (
				obj
					.getClass()
					.getTypeName()
					.equals(type.getJavaType().getName())
			) return type.getName();
		}
		throw new JPAException(
			"ERROR: Cannot get any entity name for object " +
			obj.getClass().getTypeName() +
			". Class not found."
		);
	}

	/**
	 * Execute all native SQL commands from a given file (InputStream). It assumes -- as
	 * comment marker, and ; as command separator. It will also automatically check, if it
	 * is a DDL (data definition language) or DML (data manipulation language) construct
	 * and calls either executeUpdate or getSingleResult.
	 *
	 * @param query
	 *            The query input stream
	 * @throws Exception
	 */
	public static void executeNativeQueries(final InputStream query)
		throws Exception {
		List<String> commands = new ArrayList<>();
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
				if (cmd.length() > 0) commands.add(cmd + ";");
			}
		}

		// Execute all remaining commands
		EntityManager em = JPAUtil.createEntityManager();
		try {
			em.getTransaction().begin();
			for (String cmd : commands) {
				if (cmd.toLowerCase().startsWith("select")) {
					em.createNativeQuery(cmd).getSingleResult();
				} else {
					em.createNativeQuery(cmd).executeUpdate();
				}
				System.err.println(
					"Execution from input stream successful: " + cmd
				);
			}
			em.getTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
			throw new PersistenceException(
				"ERROR: Native querry failed: " + e.getMessage(),
				e
			);
		} finally {
			em.close();
		}
	}
}
