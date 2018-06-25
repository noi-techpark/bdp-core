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
 * along with this program (see LICENSE/GPLv3). If not, see
 * <http://www.gnu.org/licenses/>.
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

import io.swagger.annotations.ApiParam;
import it.bz.idm.bdp.dto.RecordDto;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.dto.security.AccessTokenDto;
import it.bz.idm.bdp.dto.security.JwtTokenDto;
import it.bz.idm.bdp.ws.util.DtoParser;

public abstract class RestController {
	
	private static final String TOKEN_POLICY = "All access token need to start with prefix 'Bearer '(see https://tools.ietf.org/html/rfc6750#section-2.1)";
	protected DataRetriever retriever;
	
	public abstract DataRetriever initDataRetriever();
	
	@PostConstruct
	public void init(){
		this.retriever = initDataRetriever();
	}
	
	@RequestMapping(value = "refresh-token", method = RequestMethod.GET)
	public @ResponseBody JwtTokenDto getToken(@RequestParam(value="user",required=true) String user,@RequestParam(value="pw",required=true)String pw) {
		return retriever.fetchRefreshToken(user, pw);
	}
	@RequestMapping(value = "access-token", method = RequestMethod.GET)
	public @ResponseBody AccessTokenDto getAccessToken(@RequestHeader(required=true,value=HttpHeaders.AUTHORIZATION)@ApiParam(TOKEN_POLICY) String refreshToken) {
		return retriever.fetchAccessToken(refreshToken);
	}
	@RequestMapping(value = "get-stations", method = RequestMethod.GET)
	public @ResponseBody String[] getStationIds() {
		return retriever.fetchStations();
	}

	@RequestMapping(value = "get-station-details", method = RequestMethod.GET)
	public @ResponseBody List<StationDto> getStationDetails() {
		return retriever.fetchStationDetails(null);
	}

	@RequestMapping(value = {"get-data-types"}, method = RequestMethod.GET)
	public @ResponseBody List<List<String>> getDataTypes(
			@RequestParam(value = "station", required = false) String station) {
		return retriever.fetchDataTypes(station);
	}
	
	@RequestMapping(value = {"get-records"}, method = RequestMethod.GET)
	public @ResponseBody List<SlimRecordDto> getRecords(@RequestHeader(required=false,value=HttpHeaders.AUTHORIZATION)@ApiParam(TOKEN_POLICY) String accessToken,
			@RequestParam("station") String station,
			@RequestParam("name") String cname,
			@RequestParam("seconds") Integer seconds,
			@RequestParam(value = "period", required = false) Integer period) {
		retriever.setAccessToken(accessToken);
		List<RecordDto> records = retriever.fetchRecords(station, cname, seconds, period);
		List<SlimRecordDto> list = DtoParser.reduce(records);
		return list;
	}
	
	@RequestMapping(value = {"get-records-in-timeframe"}, method = RequestMethod.GET)
	public @ResponseBody List<SlimRecordDto> getRecordsInTimeFrame(@RequestHeader(required=false,value=HttpHeaders.AUTHORIZATION)@ApiParam(TOKEN_POLICY) String accessToken,
			@RequestParam("station") String station,
			@RequestParam("name") String cname,
			@RequestParam("from") Long from, @RequestParam("to") Long to,
			@RequestParam(value = "period", required = false) Integer period) {
		retriever.setAccessToken(accessToken);
		List<RecordDto> records = retriever.fetchRecords(station, cname, from, to, period);
		List<SlimRecordDto> list = DtoParser.reduce(records);
		return list;
	}

	@RequestMapping(value = {"get-date-of-last-record"}, method = RequestMethod.GET)
	public @ResponseBody Date getDateOfLastRecord(@RequestHeader(required=false,value=HttpHeaders.AUTHORIZATION)@ApiParam(TOKEN_POLICY) String accessToken,
			@RequestParam("station") String station,
			@RequestParam(value="type",required=false) String type,
			@RequestParam(value="period",required=false) Integer period) {
		retriever.setAccessToken(accessToken);
		return retriever.fetchDateOfLastRecord(station, type,period);
	}
	@RequestMapping(value = {"get-newest-record"}, method = RequestMethod.GET)
	public @ResponseBody RecordDto getNewestRecord(@RequestHeader(required=false,value=HttpHeaders.AUTHORIZATION)@ApiParam(TOKEN_POLICY) String accessToken,
			@RequestParam("station") String station,
			@RequestParam(value="type",required=false) String type,
			@RequestParam(value="period",required=false) Integer period) {
		retriever.setAccessToken(accessToken);
		return retriever.fetchNewestRecord(station, type,period);
	}
}
