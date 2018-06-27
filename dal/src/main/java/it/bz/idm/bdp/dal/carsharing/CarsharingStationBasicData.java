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

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import it.bz.idm.bdp.dal.BasicData;
import it.bz.idm.bdp.dal.Station;
import it.bz.idm.bdp.dal.util.JPAUtil;

@Entity
public class CarsharingStationBasicData extends BasicData{

	private Boolean hasFixedParking;
	private Boolean canBookAhead;
	private Boolean spontaneously;
	private Integer parking;
	private String companyShortName;

	public Boolean isHasFixedParking() {
		return hasFixedParking;
	}
	public void setHasFixedParking(Boolean hasFixedParking) {
		this.hasFixedParking = hasFixedParking;
	}
	public Boolean isCanBookAhead() {
		return canBookAhead;
	}
	public void setCanBookAhead(Boolean canBookAhead) {
		this.canBookAhead = canBookAhead;
	}
	public Boolean isSpontaneously() {
		return spontaneously;
	}
	public void setSpontaneously(Boolean spontaneously) {
		this.spontaneously = spontaneously;
	}
	public Integer getParking() {
		return parking;
	}
	public void setParking(Integer parking) {
		this.parking = parking;
	}
	public String getCompanyShortName() {
		return companyShortName;
	}
	public void setCompanyShortName(String companyShortName) {
		this.companyShortName = companyShortName;
	}
	public static CarsharingStationBasicData findBasicByStation(EntityManager em, Station station) {
		TypedQuery<CarsharingStationBasicData> query = em.createQuery("Select basic from CarsharingStationBasicData basic where basic.station=:station", CarsharingStationBasicData.class);
		query.setParameter("station", station);
		return JPAUtil.getSingleResultOrNull(query);
	}
	public static List<CarsharingStationBasicData> findAllCarsharingStations(EntityManager em) {
		TypedQuery<CarsharingStationBasicData> query = em.createQuery("Select basic from CarsharingStationBasicData basic where basic.station.active=true", CarsharingStationBasicData.class);
		return query.getResultList();

	}
	@Override
	public BasicData findByStation(EntityManager em, Station station) {
		TypedQuery<CarsharingStationBasicData> query = em.createQuery("Select basic from CarsharingStationBasicData basic where basic.station=:station", CarsharingStationBasicData.class);
		query.setParameter("station", station);
		return JPAUtil.getSingleResultOrNull(query);
	}
}
