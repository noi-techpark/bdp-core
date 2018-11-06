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
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import it.bz.idm.bdp.dal.authentication.BDPRole;
import it.bz.idm.bdp.dto.RecordDto;
import it.bz.idm.bdp.dto.SimpleRecordDto;
import it.bz.idm.bdp.dto.TypeDto;


public class MeasurementStation extends Station {

	@Override
	public List<String[]> findDataTypes(EntityManager em, String stationId) {
		String queryBase = "SELECT m.type.cname, m.type.cunit, m.type.description, m.period FROM Measurement m INNER JOIN m.type WHERE m.station.stationtype = :stationtype";
		String queryGrouping = " GROUP BY m.type.cname, m.type.cunit, m.type.description, m.period";
		TypedQuery<Object[]> query;
		if (stationId != null && !stationId.isEmpty()){
			query = em.createQuery(queryBase + " AND m.station.stationcode = :station" + queryGrouping, Object[].class);
			query.setParameter("station", stationId);
		} else {
			query = em.createQuery(queryBase + queryGrouping, Object[].class);
		}
		query.setParameter("stationtype", this.stationtype);
		return getDataTypesFromQuery(query.getResultList());
	}

	@Override
	public List<TypeDto> findTypes(EntityManager em, String stationId) {
		String queryBase = "SELECT type, m.period FROM Measurement m INNER JOIN m.type type WHERE m.station.stationtype = :stationtype";
		String queryGrouping = " GROUP BY type, m.period";
		TypedQuery<Object[]> query;
		if (stationId == null || stationId.isEmpty()) {
			query = em.createQuery(queryBase + queryGrouping, Object[].class);
		} else {
			query = em.createQuery(queryBase + " AND m.station.stationcode = :station" + queryGrouping, Object[].class);
			query.setParameter("station",stationId);
		}
		query.setParameter("stationType", this.stationtype);

		List<Object[]> resultList = query.getResultList();
		List<TypeDto> types = new ArrayList<TypeDto>();
		Map<String,TypeDto> dtos = new HashMap<String, TypeDto>();

		for (Object obj:resultList){
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

	@Override
	public RecordDto findLastRecord(EntityManager em, String stringType, Integer period, BDPRole role) {
		SimpleRecordDto dto = null;
		DataType type = DataType.findByCname(em, stringType);
		Measurement latestEntry = Measurement.findLatestEntry(em, this, type, period, role);
		if (latestEntry != null){
			dto = new SimpleRecordDto(latestEntry.getTimestamp().getTime(),latestEntry.getValue());
			dto.setPeriod(latestEntry.getPeriod());
		}
		return dto;
	}

	@Override
	public List<RecordDto> getRecords(EntityManager em, String type, Date start, Date end, Integer period,
			BDPRole role) {
		List<RecordDto> records = MeasurementHistory.findRecords(em, this.getClass().getSimpleName(), this.stationcode,
				type, start, end, period, role);
		return records;
	}

	@Override
	public Date getDateOfLastRecord(EntityManager em, Station station, DataType type, Integer period, BDPRole role) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object pushRecords(EntityManager em, Object... object) {
		// TODO Auto-generated method stub
		return null;
	}
}
