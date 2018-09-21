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
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import it.bz.idm.bdp.dal.bluetooth.StreetBasicData;
import it.bz.idm.bdp.dal.meteo.Meteostation;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.dto.bluetooth.StreetStationDto;

@Entity
public class Streetstation extends ElaborationStation{

	@Override
	public List<StationDto> convertToDtos(EntityManager em, List<Station> resultList) {
		List<StationDto> stationList = new ArrayList<StationDto>();
		for (Station station: resultList){
			Double x = null,y = null;
			if (station.getPointprojection() != null){
				y = station.getPointprojection().getY();
				x = station.getPointprojection().getX();
			}
			StreetBasicData basicData = (StreetBasicData) new StreetBasicData().findByStation(em, station);
			if (basicData == null)
				continue;
			StreetStationDto dto = new StreetStationDto(station.getStationcode(),station.getName(),y,x);
			dto.setDescription(basicData.getDescription());
			dto.setOld_idstr(basicData.getOld_idstr());
			dto.setSpeed_default(basicData.getSpeed_default());
			dto.setLength(basicData.getLength());
			stationList.add(dto);
		}
		return stationList;
	}

	@Override
	public List<String[]> findDataTypes(EntityManager em,String stationId) {
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
	public Object pushRecords(EntityManager em, Object... object) {
		// TODO Auto-generated method stub
		return null;
	}

}
