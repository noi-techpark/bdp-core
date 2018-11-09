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

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.TypedQuery;

import org.hibernate.annotations.ColumnDefault;

import it.bz.idm.bdp.dal.authentication.BDPRole;
import it.bz.idm.bdp.dal.util.JPAUtil;
import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.SimpleRecordDto;

@Table(name="measurementstringhistory",schema="intime")
@Entity
public class MeasurementStringHistory extends MHistory{
	
    @Id
	@GeneratedValue(generator = "measurementstringhistory_gen", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "measurementstringhistory_gen", sequenceName = "measurementstringhistory_seq", schema = "intime", allocationSize = 1)
	@ColumnDefault(value = "nextval('intime.measurementstringhistory_seq')")
	private Long id;
    
	private String value;

	public MeasurementStringHistory() {
	}
	public MeasurementStringHistory(Station station, DataType type,
			String value, Date timestamp, Integer period) {
		this.setStation(station);
		this.setType(type);
		
		this.setTimestamp(timestamp);
		this.setCreated_on(new Date());
		this.setPeriod(period);
		this.value = value;
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}



	public static MeasurementStringHistory findRecord(EntityManager em, Station station, DataType type, String value,
			Date timestamp, Integer period, BDPRole role) {

		TypedQuery<MeasurementStringHistory> history = em.createQuery("SELECT record "
				+ "FROM MeasurementStringHistory record, BDPPermissions p "
				+ "WHERE (record.station = p.station OR p.station = null) "
				+ "AND (record.type = p.type OR p.type = null) "
				+ "AND (record.period = p.period OR p.period = null) "
				+ "AND p.role = :role "
				+ "AND record.station = :station "
				+ "AND record.type=:type "
				+ "AND record.timestamp=:timestamp "
				+ "AND record.value=:value "
				+ "AND record.period=:period", MeasurementStringHistory.class);

		history.setParameter("station", station);
		history.setParameter("type", type);
		history.setParameter("value", value);
		history.setParameter("timestamp", timestamp);
		history.setParameter("period", period);
		history.setParameter("role", role == null ? BDPRole.fetchGuestRole(em) : role);

		return JPAUtil.getSingleResultOrNull(history);
	}

	public static Date findTimestampOfNewestRecordByStationId(EntityManager em, String stationtype, String id,
			BDPRole role) {

		String sql1 = "SELECT record.timestamp "
				+ "from MeasurementString record, BDPPermissions p "
				+ "WHERE (record.station = p.station OR p.station = null) "
				+ "AND (record.type = p.type OR p.type = null) "
				+ "AND (record.period = p.period OR p.period = null) "
				+ "AND p.role = :role "
				+ "AND record.station.stationcode=:stationcode ";
		String sql2 = "record.station.stationtype=:stationtype";

		TypedQuery<Date> query;
		if (stationtype == null) {
			query = em.createQuery(sql1, Date.class);
		} else {
			query = em.createQuery(sql1 + sql2, Date.class);
			query.setParameter("stationtype",stationtype);
		}
		query.setParameter("stationcode", id);
		query.setParameter("role", role == null ? BDPRole.fetchGuestRole(em) : role);
		return JPAUtil.getSingleResultOrNull(query);
	}
	@Override
	public Object pushRecords(EntityManager em, Object... objects) {
		// TODO Auto-generated method stub
		return null;
	}
}
