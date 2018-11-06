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
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.TypedQuery;

import org.hibernate.annotations.ColumnDefault;

import it.bz.idm.bdp.dal.authentication.BDPRole;
import it.bz.idm.bdp.dal.util.JPAUtil;
import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.FullRecordDto;
import it.bz.idm.bdp.dto.RecordDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.SimpleRecordDto;

@Table(name = "measurementhistory", indexes = { @Index(columnList = "station_id,type_id,timestamp,period", unique = true), @Index(columnList = "timestamp desc", name = "measurementhistory_tsdesc_idx") })
@Entity
public class MeasurementHistory extends MHistory {
	@Transient
	private static final long serialVersionUID = 2900270107783989197L;

    @Id
	@GeneratedValue(generator = "measurementhistory_gen", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "measurementhistory_gen", sequenceName = "measurementhistory_seq", schema = "intime", allocationSize = 1)
	@ColumnDefault(value = "nextval('intime.measurementhistory_seq')")
	private Long id;

	private Double value;

	public MeasurementHistory() {
	}
	public MeasurementHistory(Station station, DataType type,
			Double value, Date timestamp, Integer period,Date created_on) {
		super(station,type,timestamp,period);
		this.value = value;
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	public static List<RecordDto> findRecords(EntityManager em, String stationtype,
			String identifier, String cname, Long seconds, Integer period, BDPRole role) {

		Date past = new Date(Calendar.getInstance().getTimeInMillis() - 1000 * seconds);

		String querySQL = "SELECT record.timestamp, record.value FROM MeasurementHistory record, BDPPermissions p"
						+ " WHERE (record.station = p.station OR p.station = null)"
						+ " AND (record.type = p.type OR p.type = null)"
						+ " AND (record.period = p.period OR p.period = null)"
						+ " AND p.role = :role"
						+ " AND record.station.stationtype = :stationtype"
						+ " AND record.station.stationcode= :stationcode"
						+ " AND record.type.cname = :cname"
						+ " AND record.timestamp > :date";
		String orderSQL = " ORDER BY record.timestamp";

		TypedQuery<Object[]> query;
		if (period == null) {
			query = em.createQuery(querySQL + orderSQL, Object[].class);
		} else {
			query = em.createQuery(querySQL + " AND record.period=:period" + orderSQL, Object[].class);
			query.setParameter("period", period);
		}
		query.setParameter("stationtype", stationtype);
		query.setParameter("stationcode", identifier);
		query.setParameter("cname", cname);
		query.setParameter("date", past);
		query.setParameter("role", role == null ? BDPRole.fetchGuestRole(em) : role);
		return castToDtos(query.getResultList());
	}

	private static List<RecordDto> castToDtos(List<Object[]> resultList) {
		List<RecordDto> dtos = new ArrayList<RecordDto>();
		for (Object[] row: resultList){
			SimpleRecordDto dto = new SimpleRecordDto(((Date) row[0]).getTime(),
					Double.parseDouble(String.valueOf(row[1])));
			if (row.length>2)
				dto.setPeriod(Integer.parseInt(row[2].toString()));
			dtos.add(dto);
		}
		return dtos;
	}
	public static List<RecordDto> findRecords(EntityManager em, String stationtype,
			String identifier, String cname, Date start, Date end, Integer period, BDPRole role) {

		String querySQL1 = "SELECT record.timestamp, record.value ";
		String querySQL2 = " FROM MeasurementHistory record, BDPPermissions p"
						 + " WHERE (record.station = p.station OR p.station = null)"
						 + " AND (record.type = p.type OR p.type = null)"
						 + " AND (record.period = p.period OR p.period = null)"
						 + " AND p.role = :role"
						 + " AND record.station.stationtype = :stationtype"
						 + " AND record.station.stationcode= :stationcode"
						 + " AND record.type.cname = :cname"
						 + " AND record.timestamp between :start AND :end ";
		String orderSQL = " ORDER BY record.timestamp";

		TypedQuery<Object[]> query;
		if (period == null) {
			query = em.createQuery(querySQL1 + ", record.period" + querySQL2 + orderSQL, Object[].class);
		} else {
			query = em.createQuery(querySQL1 + querySQL2 + " AND record.period=:period" + orderSQL, Object[].class);
			query.setParameter("period", period);
		}

		query.setParameter("stationtype", stationtype);
		query.setParameter("stationcode", identifier);
		query.setParameter("cname", cname);
		query.setParameter("start", start);
		query.setParameter("end", end);
		query.setParameter("role", role == null ? BDPRole.fetchGuestRole(em) : role);
		return castToDtos(query.getResultList());
	}

	public static boolean recordExists(EntityManager em, Station station, DataType type, Date date, Integer period,
			BDPRole role) {
		String baseQuery = "SELECT record FROM MeasurementHistory record, BDPPermissions p"
						 + " WHERE (record.station = p.station OR p.station = null)"
						 + " AND (record.type = p.type OR p.type = null)"
						 + " AND (record.period = p.period OR p.period = null)"
						 + " AND p.role = :role"
						 + " AND record.station= :station"
						 + " AND record.type=:type"
						 + " AND record.timestamp =:timestamp"
						 + " AND record.period=:period";
		TypedQuery<MeasurementHistory> preparedQuery = em.createQuery(baseQuery, MeasurementHistory.class);
		preparedQuery.setParameter("station",station);
		preparedQuery.setParameter("type",type);
		preparedQuery.setParameter("timestamp",date);
		preparedQuery.setParameter("period",period);
		preparedQuery.setParameter("role", role == null ? BDPRole.fetchGuestRole(em) : role);
		return JPAUtil.getSingleResultOrNull(preparedQuery) != null;
	}

	public Object pushRecords(EntityManager em, Object... objects) {
		Object object = objects[0];
		BDPRole adminRole = BDPRole.fetchAdminRole(em);
		if (object instanceof DataMapDto) {
			@SuppressWarnings("unchecked")
			DataMapDto<RecordDtoImpl> dto = (DataMapDto<RecordDtoImpl>) object;
			try{
				for (Map.Entry<String, DataMapDto<RecordDtoImpl>> entry:dto.getBranch().entrySet()){
					Station station = findStation(em, entry.getKey());
					for(Map.Entry<String,DataMapDto<RecordDtoImpl>> typeEntry : entry.getValue().getBranch().entrySet()){
						try{
							em.getTransaction().begin();
							DataType type = DataType.findByCname(em, typeEntry.getKey());
							List<? extends RecordDtoImpl> dataRecords = typeEntry.getValue().getData();
							if (station != null && this.getClass().isInstance(station) && type != null && !dataRecords.isEmpty()){
								Measurement lastEntry = Measurement.findLatestEntry(em, station, type, null, adminRole);
								Date created_on = new Date();
								Collections.sort(dataRecords);
								long lastEntryTime = (lastEntry != null)?lastEntry.getTimestamp().getTime():0;
								for (RecordDto recordDto : dataRecords){
									if (recordDto instanceof SimpleRecordDto){
										SimpleRecordDto simpleRecordDto = (SimpleRecordDto)recordDto;
										Long dateOfMeasurement = simpleRecordDto.getTimestamp();
										Double value = (Double) simpleRecordDto.getValue();
										if(lastEntryTime < dateOfMeasurement){
											MeasurementHistory record = new MeasurementHistory(station,type,value,new Date(dateOfMeasurement),simpleRecordDto.getPeriod(),created_on);
											em.persist(record);
										}
									}
								}
								SimpleRecordDto newestDto = (SimpleRecordDto) dataRecords.get(dataRecords.size()-1);
								if (lastEntry == null){
									Double value = (Double) newestDto.getValue();
									lastEntry = new Measurement(station, type, value, new Date(newestDto.getTimestamp()), newestDto.getPeriod());
									em.persist(lastEntry);
								}
								else if (newestDto != null && newestDto.getTimestamp()>lastEntryTime){
									Double value = (Double) newestDto.getValue();
									lastEntry.setTimestamp(new Date(newestDto.getTimestamp()));
									lastEntry.setValue(value);
									em.merge(lastEntry);
								}
							}
							em.getTransaction().commit();
						}catch(Exception ex){
							ex.printStackTrace();
							if (em.getTransaction().isActive())
								em.getTransaction().rollback();
							continue;
						}
					}

				}
			}catch(Exception ex){
				ex.printStackTrace();
				if (em.getTransaction().isActive())
					em.getTransaction().rollback();
			}finally{
				em.clear();
				em.close();
			}
		}else if (object instanceof Object[]){
			List<Object> dtos = Arrays.asList((Object[]) object);
			try{
				em.getTransaction().begin();
				Station station = null;
				DataType type = null;
				String tempSS = null,tempTS = null;
				for (Object dto : dtos){
					FullRecordDto full = (FullRecordDto)dto;
					if (full.validate()){
						if (!full.getStation().equals(tempSS)){
							station = findStation(em, full.getStation());
							if (station == null)
								continue;
							tempSS = station.getStationcode();
						}
						if (!full.getType().equals(tempTS)){
							type = DataType.findByCname(em, full.getType());
							if (type == null)
								continue;
							tempTS = full.getType();
						}
						Measurement lastEntry = Measurement.findLatestEntry(em, station, type, full.getPeriod(), adminRole);
						Number value = (Number) full.getValue();
						if (lastEntry == null){
							lastEntry = new Measurement(station, type,value.doubleValue(), new Date(full.getTimestamp()), full.getPeriod());
							em.persist(lastEntry);
						}
						else if (lastEntry.getTimestamp().getTime()<full.getTimestamp()){
							lastEntry.setValue(value.doubleValue());
							lastEntry.setTimestamp(new Date(full.getTimestamp()));
							em.merge(lastEntry);
						}
						MeasurementHistory record = new MeasurementHistory(station,type,value.doubleValue(), new Date(full.getTimestamp()),full.getPeriod(),new Date());
						em.persist(record);
					}
				}
				em.getTransaction().commit();
			}catch(Exception ex){
				ex.printStackTrace();
				if (em.getTransaction().isActive())
					em.getTransaction().rollback();
			}
		}
		return null;
	}

}
