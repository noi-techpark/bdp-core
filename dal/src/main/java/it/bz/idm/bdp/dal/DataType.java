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
package it.bz.idm.bdp.dal;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.persistence.Column;
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

import it.bz.idm.bdp.dal.util.JPAException;
import it.bz.idm.bdp.dal.util.JPAUtil;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.TypeDto;


@Table(name="type",schema="intime")
@Entity
public class DataType {

	@Id
	@GeneratedValue(generator = "type_gen", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "type_gen", sequenceName = "type_seq", schema = "intime", allocationSize = 1)
	@ColumnDefault(value = "nextval('intime.type_seq')")
	protected Long id;

	@Column(nullable = false, unique = true)
	private String cname;

	private Date created_on;
	private String cunit;
	private String description;

	@ElementCollection(fetch=FetchType.EAGER)
	private Map<String, String> i18n = new HashMap<String, String>();
	private String rtype;

	public DataType() {
		setCreated_on(new Date());
	}

	public DataType(String cname,  String cunit, String description, String rtype) {
		this(cname);
		setCunit(cunit);
		setDescription(description);
		setRtype(rtype);
	}

	public DataType(String cname) {
		this();
		setCname(cname);
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
		TypedQuery<DataType> query = manager.createQuery("SELECT type from DataType type where type.cname = :cname ", DataType.class)
											.setParameter("cname",cname);
		return JPAUtil.getSingleResultOrNull(query);

	}

	public static List<String> findTypeNames(EntityManager em) {
		return em.createQuery("SELECT t.cname FROM DataType t GROUP BY t.cname", String.class)
				 .getResultList();
	}

	public static List<TypeDto> findTypes(EntityManager em, String stationType, String stationId) {

		String queryString = "SELECT type, m.period FROM Measurement m INNER JOIN m.type type"
						   + " WHERE m.station.stationtype = :stationType";
		String queryGroupBy = " GROUP BY type, m.period";
		String andStationCode = " AND record.station.stationcode = :station";

		List<Object[]> resultList = null;
		if (stationId == null || stationId.isEmpty()) {
			resultList = em.createQuery(queryString + queryGroupBy, Object[].class)
						   .setParameter("stationType", stationType)
						   .getResultList();
		} else {
			resultList = em.createQuery(queryString + andStationCode + queryGroupBy, Object[].class)
						   .setParameter("stationType", stationType)
						   .setParameter("station",stationId)
						   .getResultList();
		}

		List<TypeDto> types = new ArrayList<TypeDto>();
		Map<String,TypeDto> dtos = new HashMap<String, TypeDto>();

		for (Object obj : resultList){
			Object[] results = (Object[]) obj;
			DataType type = (DataType) results[0];
			Integer acqIntervall = (Integer) results[1];
			String id = type.getCname();
			TypeDto dto = dtos.get(id);
			if (dto == null){
				dto = new TypeDto();
				dto.getDesc().putAll(type.getI18n());
				dto.setId(id);
				dto.setUnit(type.getCunit());
				dto.setTypeOfMeasurement(type.getRtype());
				dtos.put(id, dto);
			}
			dto.getAquisitionIntervalls().add(acqIntervall);
		}
		for (Map.Entry<String, TypeDto> entry : dtos.entrySet())
			types.add(entry.getValue());
		return types;
	}

	public static List<String[]> findDataTypes(EntityManager em,String stationType, String stationId) {
		String queryString = "SELECT m.type.cname, m.type.cunit, m.type.description, m.period FROM Measurement m"
						   + " INNER JOIN m.type WHERE m.station.stationtype = :stationtype";
		String andStationCode = " AND m.station.stationcode = :station";
		String queryGroupBy = " GROUP BY m.type.cname, m.type.cunit, m.type.description, m.period";
		List<Object[]> resultList = null;
		if (stationId == null || stationId.isEmpty()) {
			resultList = em.createQuery(queryString + queryGroupBy, Object[].class)
						   .setParameter("stationtype", stationType)
						   .getResultList();
		} else {
			resultList = em.createQuery(queryString + andStationCode + queryGroupBy, Object[].class)
						   .setParameter("station", stationId)
						   .setParameter("stationtype", stationType)
						   .getResultList();
		}
		return getDataTypesFromQuery(resultList);
	}

	private static List<String[]> getDataTypesFromQuery(List<Object[]> resultList){
		List<String[]> stringlist = new ArrayList<String[]>();
		for(Object[] objects : resultList){
			String[] stringarray= new String[objects.length];
			for (int i = 0; i< objects.length;i++){
				String value = String.valueOf(objects[i]);
				stringarray[i]= "null".equals(value) ? "" : value;
			}
			stringlist.add(stringarray);
		}
		return stringlist;
	}

	public static void sync(EntityManager em, List<DataTypeDto> data) {
		try {
			em.getTransaction().begin();
			for (DataTypeDto dto : data) {
				if (! dto.isValid()) {
					throw new JPAException("Invalid JSON for " + DataTypeDto.class.getSimpleName(), DataTypeDto.class);
				}

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
		} catch (Exception e) {
			e.printStackTrace();
			em.getTransaction().rollback();
			if (e instanceof JPAException)
				throw (JPAException) e;
			throw new JPAException(e.getMessage(), e);
		}
	}
}
