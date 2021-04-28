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
package it.bz.idm.bdp.dal;

import java.util.Date;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;

/**
 * <p>
 * MetaData is a versioned JSONB map containing all additional information for a<br/>
 * {@link DataType}. This is needed when a datatype contains additional information that<br/>
 * can change in time.
 * </p>
 *
 * @author Patrick Bertolla
 *
 */
@TypeDefs({ @TypeDef(name = "jsonb", typeClass = JsonBinaryType.class) })
@Entity
@Table(name = "type_metadata")
public class DataTypeMetaData {

	@Id
	@GeneratedValue(generator = "type_metadata_gen", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "type_metadata_gen", sequenceName = "type_metadata_seq", allocationSize = 1)
	@ColumnDefault(value = "nextval('type_metadata_seq')")
	protected Long id;

	@Type(type = "jsonb")
	@Column(columnDefinition = "jsonb")
	private Map<String, Object> json;

	@ManyToOne
	private DataType type;

	private Date created_on;

	public DataTypeMetaData() {
		created_on = new Date();
	}

	public DataTypeMetaData(DataType type, Map<String, Object> metaData) {
		this();
		this.type = type;
		this.setJson(metaData);
	}

	public Map<String, Object> getJson() {
		return json;
	}

	public void setJson(Map<String, Object> metaData) {
		this.json = metaData;
	}
	
	public DataType getType() {
		return type;
	}

	public void setType(DataType type) {
		this.type = type;
	}

	public Date getCreated() {
		return created_on;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((json == null) ? 0 : json.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DataTypeMetaData other = (DataTypeMetaData) obj;
		if (json == null) {
			if (other.json != null)
				return false;
		} else if (!json.equals(other.json))
			return false;
		return true;
	}
	
}
