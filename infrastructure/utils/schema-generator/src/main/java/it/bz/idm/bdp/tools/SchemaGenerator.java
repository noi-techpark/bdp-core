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
package it.bz.idm.bdp.tools;

import java.net.URL;
import java.net.URLClassLoader;
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
 * <p>Generate a schema dump of all entities inside a given path and write it into a file.
 * The output patterns are defined inside the implicit naming strategy class.
 *
 * <p>Configure connections to PostgreSQL with environmental variables:</p>
 * <ul>
 *   <li><code>ODH_SG_SERVER</code> - database server name (default = <PGSERVER_DEFAULT>)
 *   <li><code>ODH_SG_DBNAME</code> - database name (ex., bdp, no default)
 *   <li><code>ODH_SG_USER</code>   - database user (default = <PGUSER_DEFAULT>)
 *   <li><code>ODH_SG_PASSWD</code> - database password (no default)
 * </ul>
 *
 * <p>Usage:
 *   <code>SchemaGenerator PREFIX STRATEGYCLASS OUTPUTFILE</code>
 *
 * <p>Example:
 *   <code>SchemaGenerator it.bz.idm.bdp.dal it.bz.idm.bdp.dal.util.SchemaGeneratorImplicitNamingStrategy /tmp/schema_dump.sql</code>
 *
 * <p>We assume that it is a Postgis dialect, and that only @Entity annotated classes are important.
 *
 * @author Peter Moser
 */
public class SchemaGenerator {

	private static final String PGSERVER_DEFAULT  = "localhost";
	private static final String PGUSER_DEFAULT    = "postgres";
	private static final String HIBERNATE_DIALECT = "org.hibernate.spatial.dialect.postgis.PostgisDialect";

	public static void main(String[] args) throws SQLException {
		Map<String, String> env = System.getenv();

		if (args.length != 3) {
			System.out.println(
				"\nSCHEMA GENERATOR - Dump the Hibernate SQL DDL script into a file" +
				"\n" +
				"  Generate a schema dump of all entities inside a given path and write it into a file.\n" +
				"  The output patterns are defined inside the implicit naming strategy class.\n" +
				"\n" +
				"  Configure connections to PostgreSQL with environmental variables:\n" +
				"  *   ODH_SG_SERVER - database server name (default = <PGSERVER_DEFAULT>)\n" +
				"  *   ODH_SG_DBNAME - database name (ex., bdp, no default)\n" +
				"  *   ODH_SG_USER   - database user (default = <PGUSER_DEFAULT>)\n" +
				"  *   ODH_SG_PASSWD - database password (no default)" +
				"\n" +
				"  USAGE: \n" +
				"    java -cp 'ENTITY_MODEL.jar:schemagenerator-x.y.z.jar' \\ \n" +
				"         it.bz.idm.bdp.tools.SchemaGenerator \\ \n" +
				"         ENTITY_MODEL_PATH_PREFIX \\ \n" +
				"         STRATEGYCLASS \\ \n" +
				"         OUTPUTFILE \n" +
				"\n" +
				"  EXAMPLE: \n" +
				"    java -cp 'dal/target/dal-2.0.0.jar:tools/target/schemagenerator-1.0.0.jar' \\ \n" +
				"         it.bz.idm.bdp.tools.SchemaGenerator \\ \n" +
				"         it.bz.idm.bdp.dal \\ \n" +
				"         it.bz.idm.bdp.dal.util.SchemaGeneratorImplicitNamingStrategy \\ \n" +
				"         /tmp/schema_dump.sql \n" +
				"\n");
			System.exit(1);
		}

		String pathPrefix = args[0];
		String namingStrategy = args[1];
		String outputFile = args[2];

		/*
		 * Show what we load, to be sure we dump the correct entities...
		 */
		ClassLoader cl = ClassLoader.getSystemClassLoader();
        URL[] urls = ((URLClassLoader)cl).getURLs();
        System.out.println("Your class path contains the following files:");
        for(URL url: urls){
        	System.out.println("  -> " + url.getFile());
        }

		/*
		 * Find all classes annotated with @Entity
		 */
        System.out.println("Search for @Entity annotated classes inside '" + pathPrefix + "'.");
		Reflections reflections = new Reflections(pathPrefix);
		Set<Class<?>> typesAnnotatedWith = reflections.getTypesAnnotatedWith(Entity.class);

		/*
		 * Build meta data sources for a hibernate registry defined by a PostgreSQL data source.
		 * FIXME Unfortunately, the registry builder needs a data source, however, also if the connection
		 * fails it succeeds in building a DDL SQL script.  The drawback hereby is, that it generates
		 * a warning on stderr, which cannot be avoided with a try-catch...
		 */
		PGSimpleDataSource ds = new PGSimpleDataSource();
		ds.setServerName(env.getOrDefault("ODH_SG_SERVER", PGSERVER_DEFAULT));
		ds.setDatabaseName(env.get("ODH_SG_DBNAME"));
		ds.setUser(env.getOrDefault("ODH_SG_USER", PGUSER_DEFAULT));
		ds.setPassword(env.get("ODH_SG_PASSWD"));
		StandardServiceRegistryBuilder registryBuilder = new StandardServiceRegistryBuilder()
				.applySetting("hibernate.connection.datasource", ds)
				.applySetting("hibernate.dialect", HIBERNATE_DIALECT)
				.applySetting("hibernate.implicit_naming_strategy", namingStrategy)
				;

		MetadataSources metaDataSources = new MetadataSources(registryBuilder.build());

		/*
		 * Add with @Entity annotated classes to the exporter's data sources
		 */
		System.out.println("The following @Entity classes have been found:");
		for(Class<?> c : typesAnnotatedWith) {
			metaDataSources.addAnnotatedClass(c);
			System.out.println("  -> " + c);
		}
		Metadata metaData = metaDataSources.buildMetadata();

		/*
		 * Export the schema to "outputFile".  We write only create statements, since we must
		 * not cleanup any prior databases.  What we want is to have a script that can populate
		 * a new empty database.
		 */
		System.out.println("Dumping schema...");
		SchemaExport export = new SchemaExport();
		export
			.setDelimiter(";")
			.setOutputFile(outputFile)
			.setFormat(false)
			.execute(EnumSet.of(TargetType.SCRIPT), Action.CREATE, metaData);
		System.out.println("Done. Schema dumped to '" + outputFile + "':");
	}
}
