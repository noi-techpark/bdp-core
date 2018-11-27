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
package it.bz.idm.bdp.dal.parking;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.TypedQuery;

import org.hibernate.annotations.ColumnDefault;

import it.bz.idm.bdp.dal.Station;
import it.bz.idm.bdp.dal.util.QueryBuilder;

@Table(name="carparkingdynamic",schema="intime")
@Entity
public class CarParkingDynamic {

	@Id
	@GeneratedValue(generator = "carparkingdynamic_gen", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "carparkingdynamic_gen", sequenceName = "carparkingdynamic_seq", schema = "intime", allocationSize = 1)
	@ColumnDefault(value = "nextval('intime.carparkingdynamic_seq')")
	private Integer id;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "station_id")
	private Station station;

	private String carparkstate;

	private String 	carparktrend;

	private Double exitrate;

	private Double fillrate;

	private Date lastupdate;

	private Date createdate;

	private Integer occupacy;

	private Integer occupacypercentage;

	public CarParkingDynamic() {
		// TODO Auto-generated constructor stub
	}

	public CarParkingDynamic(Station area,
			Integer occupacy, Date lastupdate) {
		this.station = area;
		this.occupacy = occupacy;
		this.lastupdate = lastupdate;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Station getStation() {
		return station;
	}

	public void setStation(Station station) {
		this.station = station;
	}

	public String getCarparkstate() {
		return carparkstate;
	}

	public void setCarparkstate(String carparkstate) {
		this.carparkstate = carparkstate;
	}

	public String getCarparktrend() {
		return carparktrend;
	}

	public void setCarparktrend(String carparktrend) {
		this.carparktrend = carparktrend;
	}

	public Double getExitrate() {
		return exitrate;
	}

	public void setExitrate(Double exitrate) {
		this.exitrate = exitrate;
	}

	public Double getFillrate() {
		return fillrate;
	}

	public void setFillrate(Double fillrate) {
		this.fillrate = fillrate;
	}

	public Date getLastupdate() {
		return lastupdate;
	}

	public void setLastupdate(Date lastupdate) {
		this.lastupdate = lastupdate;
	}

	public Integer getOccupacy() {
		return occupacy;
	}

	public void setOccupacy(Integer occupacy) {
		this.occupacy = occupacy;
	}

	public Integer getOccupacypercentage() {
		return occupacypercentage;
	}

	public void setOccupacypercentage(Integer occupacypercentage) {
		this.occupacypercentage = occupacypercentage;
	}

	public Date getCreatedate() {
		return createdate;
	}

	public void setCreatedate(Date createdate) {
		this.createdate = createdate;
	}

	public static CarParkingDynamic findByParkingStation(EntityManager em,Station area) {
		TypedQuery<CarParkingDynamic> typedQuery = em.createQuery("select dynamic from CarParkingDynamic dynamic where dynamic.station.id = :area order by dynamic.lastupdate asc",CarParkingDynamic.class);
		typedQuery.setParameter("area", area.getId());
		return QueryBuilder.getSingleResultOrNull(typedQuery);
	}

	public static CarParkingDynamic findLastRecord(EntityManager em,Station station, Integer period) {
		if (period == null)
			return findLastRecord(em,station);
		TypedQuery<CarParkingDynamic> query = em.createQuery("SELECT dynamic FROM CarParkingDynamic dynamic WHERE dynamic.station.id = :station order by dynamic.lastupdate asc",CarParkingDynamic.class);
		query.setParameter("station", station.getId());
		return QueryBuilder.getSingleResultOrNull(query);
	}

	private static CarParkingDynamic findLastRecord(EntityManager em, Station station) {
		if (station == null)
			return null;
		TypedQuery<CarParkingDynamic> query = em.createQuery("SELECT dynamic FROM CarParkingDynamic dynamic WHERE dynamic.station.id = :station order by dynamic.lastupdate asc",CarParkingDynamic.class);
		query.setParameter("station", station.getId());
		return QueryBuilder.getSingleResultOrNull(query);
	}

}