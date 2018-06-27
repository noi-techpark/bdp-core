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
package it.bz.idm.bdp.dal.bikesharing;

import it.bz.idm.bdp.dal.MeasurementStation;
import it.bz.idm.bdp.dal.Station;
import it.bz.idm.bdp.dto.ChildDto;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.dto.bikesharing.BikeDto;
import it.bz.idm.bdp.dto.bikesharing.BikesharingDetailsDto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.EntityManager;

@Entity
public class BikesharingStation extends MeasurementStation{

	@Override
	public List<StationDto> convertToDtos(EntityManager em, List<Station> resultList) {
		List<StationDto> stationList = new ArrayList<StationDto>();
		for (Station station: resultList){
			
			List<BikesharingStationBasicData> basicDetails = BikesharingStationBasicData.findBasicByStation(em,station);
			Map<String,Integer> map = new HashMap<String, Integer>();
			for (BikesharingStationBasicData detail : basicDetails){
				map.put(detail.getType().getCname(), detail.getMax_available());
			}
			Double x = null,y = null;
			if (station.getPointprojection() != null){
				y = station.getPointprojection().getY();
				x = station.getPointprojection().getX();
			}
			BikesharingDetailsDto dto = new BikesharingDetailsDto(station.getStationcode(),station.getName(),y,x,map);
			dto.setCrs(GEOM_CRS);
			dto.setMunicipality(station.getMunicipality());
			stationList.add(dto);
		}
		return stationList;
	}

	@Override
	public void sync(EntityManager em, Station station, StationDto dto) {
	}
	@Override
	public List<ChildDto> findChildren(EntityManager em, String parent) {
		List<ChildDto> dtos = new ArrayList<ChildDto>();
		List<Station> bicycles = new ArrayList<Station>();
		if (parent == null)
			bicycles = new Bicycle().findStations(em);
		else {
			Station station = findStation(em, parent);
			if (station!= null && station instanceof BikesharingStation)
			bicycles = new Bicycle().findByParent(em,station);
		}
		if (! bicycles.isEmpty())
			for (Station s : bicycles){
				Bicycle bike = (Bicycle) s;
				Bicyclebasicdata basic = (Bicyclebasicdata) new Bicyclebasicdata().findByStation(em, bike);
				BikeDto dto = new BikeDto();
				dto.setIdentifier(bike.getStationcode());
				if (basic!= null){
					if (basic.getBikeSharingStation() != null)
						dto.setStation(basic.getBikeSharingStation().getStationcode());
					dto.setType(basic.getType().getCname());
				}
				dtos.add(dto);
			}
		return dtos;
	}
}
