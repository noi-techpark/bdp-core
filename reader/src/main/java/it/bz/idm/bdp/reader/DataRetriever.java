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
import it.bz.idm.bdp.dal.Station;
import it.bz.idm.bdp.dal.authentication.BDPRole;
import it.bz.idm.bdp.dal.authentication.BDPUser;
import it.bz.idm.bdp.dal.util.JPAUtil;
import it.bz.idm.bdp.dto.RecordDto;
import it.bz.idm.bdp.dto.SimpleRecordDto;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.dto.TypeDto;

public class DataRetriever {

	/** Default seconds while retrieving records, when no [start, end], nor "seconds" are given */
	private static final int DEFAULT_SECONDS = 60 * 60 * 24; // one day

	//Utility
	private List<String> getStationTypes(EntityManager em){
		List<String> result = null;
		result = Station.findStationTypes(em);
		return result;

	}

	//API additions for V2
	public List<TypeDto> getTypes(String type, String stationId) {
		List<TypeDto> dataTypes = null;
		EntityManager em = JPAUtil.createEntityManager();
		try{
			dataTypes =	DataType.findTypes(em, type, stationId);
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			em.close();
		}
		return dataTypes;
	}

	//APIv1

	public List<? extends StationDto> getStationDetails(String stationType, String stationID) {
		List<StationDto> stations = new ArrayList<StationDto>();
		EntityManager em = JPAUtil.createEntityManager();
		try{
			Station stationById = null;
			if (stationID != null && !stationID.isEmpty()) {
				stationById = Station.findStation(em, stationType, stationID);
			}
			if ((stationById == null && (stationID == null || stationID.isEmpty())) || (stationById != null && stationType.equals(stationById.getStationtype())))
				stations = Station.findStationsDetails(em, stationType, stationById);
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			if (em.isOpen())
				em.close();
		}
		return stations;
	}

	public List<String> getStations(String type){
		List<String> stations = null;
		EntityManager em = JPAUtil.createEntityManager();
		try{
			if (getStationTypes(em).contains(type))
				stations = Station.findStationCodes(em, type, true);
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			em.close();
		}
		return stations;
	}

	public List<String[]> getDataTypes(String type, String stationId) {
		List<String[]> dataTypes = null;
		EntityManager em = JPAUtil.createEntityManager();
		try{
			dataTypes = DataType.findDataTypes(em, type, stationId);
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			em.close();
		}
		return dataTypes;
	}

	public List<String[]> getDataTypes(String type){
		return getDataTypes(type, null);
	}

	public Date getDateOfLastRecord(String stationTypology, String stationcode, String cname, Integer period,Principal principal) {
		EntityManager em = JPAUtil.createEntityManager();
		BDPRole role = principal != null ? getRoleByPrincipal(principal, em) : BDPRole.fetchGuestRole(em);
		Date dateOfLastRecord = null;
		try{
			Station station = Station.findStation(em, stationTypology, stationcode);
			DataType type = DataType.findByCname(em,cname);
			dateOfLastRecord = Measurement.getDateOfLastRecord(em, station, type, period, role);
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			em.close();
		}
		return dateOfLastRecord;
	}

	public RecordDto getLastRecord(String stationTypology, String stationcode, String cname, Integer period,Principal principal) {
		SimpleRecordDto dto = null;
		EntityManager em = JPAUtil.createEntityManager();
		BDPRole role = principal != null ? getRoleByPrincipal(principal, em) : BDPRole.fetchGuestRole(em);
		try{
			Station station = Station.findStation(em, stationTypology, stationcode);
			DataType type = DataType.findByCname(em,cname);
			Measurement latestEntry = (Measurement) new Measurement().findLatestEntry(em, station, type, period, role);
			if (latestEntry != null){
				dto = new SimpleRecordDto(latestEntry.getTimestamp().getTime(),latestEntry.getValue());
				dto.setPeriod(latestEntry.getPeriod());
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			em.close();
		}
		return dto;
	}
	public RecordDto getNewestRecord(String typology, String stationId, String typeId, Integer period, Principal principal) {
		return getLastRecord(typology, stationId, typeId, period, principal);
	}


	public List<RecordDto> getRecords(String stationtypology,String identifier, String type, Integer seconds, Integer period, Principal p){
		seconds = seconds == null ? DEFAULT_SECONDS : seconds;
		Date end = new Date();
		Date start = new Date(end.getTime()-(seconds*1000l));
		return getRecords(stationtypology, identifier, type, start, end, period,seconds, p);
	}

	public List<RecordDto> getRecords(String stationtypology, String identifier, String type, Date start, Date end,
			Integer period, Integer seconds, Principal p) {
		if (start == null && end == null) {
			seconds = seconds == null ? DEFAULT_SECONDS : seconds;
			end = new Date();
			start = new Date(end.getTime() - (seconds * 1000l));
		}
		EntityManager em = JPAUtil.createEntityManager();
		BDPRole role = p != null ? getRoleByPrincipal(p, em) : BDPRole.fetchGuestRole(em);

		List<RecordDto> records = new ArrayList<RecordDto>();
		try{
			Station station = Station.findStation(em, stationtypology, identifier);
			if (station != null) {
				records.addAll(MeasurementHistory.findRecords(em, stationtypology, identifier, type, start, end, period, role));
				return records;
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		finally{
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
