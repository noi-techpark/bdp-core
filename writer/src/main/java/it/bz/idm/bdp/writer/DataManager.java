/**
 * writer - Data Writer for the Big Data Platform
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
package it.bz.idm.bdp.writer;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import it.bz.idm.bdp.dal.DataType;
import it.bz.idm.bdp.dal.MeasurementStringHistory;
import it.bz.idm.bdp.dal.Station;
import it.bz.idm.bdp.dal.authentication.BDPRole;
import it.bz.idm.bdp.dal.util.JPAException;
import it.bz.idm.bdp.dal.util.JPAUtil;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.StationDto;

@Component
public class DataManager {

	public Object pushRecords(String stationType, Object... data){
		EntityManager em = JPAUtil.createEntityManager();
		Station station;
		try {
			station = (Station) JPAUtil.getInstanceByType(em,stationType);
			return station.pushRecords(em, data);
		} finally {
			if (em.isOpen())
				em.close();
		}
	}

	public void syncStations(String stationType, List<StationDto> dtos) {
		EntityManager em = JPAUtil.createEntityManager();
		try {
			Station station = (Station) JPAUtil.getInstanceByType(em,stationType);
			station.syncStations(em, dtos);
		} finally {
			if (em.isOpen())
				em.close();
		}
	}

	public Object syncDataTypes(List<DataTypeDto> dtos) {
		EntityManager em = JPAUtil.createEntityManager();
		try {
			return DataType.sync(em,dtos);
		} finally {
			if (em.isOpen())
				em.close();
		}
	}

	public Object getDateOfLastRecord(String stationtype,String stationcode,String type,Integer period){
		EntityManager em = JPAUtil.createEntityManager();
		Date date = new Date(-1);
		try {
			Station s = (Station) JPAUtil.getInstanceByType(em, stationtype);
			Station station = s.findStation(em,stationcode);
			if (station != null) {
				DataType dataType = DataType.findByCname(em,type);
				BDPRole role = BDPRole.fetchAdminRole(em);
				date = station.getDateOfLastRecord(em, station, dataType, period, role);
			}
		} finally {
			if (em.isOpen())
				em.close();
		}
		return date;
	}

	public Object getLatestMeasurementStringRecord(String stationtype, String id, BDPRole role) {
		EntityManager em = JPAUtil.createEntityManager();
		try {
			return MeasurementStringHistory.findTimestampOfNewestRecordByStationId(em, stationtype, id, role);
		} finally {
			if (em.isOpen())
				em.close();
		}
	}

	public List<StationDto> getStations(String stationType, String origin) throws JPAException {
		EntityManager em = JPAUtil.createEntityManager();
		List<StationDto> stationsDtos = new ArrayList<StationDto>();
		try {
			List<Station> stations = Station.findStations(em,stationType,origin);
			if (!stations.isEmpty())
				stationsDtos = stations.get(0).convertToDtos(em, stations);
			return stationsDtos;
		} finally {
			if (em.isOpen())
				em.close();
		}
	}

	public List<String> getStationTypes() {
		EntityManager em = JPAUtil.createEntityManager();
		try {
			return Station.findStationTypes(em);
		} finally {
			if (em.isOpen())
				em.close();
		}
	}

	public void patchStations(List<StationDto> stations) {
		EntityManager em = JPAUtil.createEntityManager();
		try {
			em.getTransaction().begin();
			for (StationDto dto:stations) {
				Station.patch(em, dto);
			}
			em.getTransaction().commit();
		} finally {
			if (em.isOpen())
				em.close();
		}
	}

	protected URI getURIMapping(String mapping, Object... uriVariableValues) {
		if (mapping == null)
			mapping = "";
		else if (mapping.length() > 0)
			mapping = "/" + mapping;
		String mappingController = this.getClass().getAnnotation(RequestMapping.class).value()[0];
		return ServletUriComponentsBuilder.fromCurrentContextPath()
										  .path(mappingController + mapping)
										  .buildAndExpand(uriVariableValues)
										  .toUri();
	}
}
