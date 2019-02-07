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

public class SchemaGenerator {

	public static void main(String[] args) throws SQLException {
		String outputPath = "outputxx.sql";
		Reflections reflections = new Reflections("it.bz.idm.bdp.dal");

		//find all classes annotated with @Entity
		Set<Class<?>> typesAnnotatedWith = reflections.getTypesAnnotatedWith(Entity.class);

		PGSimpleDataSource ds = new PGSimpleDataSource() ;  // Empty instance.
		ds.setServerName( "localhost" );  // The value `localhost` means the Postgres cluster running locally on the same machine.
		ds.setDatabaseName( "bdptest" );   // A connection to Postgres must be made to a specific database rather than to the server as a whole. You likely have an initial database created named `public`.
		ds.setUser( "postgres" );         // Or use the super-user 'postgres' for user name if you installed Postgres with defaults and have not yet created user(s) for your application.
		ds.setPassword( "qwertz" );     // You would not really use 'password' as a password, would you?

		StandardServiceRegistryBuilder registryBuilder = new StandardServiceRegistryBuilder()
				.applySetting("hibernate.connection.datasource", ds)
				.applySetting("hibernate.dialect", "org.hibernate.spatial.dialect.postgis.PostgisDialect")
				.applySetting("hibernate.implicit_naming_strategy", SchemaGeneratorImplicitNamingStrategy.class.getCanonicalName())
				;

		MetadataSources metaDataSources = new MetadataSources(registryBuilder.build());

		for(Class<?> c : typesAnnotatedWith){
			metaDataSources.addAnnotatedClass(c);
		}

		Metadata metaData = metaDataSources.buildMetadata();

		//execute the export
		SchemaExport export = new SchemaExport();
		export.setOutputFile(outputPath);
		export.setDelimiter(";");
		export.setFormat(false);
		export.execute(EnumSet.of(TargetType.STDOUT, TargetType.SCRIPT), Action.CREATE, metaData);
	}

}
