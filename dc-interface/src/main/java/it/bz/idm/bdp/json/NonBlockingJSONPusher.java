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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

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
import it.bz.idm.bdp.util.Utils;
import reactor.core.publisher.Mono;

import static net.logstash.logback.argument.StructuredArguments.v;

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
	private static final String SYNC_STATION_STATES = "syncStationStates";
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
    public Object pushData(String stationType, DataMapDto<? extends RecordDtoImpl> dto) {
		LOG.info(
			"NonBlockingJSONPusher/pushData",
			v("provenance", provenance)
		);
        this.pushProvenance();
        dto.setProvenance(this.provenance.getUuid());
        return client
			.post()
			.uri(uriBuilder -> uriBuilder
				.path(PUSH_RECORDS + stationType)
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
		LOG.info(
			"NonBlockingJSONPusher/pushProvenance",
			v("provenance", provenance)
		);
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

	/** syncStations with stationType */
	@Override
	public Object syncStations(String stationType, StationList stations) {
		return syncStationSingleChunk(stationType, stations, true, false);
	}
	@Override
	public List<Object> syncStations(String stationType, StationList stations, int chunkSize) {
		return syncStationMultiChunk(stationType, stations, chunkSize, true, false);
	}
	public List<Object> syncStations(String stationType, StationList stations, boolean syncState, boolean onlyActivation) {
		return syncStationMultiChunk(stationType, stations, STATION_CHUNK_SIZE, syncState, onlyActivation);
	}
	@Override
	public List<Object> syncStations(String stationType, StationList stations, int chunkSize, boolean syncState, boolean onlyActivation) {
		return syncStationMultiChunk(stationType, stations, chunkSize, syncState, onlyActivation);
	}

	/** syncStations without stationType */
    public Object syncStations(StationList stations) {
        return syncStationMultiChunk(this.integreenTypology, stations, STATION_CHUNK_SIZE, true, false);
    }
    public Object syncStations(StationList stations, int chunkSize) {
        return syncStationMultiChunk(this.integreenTypology, stations, chunkSize, true, false);
    }
	public Object syncStations(StationList stations, boolean syncState, boolean onlyActivation) {
		return syncStationMultiChunk(this.integreenTypology, stations, STATION_CHUNK_SIZE, syncState, onlyActivation);
    }
	public Object syncStations(StationList stations, int chunkSize, boolean syncState, boolean onlyActivation) {
		return syncStationMultiChunk(this.integreenTypology, stations, chunkSize, syncState, onlyActivation);
	}

    private Object syncStationSingleChunk(String stationType, StationList stations, boolean syncState, boolean onlyActivation) {
		LOG.info(
			"NonBlockingJSONPusher/syncStations",
			v("parameters",
				Utils.mapOf("stationType", stationType )
			),
			v("provenance", provenance)
		);
		if (stations == null || stations.isEmpty()) {
			LOG.warn("NonBlockingJSONPusher/syncStation: No stations given. Returning!");
			return null;
		}
		LOG.info(
			"NonBlockingJSONPusher/syncStation: Pushing {} stations to the writer",
			stations.size()
		);

		this.pushProvenance();

        return client
			.post()
			.uri(uriBuilder -> uriBuilder
				.path(SYNC_STATIONS + stationType)
				.queryParams(
					createParams(
						"syncState", syncState,
						"onlyActivation", onlyActivation
					)
				)
				.build()
			)
			.body(Mono.just(stations), Object.class)
			.retrieve()
            .bodyToMono(Object.class)
			.block();
    }

	private List<Object> syncStationMultiChunk(String stationType, StationList stations, int chunkSize, boolean syncState, boolean onlyActivation) {
		LOG.info(
			"NonBlockingJSONPusher/syncStations",
			v("parameters",
				Utils.mapOf(
					"stationType", stationType,
					"chunkSize", chunkSize
				)
			),
			v("provenance", provenance)
		);
		if (stations == null || stations.isEmpty()) {
			LOG.warn("NonBlockingJSONPusher/syncStation: No stations given. Returning!");
			return null;
		}

		/* Syncronize station states, that is, set the active flag to true or false */
		if (syncState) {
			List<String> stationCodes = new ArrayList<>();
			for (StationDto station : stations) {
				if (stationCodes.contains(station.getId()))
					continue;
				stationCodes.add(station.getId());
			}
			syncStationStates(stationType, stations.get(0).getOrigin(), stationCodes, onlyActivation);
		}

		if (chunkSize <= 0)
			chunkSize = STATION_CHUNK_SIZE;
		int chunks = (int) Math.ceil((float) stations.size() / chunkSize);
		LOG.info(
			"NonBlockingJSONPusher/syncStation: Syncing {} stations in {} chunks of a maximum size {}!",
			stations.size(),
			chunks,
			STATION_CHUNK_SIZE
		);

		List<Object> results = new ArrayList<>();
		for (int i = 0; i < chunks; i++) {
			// We have the following interval boundaries for subList: [from, to)
			int from = STATION_CHUNK_SIZE * i;
			int to = from + STATION_CHUNK_SIZE;
			if (to > stations.size())
				to = stations.size();
			StationList stationChunk = stations.subList(from, to);

			if (stationChunk == null || stationChunk.isEmpty()) {
				LOG.warn(
					"NonBlockingJSONPusher/syncStation: No stations in chunk {} of {}. Skipping!",
					i+1,
					chunks
				);
				continue;
			}
			LOG.info("NonBlockingJSONPusher/syncStation: Chunk {} of {}", i+1, chunks);

			// Do not sync states, since we need to do this once for all and not for each chunk
			// otherwise the last chunk would set all other stations for that station type to inactive.
			results.add(syncStationSingleChunk(stationType, stationChunk, false, onlyActivation));
		}
		LOG.info("NonBlockingJSONPusher/syncStation: READY!");

		return results;
	}

	public Object syncStationStates(
		String stationType,
		String origin,
		List<String> stationCodes,
		boolean onlyActivation
	) {
		LOG.info(
			"NonBlockingJSONPusher/syncStationStates",
			v("parameters",
				Utils.mapOf(
					"stationType", stationType,
					"origin", origin
				)
			),
			v("provenance", provenance)
		);
		if (stationCodes == null || stationCodes.isEmpty()) {
			LOG.warn("NonBlockingJSONPusher/syncStationStates: No station codes given. Returning!");
			return null;
		}
		LOG.info(
			"NonBlockingJSONPusher/syncStationStates: Syncronizing {} station states (active flag)",
			stationCodes.size()
		);

		this.pushProvenance();

        return client
			.post()
			.uri(uriBuilder -> uriBuilder
				.pathSegment(SYNC_STATION_STATES, stationType, origin)
				.queryParams(
					createParams(
						"onlyActivation", onlyActivation
					)
				)
				.build()
			)
			.body(Mono.just(stationCodes), Object.class)
			.retrieve()
            .bodyToMono(Object.class)
			.block();
	}


    @Override
    public Object syncDataTypes(String stationType, List<DataTypeDto> data) {
		LOG.info(
			"NonBlockingJSONPusher/syncDataTypes",
			v("parameters", Utils.mapOf(
					"stationType", stationType
				)
			),
			v("provenance", provenance)
		);
		if (data == null) {
			LOG.warn("NonBlockingJSONPusher/syncDataTypes: No data types given. Returning!");
			return null;
		}
		LOG.info(
			"NonBlockingJSONPusher/syncDataTypes: Pushing {} data types to the writer",
			data.size()
		);

		this.pushProvenance();

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
		LOG.info(
			"NonBlockingJSONPusher/getDateOfLastRecord",
			v("parameters", Utils.mapOf(
					"stationCode", stationCode,
					"dataType", dataType,
					"period", period
				)
			),
			v("provenance", provenance)
		);
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
    public List<StationDto> fetchStations(String stationType, String origin) {
		LOG.info(
			"NonBlockingJSONPusher/fetchStations",
			v(
				"parameters", Utils.mapOf(
					"stationType", stationType,
					"origin", origin
				)
			),
			v("provenance", provenance)
		);
        StationDto[] object = client
			.get()
			.uri(uriBuilder->uriBuilder
				.path(stationType == null ? STATIONS + this.integreenTypology : STATIONS + stationType)
				.queryParams(createParams("origin", origin))
				.build()
			)
			.retrieve()
            .bodyToMono(StationDto[].class)
			.block();
        return Arrays.asList(object);
    }

	public Object addEvents(List<EventDto> dtos) {
		LOG.info(
			"NonBlockingJSONPusher/addEvents",
			v("provenance", provenance)
		);
		if (dtos == null) {
			LOG.warn("NonBlockingJSONPusher/addEvents: No events given. Returning!");
			return null;
		}
		this.pushProvenance();
		List<EventDto> eventsToSend = new ArrayList<>();
		for (EventDto dto: dtos) {
			dto.setProvenance(this.provenance.getUuid());
			if (EventDto.isValid(dto, true)) {
				eventsToSend.add(dto);
			} else {
				LOG.warn(
					"NonBlockingJSONPusher/addEvents: The given event DTO is invalid. Skipping!",
					v("eventDto", dto)
				);
			}
		}
		LOG.info(
			"NonBlockingJSONPusher/addEvents: Pushing {} events to the writer",
			eventsToSend.size()
		);
        return client
			.post()
			.uri(uriBuilder->uriBuilder
				.path(EVENTS)
				.queryParams(createParams())
				.build()
			)
			.body(Mono.just(eventsToSend), Object.class)
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
