/**
 * ws-interface - Web Service Interface for the Big Data Platform
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
package it.bz.idm.bdp.ws;

import java.util.Date;
import java.util.List;

import it.bz.idm.bdp.dto.ChildDto;
import it.bz.idm.bdp.dto.RecordDto;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.dto.TypeDto;
import it.bz.idm.bdp.dto.security.AccessTokenDto;
import it.bz.idm.bdp.dto.security.JwtTokenDto;

/**
 * Currently supported reader API Calls
 *
 * @author Patrick Bertolla
 */
public interface IntegreenRunnable {

	/**
	 * @return list of unique station identifiers
	 */
	public abstract String[] fetchStations();

	/**
	 * @param stationId unique station identifier to filter by
	 * @return list of stations with all metadata
	 */
	public abstract List<StationDto> fetchStationDetails(String stationId);

	/**
	 * @param station unique station identifier
	 * @return list of datatypes filtered by stationid
	 */
	public abstract List<List<String>> fetchDataTypes(String station);

	/**
	 * @param station unique station identifier
	 * @return list of datatypes filtered by stationid
	 */
	public abstract List<TypeDto> fetchTypes(String station);

	/**
	 * @param stationId unique station identifier
	 * @param typeId unique datatype identifier
	 * @param seconds from now back in time to query for
	 * @param period standard interval between 2 measurements
	 * @return list of measurments
	 */
	public abstract List<RecordDto> fetchRecords(String stationId, String typeId, Integer seconds, Integer period);
	/**
	 * @param stationId unique station identifier
	 * @param typeId unique datatype identifier
	 * @param start time in milliseconds UTC to query for
	 * @param end time in milliseconds UTC to query for
	 * @param period standard interval between 2 measurements
	 * @return list of measurements
	 */
	public abstract List<RecordDto> fetchRecords(String stationId, String typeId, Long start, Long end, Integer period);

	/**
	 * @param stationId unique station identifier
	 * @param typeId unique datatype identifier
	 * @param period standard interval between 2 measurements
	 * @return newest record filtered by parameters
	 */
	public abstract RecordDto fetchNewestRecord(String stationId, String typeId, Integer period);

	/**
	 * @param stationId unique station identifier
	 * @param typeId unique datatype identifier
	 * @param period standard interval between 2 measurements
	 * @return date of newest record filtered by parameters
	 */
	public abstract Date fetchDateOfLastRecord(String stationId, String typeId, Integer period);

	/**
	 * @param id unique station identifier of the parent station
	 * @return list of stations with the common parent
	 */
	public abstract List<? extends ChildDto> fetchChildStations(String id);

	/**
	 * @param refreshToken authenticationToken to be allowed to fetch a new refresh token
	 * @return accesstoken
	 */
	public AccessTokenDto fetchAccessToken(String refreshToken);

	/**
	 * @param userName
	 * @param password
	 * @return jwt token containing the refresh token and the first accesstoken
	 */
	public JwtTokenDto fetchRefreshToken(String userName, String password);
}
