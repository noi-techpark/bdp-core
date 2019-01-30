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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import it.bz.idm.bdp.dal.DataType;
import it.bz.idm.bdp.dal.MeasurementStringHistory;
import it.bz.idm.bdp.dal.Station;
import it.bz.idm.bdp.dal.authentication.BDPRole;
import it.bz.idm.bdp.dal.util.JPAUtil;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.StationDto;

@Component
public class DataManager {

	@Value("classpath:META-INF/sql/init.sql")
	private Resource sql;

	public Object pushRecords(String stationType, Object... data){
		EntityManager em = JPAUtil.createEntityManager();
		Station station;
		try {
			station = (Station) JPAUtil.getInstanceByType(em,stationType);
			return station.pushRecords(em, data);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if (em.isOpen())
				em.close();
		}
		return null;
	}

	public Object syncStations(String stationType, List<StationDto> dtos) {
		EntityManager em = JPAUtil.createEntityManager();
		try{
			Station station = (Station) JPAUtil.getInstanceByType(em,stationType);
			return station.syncStations(em, dtos);
		} catch (Exception e) {
			// FIXME Add error handling to report back to the writer method caller
			e.printStackTrace();
		} finally {
			em.close();
		}
		return null;
	}

	public Object syncDataTypes(List<DataTypeDto> dtos) {
		Object object = null;
		EntityManager em = JPAUtil.createEntityManager();
		try{
			object = DataType.sync(em,dtos);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			em.close();
		}
		return object;
	}

	public Object getDateOfLastRecord(String stationtype,String stationcode,String type,Integer period){
		EntityManager em = JPAUtil.createEntityManager();
		BDPRole role = BDPRole.fetchAdminRole(em);
		Date date = new Date(-1);
		try{
			Station s = (Station) JPAUtil.getInstanceByType(em, stationtype);
			Station station = s.findStation(em,stationcode);
			DataType dataType = DataType.findByCname(em,type);
			if (station != null) {
				date = station.getDateOfLastRecord(em, station, dataType, period, role);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		finally{
			em.close();
		}
		return date;
	}

	public Object getLatestMeasurementStringRecord(String stationtype, String id, BDPRole role) {
		Date date = null;
		EntityManager em = JPAUtil.createEntityManager();
		date = MeasurementStringHistory.findTimestampOfNewestRecordByStationId(em, stationtype, id, role);
		em.close();
		return date;
	}
	public List<StationDto> getStationsWithoutMunicipality(){
		List<StationDto> stationsDtos = new ArrayList<StationDto>();
		EntityManager em = JPAUtil.createEntityManager();
		List<Station> stations = Station.findStationsWithoutMunicipality(em);
		for (Station station : stations) {
			try {
				StationDto dto = station.convertToDto(station);
				String name = JPAUtil.getEntityNameByObject(station);
				dto.setStationType(name);
				stationsDtos.add(dto);
			} catch (Exception e) {
				// FIXME Give the error back to be handled in writer...
				e.printStackTrace();
			}
		}
		em.close();
		return stationsDtos;
	}
	public List<StationDto> getStations(String stationType, String origin) {
		List<StationDto> stationsDtos = new ArrayList<StationDto>();
		EntityManager em = JPAUtil.createEntityManager();
		List<Station> stations = Station.findStations(em,stationType,origin);
		if (!stations.isEmpty())
			stationsDtos = stations.get(0).convertToDtos(em, stations);
		return stationsDtos;
	}
	public void patchStations(List<StationDto> stations) {
		EntityManager em = JPAUtil.createEntityManager();
		em.getTransaction().begin();
		for (StationDto dto:stations) {
			Station.patch(em,dto);
		}
		em.getTransaction().commit();
	}

	/*
	 * Permission handling, initial inserts and some native database fixes
	 * must be executed at each startup of the big data platform.
	 */
	@EventListener(ContextRefreshedEvent.class)
	public void afterStartup() throws IOException {
		/*
		 * XXX It is not safe to run this query on every startup, we must fix an
		 * issue where Postgres fails because a table should be dropped that is
		 * a view instead or vice-versa. Until this is fixed, we must run the
		 * init.sql script manually choosing between "DROP TABLE" or "DROP VIEW"
		 * based on the actual database content.
		 */
		// JPAUtil.executeNativeQueries(sql.getInputStream());
	}

}
