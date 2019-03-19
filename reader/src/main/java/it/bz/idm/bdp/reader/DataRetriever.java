/**
 * reader - Data Reader for the Big Data Platform, that queries the database for web-services
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
package it.bz.idm.bdp.reader;


import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import it.bz.idm.bdp.dal.DataType;
import it.bz.idm.bdp.dal.Measurement;
import it.bz.idm.bdp.dal.MeasurementHistory;
import it.bz.idm.bdp.dal.MeasurementString;
import it.bz.idm.bdp.dal.MeasurementStringHistory;
import it.bz.idm.bdp.dal.Station;
import it.bz.idm.bdp.dal.authentication.BDPRole;
import it.bz.idm.bdp.dal.authentication.BDPUser;
import it.bz.idm.bdp.dal.util.JPAException;
import it.bz.idm.bdp.dal.util.JPAUtil;
import it.bz.idm.bdp.dto.RecordDto;
import it.bz.idm.bdp.dto.SimpleRecordDto;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.dto.TypeDto;

/**
 * Reader API - Data retriever
 *
 * <p>The reader's purpose is to provide data to any consumer web service. It can access
 * the DB below read-only. The <code>DataRetriever</code> connects to the persistence layer,
 * retrieves data from it and translates it into DTOs (data transport objects).</p>
 *
 * @author Patrick Bertolla
 * @author Peter Moser
 *
 */
public class DataRetriever {

	/** Default seconds while retrieving records, when no [start, end], nor "seconds" are given (currently 1 day) */
	public static final int DEFAULT_SECONDS = 60 * 60 * 24; // one day

	/**
	 * Gets data types of either all stations of a certain station type, or of a single station
	 * with a certain station type and code.
	 *
	 * @param stationType typology of a {@link Station}
	 * @param stationCode unique identifier of a {@link Station} (ignored, if null)
	 *
	 * @return a list of data types
	 */
	public List<TypeDto> getTypes(String stationType, String stationCode) {
		EntityManager em = JPAUtil.createEntityManager();
		try{
			return DataType.findTypes(em, stationType, stationCode);
		}catch(Exception e){
			throw JPAException.unnest(e);
		} finally {
			if (em.isOpen())
				em.close();
		}
	}

	/**
	 * Get a list of {@link Station}s with all their details
	 *
	 * <p>If you want only a list of station IDs use {@link DataRetriever#getStations}</p>
	 *
	 * @param stationType typology of a {@link Station}
	 * @param stationCode unique identifier of a {@link Station}
	 *
	 * @return list of station data transfer objects with all meta data of the give station
	 */
	public List<? extends StationDto> getStationDetails(String stationType, String stationCode) {
		List<StationDto> stations = new ArrayList<StationDto>();
		EntityManager em = JPAUtil.createEntityManager();
		try {
			Station stationById = null;
			if (stationCode != null && !stationCode.isEmpty()) {
				stationById = Station.findStation(em, stationType, stationCode);
			}
			if ((stationById == null && (stationCode == null || stationCode.isEmpty())) || (stationById != null && stationType.equals(stationById.getStationtype())))
				stations = Station.findStationsDetails(em, stationType, stationById);
		} catch(Exception e) {
			throw JPAException.unnest(e);
		} finally {
			if (em.isOpen())
				em.close();
		}
		return stations;
	}

	/**
	 * Get a list of {@link Station} codes
	 *
	 * <p>If you want a list of station details use {@link DataRetriever#getStationDetails}</p>
	 *
	 * @param stationType typology of a {@link Station}
	 *
	 * @return list of unique identifiers of stations with a certain station typology
	 */
	public List<String> getStations(String stationType){
		EntityManager em = JPAUtil.createEntityManager();
		try{
			return Station.findStationCodes(em, stationType, true);
		} catch(Exception e) {
			throw JPAException.unnest(e);
		} finally {
			if (em.isOpen())
				em.close();
		}
	}

	/**
	 * Get a list of station types (see {@link Station#getStationtype()})
	 *
	 * @return all station types (typologies)
	 */
	public List<String> getStationTypes(){
		EntityManager em = JPAUtil.createEntityManager();
		try{
			return Station.findStationTypes(em);
		} catch(Exception e) {
			throw JPAException.unnest(e);
		} finally {
			if (em.isOpen())
				em.close();
		}
	}

