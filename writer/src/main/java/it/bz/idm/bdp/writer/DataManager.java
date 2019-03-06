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
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import it.bz.idm.bdp.dal.DataType;
import it.bz.idm.bdp.dal.MHistory;
import it.bz.idm.bdp.dal.Measurement;
import it.bz.idm.bdp.dal.MeasurementString;
import it.bz.idm.bdp.dal.Station;
import it.bz.idm.bdp.dal.authentication.BDPRole;
import it.bz.idm.bdp.dal.util.JPAException;
import it.bz.idm.bdp.dal.util.JPAUtil;
import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.StationDto;

/**
 * Writer API
 * @author Patrick Bertolla
 * @author Peter Moser
 *
 */
@Component
public class DataManager {

	/**
	 * @param stationType all datasets must have stations as reference with given stationtype
	 * @param responseLocation
	 * @param dataMap containing all data as measuerment in a tree structure
	 * @return correct response status code
	 */
	public static ResponseEntity<?> pushRecords(String stationType, URI responseLocation, DataMapDto<RecordDtoImpl> dataMap){
		EntityManager em = JPAUtil.createEntityManager();
		try {
			MHistory.pushRecords(em, stationType, dataMap);
			return ResponseEntity.created(responseLocation).build();
		} finally {
			if (em.isOpen())
				em.close();
		}
	}

	/**
	 * @param stationType stations of only this type get synchronized
	 * @param dtos list of all station data transfer object provided by a given data collector
	 * @param responseLocation
	 * @return correct response status code
	 */
	public static ResponseEntity<?> syncStations(String stationType, List<StationDto> dtos, URI responseLocation) {
		EntityManager em = JPAUtil.createEntityManager();
		try {
			Station.syncStations(em, stationType, dtos);
			return ResponseEntity.created(responseLocation).build();
		} finally {
			if (em.isOpen())
				em.close();
		}
	}

	/**
	 * @param dtos list of all type data transfer object provided by a given data collector
	 * @param responseLocation
	 * @return correct response status code
	 */
	public static ResponseEntity<?> syncDataTypes(List<DataTypeDto> dtos, URI responseLocation) {
		EntityManager em = JPAUtil.createEntityManager();
		try {
			DataType.sync(em,dtos);
			return ResponseEntity.created(responseLocation).build();
		} finally {
			if (em.isOpen())
				em.close();
		}
	}

	/**
	 * @param stationType the station will be of this type
	 * @param stationCode unique identifier of a station
	 * @param dataTypeName unique identifier of a datatype
	 * @param period interval between 2 measurements
	 * @return a date object representing the time when the specific measurement was updated last
	 */
	public static Date getDateOfLastRecord(String stationType, String stationCode, String dataTypeName, Integer period) {
		if (stationType == null || stationType.isEmpty()
				|| stationCode == null || stationCode.isEmpty()
				|| dataTypeName == null || dataTypeName.isEmpty()) {
			throw new JPAException("Invalid parameter value, either empty or null, which is not allowed", HttpStatus.BAD_REQUEST.value());
		}
		EntityManager em = JPAUtil.createEntityManager();
		try {
			Station station = Station.findStation(em, stationType, stationCode);
			if (station == null) {
				throw new JPAException("Station '" + stationType + "/" + stationCode + "' not found (station type/station code).", HttpStatus.NOT_FOUND.value());
			}
			DataType dataType = DataType.findByCname(em, dataTypeName);
			if (dataType == null) {
				throw new JPAException("Data type '" + dataTypeName + "' not found.", HttpStatus.NOT_FOUND.value());
			}
			BDPRole role = BDPRole.fetchAdminRole(em);

			/* Hibernate does not support UNION ALL queries, hence we must run two retrieval queries here */
			Date date1 = new Measurement().getDateOfLastRecord(em, station, dataType, period, role);
			Date date2 = new MeasurementString().getDateOfLastRecord(em, station, dataType, period, role);
			return date1.after(date2) ? date1 : date2;
		} catch (Exception e) {
			if (!(e instanceof JPAException)) {
				e.printStackTrace();
			}
			throw e;
		} finally {
			if (em.isOpen())
				em.close();
		}
	}

	/**
	 * @param stationType type to filter for
	 * @param origin to filter for
	 * @return list of station dtos converted from station entities
	 * @throws JPAException
	 */
	public static List<StationDto> getStations(String stationType, String origin) throws JPAException {
		EntityManager em = JPAUtil.createEntityManager();
		try {
			return Station.convertToDto(Station.findStations(em, stationType, origin));
		} finally {
			if (em.isOpen())
				em.close();
		}
	}

	/**
	 * @return list of unique stationtype identifier
	 */
	public static List<String> getStationTypes() {
		EntityManager em = JPAUtil.createEntityManager();
		try {
			return Station.findStationTypes(em);
		} finally {
			if (em.isOpen())
				em.close();
		}
	}

	/**
	 * @return list of unique datatype identifier
	 */
	public static List<String> getDataTypes() {
		EntityManager em = JPAUtil.createEntityManager();
		try {
			return DataType.findTypeNames(em);
		} finally {
			if (em.isOpen())
				em.close();
		}
	}

	/**
	 * Does nothing right now
	 * @param stations list of data transferobjects 
	 */
	@Deprecated
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
