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
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;
import javax.transaction.Transactional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

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

	/*
	 * In the past we used @PersistenceContext here. However, if we inject this
	 * EntityManager like that in environments where the concurrency is a
	 * concern, we're not thread-safe. This caused some invalid pushRecord
	 * calls, where strings would have been pushed to number measurement tables
	 * which resulted in a database error. Hence, we better use a application
	 * managed entity manager here.
	 */
	@PersistenceUnit
	private EntityManagerFactory entityManagerFactory;

	/**
	 * @param stationType all data sets must have stations as reference with given station type
	 * @param responseLocation
	 * @param dataMap containing all data as measurement in a tree structure
	 * @return correct response status code
	 */
	@Transactional
	public ResponseEntity<Object> pushRecords(String stationType, URI responseLocation, DataMapDto<RecordDtoImpl> dataMap){
		LOG.debug("DataManager: pushRecords: {}, {}", stationType, responseLocation);
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			MeasurementAbstractHistory.pushRecords(entityManager, stationType, dataMap);
		} catch (Exception e) {
			throw JPAException.unnest(e);
		} finally {
			entityManager.close();
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
	public ResponseEntity<Object> syncStations(
		String stationType,
		List<StationDto> dtos,
		URI responseLocation,
		String provenanceName,
		String provenanceVersion,
		boolean syncState
	) {
		LOG.debug(
			"[{}/{}] DataManager: syncStations: {}, {}, List<StationDto>.size = {}",
			provenanceName,
			provenanceVersion,
			stationType,
			responseLocation,
			dtos.size()
		);
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			entityManager.getTransaction().begin();
			Station.syncStations(entityManager, stationType, dtos, provenanceName, provenanceVersion, syncState);
			entityManager.getTransaction().commit();
		} catch (Exception e) {
			entityManager.getTransaction().rollback();
			throw JPAException.unnest(e);
		} finally {
			entityManager.close();
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
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			entityManager.getTransaction().begin();
			DataType.sync(entityManager, dtos);
			entityManager.getTransaction().commit();
		} catch (Exception e) {
			entityManager.getTransaction().rollback();
			throw JPAException.unnest(e);
		} finally {
			entityManager.close();
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
		if (isEmpty(stationType) || isEmpty(stationCode)) {
			throw new JPAException(
				"Invalid parameter value, either empty or null, which is not allowed",
				HttpStatus.BAD_REQUEST.value()
			);
		}
		LOG.debug("DataManager: getDateOfLastRecord: {}, {}, {}, {}", stationType, stationCode, dataTypeName, period);
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			Date queryResult = MeasurementAbstract.getDateOfLastRecordSingleImpl(
				entityManager,
				stationType,
				stationCode,
				dataTypeName,
				period
			);

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
				if (isEmpty(dataTypeName) && period == null) {
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
		} finally {
			entityManager.close();
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
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			return Station.convertToDto(Station.findStations(entityManager, stationType, origin));
		} catch (Exception e) {
			throw JPAException.unnest(e);
		} finally {
			entityManager.close();
		}
	}

	@Transactional
	public Object getStationsNative(String stationType, String origin) throws JPAException {
		LOG.debug("DataManager: getStationsNative: {}, {}", stationType, origin);
		EntityManager entityManager = entityManagerFactory.createEntityManager();
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
				.setParameterIfNotEmpty("origin", origin, "AND origin = :origin")
				.buildSingleResultOrAlternative(Object.class, new ArrayList<>());
		} catch (Exception e) {
			throw JPAException.unnest(e);
		} finally {
			entityManager.close();
		}
	}

	/**
	 * @return list of unique station type identifier
	 */
	@Transactional
	public List<String> getStationTypes() {
		LOG.debug("DataManager: getStationTypes");
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			return Station.findStationTypes(entityManager);
		} catch (Exception e) {
			throw JPAException.unnest(e);
		} finally {
			entityManager.close();
		}
	}

	/**
	 * @return list of unique data type identifier
	 */
	@Transactional
	public List<String> getDataTypes() {
		LOG.debug("DataManager: getDataTypes");
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			return DataType.findTypeNames(entityManager);
		} catch (Exception e) {
			throw JPAException.unnest(e);
		} finally {
			entityManager.close();
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
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			entityManager.getTransaction().begin();
			for (StationDto dto:stations) {
				Station.patch(entityManager, dto);
			}
			entityManager.getTransaction().commit();
		} catch (Exception e) {
			entityManager.getTransaction().rollback();
			throw JPAException.unnest(e);
		} finally {
			entityManager.close();
		}
	}

	@Transactional
	public String addProvenance(ProvenanceDto provenance) {
		String uuid = null;
		LOG.debug("DataManager: addProvenance: {}", provenance.toString());
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			entityManager.getTransaction().begin();
			uuid = Provenance.add(entityManager, provenance);
			entityManager.getTransaction().commit();
		} catch (Exception e) {
			entityManager.getTransaction().rollback();
			throw JPAException.unnest(e);
		} finally {
			entityManager.close();
		}
		return uuid;
	}

	@Transactional
	public List<ProvenanceDto> findProvenance(String uuid, String name, String version, String lineage) {
		List<Provenance> resultList = new ArrayList<>();
		LOG.debug("DataManager: findProvenance: {}, {}, {}, {}", uuid, name, version, lineage);
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			resultList = Provenance.find(entityManager, uuid, name, version, lineage);
		} catch (Exception e) {
			throw JPAException.unnest(e);
		} finally {
			entityManager.close();
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
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			entityManager.getTransaction().begin();
			Event.pushEvents(entityManager, eventDtos);
			entityManager.getTransaction().commit();
		} catch (Exception e) {
			entityManager.getTransaction().rollback();
			throw JPAException.unnest(e);
		} finally {
			entityManager.close();
		}
		return ResponseEntity.created(responseLocation).build();
	}

	@PostConstruct
    public void postConstruct() {
        Objects.requireNonNull(entityManagerFactory);
    }

	private boolean isEmpty(String what) {
		return what == null || what.isEmpty();
	}

	@Transactional
	public ResponseEntity<Object> syncStationStates(
		String stationType,
		String origin,
		List<String> stationCodeList,
		URI uriMapping,
		String provenanceName,
		String provenanceVersion
	) {
		LOG.debug("DataManager: syncStationStates: {}, {}", stationType, origin);
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		try {
			entityManager.getTransaction().begin();
			int updatedRecords = Station.syncStationStates(
				entityManager,
				stationType,
				origin,
				stationCodeList,
				provenanceName,
				provenanceVersion
			);
			entityManager.getTransaction().commit();
			LOG.debug("DataManager: syncStationStates: {} records updated", updatedRecords);
		} catch (Exception e) {
			entityManager.getTransaction().rollback();
			throw JPAException.unnest(e);
		} finally {
			entityManager.close();
		}
		return ResponseEntity.created(uriMapping).build();
	}

}