	/**
	 * Get a list of data types as string arrays with 4 elements each
	 * <code> [ID, UNIT, DESCRIPTION, INTERVAL] </code>
	 * for all stations of a certain typology and a specific code.
	 *
	 * @param stationType typology of a {@link Station}
	 * @param stationCode unique identifier of a {@link Station} (optional)
	 *
	 * @return all data types for a specific station where measurements exist
	 */
	public List<String[]> getDataTypes(String stationType, String stationCode) {
		EntityManager em = JPAUtil.createEntityManager();
		try{
			return DataType.findDataTypes(em, stationType, stationCode);
		}catch(Exception e){
			throw JPAException.unnest(e);
		}finally{
			if (em.isOpen())
				em.close();
		}
	}

	/**
	 * Get a list of data types as string arrays with 4 elements each
	 * <code> [ID, UNIT, DESCRIPTION, INTERVAL] </code>
	 * for all stations of a certain typology.
	 *
	 * @param stationType typology of a {@link Station}
	 *
	 * @return all data types for a specific station where measurements exist
	 */
	public List<String[]> getDataTypes(String stationType){
		return getDataTypes(stationType, null);
	}

	/**
	 * Get the date, when the last measured data has been recorded
	 *
	 * @param stationType typology of a {@link Station}
	 * @param stationCode unique identifier of a {@link Station}
	 * @param period interval between 2 measurements
	 * @param principal authorization level of the request
	 *
	 * @return date of last measured data
	 */
	public Date getDateOfLastRecord(String stationType, String stationCode, String dataType, Integer period, Principal principal) {
		EntityManager em = JPAUtil.createEntityManager();
		BDPRole role = principal != null ? getRoleByPrincipal(principal, em) : BDPRole.fetchGuestRole(em);
		Date date = new Date(-1);
		try {
			Station station = Station.findStation(em, stationType, stationCode);
			if (station == null)
				return date;

			DataType type = null;
			if (dataType != null) {
				type = DataType.findByCname(em, dataType);
				if (type == null)
					return date;
			}

			/* Hibernate does not support UNION ALL queries, hence we must run two retrieval queries here */
			Date date1 = new Measurement().getDateOfLastRecord(em, station, type, period, role);
			Date date2 = new MeasurementString().getDateOfLastRecord(em, station, type, period, role);
			return date1.after(date2) ? date1 : date2;
		} catch(Exception e) {
			throw JPAException.unnest(e);
		} finally {
			if (em.isOpen())
				em.close();
		}
	}

	/**
	 * Get the last measured record
	 *
	 * @param stationType typology of a {@link Station}
	 * @param stationCode unique identifier of a {@link Station}
	 * @param dataType unique identifiers for a {@link DataType}
	 * @param period interval between 2 measurements
	 * @param principal authorization level of the request
	 *
	 * @return last measured data record
	 */
	public RecordDto getLastRecord(String stationType, String stationCode, String dataType, Integer period, Principal principal) {
		EntityManager em = JPAUtil.createEntityManager();
		BDPRole role = principal != null ? getRoleByPrincipal(principal, em) : BDPRole.fetchGuestRole(em);
		try {
			Station station = Station.findStation(em, stationType, stationCode);
			if (station == null)
				return null;

			DataType type = DataType.findByCname(em, dataType);

			Measurement latestEntry = (Measurement) new Measurement().findLatestEntry(em, station, type, period, role);
			MeasurementString latestStringEntry = (MeasurementString) new MeasurementString().findLatestEntry(em, station, type, period, role);

			if (latestEntry == null && latestStringEntry == null) {
				return null;
			}

			if (latestEntry == null && latestStringEntry != null) {
				return new SimpleRecordDto(latestStringEntry.getTimestamp().getTime(),
										   latestStringEntry.getValue(),
										   latestStringEntry.getCreated_on().getTime());
			}

			if (latestEntry != null && latestStringEntry == null) {
				return new SimpleRecordDto(latestEntry.getTimestamp().getTime(),
										   latestEntry.getValue(),
										   latestEntry.getCreated_on().getTime());
			}

			if (latestEntry.getTimestamp().after(latestStringEntry.getTimestamp())) {
				return new SimpleRecordDto(latestEntry.getTimestamp().getTime(),
										   latestEntry.getValue(),
										   latestEntry.getCreated_on().getTime());
			}

			return new SimpleRecordDto(latestStringEntry.getTimestamp().getTime(),
									   latestStringEntry.getValue(),
									   latestStringEntry.getCreated_on().getTime());
		} catch(Exception e) {
			throw JPAException.unnest(e);
		} finally {
			if (em.isOpen())
				em.close();
		}
	}

