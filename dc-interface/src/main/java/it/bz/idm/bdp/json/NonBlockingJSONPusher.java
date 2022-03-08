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
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import it.bz.idm.bdp.DataPusher;
import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.EventDto;
import it.bz.idm.bdp.dto.ProvenanceDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.dto.StationList;
import reactor.core.publisher.Mono;

/**
 * Send data as JSON-format to the writer. Implementation with spring REST
 * template.
 *
 * @author Patrick Bertolla
 *
 */
@Component
public abstract class NonBlockingJSONPusher extends DataPusher {
    private static final String SYNC_DATA_TYPES = "/syncDataTypes/";
    private static final String SYNC_STATIONS = "/syncStations/";
    private static final String PUSH_RECORDS = "/pushRecords/";
    private static final String GET_DATE_OF_LAST_RECORD = "/getDateOfLastRecord/";
    private static final String STATIONS = "/stations/";
    private static final String PROVENANCE = "/provenance/";
	private static final String EVENTS = "/event/";

	private static final Logger LOG = LoggerFactory.getLogger(NonBlockingJSONPusher.class);

    @Resource(name = "webClient")
    protected WebClient client;

    @Override
    @PostConstruct
    public void init() {
        super.init();
    }

    @Override
    public Object pushData(String datasourceName, DataMapDto<? extends RecordDtoImpl> dto) {
        this.pushProvenance();
        dto.setProvenance(this.provenance.getUuid());
        return client
			.post()
			.uri(uriBuilder -> uriBuilder
				.path(PUSH_RECORDS + datasourceName)
				.queryParams(createParams())
				.build()
			)
			.body(Mono.just(dto), Object.class)
			.retrieve()
            .bodyToMono(Object.class)
			.block();
    }

    private void pushProvenance() {
		// We know that the provenance exist, and which UUID it has.
		// So we do not need to get that information again from the DB
		// This approach assumes, that the DB will not change from any
		// other caller.
		if (this.provenance.getUuid() != null) {
			return;
		}

        String provenanceUuid = client
			.post()
			.uri(uriBuilder -> uriBuilder
				.path(PROVENANCE)
				.queryParams(createParams())
				.build()
			)
			.body(Mono.just(this.provenance), ProvenanceDto.class)
			.retrieve()
            .bodyToMono(String.class)
			.block();
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
        return client
			.post()
			.uri(uriBuilder -> uriBuilder
				.path(SYNC_STATIONS + datasourceName)
				.queryParams(createParams())
				.build()
			)
			.body(Mono.just(data), Object.class)
			.retrieve()
            .bodyToMono(Object.class)
			.block();
    }

    @Override
    public Object syncDataTypes(String datasourceName, List<DataTypeDto> data) {
        if (data == null)
            return null;
        return client
			.post()
			.uri(uriBuilder -> uriBuilder
				.path(SYNC_DATA_TYPES)
				.queryParams(createParams())
				.build()
			)
			.body(Mono.just(data), Object.class)
			.retrieve()
            .bodyToMono(Object.class)
			.block();
    }

    public Object syncDataTypes(List<DataTypeDto> data) {
        return syncDataTypes(this.integreenTypology, data);
    }

    @Override
    public Object getDateOfLastRecord(String stationCode, String dataType, Integer period) {
        LOG.debug("Calling getDateOfLastRecord: {}, {}, {}", stationCode, dataType, period);
		return client
			.get()
			.uri(uriBuilder -> uriBuilder
				.path(GET_DATE_OF_LAST_RECORD + this.integreenTypology)
				.queryParams(
					createParams(
						"stationId", stationCode,
						"typeId", dataType,
						"period", period
					)
				)
				.build()
			)
			.retrieve()
			.bodyToMono(Date.class)
			.block();
    }

    @Override
    public void connectToDataCenterCollector() {
        // TODO authentification to writer
    }

    @Override
    public List<StationDto> fetchStations(String datasourceName, String origin) {
        StationDto[] object = client
			.get()
			.uri(uriBuilder->uriBuilder
				.path(datasourceName == null ? STATIONS + this.integreenTypology : STATIONS + datasourceName)
				.queryParams(createParams("origin", origin))
				.build()
			)
			.retrieve()
            .bodyToMono(StationDto[].class)
			.block();
        return Arrays.asList(object);
    }

	public Object addEvents(List<EventDto> dtos) {
		if (dtos == null)
			return null;
		this.pushProvenance();
		for (EventDto dto: dtos) {
			dto.setProvenance(this.provenance.getUuid());
			if (! EventDto.isValid(dto, true))
				throw new IllegalArgumentException("addEvents: The given event DTO is invalid. Nothing has been send to the ODH writer...");
		}
        return client
			.post()
			.uri(uriBuilder->uriBuilder
				.path(EVENTS)
				.queryParams(createParams())
				.build()
			)
			.body(Mono.just(dtos), Object.class)
			.retrieve()
			.bodyToMono(Object.class)
			.block();
	}

	private MultiValueMap<String, String> createParams(Object... params) {
		MultiValueMap<String, String> result = new LinkedMultiValueMap<>();
		for (int i = 0; i < params.length; i += 2) {
			if (params[i+1] != null) {
				result.add(params[i].toString(), params[i+1].toString());
			}
		}
		if (provenance != null) {
			result.add("prn", provenance.getDataCollector());
			result.add("prv", provenance.getDataCollectorVersion());
		}
		return result;
	}

}
