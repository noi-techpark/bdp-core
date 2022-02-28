package it.bz.idm.bdp.dal.util;

import java.sql.Types;

import com.vladmihalcea.hibernate.type.json.JsonNodeBinaryType;

import org.hibernate.spatial.dialect.postgis.PostgisPG95Dialect;

public class PostgisPG95JsonDialect extends PostgisPG95Dialect {

	public PostgisPG95JsonDialect() {
		super();
		this.registerHibernateType(
			Types.OTHER, JsonNodeBinaryType.class.getName()
		);
	}

}