	/**
	 * Get all measurements filtered by given parameters
	 *
	 * <p>If no <code>seconds</code> are provided, we go back {@link DataRetriever#DEFAULT_SECONDS}
	 * seconds from now.</p>
	 *
	 * @param stationType typology of a {@link Station}
	 * @param stationCode unique identifier of a {@link Station}
	 * @param dataType unique identifiers for a {@link DataType}
	 * @param seconds back in time from now requesting data for (optional)
	 * @param period interval between 2 measurements (optional)
	 * @param principal authorization level of the request
	 *
	 * @return list of measurements
	 */
	public List<RecordDto> getRecords(String stationType, String stationCode, String dataType, Integer seconds, Integer period, Principal principal){
		seconds = seconds == null ? DEFAULT_SECONDS : seconds;
		Date end = new Date();
		Date start = new Date(end.getTime()-(seconds*1000l));
		return getRecords(stationType, stationCode, dataType, start, end, period, seconds, principal);
	}

	/**
	 * Get all measurements filtered by given parameters
	 *
	 * <p>If no <code>[start, end]</code> interval is provided, we go back {@link DataRetriever#DEFAULT_SECONDS}
	 * seconds from now.</p>
	 *
	 * @param stationType typology of a {@link Station}
	 * @param stationCode unique identifier of a {@link Station}
	 * @param dataType unique identifiers for a {@link DataType}
	 * @param start of the time interval requesting data for
	 * @param end end of the time interval data for
	 * @param period interval between 2 measurements
	 * @param seconds back in time from now (if no start and end is defined)
	 * @param principal authorization level of the request
	 *
	 * @return list of measurements
	 */
	public List<RecordDto> getRecords(String stationType, String stationCode, String dataType, Date start, Date end, Integer period, Integer seconds, Principal principal) {
		if (start == null && end == null) {
			seconds = seconds == null ? DEFAULT_SECONDS : seconds;
			end = new Date();
			start = new Date(end.getTime() - (seconds * 1000l));
		} else if (start == null || end == null) {
			throw new JPAException("Provided interval not valid: [" + start + ", " + end + "]. Set 'start' and 'end', or leave both null.");
		}

		EntityManager em = JPAUtil.createEntityManager();
		BDPRole role = principal != null ? getRoleByPrincipal(principal, em) : BDPRole.fetchGuestRole(em);

		List<RecordDto> records = new ArrayList<RecordDto>();
		try {
			Station station = Station.findStation(em, stationType, stationCode);
			if (station != null) {
				records.addAll(new MeasurementHistory().findRecords(em, stationType, stationCode, dataType, start, end, period, role));
				records.addAll(new MeasurementStringHistory().findRecords(em, stationType, stationCode, dataType, start, end, period, role));
			}
		} catch(Exception e) {
			throw JPAException.unnest(e);
		} finally {
			if (em.isOpen())
				em.close();
		}
		return records;
	}

	/**
	 * Get the {@link BDPRole} associated to a given principal
	 *
	 * @param principal authorization level of the request
	 * @param em entity manager
	 *
	 * @return a {@link BDPRole}, or null if not found
	 */
	private BDPRole getRoleByPrincipal(Principal principal, EntityManager em) {
		BDPUser user = BDPUser.findByEmail(em, principal.getName());
		BDPRole role = user==null || user.getRoles().isEmpty() ? null : user.getRoles().get(0);
		if (role == null)
			role = BDPRole.fetchGuestRole(em);
		return role;
	}
}
