/**
 * writer - Data Writer for the Big Data Platform
 *
 * Copyright © 2018 IDM Südtirol - Alto Adige (info@idm-suedtirol.com)
 * Copyright © 2019 NOI Techpark - Südtirol / Alto Adige (info@opendatahub.bz.it)
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

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import it.bz.idm.bdp.dal.DataType;
import it.bz.idm.bdp.dal.Event;
import it.bz.idm.bdp.dal.MeasurementAbstract;
import it.bz.idm.bdp.dal.MeasurementAbstractHistory;
import it.bz.idm.bdp.dal.Provenance;
import it.bz.idm.bdp.dal.Station;
import it.bz.idm.bdp.dal.util.JPAException;
import it.bz.idm.bdp.dal.util.JPAUtil;
import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.EventDto;
import it.bz.idm.bdp.dto.ProvenanceDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.StationDto;

/**
 * Writer API
 *
 * @author Patrick Bertolla
 * @author Peter Moser
 */
@Component
public class DataManager {

	/**
	 * @param stationType all data sets must have stations as reference with given station type
	 * @param responseLocation
	 * @param dataMap containing all data as measurement in a tree structure
	 * @return correct response status code
	 */
	public static ResponseEntity<?> pushRecords(String stationType, URI responseLocation, DataMapDto<RecordDtoImpl> dataMap){
		EntityManager em = JPAUtil.createEntityManager();
		try {
			MeasurementAbstractHistory.pushRecords(em, stationType, dataMap);
		} catch (Exception e) {
			throw JPAException.unnest(e);
		} finally {
			if (em.isOpen())
				em.close();
		}
		return ResponseEntity.created(responseLocation).build();
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
		} catch (Exception e) {
			throw JPAException.unnest(e);
		} finally {
			if (em.isOpen())
				em.close();
		}
		return ResponseEntity.created(responseLocation).build();
	}

	/**
	 * @param dtos list of all type data transfer object provided by a given data collector
	 * @param responseLocation
	 * @return correct response status code
	 */
	public static ResponseEntity<Object> syncDataTypes(List<DataTypeDto> dtos, URI responseLocation) {
		EntityManager em = JPAUtil.createEntityManager();
		try {
			DataType.sync(em,dtos);
		} catch (Exception e) {
			throw JPAException.unnest(e);
		} finally {
			if (em.isOpen())
				em.close();
		}
		return ResponseEntity.created(responseLocation).build();
	}

	/**
	 * @param stationType the station will be of this type
	 * @param stationCode unique identifier of a station
	 * @param dataTypeName unique identifier of a data type
	 * @param period interval between 2 measurements
	 * @return a date object representing the time when the specific measurement was updated last
	 */
	public static Date getDateOfLastRecord(String stationType, String stationCode, String dataTypeName, Integer period) {
		if (stationType == null || stationType.isEmpty()
				|| stationCode == null || stationCode.isEmpty()) {
			throw new JPAException("Invalid parameter value, either empty or null, which is not allowed", HttpStatus.BAD_REQUEST.value());
		}
		EntityManager em = JPAUtil.createEntityManager();
		try {
			Date queryResult = MeasurementAbstract.getDateOfLastRecordSingleImpl(em, stationType, stationCode, dataTypeName, period);

			/*
			 * Backward compatibility: We need to distinguish station-not-found
			 * from station found, but not a datatype and period combination
			 * within.
			 * TODO: We tried the following in the past, but some data collectors no longer worked afterwards:
			 * >  throw new JPAException(
			 * >      "Station '" + stationType + "/" + stationCode + "' not found (station type/station code).",
			 * >      HttpStatus.NOT_FOUND.value()
			 * >  );
			 */
			if (queryResult == null) {
				// Query did not return a result and we used only station-related constraints, then
				// the only possible empty result is a station not found, otherwise we need to check
				// if such a station would exist. This is slow, but needed for backward compatibility.
				if ((dataTypeName == null || dataTypeName.isEmpty()) && period == null) {
					return new Date(0);
				}
				Station station = Station.findStation(em, stationType, stationCode);
				if (station == null) {
					return new Date(0);
				}
				return new Date(-1);
			}

			return queryResult;
		} catch (Exception e) {
			throw JPAException.unnest(e);
		} finally {
			if (em.isOpen())
				em.close();
		}
	}

	/**
	 * @param stationType type to filter for
	 * @param origin to filter for
	 * @return list of station DTOs converted from station entities
	 * @throws JPAException
	 */
	public static List<StationDto> getStations(String stationType, String origin) throws JPAException {
		EntityManager em = JPAUtil.createEntityManager();
		try {
			return Station.convertToDto(Station.findStations(em, stationType, origin));
		} catch (Exception e) {
			throw JPAException.unnest(e);
		} finally {
			if (em.isOpen())
				em.close();
		}
	}

	/**
	 * @return list of unique station type identifier
	 */
	public static List<String> getStationTypes() {
		EntityManager em = JPAUtil.createEntityManager();
		try {
			return Station.findStationTypes(em);
		} catch (Exception e) {
			throw JPAException.unnest(e);
		} finally {
			if (em.isOpen())
				em.close();
		}
	}

	/**
	 * @return list of unique data type identifier
	 */
	public static List<String> getDataTypes() {
		EntityManager em = JPAUtil.createEntityManager();
		try {
			return DataType.findTypeNames(em);
		} catch (Exception e) {
			throw JPAException.unnest(e);
		} finally {
			if (em.isOpen())
				em.close();
		}
	}

	/**
	 * @deprecated
	 *
	 * Does nothing right now
	 * @param stations list of data transfer objects
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
		} catch (Exception e) {
			throw JPAException.unnest(e);
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

	public static ResponseEntity<?> addProvenance(ProvenanceDto provenance) {
		EntityManager em = JPAUtil.createEntityManager();
		String uuid = null;
		try {
			em.getTransaction().begin();
			uuid = Provenance.add(em,provenance);
			em.getTransaction().commit();
		} catch (Exception e) {
			throw JPAException.unnest(e);
		} finally {
			if (em.isOpen())
				em.close();
		}
		return new ResponseEntity<>(uuid, HttpStatus.OK);
	}

	public static List<ProvenanceDto> findProvenance(String uuid, String name, String version, String lineage) {
		EntityManager em = JPAUtil.createEntityManager();
		List<Provenance> resultList = new ArrayList<>();
		try {
			resultList = Provenance.find(em, uuid, name, version, lineage);
		} catch (Exception e) {
			throw JPAException.unnest(e);
		} finally {
			if (em.isOpen())
				em.close();
		}
		List<ProvenanceDto> provenances = new ArrayList<>();
		for (Provenance p : resultList) {
			ProvenanceDto dto = new ProvenanceDto(p.getUuid(), p.getDataCollector(), p.getDataCollectorVersion(), p.getLineage());
			provenances.add(dto);
		}
		return provenances;
	}

	public static ResponseEntity<?> addEvents(List<EventDto> eventDtos, URI responseLocation) {
		EntityManager em = JPAUtil.createEntityManager();
		try {
			em.getTransaction().begin();
			Event.pushEvents(em, eventDtos);
			em.getTransaction().commit();
		} catch (Exception e) {
			if (em.getTransaction().isActive())
				em.getTransaction().rollback();
			throw JPAException.unnest(e);
		} finally {
			if (em.isOpen())
				em.close();
		}
		return ResponseEntity.created(responseLocation).build();
	}
}
