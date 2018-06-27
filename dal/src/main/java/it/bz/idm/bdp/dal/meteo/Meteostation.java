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
package it.bz.idm.bdp.dal.meteo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EntityManager;

import it.bz.idm.bdp.dal.MeasurementStation;
import it.bz.idm.bdp.dal.Station;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.dto.meteo.MeteoStationDto;

@Entity
public class Meteostation extends MeasurementStation {

	@Override
	public List<StationDto> convertToDtos(EntityManager em, List<Station> resultList) {
		List<StationDto> stationList = new ArrayList<StationDto>();
		for (Station station : resultList){
			Double x = null,y = null;
			if (station.getPointprojection() != null){
				y = station.getPointprojection().getY();
				x = station.getPointprojection().getX();
			}
			MeteoStationDto dto = new MeteoStationDto(station.getStationcode(),station.getName(),y,x);
			dto.setMunicipality(station.getMunicipality());
			MeteoBasicData basicData = (MeteoBasicData) new MeteoBasicData().findByStation(em, station);
			if (basicData != null)
				dto.setArea(basicData.getArea());
			stationList.add(dto);
		}
		return stationList;
	}
	@Override
	public void sync(EntityManager em, Station station,StationDto stationDto) {
		if (stationDto instanceof MeteoStationDto){
			MeteoStationDto dto = (MeteoStationDto) stationDto;
			MeteoBasicData basic = (MeteoBasicData) new MeteoBasicData().findByStation(em, station);
			if (basic == null){
				basic = new MeteoBasicData();
				basic.setStation(station);
				em.persist(basic);
			}
			basic.setArea(dto.getArea());
			basic.setZeus(dto.getId());
			em.merge(basic);
		}
	}
}
