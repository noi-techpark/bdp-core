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

import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import it.bz.idm.bdp.dto.ChildDto;
import it.bz.idm.bdp.dto.RecordDto;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.dto.TypeDto;
import it.bz.idm.bdp.dto.security.AccessTokenDto;
import it.bz.idm.bdp.dto.security.JwtTokenDto;
import reactor.core.publisher.Mono;

public abstract class RestClient extends DataRetriever {

	private static final String REQUEST_TIMEOUT_IN_SECONDS_KEY = "requestTimeoutInSeconds";
	private static final int DEFAULT_HTTP_REQUEST_TIMEOUT = 10;
	protected WebClient webClient;
	private String url;

	@Override
	public void connect() {
		String sslString = DEFAULT_SSL ? "https" : "http";
		this.url = sslString + "://" + DEFAULT_HOST + ":" + DEFAULT_PORT + DEFAULT_ENDPOINT;
		webClient = WebClient.create(url);
	}

	@Override
	public String[] fetchStations() {
		Map<String, String> params = new HashMap<>();
		params.put("stationType", this.integreenTypology);
		Mono<String[]> body = webClient.get().uri("/stations?stationType={stationType}", params)
				.accept(MediaType.APPLICATION_JSON).retrieve().bodyToMono(String[].class);
		return body.block();
	}

	@Override
	public List<StationDto> fetchStationDetails(String stationId) {
		Map<String, String> params = new HashMap<>();
		params.put("stationType", this.integreenTypology);
		params.put("stationId", stationId);
		Mono<List<StationDto>> mono = webClient.get()
				.uri("/station-details/?stationType={stationType}&stationId={stationId}", params)
				.accept(MediaType.APPLICATION_JSON).retrieve()
				.bodyToMono(new ParameterizedTypeReference<List<StationDto>>() {
				});
		return mono.block();
	}

	@Override
	public List<List<String>> fetchDataTypes(String stationId) {
		Map<String, String> params = new HashMap<>();
		params.put("stationType", this.integreenTypology);
		params.put("stationId", stationId);
		Mono<List<List<String>>> mono = webClient.get()
				.uri("/data-types/?stationType={stationType}&stationId={stationId}", params)
				.accept(MediaType.APPLICATION_JSON).retrieve()
				.bodyToMono(new ParameterizedTypeReference<List<List<String>>>() {
				});
		return mono.block();

	}

	@Override
	public List<TypeDto> fetchTypes(String station) {
		Map<String, String> params = new HashMap<>();
		params.put("stationType", this.integreenTypology);
		params.put("stationId", station);
		Mono<List<TypeDto>> response = webClient.get()
				.uri("/types/?stationType={stationType}&stationId={stationId}", params)
				.accept(MediaType.APPLICATION_JSON).retrieve()
				.bodyToMono(new ParameterizedTypeReference<List<TypeDto>>() {
				});
		return response.block();
	}

	@Override
	public List<RecordDto> fetchRecords(String stationId, String typeId, Integer seconds, Integer period) {
		Map<String, Object> map = new HashMap<>();
		map.put("stationType", this.integreenTypology);
		map.put("stationId", stationId);
		map.put("typeId", typeId);
		map.put("seconds", seconds);
		map.put("period", period);
		Mono<List<RecordDto>> mono = webClient.get().uri(
				"/records/?stationType={stationType}&stationId={stationId}&typeId={typeId}&period={period}&seconds={seconds}",
				map).header(HttpHeaders.AUTHORIZATION, accessToken).accept(MediaType.APPLICATION_JSON).retrieve()
				.bodyToMono(new ParameterizedTypeReference<List<RecordDto>>() {
				});
		return mono.block();
	}

