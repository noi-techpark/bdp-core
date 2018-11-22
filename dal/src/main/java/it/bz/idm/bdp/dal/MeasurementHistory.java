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
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.TypedQuery;

import org.hibernate.annotations.ColumnDefault;

import it.bz.idm.bdp.dal.authentication.BDPRole;
import it.bz.idm.bdp.dal.util.JPAUtil;
import it.bz.idm.bdp.dto.RecordDto;
import it.bz.idm.bdp.dto.SimpleRecordDto;

@Table(name = "measurementhistory")
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
	public MeasurementHistory(Station station, DataType type, Double value, Date timestamp, Integer period, Date created_on) {
		super(station,type,timestamp,period);
		setValue(value);
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

}
