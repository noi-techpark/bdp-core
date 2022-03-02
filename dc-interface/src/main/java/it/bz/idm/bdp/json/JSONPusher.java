/**
 * dc-interface - Data Collector Interface for the Big Data Platform
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
package it.bz.idm.bdp.json;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import it.bz.idm.bdp.DataPusher;
import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.ProvenanceDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.dto.StationList;


/**
 * Send data as JSON-format to the writer. Implementation with spring REST template.
 *
 * @author Patrick Bertolla
 *
 */
@Component
public abstract class JSONPusher extends DataPusher {
	private static final String SYNC_DATA_TYPES = "/syncDataTypes/";
	private static final String SYNC_STATIONS = "/syncStations/";
	private static final String PUSH_RECORDS = "/pushRecords/";
	private static final String GET_DATE_OF_LAST_RECORD = "/getDateOfLastRecord/";
	private static final String JSON_ENDPOINT = "json_endpoint";
	private static final String STATIONS = "/stations/";
	private static final String PROVENANCE = "/provenance/";

	protected RestTemplate restTemplate = new RestTemplate();
	private String url;

	@Override
	@PostConstruct
	public void init() {
		super.init();
		this.url = "http://" + config.getString(HOST_KEY)+":"+config.getString(PORT_KEY)+config.getString(JSON_ENDPOINT);
	}

	@Override
	public Object pushData(String datasourceName, DataMapDto<? extends RecordDtoImpl> dto) {
		this.pushProvenance();
		dto.setProvenance(this.provenance.getUuid());
		return restTemplate
			.exchange(
				url + PUSH_RECORDS + "{datasourceName}?prn={}&prv={}",
				HttpMethod.POST,
				new HttpEntity<DataMapDto<? extends RecordDtoImpl>>(dto),
				Object.class,
				datasourceName,
				provenance.getDataCollector(),
				provenance.getDataCollectorVersion()
			)
			.getBody();
	}

	private void pushProvenance() {
		// We know that the provenance exist, and which UUID it has.
		// So we do not need to get that information again from the DB
		// This approach assumes, that the DB will not change from any
		// other caller.
		if (this.provenance.getUuid() != null) {
			return;
		}
		ResponseEntity<String> provenanceUuid = restTemplate.exchange(
			url + PROVENANCE + "?prn={}&prv={}",
			HttpMethod.POST,
			new HttpEntity<ProvenanceDto>(this.provenance),
			String.class,
			provenance.getDataCollector(),
			provenance.getDataCollectorVersion()
		);
		this.provenance.setUuid(provenanceUuid.getBody());
	}

	public Object pushData(DataMapDto<? extends RecordDtoImpl> dto) {
		dto.clean();
		return pushData(this.integreenTypology, dto);
	}

	public Object syncStations(StationList data) {
		return this.syncStations(this.integreenTypology, data);
	}

	@Override
	public Object syncStations(String datasourceName, StationList data) {
		if (data == null)
			return null;
		return restTemplate
			.exchange(
				url + SYNC_STATIONS + "{datasourceName}?prn={}&prv={}",
				HttpMethod.POST,
				new HttpEntity<StationList>(data),
				Object.class,
				datasourceName,
				provenance.getDataCollector(),
				provenance.getDataCollectorVersion()
			)
			.getBody();
	}

	@Override
	public Object syncDataTypes(String datasourceName, List<DataTypeDto> data) {
		if (data == null)
			return null;
		return restTemplate
			.exchange(
				url + SYNC_DATA_TYPES + "?prn={}&prv={}",
				HttpMethod.POST,
				new HttpEntity<List<DataTypeDto>>(data),
				Object.class,
				provenance.getDataCollector(),
				provenance.getDataCollectorVersion()
			)
			.getBody();
	}

	public Object syncDataTypes(List<DataTypeDto> data) {
		return syncDataTypes(this.integreenTypology, data);
	}

	@Override
	public Object getDateOfLastRecord(String stationCode, String dataType, Integer period) {
		return restTemplate
			.getForObject(
				url + GET_DATE_OF_LAST_RECORD + "{datasourceName}/?stationId={stationId}&typeId={dataType}&period={period}&prn={}&prv={}",
				Date.class,
				this.integreenTypology,
				stationCode,
				dataType,
				period,
				provenance.getDataCollector(),
				provenance.getDataCollectorVersion()
			);
	}

	@Override
	public void connectToDataCenterCollector() {
		// TODO authentification to writer
	}

	@Override
	public List<StationDto> fetchStations(String datasourceName, String origin) {
		StationDto[] object = restTemplate
			.getForObject(
				url + STATIONS +"{datasourceName}/?origin={origin}&prn={}&prv={}",
				StationDto[].class,
				datasourceName == null ? this.integreenTypology : datasourceName,
				origin,
				provenance.getDataCollector(),
				provenance.getDataCollectorVersion()
			);
		return Arrays.asList(object);
	}

}
