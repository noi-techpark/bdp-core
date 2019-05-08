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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import it.bz.idm.bdp.dal.authentication.BDPRole;
import it.bz.idm.bdp.dal.util.JPAException;
import it.bz.idm.bdp.dal.util.QueryBuilder;
import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.RecordDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.SimpleRecordDto;

/**
 * <p>This entity contains all measurements and is the biggest container for the data.
 * Each measurement <strong>must</strong> extend this base class to keep integrity.
 * It contains the two most important references to station and type and also contains generic
 * methods on how data gets stored and retrieved.
 *
 * @author Peter Moser
 * @author Patrick Bertolla
 */
@MappedSuperclass
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
public abstract class MeasurementAbstractHistory implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(nullable = false)
	private Date created_on;

	@Column(nullable = false)
	private Date timestamp;

	@ManyToOne(optional = false, cascade = CascadeType.ALL)
	private Station station;

	@ManyToOne(optional = false, cascade = CascadeType.PERSIST)
	private DataType type;

	@Column(nullable = false)
	private Integer period;

	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	private Provenance provenance;

	public abstract List<RecordDto> findRecords(EntityManager em, String stationtype, String identifier, String cname, Date start, Date end, Integer period, BDPRole role);

	public MeasurementAbstractHistory() {
		this.created_on = new Date();
	}
	/**
	 * @param station entity the measurement refers to
	 * @param type entity the measurement refers to
	 * @param timestamp UTC time of the measurement detection
	 * @param period standard interval between 2 measurements
	 */
	public MeasurementAbstractHistory(Station station, DataType type, Date timestamp, Integer period) {
		this.station = station;
		this.type = type;
		this.timestamp = timestamp;
		this.period = period;
		this.created_on = new Date();
	}

	public Date getCreated_on() {
		return created_on;
	}

	public void setCreated_on(Date created_on) {
		this.created_on = created_on;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	public Station getStation() {
		return station;
	}

	public void setStation(Station station) {
		this.station = station;
	}

	public DataType getType() {
		return type;
	}

	public void setType(DataType type) {
		this.type = type;
	}

	public Integer getPeriod() {
		return period;
	}

	public void setPeriod(Integer period) {
		this.period = period;
	}

	public Provenance getProvenance() {
		return provenance;
	}

	public void setProvenance(Provenance provenance) {
		this.provenance = provenance;
	}
	public abstract void setValue(Object value);
	public abstract Object getValue();

	/**
	 * <p>
	 * persists all measurement data send to the writer from data collectors to the database.<br/>
	 * This method goes down the data tree and persists all new records<br/>
	 * it also updates the newest measurement in {@link MeasurementAbstract}, if it really is newer
	 * </p>
	 * @param em entity manager
	 * @param stationType typology of the specific station, e.g., MeteoStation, EnvironmentStation
	 * @param dataMap  container for data send from data collector containing measurements<br/>
	 * Data is received in a tree structure, containing in the first level the identifier of the correlated station,<br/>
	 * on the second level the identifier of the correlated data type and on the last level the data itself
	 * @throws JPAException if data is in any way corrupted or one of the references {@link Station}, {@link DataType}<br/> does not exist in the database yet
	 */
	public static void pushRecords(EntityManager em, String stationType, DataMapDto<RecordDtoImpl> dataMap) {
		boolean givenDataOK = false;
		boolean stationFound = false;
		boolean typeFound = false;
		boolean jsonOK = false;
		try {
			for (Entry<String, DataMapDto<RecordDtoImpl>> stationEntry : dataMap.getBranch().entrySet()) {
				Station station = Station.findStation(em, stationType, stationEntry.getKey());
				if (station == null) {
					System.err.println("pushRecords: Station '" + stationType + "/" + stationEntry.getKey() + "' not found. Skipping...");
					continue;
				}
				stationFound = true;
				for(Entry<String,DataMapDto<RecordDtoImpl>> typeEntry : stationEntry.getValue().getBranch().entrySet()) {
					try {
						DataType type = DataType.findByCname(em, typeEntry.getKey());
						if (type == null) {
							System.err.println("pushRecords: Type '" + typeEntry.getKey() + "' not found. Skipping...");
							continue;
						}
						typeFound = true;
						List<? extends RecordDtoImpl> dataRecords = typeEntry.getValue().getData();
						if (dataRecords.isEmpty()) {
							System.err.println("pushRecords: Empty data set. Skipping...");
							continue;
						}
						em.getTransaction().begin();

						//TODO: remove period check once it gets removed from database
						Integer period = ((SimpleRecordDto) dataRecords.get(0)).getPeriod();
						if (period == null){
							System.err.println("pushRecords: No period specified. Skipping...");
							continue;
						}
						MeasurementAbstract latestNumberMeasurement = MeasurementAbstract.findLatestEntry(em, station, type, period, Measurement.class);
						long latestNumberMeasurementTime = (latestNumberMeasurement != null) ? latestNumberMeasurement.getTimestamp().getTime() : 0;
						MeasurementAbstract latestStringMeasurement = MeasurementAbstract.findLatestEntry(em, station, type, period, MeasurementString.class);
						long latestStringMeasurementTime = (latestStringMeasurement != null) ? latestStringMeasurement.getTimestamp().getTime() : 0;

						SimpleRecordDto newestStringDto = null;
						SimpleRecordDto newestNumberDto = null;
						for (RecordDto recordDto : dataRecords) {

							/*
							 * XXX We support only SimpleRecordDtos at the moment. This should be removed,
							 * when we see that we do not need anything else then SimpleRecords
							 */
							if (! (recordDto instanceof SimpleRecordDto))
								continue;

							SimpleRecordDto simpleRecordDto = (SimpleRecordDto)recordDto;
							Long dateOfMeasurement = simpleRecordDto.getTimestamp();
							Object valueObj = simpleRecordDto.getValue();
							if (valueObj instanceof Number) {
								if (latestNumberMeasurementTime < dateOfMeasurement) {
									Double value = ((Number)valueObj).doubleValue();
									MeasurementHistory record = new MeasurementHistory(station, type, value, new Date(dateOfMeasurement), simpleRecordDto.getPeriod());
									em.persist(record);
								}
								if (newestNumberDto == null || newestNumberDto.getTimestamp() < simpleRecordDto.getTimestamp()) {
									newestNumberDto = simpleRecordDto;
								}
								givenDataOK = true;
							} else if (valueObj instanceof String) {
								if (latestStringMeasurementTime < dateOfMeasurement) {
									String value = (String) valueObj;
									MeasurementStringHistory record = new MeasurementStringHistory(station, type, value, new Date(dateOfMeasurement), simpleRecordDto.getPeriod());
									em.persist(record);
								}
								if (newestStringDto == null || newestStringDto.getTimestamp() < simpleRecordDto.getTimestamp()) {
									newestStringDto = simpleRecordDto;
								}
								givenDataOK = true;
							} else {
								System.err.println("pushRecords: Unsupported data format for "
												   + stationType + "/" + stationEntry.getKey() + "/" + typeEntry.getKey()
												   + ": " + (valueObj == null ? "(null)" : valueObj.getClass().getSimpleName()
												   + ". Skipping..."));
							}
							jsonOK = true;
						}

						if (newestNumberDto != null) {
							Double valueNumber = ((Number)newestNumberDto.getValue()).doubleValue();
							if (latestNumberMeasurement == null) {
								latestNumberMeasurement = new Measurement(station, type, valueNumber, new Date(newestNumberDto.getTimestamp()), newestNumberDto.getPeriod());
								em.persist(latestNumberMeasurement);
							} else if (newestNumberDto.getTimestamp() > latestNumberMeasurementTime) {
								latestNumberMeasurement.setTimestamp(new Date(newestNumberDto.getTimestamp()));
								latestNumberMeasurement.setValue(valueNumber);
								em.merge(latestNumberMeasurement);
							}
						}

						if (newestStringDto != null) {
							String valueString = (String) newestStringDto.getValue();
							if (latestStringMeasurement == null) {
								latestStringMeasurement = new MeasurementString(station, type, valueString, new Date(newestStringDto.getTimestamp()), newestStringDto.getPeriod());
								em.persist(latestStringMeasurement);
							} else if (newestStringDto.getTimestamp() > latestStringMeasurementTime) {
								latestStringMeasurement.setTimestamp(new Date(newestStringDto.getTimestamp()));
								latestStringMeasurement.setValue(valueString);
								em.merge(latestStringMeasurement);
							}
						}

						em.getTransaction().commit();
					} catch(Exception ex) {
						ex.printStackTrace();
						if (em.getTransaction().isActive())
							em.getTransaction().rollback();
						continue;
					}
				}
			}

			if (stationFound == false) {
				throw new JPAException("No station found inside your DB corresponding to " + DataMapDto.class.getSimpleName(), DataMapDto.class);
			}
			if (typeFound == false) {
				throw new JPAException("No station/type found inside your DB corresponding to " + DataMapDto.class.getSimpleName(), DataMapDto.class);
			}
			if (givenDataOK == false) {
				throw new JPAException("No valid data format for station/type found inside " + SimpleRecordDto.class.getSimpleName(), SimpleRecordDto.class);
			}
			/* FALSE, if no valid data can be found, either because of missing station/type/data combinations, hence invalid JSON */
			if (jsonOK == false) {
				throw new JPAException("Invalid JSON for " + DataMapDto.class.getSimpleName(), DataMapDto.class);
			}

		} catch(Exception e) {
			if (em.getTransaction().isActive())
				em.getTransaction().rollback();
			throw JPAException.unnest(e);
		} finally {
			em.clear();
			if (em.isOpen())
				em.close();
		}
	}

	private static List<RecordDto> castToDtos(List<MeasurementAbstractHistory> result, boolean setPeriod) {
		List<RecordDto> dtos = new ArrayList<>();
		for (MeasurementAbstractHistory m : result) {
			SimpleRecordDto dto = new SimpleRecordDto(m.getTimestamp().getTime(), m.getValue(), setPeriod ? m.getPeriod() : null);
			dto.setCreated_on(m.getCreated_on().getTime());
			dtos.add(dto);
		}
		return dtos;
	}

	/**
	 * <p>
	 * the only method which requests history data from the biggest existing tables in the underlying DB,<br/>
	 * it's very important that indexes are set correctly to avoid bad performance
	 * </p>
	 * @param em entity manager
	 * @param typology of the specific station, e.g., MeteoStation, EnvironmentStation
	 * @param identifier unique station identifier, required
	 * @param cname unique type identifier, required
	 * @param start time filter start in milliseconds UTC for query, required
	 * @param end time filter start in milliseconds UTC for query, required
	 * @param period interval between measurements
	 * @param role authorization level of the current session
	 * @param tableObject implementation which calls this method to decide which table to query, required
	 * @return a list of measurements from history tables
	 */
	protected static <T> List<RecordDto> findRecordsImpl(EntityManager em, String stationtype, String identifier, String cname, Date start, Date end, Integer period, BDPRole role, T tableObject) {
		List<MeasurementAbstractHistory> result = QueryBuilder
				.init(em)
				.addSql("SELECT record")
				.addSql("FROM  " + tableObject.getClass().getSimpleName() + " record, BDPPermissions p",
						"WHERE (record.station = p.station OR p.station = null)",
						"AND (record.type = p.type OR p.type = null)",
						"AND (record.period = p.period OR p.period = null)",
						"AND p.role = :role",
						"AND record.station = (",
							"SELECT s FROM Station s WHERE s.stationtype = :stationtype AND s.stationcode = :stationcode",
						")",
						"AND record.type = (SELECT t FROM DataType t WHERE t.cname = :cname)",
						"AND record.timestamp between :start AND :end")
				.setParameterIfNotNull("period", period, "AND record.period = :period")
				.setParameter("stationtype", stationtype)
				.setParameter("stationcode", identifier)
				.setParameter("cname", cname)
				.setParameter("start", start)
				.setParameter("end", end)
				.setParameter("role", role == null ? BDPRole.fetchGuestRole(em) : role)
				.addSql("ORDER BY record.timestamp")
				.buildResultList(MeasurementAbstractHistory.class);
		return MeasurementAbstractHistory.castToDtos(result, period == null);
	}
}
