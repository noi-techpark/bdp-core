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
import javax.annotation.Resource;

import org.springframework.stereotype.Component;
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
        return client.post().uri(PUSH_RECORDS + datasourceName).body(Mono.just(dto), Object.class).retrieve()
                .bodyToMono(Object.class).block();
    }

    private void pushProvenance() {
        String provenanceUuid = client.post().uri(PROVENANCE).body(Mono.just(this.provenance), ProvenanceDto.class).retrieve()
                .bodyToMono(String.class).block();
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
        return client.post().uri(SYNC_STATIONS + datasourceName).body(Mono.just(data), Object.class).retrieve()
                .bodyToMono(Object.class).block();
    }

    @Override
    public Object syncDataTypes(String datasourceName, List<DataTypeDto> data) {
        if (data == null)
            return null;
        return client.post().uri(SYNC_DATA_TYPES).body(Mono.just(data), Object.class).retrieve()
                .bodyToMono(Object.class).block();
    }

    public Object syncDataTypes(List<DataTypeDto> data) {
        return syncDataTypes(this.integreenTypology, data);
    }

    @Override
    public Object getDateOfLastRecord(String stationCode, String dataType, Integer period) {
        return client.get().uri(uriBuilder -> uriBuilder
                .path(GET_DATE_OF_LAST_RECORD + this.integreenTypology)
                .queryParam("stationId", stationCode)
                .queryParam("typeId", dataType)
                .queryParam("period", period).build())
                .retrieve().bodyToMono(Date.class).block();
    }

    @Override
    public void connectToDataCenterCollector() {
        // TODO authentification to writer
    }

    @Override
    public List<StationDto> fetchStations(String datasourceName, String origin) {
        final String uri = STATIONS + datasourceName;
        if (datasourceName == null)
            datasourceName = this.integreenTypology;
        StationDto[] object = client.get().uri(uriBuilder->uriBuilder.path(uri).queryParam("origin", "{origin}").build(origin)).retrieve()
                .bodyToMono(StationDto[].class).block();
        return Arrays.asList(object);
    }

	public Object addEvents(List<EventDto> dtos) {
		if (dtos == null)
			return null;
		this.pushProvenance();
		for (EventDto dto: dtos) {
			dto.setProvenance(this.provenance.getUuid());
			if (! EventDto.isValid(dto))
				throw new IllegalArgumentException("addEvents: The given event DTO is invalid.");
		}
        return client
			.post()
			.uri(EVENTS)
			.body(Mono.just(dtos), Object.class)
			.retrieve()
			.bodyToMono(Object.class)
			.block();
	}
}
