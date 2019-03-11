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

import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import it.bz.idm.bdp.dto.ChildDto;
import it.bz.idm.bdp.dto.RecordDto;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.dto.TypeDto;
import it.bz.idm.bdp.dto.security.AccessTokenDto;
import it.bz.idm.bdp.dto.security.JwtTokenDto;

/**
 * @author Patrick Bertolla
 *
 * Retriever implementation with a non-blocking, reactive http client
 */
@Component
public class RestClient extends DataRetriever {

	protected WebClient webClient;

	@Override
	public void connect() {
		webClient = WebClient.create(endpoint);
	}

	@Override
	public String[] fetchStations() {
		Map<String, String> params = new HashMap<>();
		params.put("stationType", this.getStationType());
		return webClient
				.get()
				.uri("/stations?stationType={stationType}", params)
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.bodyToMono(String[].class)
				.block();
	}

	@Override
	public List<StationDto> fetchStationDetails(String stationId) {
		Map<String, String> params = new HashMap<>();
		params.put("stationType", this.getStationType());
		params.put("stationId", stationId);
		return webClient
				.get()
				.uri("/station-details/?stationType={stationType}&stationId={stationId}", params)
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.bodyToMono(new ParameterizedTypeReference<List<StationDto>>(){})
				.block();
	}

	@Override
	public List<List<String>> fetchDataTypes(String stationId) {
		Map<String, String> params = new HashMap<>();
		params.put("stationType", this.getStationType());
		params.put("stationId", stationId);
		return webClient
				.get()
				.uri("/data-types/?stationType={stationType}&stationId={stationId}", params)
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.bodyToMono(new ParameterizedTypeReference<List<List<String>>>(){})
				.block();
	}

	@Override
	public List<TypeDto> fetchTypes(String station) {
		Map<String, String> params = new HashMap<>();
		params.put("stationType", this.getStationType());
		params.put("stationId", station);
		return webClient
				.get()
				.uri("/types/?stationType={stationType}&stationId={stationId}", params)
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.bodyToMono(new ParameterizedTypeReference<List<TypeDto>>(){})
				.block();
	}

	@Override
	public List<RecordDto> fetchRecords(String stationId, String typeId, Integer seconds, Integer period) {
		Map<String, Object> map = new HashMap<>();
		map.put("stationType", this.getStationType());
		map.put("stationId", stationId);
		map.put("typeId", typeId);
		map.put("seconds", seconds);
		map.put("period", period);
		return webClient
				.get()
				.uri("/records/?stationType={stationType}&stationId={stationId}&typeId={typeId}&period={period}&seconds={seconds}",	map)
				.header(HttpHeaders.AUTHORIZATION, accessToken)
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.bodyToMono(new ParameterizedTypeReference<List<RecordDto>>(){})
				.block();
	}

	@Override
	public List<RecordDto> fetchRecords(String stationId, String typeId, Long start, Long end, Integer period) {
		Map<String, Object> map = new HashMap<>();
		map.put("stationType", this.getStationType());
		map.put("stationId", stationId);
		map.put("typeId", typeId);
		map.put("start", start);
		map.put("end", end);
		map.put("period", period);
		return webClient.get()
				.uri("/records/?stationType={stationType}&stationId={stationId}&typeId={typeId}&period={period}&start={start}&end={end}", map)
				.header(HttpHeaders.AUTHORIZATION, accessToken)
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.bodyToMono(new ParameterizedTypeReference<List<RecordDto>>(){})
				.block();
	}

	@Override
	public RecordDto fetchNewestRecord(String stationId, String typeId, Integer period) {
		Map<String, String> map = new HashMap<>();
		map.put("stationType", this.getStationType());
		map.put("stationId", stationId);
		map.put("typeId", typeId);
		map.put("period", period != null ? String.valueOf(period) : null);
		return webClient
				.get()
				.uri("/newest-record/?stationType={stationType}&stationId={stationId}&typeId={typeId}&period={period}",	map)
				.header(HttpHeaders.AUTHORIZATION, accessToken)
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.bodyToMono(new ParameterizedTypeReference<RecordDto>(){})
				.block();
	}

	@Override
	public Date fetchDateOfLastRecord(String stationId, String typeId, Integer period) {
		Map<String, Object> map = new HashMap<>();
		map.put("stationType", this.getStationType());
		map.put("stationId", stationId);
		map.put("typeId", typeId);
		map.put("period", period);
		return webClient.get()
				.uri("/date-of-last-record/?stationType={stationType}&stationId={stationId}&typeId={typeId}&period={period}", map)
				.header(HttpHeaders.AUTHORIZATION, accessToken)
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.bodyToMono(new ParameterizedTypeReference<Date>(){})
				.block();
	}

	@Override
	public List<? extends ChildDto> fetchChildStations(String id) {
		Map<String, String> map = new HashMap<>();
		map.put("stationType", this.getStationType());
		map.put("parent", id);
		return webClient
				.get()
				.uri("/child-stations?stationType={stationType}&parent={parent}", map)
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.bodyToMono(new ParameterizedTypeReference<List<ChildDto>>(){})
				.block();
	}

	@Override
	public AccessTokenDto fetchAccessToken(String refreshToken) {
		return webClient
				.get()
				.uri("/accessToken").header(HttpHeaders.AUTHORIZATION, refreshToken)
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.bodyToMono(new ParameterizedTypeReference<AccessTokenDto>(){})
				.block(Duration.ofSeconds(requestTimeoutInSeconds));
	}

	@Override
	public JwtTokenDto fetchRefreshToken(String username, String password) {
		Map<String, String> map = new HashMap<>();
		map.put("username", username);
		map.put("password", password);
		return webClient
				.get()
				.uri("/refreshToken?user={username}&password={password}", map)
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.bodyToMono(new ParameterizedTypeReference<JwtTokenDto>(){})
				.block(Duration.ofSeconds(requestTimeoutInSeconds));
	}

}
