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
package it.bz.idm.bdp.dal.carsharing;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.ManyToOne;
import javax.persistence.TypedQuery;

import it.bz.idm.bdp.dal.BasicData;
import it.bz.idm.bdp.dal.Station;
import it.bz.idm.bdp.dal.util.JPAUtil;

@Entity
public class CarsharingCarStationBasicData extends BasicData{

	@ManyToOne(cascade = CascadeType.MERGE)
	private Carsharingstation carsharingStation;

	private String brand;
	private String licensePlate;

	public Carsharingstation getCarsharingStation() {
		return carsharingStation;
	}
	public void setCarsharingStation(Carsharingstation carsharingStation) {
		this.carsharingStation = carsharingStation;
	}
	public String getBrand() {
		return brand;
	}
	public void setBrand(String brand) {
		this.brand = brand;
	}
	public String getLicensePlate() {
		return licensePlate;
	}
	public void setLicensePlate(String licensePlate) {
		this.licensePlate = licensePlate;
	}

	public static CarsharingCarStationBasicData findBasicByStation(EntityManager em, Station station) {
		TypedQuery<CarsharingCarStationBasicData> query = em.createQuery("Select basicData from CarsharingCarStationBasicData basicData where basicData.station=:station", CarsharingCarStationBasicData.class);
		query.setParameter("station", station);
		return JPAUtil.getSingleResultOrNull(query);
	}
	public static List<CarsharingCarStationBasicData> findAllCars(
			EntityManager em) {
		TypedQuery<CarsharingCarStationBasicData> query = em.createQuery("Select basic from CarsharingCarStationBasicData basic where basic.station.active=true", CarsharingCarStationBasicData.class);
		return query.getResultList();
	}
	@Override
	public BasicData findByStation(EntityManager em, Station station) {
		TypedQuery<CarsharingCarStationBasicData> query = em.createQuery("Select basicData from CarsharingCarStationBasicData basicData where basicData.station=:station AND basicData.station.active = true", CarsharingCarStationBasicData.class);
		query.setParameter("station", station);
		return JPAUtil.getSingleResultOrNull(query);
	}
}