	@Override
	public List<RecordDto> fetchRecords(String stationId, String typeId, Long start, Long end, Integer period) {
		Map<String, Object> map = new HashMap<>();
		map.put("stationType", this.integreenTypology);
		map.put("stationId", stationId);
		map.put("typeId", typeId);
		map.put("start", start);
		map.put("end", end);
		map.put("period", period);
		Mono<List<RecordDto>> mono = webClient.get().uri(
				"/records/?stationType={stationType}&stationId={stationId}&typeId={typeId}&period={period}&start={start}&end={end}",
				map).header(HttpHeaders.AUTHORIZATION, accessToken).accept(MediaType.APPLICATION_JSON).retrieve()
				.bodyToMono(new ParameterizedTypeReference<List<RecordDto>>() {
				});
		return mono.block();
	}

	@Override
	public RecordDto fetchNewestRecord(String stationId, String typeId, Integer period) {
		Map<String, String> map = new HashMap<>();
		map.put("stationType", this.integreenTypology);
		map.put("stationId", stationId);
		map.put("typeId", typeId);
		map.put("period", period != null ? String.valueOf(period) : null);
		Mono<RecordDto> mono = webClient.get()
				.uri("/newest-record/?stationType={stationType}&stationId={stationId}&typeId={typeId}&period={period}",
						map)
				.header(HttpHeaders.AUTHORIZATION, accessToken).accept(MediaType.APPLICATION_JSON).retrieve()
				.bodyToMono(new ParameterizedTypeReference<RecordDto>() {
				});
		return mono.block();
	}

	@Override
	public Date fetchDateOfLastRecord(String stationId, String typeId, Integer period) {
		Map<String, Object> map = new HashMap<>();
		map.put("stationType", this.integreenTypology);
		map.put("stationId", stationId);
		map.put("typeId", typeId);
		map.put("period", period);
		Mono<Date> mono = webClient.get().uri(
				"/date-of-last-record/?stationType={stationType}&stationId={stationId}&typeId={typeId}&period={period}",
				map).header(HttpHeaders.AUTHORIZATION, accessToken).accept(MediaType.APPLICATION_JSON).retrieve()
				.bodyToMono(new ParameterizedTypeReference<Date>() {
				});
		return mono.block();
	}

	@Override
	public List<? extends ChildDto> fetchChildStations(String id) {
		Map<String, String> map = new HashMap<>();
		map.put("stationType", this.integreenTypology);
		map.put("parent", id);
		Mono<List<ChildDto>> mono = webClient.get()
				.uri("/child-stations?stationType={stationType}&parent={parent}", map)
				.accept(MediaType.APPLICATION_JSON).retrieve()
				.bodyToMono(new ParameterizedTypeReference<List<ChildDto>>() {
				});
		return mono.block();
	}

	@Override
	public AccessTokenDto fetchAccessToken(String refreshToken) {
		Mono<AccessTokenDto> mono = webClient.get().uri("/accessToken").header(HttpHeaders.AUTHORIZATION, refreshToken)
				.accept(MediaType.APPLICATION_JSON).retrieve()
				.bodyToMono(new ParameterizedTypeReference<AccessTokenDto>() {
				});
		return mono.block(
				Duration.ofSeconds(config.getLong(REQUEST_TIMEOUT_IN_SECONDS_KEY, DEFAULT_HTTP_REQUEST_TIMEOUT)));
	}

	@Override
	public JwtTokenDto fetchRefreshToken(String username, String password) {
		Map<String, String> map = new HashMap<>();
		map.put("username", username);
		map.put("password", password);
		Mono<JwtTokenDto> mono = webClient.get().uri("/refreshToken?user={username}&password={password}", map)
				.accept(MediaType.APPLICATION_JSON).retrieve()
				.bodyToMono(new ParameterizedTypeReference<JwtTokenDto>() {
				});
		return mono.block(
				Duration.ofSeconds(config.getLong(REQUEST_TIMEOUT_IN_SECONDS_KEY, DEFAULT_HTTP_REQUEST_TIMEOUT)));
	}

}
