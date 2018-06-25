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
 * along with this program (see LICENSE/GPLv3). If not, see
 * <http://www.gnu.org/licenses/>.
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

public abstract class ElaborationStation extends Station {

	@Override
	public List<RecordDto> getRecords(EntityManager em, String type,
			Date start, Date end, Integer period, BDPRole role) {
		List<RecordDto> records = ElaborationHistory.findRecords(em, this.getClass().getSimpleName(), this.stationcode,
				type, start, end, period, role);
		return records;
	}

	@Override
	public List<String[]> findDataTypes(EntityManager em, String stationId) {
		TypedQuery<Object[]> query;
		if (stationId == null) {
			query = em
					.createQuery(
							"SELECT elab.type.cname,elab.type.cunit,elab.type.description,elab.period FROM Elaboration elab INNER JOIN elab.type where elab.station.class=:stationType "
							+ "GROUP BY elab.type.cname,elab.type.cunit,elab.type.description,elab.period",
							Object[].class);
			query.setParameter("stationType", this.getClass().getSimpleName());
		} else {
			query = em
					.createQuery(
							"SELECT elab.type.cname,elab.type.cunit,elab.type.description,elab.period FROM Elaboration elab INNER JOIN elab.type "
							+ "WHERE elab.station.class=:stationType AND elab.station.stationcode=:station GROUP BY elab.type.cname,elab.type.cunit,elab.type.description,elab.period",
							Object[].class);
			query.setParameter("stationType", this.getClass().getSimpleName());
			query.setParameter("station", stationId);
		}
		List<Object[]> resultList = query.getResultList();
		return getDataTypesFromQuery(resultList);
	}

	@Override
	public RecordDto findLastRecord(EntityManager em, String cname, Integer period, BDPRole role) {
		SimpleRecordDto dto = null;
		DataType type = DataType.findByCname(em, cname);
		Elaboration latestEntry = new Elaboration().findLastRecord(em, this, type, period, role);
		if (latestEntry != null) {
			dto = new SimpleRecordDto(latestEntry.getTimestamp().getTime(),
					latestEntry.getValue());
			dto.setPeriod(latestEntry.getPeriod());
		}
		return dto;
	}

	@Override
	public List<TypeDto> findTypes(EntityManager em, String stationId) {
		TypedQuery<Object[]> query;
		if (stationId == null) {
			query = em
					.createQuery(
							"SELECT type,record.period FROM Elaboration record INNER JOIN record.type type  where record.station.class=:stationType GROUP BY type,record.period",
							Object[].class);
			query.setParameter("stationType", this.getClass().getSimpleName());
		} else {
			query = em
					.createQuery(
							"SELECT type,record.period FROM Elaboration record INNER JOIN record.type type "
							+ "where record.station.class=:stationType AND record.station.stationcode=:station GROUP BY type,record.period",
							Object[].class);
			query.setParameter("stationType", this.getClass().getSimpleName());
			query.setParameter("station", stationId);
		}
		List<Object[]> resultList = query.getResultList();
		List<TypeDto> types = new ArrayList<TypeDto>();
		Map<String, TypeDto> dtos = new HashMap<String, TypeDto>();
		for (Object obj : resultList) {
			Object[] results = (Object[]) obj;
			DataType type = (DataType) results[0];
			Integer acqIntervall = (Integer) results[1];
			String id = type.getCname();
			TypeDto dto = dtos.get(id);
			if (dto == null) {
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
	public Date getDateOfLastRecord(EntityManager em, Station station, DataType type, Integer period, BDPRole role) {
		return getDateOfLastRecordImpl(em, station, type, period, role, "Elaboration");
	}
}
