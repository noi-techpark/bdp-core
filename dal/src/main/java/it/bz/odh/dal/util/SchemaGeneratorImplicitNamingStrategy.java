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
package it.bz.odh.dal.util;

import java.util.List;
import java.util.Locale;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.ImplicitForeignKeyNameSource;
import org.hibernate.boot.model.naming.ImplicitIndexNameSource;
import org.hibernate.boot.model.naming.ImplicitNamingStrategyComponentPathImpl;
import org.hibernate.boot.model.naming.ImplicitUniqueKeyNameSource;
import org.hibernate.boot.spi.MetadataBuildingContext;

/**
 * Naming patterns to be applied while running the schema generator tool.  We generate all
 * identifiers as snake case strings (lower case with underscores), and add prefixes to
 * constraints, indexes and foreign keys.  Finally, we sanitize identifiers to not exceed
 * Postgres' NAMELEN constraint.
 *
 * @author Peter Moser
 */
public class SchemaGeneratorImplicitNamingStrategy extends ImplicitNamingStrategyComponentPathImpl {

	/* Postgres supports names with at most NAMELEN chars */
	private static final int POSTGRES_NAMELEN = 63;
	private static final long serialVersionUID = 1L;

	@Override
	protected Identifier toIdentifier(String stringForm, MetadataBuildingContext buildingContext) {
		return super.toIdentifier(lowerSnakecase(stringForm), buildingContext);
	}

	@Override
	public Identifier determineUniqueKeyName(ImplicitUniqueKeyNameSource source) {
		String cols = identifiersToSnakeCase(source.getColumnNames());
		String name = sanitizeName("uc_" + source.getTableName() + cols);
		return toIdentifier(name, source.getBuildingContext());
	}

	@Override
	public Identifier determineForeignKeyName(ImplicitForeignKeyNameSource source) {
		String cols = identifiersToSnakeCase(source.getColumnNames());
		String colsRef = identifiersToSnakeCase(source.getReferencedColumnNames());
		if (colsRef.length() == 0) {
			/* It references the primary key */
			colsRef = "_pk";
		}
		String name = sanitizeName("fk_" + source.getTableName() + cols + "_" + source.getReferencedTableName() + colsRef);
		return toIdentifier(name, source.getBuildingContext());
	}

	@Override
	public Identifier determineIndexName(ImplicitIndexNameSource source) {
		String cols = identifiersToSnakeCase(source.getColumnNames());
		String name = sanitizeName("idx_" + source.getTableName() + cols);
		return toIdentifier(name, source.getBuildingContext());
	}

	/**
	 * Translate camel-case strings into lower-case strings with underscores.
	 * @param text
	 * @return
	 */
	private static String lowerSnakecase(String text) {
		final StringBuilder buf = new StringBuilder(text.replace('.', '_'));
		for (int i = 1; i < buf.length() - 1; i++) {
			if (Character.isLowerCase(buf.charAt(i - 1))
					&& Character.isUpperCase(buf.charAt(i))
					&& Character.isLowerCase(buf.charAt(i + 1))) {
				buf.insert(i++, '_');
			}
		}
		return buf.toString().toLowerCase(Locale.ROOT);
	}

	private static String identifiersToSnakeCase(List<Identifier> identifiers) {
		String cols = "";
		for (Identifier col : identifiers) {
			cols += "_" + col;
		}
		return cols;
	}

	/**
	 * PostgreSQL supports max. lengths of POSTGRES_NAMELEN characters. It would truncate
	 * the end of the identifier name, but we do it better in the middle and add another
	 * underscore at the end to signal that it is shortened.
	 * @param name
	 * @return
	 */
	private static String sanitizeName(String name) {
		if (name.length() <= POSTGRES_NAMELEN)
			return name;

		int h = (POSTGRES_NAMELEN - 2) / 2;
		return name.substring(0, h + 1) + "_" + name.substring(name.length() - h) + "_";
	}
}

