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
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.ColumnDefault;

import it.bz.idm.bdp.dal.util.JPAException;
import it.bz.idm.bdp.dal.util.QueryBuilder;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.TypeDto;


@Table(name="type")
@Entity
public class DataType {

	@Id
	@GeneratedValue(generator = "type_gen", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "type_gen", sequenceName = "type_seq", allocationSize = 1)
	@ColumnDefault(value = "nextval('type_seq')")
	protected Long id;

	@Column(nullable = false, unique = true)
	private String cname;

	private Date created_on;
	private String cunit;
	private String description;
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

	public static DataType findByCname(EntityManager em, String cname) {
		return QueryBuilder
				.init(em)
				.addSql("SELECT type FROM DataType type WHERE type.cname = :cname")
				.setParameter("cname", cname)
				.buildSingleResultOrNull(DataType.class);

	}

	public static List<String> findTypeNames(EntityManager em) {
		return em.createQuery("SELECT t.cname FROM DataType t GROUP BY t.cname", String.class)
				 .getResultList();
	}

	public static <T> List<TypeDto> findTypes(EntityManager em, String stationType, String stationId, Class<T> subClass) {
		List<Object[]> resultList = QueryBuilder
				.init(em)
				.addSql("SELECT type, m.period FROM " + subClass.getSimpleName() + " m INNER JOIN m.type type",
						"WHERE m.station.stationtype = :stationType")
				.setParameter("stationType", stationType)
				.setParameterIf("station", stationId, "AND m.station.stationcode = :station", stationId != null && !stationId.isEmpty())
				.addSql("GROUP BY type, m.period")
				.buildResultList(Object[].class);

		List<TypeDto> types = new ArrayList<TypeDto>();
		Map<String,TypeDto> dtos = new HashMap<String, TypeDto>();

		for (Object obj : resultList){
			Object[] results = (Object[]) obj;
			DataType type = (DataType) results[0];
			Integer acqInterval = (Integer) results[1];
			String id = type.getCname();
			TypeDto dto = dtos.get(id);
			if (dto == null){
				dto = new TypeDto();
				dto.setId(id);
				dto.setUnit(type.getCunit());
				dto.setTypeOfMeasurement(type.getRtype());
				dtos.put(id, dto);
			}
			dto.getAcquisitionIntervals().add(acqInterval);
		}
		for (Map.Entry<String, TypeDto> entry : dtos.entrySet())
			types.add(entry.getValue());
		return types;
	}

	public static <T> List<TypeDto> findTypes(EntityManager em, String stationType, String stationId) {
		List<TypeDto> resultDouble = findTypes(em, stationType, stationId, Measurement.class);
		List<TypeDto> resultString = findTypes(em, stationType, stationId, MeasurementString.class);
		resultDouble.addAll(resultString);
		return new ArrayList<>(new HashSet<>(resultDouble));
	}

	/**
	 * Find data types and return them as a list of string arrays with 4 elements each: <br />
	 * <code> [[ID, UNIT, DESCRIPTION, INTERVAL], ...] </code>
	 * <p>
	 * We use the new function {@link findTypes} internally, and convert the
	 * {@link TypeDto} output into string arrays, because we do not want to duplicate
	 * the finding-types query.
	 * </p>
	 *
	 * @param em			The Entity Manager
	 * @param stationType	Station type, i.e., {@code EnvironmentStation}
	 * @param stationId		Station ID, i.e., {@code BZ:01}
	 * @return				List of string arrays with 4 elements each, see above <br />
	 * 						or an empty string array, if nothing can be found
	 */
	public static List<String[]> findDataTypes(EntityManager em, String stationType, String stationId) {
		List<TypeDto> typeDtoList = findTypes(em, stationType, stationId);
		List<String[]> result = new ArrayList<>();
		for (TypeDto item : typeDtoList) {
			String[] arr = {
					item.getId(),
					item.getUnit() == null ? "" : item.getUnit(),
					item.getDesc().isEmpty() ? "" : item.getDesc().entrySet().iterator().next().getValue(),
					item.getAcquisitionIntervals().isEmpty() ? "" : item.getAcquisitionIntervals().iterator().next().toString()
				};
			result.add(arr);
		}
		return result;
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
					em.merge(type);
				}else{
					type = new DataType(dto.getName(), dto.getUnit(), dto.getDescription(), dto.getRtype());
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
