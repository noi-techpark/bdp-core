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
package it.bz.idm.bdp.dal.carsharing;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import it.bz.idm.bdp.dal.MeasurementStation;
import it.bz.idm.bdp.dal.Station;
import it.bz.idm.bdp.dal.util.JPAUtil;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.dto.carsharing.CarsharingCarDetailsDto;
import it.bz.idm.bdp.dto.carsharing.CarsharingVehicleDto;

@Entity
public class Carsharingcar extends MeasurementStation {

	private static Long getCarCount(EntityManager em, Carsharingstation station) {
		TypedQuery<Long> query = em.createQuery("Select count(basicdata) from CarsharingCarStationBasicData basicdata where basicdata.carsharingStation = :carsharing", Long.class);
		query.setParameter("carsharing", station);
		return JPAUtil.getSingleResultOrNull(query);
	}
	@Override
	public List<StationDto> convertToDtos(EntityManager em, List<Station> resultList) {
		List<StationDto> dtos = new ArrayList<StationDto>();
		for (Station station: resultList){
			Double x = null,y = null;
			if (station.getPointprojection() != null){
				y = station.getPointprojection().getY();
				x = station.getPointprojection().getX();
			}
			CarsharingCarStationBasicData basic =  (CarsharingCarStationBasicData) new CarsharingCarStationBasicData().findByStation(em, station);
			String carsharingCode = (basic.getCarsharingStation() == null) ? null : basic.getCarsharingStation().getStationcode();
			CarsharingCarDetailsDto dto = new CarsharingCarDetailsDto(station.getStationcode(),station.getName(),y,x,basic.getBrand(),carsharingCode,basic.getLicensePlate());
			dto.setMunicipality(station.getMunicipality());
			dtos.add(dto);
		}
		return dtos;
	}
	@Override
	public void sync(EntityManager em, Station station, StationDto dto) {
		if (dto instanceof CarsharingVehicleDto){
			CarsharingVehicleDto vehicleDto = (CarsharingVehicleDto) dto;
			CarsharingCarStationBasicData basicData = (CarsharingCarStationBasicData) new CarsharingCarStationBasicData().findByStation(em, station);
			Carsharingstation carsharingStation = (Carsharingstation) new Carsharingstation().findStation(em,vehicleDto.getStationId());
			if (basicData == null){
				basicData = new CarsharingCarStationBasicData();
				basicData.setStation(station);
				em.persist(basicData);
			}
			basicData.setCarsharingStation(carsharingStation);
			Long count = Carsharingcar.getCarCount(em,carsharingStation);
			CarsharingStationBasicData basic = CarsharingStationBasicData.findBasicByStation(em, carsharingStation);
			if (count!= null && basic != null){
				basic.setParking(count.intValue());
				em.merge(basic);
			}

			basicData.setBrand(vehicleDto.getBrand());
			basicData.setLicensePlate(vehicleDto.getLicensePlate());
			em.merge(basicData);
		}
	}

	public List<Station> findByParent(EntityManager em, Station station) {
		Carsharingstation carsharingStation = (Carsharingstation) station;
		TypedQuery<Station> query = em.createQuery("Select car from CarsharingCarStationBasicData c join c.station car join c.carsharingStation s where s = :station", Station.class);
		query.setParameter("station", carsharingStation);
		return query.getResultList();
	}

}
