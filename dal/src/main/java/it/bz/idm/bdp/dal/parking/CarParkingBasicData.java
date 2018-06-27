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
package it.bz.idm.bdp.dal.parking;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Table;
import javax.persistence.TypedQuery;

import com.vividsolutions.jts.geom.MultiPolygon;

import it.bz.idm.bdp.dal.BasicData;
import it.bz.idm.bdp.dal.Station;
import it.bz.idm.bdp.dal.util.JPAUtil;

@Table(name="carparkingbasicdata",schema="intime")
@Entity
public class CarParkingBasicData extends BasicData{

	private Integer disabledcapacity;

	private Integer womencapacity;

	private Boolean disabledtoiletavailable;

	private String owneroperator;

	private String parkingtype;

	private String permittedvehicletypes;

	private Boolean toiletsavailable;

	private Integer capacity;

	//@Type(type="org.hibernate.spatial.JTSGeometryType")
	private MultiPolygon area;

	private String phonenumber;

	private String email;

	private String url;

	private String mainaddress;

	private Integer state;

	public Integer getDisabledcapacity() {
		return disabledcapacity;
	}

	public void setDisabledcapacity(Integer disabledcapacity) {
		this.disabledcapacity = disabledcapacity;
	}

	public Boolean getDisabledtoiletavailable() {
		return disabledtoiletavailable;
	}

	public void setDisabledtoiletavailable(Boolean disabledtoiletavailable) {
		this.disabledtoiletavailable = disabledtoiletavailable;
	}

	public String getOwneroperator() {
		return owneroperator;
	}

	public void setOwneroperator(String owneroperator) {
		this.owneroperator = owneroperator;
	}

	public String getParkingtype() {
		return parkingtype;
	}

	public void setParkingtype(String parkingtype) {
		this.parkingtype = parkingtype;
	}

	public String getPermittedvehicletypes() {
		return permittedvehicletypes;
	}

	public void setPermittedvehicletypes(String permittedvehicletypes) {
		this.permittedvehicletypes = permittedvehicletypes;
	}

	public Boolean getToiletsavailable() {
		return toiletsavailable;
	}

	public void setToiletsavailable(Boolean toiletsavailable) {
		this.toiletsavailable = toiletsavailable;
	}

	public Integer getCapacity() {
		return capacity;
	}

	public void setCapacity(Integer capacity) {
		this.capacity = capacity;
	}

	public MultiPolygon getArea() {
		return area;
	}

	public void setArea(MultiPolygon area) {
		this.area = area;
	}

	public String getPhonenumber() {
		return phonenumber;
	}

	public void setPhonenumber(String phonenumber) {
		this.phonenumber = phonenumber;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getMainaddress() {
		return mainaddress;
	}

	public void setMainaddress(String mainaddress) {
		this.mainaddress = mainaddress;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public Integer getWomencapacity() {
		return womencapacity;
	}

	public void setWomencapacity(Integer womencapacity) {
		this.womencapacity = womencapacity;
	}

	@Override
	public CarParkingBasicData findByStation(EntityManager em, Station station) {
		return findBasicByStation(em, station);
	}

	public static CarParkingBasicData findBasicByStation(EntityManager em, Station station) {
		TypedQuery<CarParkingBasicData> typedQuery = em.createQuery("select basic from CarParkingBasicData basic where basic.station = :station and basic.station.active=:active",CarParkingBasicData.class);
		typedQuery.setParameter("station", station);
		typedQuery.setParameter("active",true);
		return JPAUtil.getSingleResultOrNull(typedQuery);
	}
}
