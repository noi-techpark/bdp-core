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
import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import it.bz.idm.bdp.dal.DataType;
import it.bz.idm.bdp.dal.Event;
import it.bz.idm.bdp.dal.MeasurementAbstract;
import it.bz.idm.bdp.dal.MeasurementAbstractHistory;
import it.bz.idm.bdp.dal.Provenance;
import it.bz.idm.bdp.dal.Station;
import it.bz.idm.bdp.dal.util.JPAException;
import it.bz.idm.bdp.dal.util.QueryBuilder;
import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.EventDto;
import it.bz.idm.bdp.dto.ProvenanceDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.StationDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Writer API
 *
 * @author Patrick Bertolla
 * @author Peter Moser
 */
@Component
public class DataManager {

	private static final Logger LOG = LoggerFactory.getLogger(DataManager.class);

	@PersistenceContext
	private EntityManager entityManager;

	/**
	 * @param stationType all data sets must have stations as reference with given station type
	 * @param responseLocation
	 * @param dataMap containing all data as measurement in a tree structure
	 * @return correct response status code
	 */
	@Transactional
	public ResponseEntity<Object> pushRecords(String stationType, URI responseLocation, DataMapDto<RecordDtoImpl> dataMap){
		LOG.debug("DataManager: pushRecords: {}, {}", stationType, responseLocation);
		try {
			MeasurementAbstractHistory.pushRecords(entityManager, stationType, dataMap);
		} catch (Exception e) {
			throw JPAException.unnest(e);
		}
		return ResponseEntity.created(responseLocation).build();
	}

	/**
	 * @param stationType stations of only this type get synchronized
	 * @param dtos list of all station data transfer object provided by a given data collector
	 * @param responseLocation
	 * @return correct response status code
	 */
	@Transactional
	public ResponseEntity<Object> syncStations(String stationType, List<StationDto> dtos, URI responseLocation) {
		LOG.debug("DataManager: syncStations: {}, {}, List<StationDto>.size = {}", stationType, responseLocation, dtos.size());
		try {
			Station.syncStations(entityManager, stationType, dtos);
		} catch (Exception e) {
			throw JPAException.unnest(e);
		}
		return ResponseEntity.created(responseLocation).build();
	}

