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
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.TypedQuery;

import org.hibernate.annotations.ColumnDefault;

import it.bz.idm.bdp.dal.authentication.BDPRole;
import it.bz.idm.bdp.dal.util.JPAUtil;
import it.bz.idm.bdp.dto.RecordDto;
import it.bz.idm.bdp.dto.bluetooth.BluetoothRecordExtendedDto;
@Entity
public class ElaborationHistory {

	@Id
	@GeneratedValue(generator = "elaborationhistory_gen", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "elaborationhistory_gen", sequenceName = "elaborationhistory_seq", schema = "intime", allocationSize = 1)
	@ColumnDefault(value = "nextval('intime.elaborationhistory_seq')")
	protected Long id;
	private Date created_on;
	private Date timestamp;

	@ManyToOne
	private DataType type;
	private Double value;

	@ManyToOne
	private Station station;
	private Integer period;

	public ElaborationHistory() {
	}
	public ElaborationHistory(Station station, DataType type, Double value, Date timestamp, Integer period) {
		super();
		this.created_on = new Date();
		this.timestamp = timestamp;
		this.type = type;
		this.value = value;
		this.station = station;
		this.period = period;
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
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
	public DataType getType() {
		return type;
	}
	public void setType(DataType type) {
		this.type = type;
	}
	public Double getValue() {
		return value;
	}
	public void setValue(Double value) {
		this.value = value;
	}
	public Station getStation() {
		return station;
	}
	public void setStation(Station station) {
		this.station = station;
	}
	public Integer getPeriod() {
		return period;
	}
	public void setPeriod(Integer period) {
		this.period = period;
	}

	public static List<RecordDto> findRecords(EntityManager em, String stationtype,
			String uuid, String type, Date start, Date end, Integer period, BDPRole role) {

		String sql1 = "select record "
				+ "FROM ElaborationHistory record, BDPPermissions p "
				+ "WHERE (record.station = p.station OR p.station = null) "
				+ "AND (record.type = p.type OR p.type = null) "
				+ "AND (record.period = p.period OR p.period = null) "
				+ "AND p.role = :role "
				+ "AND record.station.class= :stationtype "
				+ "AND record.station.stationcode= :stationid "
				+ "AND record.type.cname=:type "
				+ "AND record.timestamp between :start AND :end ";
		String sql2 = "AND record.period=:period ";
		String sql3 = "ORDER BY record.timestamp asc";

		TypedQuery<ElaborationHistory> query;
		if (period == null) {
			query = em.createQuery(sql1 + sql3, ElaborationHistory.class);
		} else {
			query = em.createQuery(sql1 + sql2 + sql3, ElaborationHistory.class);
			query.setParameter("period", period);
		}

		query.setParameter("stationtype", stationtype);
		query.setParameter("stationid", uuid);
		query.setParameter("type", type);
		query.setParameter("start", start);
		query.setParameter("end", end);
		query.setParameter("role", role == null ? BDPRole.fetchGuestRole(em) : role);

		List<RecordDto> dtos = new ArrayList<RecordDto>();
		parseDtos(dtos, query);
		return dtos;
	}

	public static ElaborationHistory findRecordByProps(EntityManager em, Station station, DataType type, Date timestamp,
			Integer period, BDPRole role) {
		TypedQuery<ElaborationHistory> query = em.createQuery("select record "
				+ "FROM ElaborationHistory record, BDPPermissions p "
				+ "WHERE (record.station = p.station OR p.station = null) "
				+ "AND (record.type = p.type OR p.type = null) "
				+ "AND (record.period = p.period OR p.period = null) "
				+ "AND p.role = :role "
				+ "AND record.station= :station "
				+ "AND record.type=:type "
				+ "AND record.timestamp = :timestamp "
				+ "AND record.period=:period "
				+ "ORDER BY record.timestamp asc", ElaborationHistory.class);

		query.setParameter("station", station);
		query.setParameter("type", type);
		query.setParameter("timestamp", timestamp);
		query.setParameter("period", period);
		query.setParameter("role", role == null ? BDPRole.fetchGuestRole(em) : role);

		return JPAUtil.getSingleResultOrNull(query);
	}

	private static void parseDtos(List<RecordDto> dtos, TypedQuery<ElaborationHistory> query) {
		for (ElaborationHistory history : query.getResultList()) {
			Date date = history.getTimestamp();
			Long created_on = history.getCreated_on().getTime();
			Double value = history.getValue();
			BluetoothRecordExtendedDto dto = new BluetoothRecordExtendedDto(date.getTime(), value, created_on);
			dtos.add(dto);
		}
	}
}
