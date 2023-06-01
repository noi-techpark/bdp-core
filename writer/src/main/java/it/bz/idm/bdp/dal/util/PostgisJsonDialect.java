// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.idm.bdp.dal.util;

import java.sql.Types;

import com.vladmihalcea.hibernate.type.json.JsonNodeBinaryType;
import org.hibernate.spatial.dialect.postgis.PostgisPG10Dialect;

public class PostgisJsonDialect extends PostgisPG10Dialect {

	public PostgisJsonDialect() {
		super();
		this.registerHibernateType(
			Types.OTHER, JsonNodeBinaryType.class.getName()
		);
	}

}

