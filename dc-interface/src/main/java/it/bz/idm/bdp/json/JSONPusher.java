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

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import it.bz.idm.bdp.DataPusher;
import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.DataTypeDto;
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
		return restTemplate.postForObject(url + PUSH_RECORDS + "{datasourceName}", dto, Object.class, datasourceName);
	}


	private void pushProvenance() {
		String provenanceUuid = restTemplate.postForObject(url + PROVENANCE , this.provenance, String.class);
		this.provenance.setUuid(provenanceUuid);
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
		return restTemplate.postForObject(url + SYNC_STATIONS + "{datasourceName}" , data, Object.class, datasourceName);
	}
	@Override
	public Object syncDataTypes(String datasourceName, List<DataTypeDto> data) {
		if (data == null)
			return null;
		return restTemplate.postForObject(url + SYNC_DATA_TYPES, data, Object.class);
	}
	public Object syncDataTypes(List<DataTypeDto> data) {
		return syncDataTypes(this.integreenTypology, data);
	}

	@Override
	public Object getDateOfLastRecord(String stationCode, String dataType, Integer period) {
		return restTemplate.getForObject(url + GET_DATE_OF_LAST_RECORD+"{datasourceName}/?stationId={stationId}&typeId={dataType}&period={period}", Date.class,this.integreenTypology, stationCode, dataType, period);
	}

	@Override
	public void connectToDataCenterCollector() {
		// TODO authentification to writer
	}
	@Override
	public List<StationDto> fetchStations(String datasourceName, String origin) {
		StationDto[] object = restTemplate.getForObject(url + STATIONS +"{datasourceName}/?origin={origin}",StationDto[].class,datasourceName, origin);
		return Arrays.asList(object);
	}
}
