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
 * along with this program (see LICENSES/GPL-3.0.txt). If not, see
 * <http://www.gnu.org/licenses/>.
 *
 * SPDX-License-Identifier: GPL-3.0
 */
package it.bz.idm.bdp.dal.bluetooth;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.EntityManager;

import it.bz.idm.bdp.dal.DataType;
import it.bz.idm.bdp.dal.ElaborationStation;
import it.bz.idm.bdp.dal.MeasurementString;
import it.bz.idm.bdp.dal.MeasurementStringHistory;
import it.bz.idm.bdp.dal.Station;
import it.bz.idm.bdp.dal.authentication.BDPRole;
import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.SimpleRecordDto;
import it.bz.idm.bdp.dto.StationDto;

@Entity
public class Bluetoothstation extends ElaborationStation {

	public static final String VEHICLE_DETECTION = "vehicle detection";
	public static final String STATION_TYPE="Bluetoothstation";
	public static final Integer PERIOD = 1;

	public Bluetoothstation() {
		super();
	}

	public Bluetoothstation(String stationcode) {
		this.stationcode = stationcode;
		this.name = stationcode;
	}

	@Override
	public Date getDateOfLastRecord(EntityManager em, Station station, DataType type, Integer period, BDPRole role) {
		return getDateOfLastRecordImpl(em, station, type, period, role, "MeasurementString");
	}

	@Override
	public Object pushRecords(EntityManager em, Object... objects) {
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
					station = new Bluetoothstation(entry.getKey());
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

	@Override
	public void sync(EntityManager em, Station station, StationDto dto) {
	}

}
