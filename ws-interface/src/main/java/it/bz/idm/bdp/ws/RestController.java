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

import javax.annotation.PostConstruct;

import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import it.bz.idm.bdp.dto.RecordDto;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.dto.security.AccessTokenDto;
import it.bz.idm.bdp.dto.security.JwtTokenDto;
import it.bz.idm.bdp.ws.util.DtoParser;

/**
 * Spring MVC template with all current API calls of data retriever {@link DataRetriever}
 * @author Patrick Bertolla
 *
 */
public abstract class RestController {

	protected static final String TOKEN_POLICY = "All access token need to start with prefix 'Bearer '(see https://tools.ietf.org/html/rfc6750#section-2.1).";
	protected static final String STATION_PARAM = "The unique ID of the station.";
	protected static final String TYPE_PARAM = "The name of the data-type you are searching for.";
	protected static final String PERIOD_PARAM = "The interval in time between two successive data acquisitions.";
	protected static final String SECONDS_PARAM= "How many seconds in the past (from now) the search must be started.";
	protected static final String FROM_PARAM= "The timestamp in milliseconds of the start of the interval.";
	protected static final String TO_PARAM= "The timestamp in milliseconds of the end of the interval.";
	protected static final String USER_PARAM = "The username of the user to which to grant the new token.";
	protected static final String PW_PARAM = "The password corresponding to the user.";

	protected DataRetriever retriever;

	public abstract DataRetriever initDataRetriever();

	@PostConstruct
	public void init(){
		this.retriever = initDataRetriever();
	}

	@ApiOperation(value="Request a new authorisation token to access protected data.",
			notes="If you need to access protected, closed data and you have been given a username and password, invoke this method to receive a new token.")
	@RequestMapping(value = "refresh-token", method = RequestMethod.GET)
	public @ResponseBody JwtTokenDto getToken(
			@ApiParam(value=USER_PARAM, required=true) @RequestParam(value="user",required=true) String user,
			@ApiParam(value=PW_PARAM, required=true) @RequestParam(value="pw",required=true)String pw) {
		return retriever.fetchRefreshToken(user, pw);
	}

	@ApiOperation(value="Request a new access token", notes="This method would give you a new token to access protected data.")
	@RequestMapping(value = "access-token", method = RequestMethod.GET)
	public @ResponseBody AccessTokenDto getAccessToken(@RequestHeader(required=true,value=HttpHeaders.AUTHORIZATION)@ApiParam(value=TOKEN_POLICY, required=true) String refreshToken) {
		return retriever.fetchAccessToken(refreshToken);
	}

	@ApiOperation(value="Retrieve all stations in the dataset.", notes="This method returns all the ID of the stations listed in the dataset.")
	@RequestMapping(value = "get-stations", method = RequestMethod.GET)
	public @ResponseBody String[] getStationIds() {
		return retriever.fetchStations();
	}

	@ApiOperation(value="Retrieve all information about all stations.", notes="This method returns the list of all stations in the dataset, including all available information about them.")
	@RequestMapping(value = "get-station-details", method = RequestMethod.GET)
	public @ResponseBody List<StationDto> getStationDetails() {
		return retriever.fetchStationDetails(null);
	}

	@ApiOperation(value="Return the available data types for that station.", notes="")
	@RequestMapping(value = {"get-data-types"}, method = RequestMethod.GET)
	public @ResponseBody List<List<String>> getDataTypes(
			@ApiParam(STATION_PARAM) @RequestParam(value = "station", required = false) String station) {
		return retriever.fetchDataTypes(station);
	}

	@ApiOperation(value="Returns all the data in the last given seconds.",notes="")
	@RequestMapping(value = {"get-records"}, method = RequestMethod.GET)
	public @ResponseBody List<SlimRecordDto> getRecords(@RequestHeader(required=false,value=HttpHeaders.AUTHORIZATION)@ApiParam(value=TOKEN_POLICY) String accessToken,
			@ApiParam(value=STATION_PARAM, required=true) @RequestParam("station") String station,
			@ApiParam(value=TYPE_PARAM, required=true) @RequestParam("name") String cname,
			@ApiParam(value=SECONDS_PARAM, required=true) @RequestParam("seconds") Integer seconds,
			@ApiParam(PERIOD_PARAM) @RequestParam(value = "period", required = false) Integer period) {
		retriever.setAccessToken(accessToken);
		List<RecordDto> records = retriever.fetchRecords(station, cname, seconds, period);
		List<SlimRecordDto> list = DtoParser.reduce(records);
		return list;
	}

	@ApiOperation(value="Return for a given station the data acquired in a given interval.", notes="Show all the data recorded in the given interval. Remeber to convert any date into milliseconds from epoch.")
	@RequestMapping(value = {"get-records-in-timeframe"}, method = RequestMethod.GET)
	public @ResponseBody List<SlimRecordDto> getRecordsInTimeFrame(@RequestHeader(required=false,value=HttpHeaders.AUTHORIZATION)@ApiParam(value=TOKEN_POLICY) String accessToken,
			@ApiParam(value=STATION_PARAM, required=true) @RequestParam("station") String station,
			@ApiParam(value=TYPE_PARAM, required=true) @RequestParam("name") String cname,
			@ApiParam(value=FROM_PARAM, required=true) @RequestParam("from") Long from,
			@ApiParam(value=TO_PARAM, required=true) @RequestParam("to") Long to,
			@ApiParam(PERIOD_PARAM) @RequestParam(value = "period", required = false) Integer period) {
		retriever.setAccessToken(accessToken);
		List<RecordDto> records = retriever.fetchRecords(station, cname, from, to, period);
		List<SlimRecordDto> list = DtoParser.reduce(records);
		return list;
	}

	@ApiOperation(value="Return the timestamp of the latest recorded action for that station.", notes="")
	@RequestMapping(value = {"get-date-of-last-record"}, method = RequestMethod.GET)
	public @ResponseBody Date getDateOfLastRecord(@RequestHeader(required=false,value=HttpHeaders.AUTHORIZATION)@ApiParam(value=TOKEN_POLICY) String accessToken,
			@ApiParam(value=STATION_PARAM, required=true) @RequestParam("station") String station,
			@ApiParam(TYPE_PARAM) @RequestParam(value="type",required=false) String type,
			@ApiParam(PERIOD_PARAM) @RequestParam(value="period",required=false) Integer period) {
		retriever.setAccessToken(accessToken);
		return retriever.fetchDateOfLastRecord(station, type,period);
	}

	@ApiOperation(value="Return the timestamp and value of the latest recorded data.", notes="")
	@RequestMapping(value = {"get-newest-record"}, method = RequestMethod.GET)
	public @ResponseBody SlimRecordDto getNewestRecord(@RequestHeader(required=false,value=HttpHeaders.AUTHORIZATION)@ApiParam(value=TOKEN_POLICY) String accessToken,
			@ApiParam(value=STATION_PARAM, required=true) @RequestParam("station") String station,
			@ApiParam(TYPE_PARAM) @RequestParam(value="type",required=false) String type,
			@ApiParam(PERIOD_PARAM) @RequestParam(value="period",required=false) Integer period) {
		retriever.setAccessToken(accessToken);
		SlimRecordDto dto = DtoParser.reduce(retriever.fetchNewestRecord(station, type,period));
		return dto;
	}
}