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
import java.util.Iterator;
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
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.ColumnDefault;

import it.bz.idm.bdp.dal.util.JPAException;
import it.bz.idm.bdp.dal.util.QueryBuilder;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.TypeDto;


/**
 * <p>
 * DataType defines what you are measuring. Every measurement {@link MeasurementAbstractHistory}<br/>
 * references exactly one data type, which gives you the required information to<br/>
 * interpret it correctly
 * </p>
 * @author Patrick Bertolla
 * @author Peter Moser
 */
@Table(name="type",
	uniqueConstraints = {
			@UniqueConstraint(columnNames = {"cname"})
			}
)
@Entity
public class DataType {

	@Id
	@GeneratedValue(generator = "type_gen", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "type_gen", sequenceName = "type_seq", allocationSize = 1)
	@ColumnDefault(value = "nextval('type_seq')")
	protected Long id;

	@Column(nullable = false)
	private String cname;

	private Date created_on;
	private String cunit;
	private String description;
	private String rtype;

	public DataType() {
		setCreated_on(new Date());
	}

	/**
	 * @param dataType unique identifier for a data type
	 * @param cunit unit of specific measurements
	 * @param description of a specific measurements
	 * @param rtype metric of a specific measurements
	 */
	public DataType(String dataType, String cunit, String description, String rtype) {
		this(dataType);
		setCunit(cunit);
		setDescription(description);
		setRtype(rtype);
	}

	/**
	 * @param dataType unique identifier for a data type
	 */
	public DataType(String dataType) {
		this();
		setCname(dataType);
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
	public void setCname(String dataType) {
		this.cname = dataType;
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

	/**
	 * @param em entity manager
	 * @param dataType unique identifier for a data type
	 * @return a data type entity from database
	 */
	public static DataType findByCname(EntityManager em, String dataType) {
		return QueryBuilder
				.init(em)
				.addSql("SELECT type FROM DataType type WHERE type.cname = :cname")
				.setParameter("cname", dataType)
				.buildSingleResultOrNull(DataType.class);
	}

	/**
	 * @param em entity manager
	 * @return a list of unique string identifier, each representing a data type
	 */
	public static List<String> findTypeNames(EntityManager em) {
		return em.createQuery("SELECT t.cname FROM DataType t GROUP BY t.cname", String.class)
				 .getResultList();
	}

	/**
	 * <p>Finds data types grouped by period and only if at least one record of the specific data type exists in the db.<br/>
	 *  It can also be filtered by a specific station.
	 *  </p>
	 * @param em entity manager
	 * @param stationType typology of the specific station e.g. MeteoStation,
	 *                    Environmentstation etc.
	 * @param stationCode unique stringidentifier of a specific station entity
	 * @param subClass Measurementimplementation class which to read from
	 * @return
	 */
	public static <T> List<TypeDto> findTypes(EntityManager em, String stationType, String stationCode, Class<T> subClass) {
		List<Object[]> resultList = QueryBuilder
				.init(em)
				.addSql("SELECT type, m.period FROM " + subClass.getSimpleName() + " m INNER JOIN m.type type",
						"WHERE m.station.stationtype = :stationType")
				.setParameter("stationType", stationType)
				.setParameterIf("station", stationCode, "AND m.station.stationcode = :station", stationCode != null && !stationCode.isEmpty())
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
				if (type.getDescription() != null) {
					Map<String, String> desc = new HashMap<String, String>();
					desc.put("default", type.getDescription().trim());
					dto.setDesc(desc);
				}
				dto.setTypeOfMeasurement(type.getRtype());
				dtos.put(id, dto);
			}
			dto.getAcquisitionIntervals().add(acqInterval);
		}
		for (Map.Entry<String, TypeDto> entry : dtos.entrySet())
			types.add(entry.getValue());
		return types;
	}

	/**
	 * Finds data types according station types and station codes grouped by period and only if
	 * at least one record of the specific data type exists.
	 *
	 * @param em			entity manager
	 * @param stationType	typology of a {@link Station}
	 * @param stationCode	unique identifier of a {@link Station}
	 *
	 * @return a list of data types with all their details
	 */
	public static List<TypeDto> findTypes(EntityManager em, String stationType, String stationCode) {
		List<TypeDto> resultDouble = findTypes(em, stationType, stationCode, Measurement.class);
		List<TypeDto> resultString = findTypes(em, stationType, stationCode, MeasurementString.class);
		resultDouble.addAll(resultString);
		return new ArrayList<>(new HashSet<>(resultDouble));
	}

	/**
	 * Find data types and return them as a list of string arrays with 4 elements each:
	 * <code> [ID, UNIT, DESCRIPTION, INTERVAL] </code>
	 *
	 * <p>
	 * We use the new function {@link findTypes} internally, and convert the
	 * {@link TypeDto} output into string arrays, because we do not want to duplicate
	 * the finding-types query.  Finally, we need to create duplicate types for each
	 * acquisition interval.
	 * </p>
	 *
	 * @param em			The Entity Manager
	 * @param stationType	Station type, i.e., {@code EnvironmentStation}
	 * @param stationCode	Station ID, i.e., {@code BZ:01}
	 * @return				List of string arrays with 4 elements each, see above <br />
	 * 						or an empty string array, if nothing can be found
	 */
	public static List<String[]> findDataTypes(EntityManager em, String stationType, String stationCode) {
		List<TypeDto> typeDtoList = findTypes(em, stationType, stationCode);
		List<String[]> result = new ArrayList<>();
		for (TypeDto item : typeDtoList) {
			Iterator<Integer> acqIntIterator = item.getAcquisitionIntervals().iterator();
			do {
				String interval = "";
				if (acqIntIterator.hasNext()) {
					interval = acqIntIterator.next().toString();
				}
				String[] arr = {
						item.getId(),
						item.getUnit() == null ? "" : item.getUnit(),
						item.getDesc().isEmpty() ? "" : item.getDesc().entrySet().iterator().next().getValue(),
						interval
					};
				result.add(arr);
			} while (acqIntIterator.hasNext());
		}
		return result;
	}

	/**
	 * <p>
	 * Upserts a list of data types, but does not override type description if<br/>
	 * already provided in db
	 * </p>
	 * @param em   entity manager
	 * @param data list of data types provided by a data collector
	 */
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
			throw JPAException.unnest(e);
		}
		em.getTransaction().commit();
	}
}