	/**
	 * @param dtos list of all type data transfer object provided by a given data collector
	 * @param responseLocation
	 * @return correct response status code
	 */
	@Transactional
	public ResponseEntity<Object> syncDataTypes(List<DataTypeDto> dtos, URI responseLocation) {
		LOG.debug("DataManager: syncDataTypes: {}, List<DataTypeDto>.size = {}", responseLocation, dtos.size());
		try {
			DataType.sync(entityManager, dtos);
		} catch (Exception e) {
			throw JPAException.unnest(e);
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
	@Transactional
	public Date getDateOfLastRecord(String stationType, String stationCode, String dataTypeName, Integer period) {
		if (stationType == null || stationType.isEmpty()
				|| stationCode == null || stationCode.isEmpty()) {
			throw new JPAException("Invalid parameter value, either empty or null, which is not allowed", HttpStatus.BAD_REQUEST.value());
		}
		LOG.debug("DataManager: getDateOfLastRecord: {}, {}, {}, {}", stationType, stationCode, dataTypeName, period);
		try {
			Date queryResult = MeasurementAbstract.getDateOfLastRecordSingleImpl(entityManager, stationType, stationCode, dataTypeName, period);

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
				Station station = Station.findStation(entityManager, stationType, stationCode);
				if (station == null) {
					return new Date(0);
				}
				return new Date(-1);
			}

			return queryResult;
		} catch (Exception e) {
			throw JPAException.unnest(e);
		}
	}

	/**
	 * @param stationType type to filter for
	 * @param origin to filter for
	 * @return list of station DTOs converted from station entities
	 * @throws JPAException
	 */
	@Transactional
	public List<StationDto> getStations(String stationType, String origin) throws JPAException {
		LOG.debug("DataManager: getStations: {}, {}", stationType, origin);
		try {
			return Station.convertToDto(Station.findStations(entityManager, stationType, origin));
		} catch (Exception e) {
			throw JPAException.unnest(e);
		}
	}

	@Transactional
	public Object getStationsNative(String stationType, String origin) throws JPAException {
		LOG.debug("DataManager: getStationsNative: {}, {}", stationType, origin);
		try {
			return QueryBuilder
				.init(entityManager)
				.nativeQuery()
				.addSql("select jsonb_agg(jsonb_strip_nulls(jsonb_build_object(",
					"'id', s.stationcode,",
					"'name', s.name,",
					"'longitude', public.ST_X (public.ST_Transform (pointprojection, 4326)),",
					"'latitude', public.ST_Y (public.ST_Transform (pointprojection, 4326)),",
					"'coordinateReferenceSystem', 'EPSG:4326',",
					"'origin', s.origin,",
					"'metaData', cast(nullif(jsonb_strip_nulls(m.json), '{}') as jsonb)",
				")))",
				"from {h-schema}station s",
				"left join {h-schema}metadata m on m.id = s.meta_data_id")
				.addSql("where s.active = :active and s.stationtype = :stationtype")
				.setParameter("active", true)
				.setParameter("stationtype", stationType)
				.setParameterIf("origin", origin, "AND origin = :origin", origin!=null && !origin.isEmpty())
				.buildSingleResultOrAlternative(Object.class, new ArrayList<>());
		} catch (Exception e) {
			throw JPAException.unnest(e);
		}
	}

	/**
	 * @return list of unique station type identifier
	 */
	@Transactional
	public List<String> getStationTypes() {
		LOG.debug("DataManager: getStationTypes");
		try {
			return Station.findStationTypes(entityManager);
		} catch (Exception e) {
			throw JPAException.unnest(e);
		}
	}

	/**
	 * @return list of unique data type identifier
	 */
	@Transactional
	public List<String> getDataTypes() {
		LOG.debug("DataManager: getDataTypes");
		try {
			return DataType.findTypeNames(entityManager);
		} catch (Exception e) {
			throw JPAException.unnest(e);
		}
	}

	/**
	 * @deprecated
	 *
	 * Does nothing right now
	 * @param stations list of data transfer objects
	 */
	@Deprecated
	@Transactional
	public void patchStations(List<StationDto> stations) {
		LOG.debug("DataManager: patchStations (deprecated)");
		try {
			for (StationDto dto:stations) {
				Station.patch(entityManager, dto);
			}
		} catch (Exception e) {
			throw JPAException.unnest(e);
		}
	}

	@Transactional
	public ResponseEntity<String> addProvenance(ProvenanceDto provenance) {
		String uuid = null;
		LOG.debug("DataManager: addProvenance: {}", provenance.toString());
		try {
			uuid = Provenance.add(entityManager, provenance);
		} catch (Exception e) {
			throw JPAException.unnest(e);
		}
		return new ResponseEntity<>(uuid, HttpStatus.OK);
	}

	@Transactional
	public List<ProvenanceDto> findProvenance(String uuid, String name, String version, String lineage) {
		List<Provenance> resultList = new ArrayList<>();
		LOG.debug("DataManager: findProvenance: {}, {}, {}, {}", uuid, name, version, lineage);
		try {
			resultList = Provenance.find(entityManager, uuid, name, version, lineage);
		} catch (Exception e) {
			throw JPAException.unnest(e);
		}
		List<ProvenanceDto> provenances = new ArrayList<>();
		for (Provenance p : resultList) {
			ProvenanceDto dto = new ProvenanceDto(p.getUuid(), p.getDataCollector(), p.getDataCollectorVersion(), p.getLineage());
			provenances.add(dto);
		}
		return provenances;
	}

	@Transactional
	public ResponseEntity<Object> addEvents(List<EventDto> eventDtos, URI responseLocation) {
		LOG.debug("DataManager: addEvents: {}, List<EventDto>.size = {}", responseLocation, eventDtos.size());
		try {
			Event.pushEvents(entityManager, eventDtos);
		} catch (Exception e) {
			throw JPAException.unnest(e);
		}
		return ResponseEntity.created(responseLocation).build();
	}

	@PostConstruct
    public void postConstruct() {
        Objects.requireNonNull(entityManager);
    }
}
