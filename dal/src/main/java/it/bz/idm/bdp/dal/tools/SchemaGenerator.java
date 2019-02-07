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
package it.bz.idm.bdp.dal.tools;

import java.sql.SQLException;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.Entity;

import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.hbm2ddl.SchemaExport.Action;
import org.hibernate.tool.schema.TargetType;
import org.postgresql.ds.PGSimpleDataSource;
import org.reflections.Reflections;

/**
 * Generate a schema dump of all entities inside a given path and write it into a file and to stdout.
 * The output patterns are defined inside the implicit naming strategy class.
 *
 * Configure connections to PostgreSQL with environmental variables:
 *   ODH_SG_SERVER - database server name (default = <PGSERVER_DEFAULT>)
 *   ODH_SG_DBNAME - database name (ex., bdp, no default)
 *   ODH_SG_USER   - database user (default = <PGUSER_DEFAULT>)
 *   ODH_SG_PASSWD - database password (no default)
 *
 * Usage:
 *   SchemaGenerator FILE PREFIX
 *
 * Example:
 *   SchemaGenerator /tmp/schema_dump.sql it.bz.idm.bdp.dal
 *
 * We assume that it is a Postgis dialect, and that only @Entity annotated classes are important.
 *
 * @author Peter Moser
 */
public class SchemaGenerator {

	private static final String PGSERVER_DEFAULT  = "localhost";
	private static final String PGUSER_DEFAULT    = "postgres";
	private static final String HIBERNATE_DIALECT = "org.hibernate.spatial.dialect.postgis.PostgisDialect";

	public static void main(String[] args) throws SQLException {
		Map<String, String> env = System.getenv();

		String outputFile = args[0];
		String pathPrefix = args[1];

		/*
		 * Find all classes annotated with @Entity
		 */
		Reflections reflections = new Reflections(pathPrefix);
		Set<Class<?>> typesAnnotatedWith = reflections.getTypesAnnotatedWith(Entity.class);

		/*
		 * Build meta data sources for a hibernate registry defined by a PostgreSQL data source.
		 * FIXME Unfortunately, the registry builder needs a data source, however, also if the connection
		 * fails it succeeds in building a DDL SQL script.  The drawback hereby is, that it generates
		 * a warning on stderr, which cannot be avoided with a try-catch...
		 */
		PGSimpleDataSource ds = new PGSimpleDataSource() ;
		ds.setServerName(env.getOrDefault("ODH_SG_SERVER", PGSERVER_DEFAULT));
		ds.setDatabaseName(env.get("ODH_SG_DBNAME"));
		ds.setUser(env.getOrDefault("ODH_SG_USER", PGUSER_DEFAULT));
		ds.setPassword(env.get("ODH_SG_PASSWD"));
		StandardServiceRegistryBuilder registryBuilder = new StandardServiceRegistryBuilder()
				.applySetting("hibernate.connection.datasource", ds)
				.applySetting("hibernate.dialect", HIBERNATE_DIALECT)
				.applySetting("hibernate.implicit_naming_strategy", SchemaGeneratorImplicitNamingStrategy.class.getCanonicalName())
				;
		MetadataSources metaDataSources = new MetadataSources(registryBuilder.build());

		/*
		 * Add with @Entity annotated classes to the exporter's data sources
		 */
		for(Class<?> c : typesAnnotatedWith){
			metaDataSources.addAnnotatedClass(c);
		}
		Metadata metaData = metaDataSources.buildMetadata();

		/*
		 * Export the schema to "outputFile" and standard output.  We write only create statements, since
		 * we must not cleanup any prior databases.  What we want is to have a script that can populate
		 * a new empty database.
		 */
		SchemaExport export = new SchemaExport();
		export
			.setDelimiter(";")
			.setOutputFile(outputFile)
			.setFormat(false)
			.execute(EnumSet.of(TargetType.STDOUT, TargetType.SCRIPT), Action.CREATE, metaData);
	}

}
