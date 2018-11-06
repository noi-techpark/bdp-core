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
import javax.persistence.Table;
import javax.persistence.TypedQuery;

import it.bz.idm.bdp.dal.authentication.BDPRole;
import it.bz.idm.bdp.dal.util.JPAUtil;
import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.SimpleRecordDto;

@Table(name="measurementstringhistory",schema="intime")
@Entity
public class MeasurementStringHistory extends MHistory{

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
		super.pushRecordsImpl(em,objects,"MeasurementString");
		BDPRole role = BDPRole.fetchAdminRole(em);
		if (objects.length>0 && objects[0] instanceof DataMapDto<?>)
		{
			@SuppressWarnings("unchecked")
			DataMapDto<RecordDtoImpl> dataMap = (DataMapDto<RecordDtoImpl>) objects[0];
			DataType type = DataType.findByCname(em, VEHICLE_DETECTION);
			if (type == null)
				type = new DataType(VEHICLE_DETECTION); //create it if it does not exists in DB

			for(Map.Entry<String,DataMapDto<RecordDtoImpl>> entry : dataMap.getBranch().entrySet()){
				em.getTransaction().begin();
				Station station = findStation(em, entry.getKey());
				if (station == null){
					station = new BluetoothStation(entry.getKey());
					em.persist(station);
				}
				List<? extends RecordDtoImpl> data = entry.getValue().getBranch().get(VEHICLE_DETECTION).getData();
				for (RecordDtoImpl record: data){
					SimpleRecordDto dto = (SimpleRecordDto) record;
					MeasurementStringHistory history = MeasurementStringHistory.findRecord(em, station, type,
							dto.getValue().toString(), new Date(dto.getTimestamp()), PERIOD, role);
					if (history == null){
						history = new MeasurementStringHistory(station, type, dto.getValue().toString(),new Date(dto.getTimestamp()),PERIOD);
						em.persist(history);
					}
					MeasurementString lastMeasurement = MeasurementString.findLastMeasurementByStationAndType(em,
							station, type, PERIOD, role);
					if (lastMeasurement != null) {
						lastMeasurement.setTimestamp(new Date(dto.getTimestamp()));
						lastMeasurement.setCreated_on(new Date());
						lastMeasurement.setValue(dto.getValue().toString());
					} else
						lastMeasurement = new MeasurementString(station, type, dto.getValue().toString(), new Date(dto.getTimestamp()),PERIOD);
					em.merge(lastMeasurement);
				}
				em.getTransaction().commit();
			}
		}
		return "";
	}


}
