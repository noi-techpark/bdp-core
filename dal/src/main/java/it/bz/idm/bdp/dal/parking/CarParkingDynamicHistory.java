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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.TypedQuery;

import org.hibernate.annotations.ColumnDefault;

import it.bz.idm.bdp.dal.Station;
import it.bz.idm.bdp.dal.util.JPAUtil;
import it.bz.idm.bdp.dto.RecordDto;
import it.bz.idm.bdp.dto.parking.ParkingRecordExtendedDto;


@Table(name="carparkingdynamichistory",schema="intime")
@Entity
public class CarParkingDynamicHistory {

	@Id
	@GeneratedValue(generator = "carparkingdynamichistory_gen", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "carparkingdynamichistory_gen", sequenceName = "carparkingdynamichistory_seq", schema = "intime", allocationSize = 1)
	@ColumnDefault(value = "nextval('intime.carparkingdynamichistory_seq')")
	private Integer id;

	@ManyToOne
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

	public CarParkingDynamicHistory() {
	}
	public CarParkingDynamicHistory(Station station, int occupacy,
			Date slotsTS, int occupacypercentage) {
		this.station = station;
		this.occupacy = occupacy;
		this.lastupdate = slotsTS;
		this.occupacypercentage = occupacypercentage;
		this.createdate = new Date();
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

	public static List<RecordDto> findRecords(EntityManager em,String stationtype,
			String identifier, String type, Long seconds) {
		if (!"free".equals(type)&&!"occupied".equals(type))
			return new ArrayList<RecordDto>();
		Date past = new Date(Calendar.getInstance().getTimeInMillis()-(1000*seconds));
		TypedQuery<CarParkingDynamicHistory> query = em.createQuery("SELECT record FROM CarParkingDynamicHistory record WHERE record.station.stationtype = :stationtype AND record.station.stationcode=:station_id AND record.station.active=:isactive AND record.lastupdate > :date order by record.lastupdate asc" ,CarParkingDynamicHistory.class);
		query.setParameter("stationtype", stationtype);
		query.setParameter("station_id", identifier);
		query.setParameter("isactive", true);
		query.setParameter("date", past);
		List<CarParkingDynamicHistory> resultList = query.getResultList();
		List<RecordDto> dtos = castToDto(identifier, type, resultList);
		return dtos;
	}


	public static List<RecordDto> findRecords(EntityManager em, String identifier, String type, Date start, Date end) {
		if (!"free".equals(type)&&!"occupied".equals(type))
			return new ArrayList<RecordDto>();
		TypedQuery<CarParkingDynamicHistory> query = em.createQuery("SELECT record FROM CarParkingDynamicHistory record WHERE record.station.stationcode=:station_id AND record.station.active=:isactive AND record.lastupdate between :from AND :to order by record.lastupdate asc" ,CarParkingDynamicHistory.class);
		query.setParameter("station_id", identifier);
		query.setParameter("isactive", true);
		query.setParameter("from", start);
		query.setParameter("to", end);
		List<CarParkingDynamicHistory> resultList = query.getResultList();
		List<RecordDto> dtos = castToDto(identifier, type, resultList);
		return dtos;
	}

	private static List<RecordDto> castToDto(String identifier, String type,
			List<CarParkingDynamicHistory> resultList) {
		List<RecordDto> dtos = new ArrayList<RecordDto>();
		if (!resultList.isEmpty()){
			Integer capacity = null;
			if("free".equals(type))
				//TODO fix this so it returns capacity
				capacity = 0;
			for (CarParkingDynamicHistory record: resultList){
				ParkingRecordExtendedDto dto = new ParkingRecordExtendedDto();
				dto.setTimestamp(record.getLastupdate().getTime());
				if ("free".equals(type) && capacity != null)
					dto.setValue(capacity-record.getOccupacy());
				else if("occupied".equals(type))
					dto.setValue(record.getOccupacy());
				dto.setCreated_on(record.getCreatedate().getTime());
				dtos.add(dto);
			}
		}
		return dtos;
	}
	public static CarParkingDynamicHistory findRecord(EntityManager em, Station station, Long timestamp) {
		TypedQuery<CarParkingDynamicHistory> query = em.createQuery("SELECT record FROM CarParkingDynamicHistory record WHERE record.station=:station AND record.lastupdate= :lastupdate ",CarParkingDynamicHistory.class);
		query.setParameter("station", station);
		query.setParameter("lastupdate", new Date(timestamp));
		return JPAUtil.getSingleResultOrNull(query);
	}
}
