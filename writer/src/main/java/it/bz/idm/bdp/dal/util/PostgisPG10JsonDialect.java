package it.bz.idm.bdp.dal.util;

import java.sql.Types;

import com.vladmihalcea.hibernate.type.json.JsonNodeBinaryType;
import org.hibernate.spatial.dialect.postgis.PostgisPG10Dialect;

public class PostgisPG10JsonDialect extends PostgisPG10Dialect {

	public PostgisPG10JsonDialect() {
		super();
		this.registerHibernateType(
			Types.OTHER, JsonNodeBinaryType.class.getName()
		);
	}

}

