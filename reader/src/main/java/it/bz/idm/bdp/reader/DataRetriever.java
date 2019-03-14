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
 * Reader API
 * @author Patrick Bertolla
 * @author Peter Moser
 *
 */
public class DataRetriever {

	/** Default seconds while retrieving records, when no [start, end], nor "seconds" are given */
	private static final int DEFAULT_SECONDS = 60 * 60 * 24; // one day

	/**
	 * API v2 proposal for datatype dto
	 * @param type unique identifier for {@link DataType}
	 * @param stationId unique identifier for {@link Station}
	 * @return
	 */
	public List<TypeDto> getTypes(String type, String stationId) {
		EntityManager em = JPAUtil.createEntityManager();
		try{
			return DataType.findTypes(em, type, stationId);
		}catch(Exception e){
			throw JPAException.unnest(e);
		} finally {
			if (em.isOpen())
				em.close();
		}
	}

	/**
	 *
	 * @param stationType by which to filter stations
	 * @param stationID unique identifier of {@link Station}
	 * @return list of station data transfer objects with all metadata of the give station
	 */
	public List<? extends StationDto> getStationDetails(String stationType, String stationID) {
		List<StationDto> stations = new ArrayList<StationDto>();
		EntityManager em = JPAUtil.createEntityManager();
		try {
			Station stationById = null;
			if (stationID != null && !stationID.isEmpty()) {
				stationById = Station.findStation(em, stationType, stationID);
			}
			if ((stationById == null && (stationID == null || stationID.isEmpty())) || (stationById != null && stationType.equals(stationById.getStationtype())))
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
	 * @param type typology of a station
	 * @return list of unique identifiers of stations with a certain station typology
	 */
	public List<String> getStations(String type){
		EntityManager em = JPAUtil.createEntityManager();
		try{
			return Station.findStationCodes(em, type, true);
		} catch(Exception e) {
			throw JPAException.unnest(e);
		} finally {
			if (em.isOpen())
				em.close();
		}
	}

	/**
	 * @return all existing typologies in the database
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
	 * v2 API datatype call proposal
	 * @param type typology of a station
	 * @param stationId unique identifier of a station
	 * @return all datatypes for a specific station where measurements exist
	 */
	public List<String[]> getDataTypes(String type, String stationId) {
		EntityManager em = JPAUtil.createEntityManager();
		try{
			return DataType.findDataTypes(em, type, stationId);
		}catch(Exception e){
			throw JPAException.unnest(e);
		}finally{
			if (em.isOpen())
				em.close();
		}
	}

	/**
	 * @param type typology of a station
	 * @return list datatypes
	 */
	public List<String[]> getDataTypes(String type){
		return getDataTypes(type, null);
	}

	/**
	 * @param stationTypology
	 * @param stationcode unique string id for a station
	 * @param cname unique string id for a datatype
	 * @param period interval between 2 measurements
	 * @param principal authorization level of the request
	 * @return date of last measured data
	 */
	public Date getDateOfLastRecord(String stationTypology, String stationcode, String cname, Integer period,Principal principal) {
		EntityManager em = JPAUtil.createEntityManager();
		BDPRole role = principal != null ? getRoleByPrincipal(principal, em) : BDPRole.fetchGuestRole(em);
		Date date = new Date(-1);
		try {
			Station station = Station.findStation(em, stationTypology, stationcode);
			if (station == null)
				return date;

			DataType type = null;
			if (cname != null) {
				type = DataType.findByCname(em, cname);
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
	 * @param stationTypology
	 * @param stationcode unique string id for a station
	 * @param cname unique string id for a datatype
	 * @param period interval between 2 measurements
	 * @param principal authorization level of the request
	 * @return last measured data record
	 */
	public RecordDto getLastRecord(String stationTypology, String stationcode, String cname, Integer period,Principal principal) {
		EntityManager em = JPAUtil.createEntityManager();
		BDPRole role = principal != null ? getRoleByPrincipal(principal, em) : BDPRole.fetchGuestRole(em);
		try {
			Station station = Station.findStation(em, stationTypology, stationcode);
			if (station == null)
				return null;

			DataType type = DataType.findByCname(em, cname);

			Measurement latestEntry = (Measurement) new Measurement().findLatestEntry(em, station, type, period, role);
			MeasurementString latestStringEntry = (MeasurementString) new MeasurementString().findLatestEntry(em, station, type, period, role);

			if (latestEntry == null && latestStringEntry == null) {
				return null;
			}

			if (latestEntry == null && latestStringEntry != null) {
				return new SimpleRecordDto(latestStringEntry.getTimestamp().getTime(), latestStringEntry.getValue(), latestStringEntry.getPeriod());
			}

			if (latestEntry != null && latestStringEntry == null) {
				return new SimpleRecordDto(latestEntry.getTimestamp().getTime(), latestEntry.getValue(), latestEntry.getPeriod());
			}

			if (latestEntry.getTimestamp().after(latestStringEntry.getTimestamp())) {
				return new SimpleRecordDto(latestEntry.getTimestamp().getTime(), latestEntry.getValue(), latestEntry.getPeriod());
			}

			return new SimpleRecordDto(latestStringEntry.getTimestamp().getTime(), latestStringEntry.getValue(), latestStringEntry.getPeriod());
		} catch(Exception e) {
			throw JPAException.unnest(e);
		} finally {
			if (em.isOpen())
				em.close();
		}
	}
	public RecordDto getNewestRecord(String typology, String stationId, String typeId, Integer period, Principal principal) {
		return getLastRecord(typology, stationId, typeId, period, principal);
	}


	/**
	 * @param stationtypology
	 * @param identifier unique string id for a station
	 * @param type unique string id for a datatype
	 * @param seconds back in time from now requesting data for
	 * @param period interval between 2 measurements
	 * @param p authorization level of the request
	 * @return list of measurements
	 */
	public List<RecordDto> getRecords(String stationtypology,String identifier, String type, Integer seconds, Integer period, Principal p){
		seconds = seconds == null ? DEFAULT_SECONDS : seconds;
		Date end = new Date();
		Date start = new Date(end.getTime()-(seconds*1000l));
		return getRecords(stationtypology, identifier, type, start, end, period,seconds, p);
	}

	/**
	 * @param stationtypology
	 * @param identifier unique string id for a station
	 * @param type unique string id for a datatype
	 * @param start of the timeinterval requesting data for
	 * @param end end of the timeinterval data for
	 * @param period interval between 2 measurements
	 * @param seconds back in time from now (if no start and end is defined)
	 * @param p authorization level of the request
	 * @return list of measurements
	 */
	public List<RecordDto> getRecords(String stationtypology, String identifier, String type, Date start, Date end, Integer period, Integer seconds, Principal p) {
		if (start == null && end == null) {
			seconds = seconds == null ? DEFAULT_SECONDS : seconds;
			end = new Date();
			start = new Date(end.getTime() - (seconds * 1000l));
		}

		if (start == null || end == null) {
			throw new JPAException("Provided interval not valid: [" + start + ", " + end + "]. Set 'start' and 'end', or leave both null.");
		}

		EntityManager em = JPAUtil.createEntityManager();
		BDPRole role = p != null ? getRoleByPrincipal(p, em) : BDPRole.fetchGuestRole(em);

		List<RecordDto> records = new ArrayList<RecordDto>();
		try {
			Station station = Station.findStation(em, stationtypology, identifier);
			if (station != null) {
				records.addAll(new MeasurementHistory().findRecords(em, stationtypology, identifier, type, start, end, period, role));
				records.addAll(new MeasurementStringHistory().findRecords(em, stationtypology, identifier, type, start, end, period, role));
			}
		} catch(Exception e) {
			throw JPAException.unnest(e);
		} finally {
			if (em.isOpen())
				em.close();
		}
		return records;
	}

	private BDPRole getRoleByPrincipal(Principal principal, EntityManager em) {
		BDPUser user = BDPUser.findByEmail(em, principal.getName());
		BDPRole role = user==null || user.getRoles().isEmpty() ? null : user.getRoles().get(0);
		if (role == null)
			role = BDPRole.fetchGuestRole(em);
		return role;
	}
}
