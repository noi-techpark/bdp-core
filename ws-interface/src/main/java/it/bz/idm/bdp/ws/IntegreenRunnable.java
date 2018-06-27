/**
 * ws-interface - Web Service Interface for the Big Data Platform
 * Copyright © 2018 OpenDataHub (info@opendatahub.bz.it)
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
public interface IntegreenRunnable {

	public abstract String[] fetchStations();
	
	public abstract List<StationDto> fetchStationDetails(String stationId);

	public abstract List<List<String>> fetchDataTypes(String station);
	
	public abstract List<TypeDto> fetchTypes(String station);
	
	public abstract List<RecordDto> fetchRecords(String stationId, String typeId, Integer seconds, Integer period);
	public abstract List<RecordDto> fetchRecords(String stationId, String typeId, Long start, Long end, Integer period);
	
	public abstract RecordDto fetchNewestRecord(String stationId, String typeId, Integer period);
	
	public abstract Date fetchDateOfLastRecord(String stationId, String typeId, Integer period);
	
	public abstract List<? extends ChildDto> fetchChildStations(String id);

	public AccessTokenDto fetchAccessToken(String refreshToken);
	
	public JwtTokenDto fetchRefreshToken(String userName, String password);
}
