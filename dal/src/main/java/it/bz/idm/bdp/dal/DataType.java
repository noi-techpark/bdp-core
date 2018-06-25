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
package it.bz.idm.bdp.dal;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.TypedQuery;

import org.hibernate.annotations.ColumnDefault;

import it.bz.idm.bdp.dal.util.JPAUtil;
import it.bz.idm.bdp.dto.DataTypeDto;


@Table(name="type",schema="intime")
@Entity
public class DataType {

	@Id
	@GeneratedValue(generator = "type_gen", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "type_gen", sequenceName = "type_seq", schema = "intime", allocationSize = 1)
	@ColumnDefault(value = "nextval('type_seq')")
	protected Long id;
	private String cname;
	private Date created_on;
	private String cunit;
	private String description;

	@ElementCollection(fetch=FetchType.EAGER)
	private Map<String, String> i18n = new HashMap<String, String>();
	private String rtype;

	public DataType() {
		this.created_on = new Date();
	}

	public DataType(String cname,  String cunit,
			String description, String rtype) {
		this();
		this.cname = cname;
		this.cunit = cunit;
		this.description = description;
		this.rtype = rtype;
	}

	public DataType(String cname) {
		this.cname = cname;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getCname() {
		return cname;
	}
	public void setCname(String cname) {
		this.cname = cname;
	}
	public Date getCreated_on() {
		return created_on;
	}
	public void setCreated_on(Date created_on) {
		this.created_on = created_on;
	}
	public String getCunit() {
		return cunit;
	}
	public void setCunit(String cunit) {
		this.cunit = cunit;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getRtype() {
		return rtype;
	}
	public void setRtype(String rtype) {
		this.rtype = rtype;
	}
	public Map<String, String> getI18n() {
		return i18n;
	}
	public void setI18n(Map<String, String> i18n) {
		this.i18n = i18n;
	}

	public static DataType findByCname(EntityManager manager, String cname) {
		TypedQuery<DataType> query = manager.createQuery("SELECT type from DataType type where type.cname = :cname ", DataType.class);
		query.setParameter("cname",cname);
		return JPAUtil.getSingleResultOrNull(query);

	}

	public static Object sync(EntityManager em, List<DataTypeDto> data) {
		em.getTransaction().begin();
		for (DataTypeDto dto : data) {
			DataType type = DataType.findByCname(em,dto.getName());
			if (type != null){
				if (dto.getDescription() != null)
					type.setDescription(dto.getDescription());
				type.setRtype(dto.getRtype());
				type.setCunit(dto.getUnit());
				if (type.getI18n().get(Locale.ENGLISH.getLanguage()) == null && dto.getDescription() != null)
					type.getI18n().put(Locale.ENGLISH.getLanguage(), dto.getDescription());
				em.merge(type);
			}else{
				type = new DataType(dto.getName(), dto.getUnit(), dto.getDescription(), dto.getRtype());
				type.getI18n().put(Locale.ENGLISH.getLanguage(), dto.getDescription());
				em.persist(type);
			}
		}
		em.getTransaction().commit();
		return null;

	}
}
