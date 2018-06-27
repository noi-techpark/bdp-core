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
package it.bz.idm.bdp.dal.carsharing;

import it.bz.idm.bdp.dal.MeasurementStation;
import it.bz.idm.bdp.dal.Station;
import it.bz.idm.bdp.dto.ChildDto;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.dto.carsharing.CarsharingCarDto;
import it.bz.idm.bdp.dto.carsharing.CarsharingDetailsDto;
import it.bz.idm.bdp.dto.carsharing.CarsharingStationDto;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EntityManager;

@Entity
public class Carsharingstation extends MeasurementStation {


	@Override
	public List<StationDto> convertToDtos(EntityManager em, List<Station> resultList) {
		List<StationDto> dtos = new ArrayList<StationDto>();
		for (Station station: resultList){
			Double x = null,y = null;
			if (station.getPointprojection() != null){
				y = station.getPointprojection().getY();
				x = station.getPointprojection().getX();
			}
			CarsharingStationBasicData basic = (CarsharingStationBasicData) new CarsharingStationBasicData().findByStation(em, station);
			if (basic == null)
				continue;
			CarsharingDetailsDto dto = new CarsharingDetailsDto(station.getStationcode(),station.getName(),y,x,basic.getCompanyShortName(),basic.getParking(),basic.isCanBookAhead(),basic.isHasFixedParking(),basic.isSpontaneously());
			dto.setMunicipality(station.getMunicipality());
			dtos.add(dto);
		}
		return dtos;
	}

	@Override
	public void sync(EntityManager em, Station station, StationDto dto) {
		if (dto instanceof CarsharingStationDto && dto.checkIfValid()){
			CarsharingStationDto carsharingDto = (CarsharingStationDto) dto;
			CarsharingStationBasicData basicData = (CarsharingStationBasicData) new CarsharingStationBasicData().findByStation(em, station);
			if (basicData == null){
				basicData = new CarsharingStationBasicData();
				basicData.setStation(station);
				em.persist(basicData);
			}
			basicData.setCanBookAhead(carsharingDto.getBookMode().isCanBookAhead());
			basicData.setHasFixedParking(carsharingDto.isHasFixedParking());
			basicData.setSpontaneously(carsharingDto.getBookMode().isSpontaneously());
			basicData.setCompanyShortName(carsharingDto.getCompany().getShortName());
			em.merge(basicData);
		}
	}

	@Override
	public List<ChildDto> findChildren(EntityManager em, String parent) {
		List<ChildDto> dtos = new ArrayList<ChildDto>();
		List<Station> cars = new ArrayList<Station>();
		if (parent == null)
			cars = new Carsharingcar().findStations(em);
		else {
			Station station = this.findStation(em, parent);
			if (station!= null && station instanceof Carsharingstation)
				cars = new Carsharingcar().findByParent(em,station);
		}
		if (! cars.isEmpty())
			for (Station s : cars){
				Carsharingcar car = (Carsharingcar) s;
				CarsharingCarStationBasicData basic = (CarsharingCarStationBasicData) new CarsharingCarStationBasicData().findByStation(em, car);
				CarsharingCarDto dto = new CarsharingCarDto();
				dto.setIdentifier(car.getStationcode());
				if (basic!= null){
					if (basic.getCarsharingStation() != null)
						dto.setStation(basic.getCarsharingStation().getStationcode());
					dto.setBrand(basic.getBrand());
					dto.setLicensePlate(basic.getLicensePlate());
				}
				dtos.add(dto);
			}
		return dtos;
	}
}
