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

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import it.bz.idm.bdp.dal.authentication.BDPRole;
import it.bz.idm.bdp.dal.meteo.Meteostation;
import it.bz.idm.bdp.dto.RecordDto;
import it.bz.idm.bdp.dto.SimpleRecordDto;
import it.bz.idm.bdp.dto.StationDto;


@Entity
public class Trafficstation extends ElaborationStation{

	@Override
	public List<String[]> findDataTypes(EntityManager em, String stationId) {
		TypedQuery<Object[]> query;
		if (stationId == null) {
			query = em.createQuery("SELECT elab.type.cname,elab.type.cunit,elab.type.description,elab.period FROM Elaboration elab INNER JOIN elab.type where elab.station.class=:stationType GROUP BY elab.type.cname,elab.type.cunit,elab.type.description,elab.period",Object[].class);
			query.setParameter("stationType", this.getClass().getSimpleName());
		} else {
			query = em.createQuery("SELECT elab.type.cname,elab.type.cunit,elab.type.description,elab.period FROM Elaboration elab INNER JOIN elab.type where elab.station.class=:stationType AND elab.station.stationcode=:station GROUP BY elab.type.cname,elab.type.cunit,elab.type.description,elab.period",Object[].class);
			query.setParameter("stationType", this.getClass().getSimpleName());
			query.setParameter("station",stationId);
		}

		List<Object[]> resultList = query.getResultList();
		if (resultList.isEmpty())
			return new Meteostation().findDataTypes(em,stationId);
		return getDataTypesFromQuery(resultList);
	}

	@Override
	public RecordDto findLastRecord(EntityManager em, String cname, Integer period, BDPRole role) {
		RecordDto record = super.findLastRecord(em, cname, period, role);
		if (record == null){
			DataType type = DataType.findByCname(em, cname);
			Measurement measurement = Measurement.findLatestEntry(em, this, type, period, role);
			record = new SimpleRecordDto(measurement.getTimestamp().getTime(),measurement.getValue(), period);
		}
		return record;
	}

	@Override
	public List<RecordDto> getRecords(EntityManager em, String type, Date start, Date end,
			Integer period, BDPRole role) {
		List<RecordDto> records = null;
		records = ElaborationHistory.findRecords(em, this.getClass().getSimpleName(), this.stationcode, type, start,
				end, period, role);
		if (records.isEmpty())
			records = MeasurementHistory.findRecords(em, this.getClass().getSimpleName(), this.stationcode, type, start,
					end, period, role);
		return records;
	}

	@Override
	public Object pushRecords(EntityManager em, Object... object) {
		return null;
	}

	@Override
	public void sync(EntityManager em, Station station, StationDto dto) {
	}

}
