// Copyright © 2018 IDM Südtirol - Alto Adige (info@idm-suedtirol.com)
// Copyright © 2019 NOI Techpark - Südtirol / Alto Adige (info@opendatahub.com)
//
// SPDX-License-Identifier: GPL-3.0-only

package it.bz.idm.bdp.json;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import it.bz.idm.bdp.util.Utils;

import static net.logstash.logback.argument.StructuredArguments.v;

/**
 * Send data as JSON-format to the writer. Implementation with spring REST
 * template.
 *
 * @author Patrick Bertolla
 *
 */
@Component
public abstract class JSONPusher extends DataPusher {

	private static final Logger LOG = LoggerFactory.getLogger(JSONPusher.class);

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
		this.url = "http://" + config.getString(HOST_KEY) + ":" + config.getString(PORT_KEY)
				+ config.getString(JSON_ENDPOINT);
	}

	@Override
	public Object pushData(String stationType, DataMapDto<? extends RecordDtoImpl> dto) {
		LOG.info(
				"JSONPusher/pushData",
				v("provenance", provenance));
		this.pushProvenance();
		dto.setProvenance(this.provenance.getUuid());

		if (dto.getBranch() == null || dto.getBranch().isEmpty()) {
			LOG.warn("JSONPusher/pushData : Dto is empty. Returning!");
			return null;
		}

		return restTemplate
				.exchange(
						url + PUSH_RECORDS + "{stationType}?prn={}&prv={}",
						HttpMethod.POST,
						new HttpEntity<DataMapDto<? extends RecordDtoImpl>>(dto),
						Object.class,
						stationType,
						provenance.getDataCollector(),
						provenance.getDataCollectorVersion())
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
		LOG.info(
				"JSONPusher/pushProvenance",
				v("provenance", provenance));
		ResponseEntity<String> provenanceUuid = restTemplate.exchange(
				url + PROVENANCE + "?prn={}&prv={}",
				HttpMethod.POST,
				new HttpEntity<ProvenanceDto>(this.provenance),
				String.class,
				provenance.getDataCollector(),
				provenance.getDataCollectorVersion());
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
	public Object syncStations(String stationType, StationList stations) {
		LOG.info(
				"JSONPusher/syncStations",
				v(
						"parameters", Utils.mapOf(
								"stationType", stationType)),
				v("provenance", provenance));
		if (stations == null || stations.isEmpty()) {
			LOG.warn("JSONPusher/syncStation: No stations given. Returning!");
			return null;
		}
		LOG.info(
				"JSONPusher/syncStation: Pushing {} stations to the writer",
				stations.size());
		return restTemplate
				.exchange(
						url + SYNC_STATIONS + "{stationType}?prn={}&prv={}",
						HttpMethod.POST,
						new HttpEntity<StationList>(stations),
						Object.class,
						stationType,
						provenance.getDataCollector(),
						provenance.getDataCollectorVersion())
				.getBody();
	}

	@Override
	public List<Object> syncStations(String stationType, StationList stations, int chunkSize) {
		LOG.info(
				"JSONPusher/syncStations",
				v(
						"parameters", Utils.mapOf(
								"stationType", stationType)),
				v("provenance", provenance));
		if (stations == null || stations.isEmpty()) {
			LOG.warn("JSONPusher/syncStation: No stations given. Returning!");
			return null;
		}

		if (chunkSize <= 0)
			chunkSize = STATION_CHUNK_SIZE;
		int chunks = (int) Math.ceil((float) stations.size() / chunkSize);
		LOG.info(
				"JSONPusher/syncStation: Syncing {} stations in {} chunks of a maximum size {}!",
				stations.size(),
				chunks,
				STATION_CHUNK_SIZE);

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
						"JSONPusher/syncStation: No stations in chunk {} of {}. Skipping!",
						i + 1,
						chunks);
				continue;
			}

			LOG.info("JSONPusher/syncStation: Chunk {} of {}", i + 1, chunks);
			results.add(syncStations(stationType, stationChunk));
		}
		LOG.info("JSONPusher/syncStation: READY!");

		return results;
	}

	@Override
	public Object syncDataTypes(String stationType, List<DataTypeDto> data) {
		LOG.info(
				"JSONPusher/syncDataTypes",
				v(
						"parameters", Utils.mapOf(
								"stationType", stationType)),
				v("provenance", provenance));

		if (data == null) {
			LOG.warn("JSONPusher/syncDataTypes: No data types given. Returning!");
			return null;
		}
		LOG.info(
				"JSONPusher/syncDataTypes: Pushing {} data types to the writer",
				data.size());
		return restTemplate
				.exchange(
						url + SYNC_DATA_TYPES + "?prn={}&prv={}",
						HttpMethod.POST,
						new HttpEntity<List<DataTypeDto>>(data),
						Object.class,
						provenance.getDataCollector(),
						provenance.getDataCollectorVersion())
				.getBody();
	}

	public Object syncDataTypes(List<DataTypeDto> data) {
		return syncDataTypes(this.integreenTypology, data);
	}

	@Override
	public Object getDateOfLastRecord(String stationCode, String dataType, Integer period) {
		LOG.info(
				"JSONPusher/getDateOfLastRecord",
				v(
						"parameters", Utils.mapOf(
								"stationCode", stationCode,
								"dataType", dataType,
								"period", period)),
				v("provenance", provenance));
		return restTemplate
				.getForObject(
						url + GET_DATE_OF_LAST_RECORD
								+ "{datasourceName}/?stationId={stationId}&typeId={dataType}&period={period}&prn={}&prv={}",
						Date.class,
						this.integreenTypology,
						stationCode,
						dataType,
						period,
						provenance.getDataCollector(),
						provenance.getDataCollectorVersion());
	}

	@Override
	public void connectToDataCenterCollector() {
		// TODO authentification to writer
	}

	@Override
	public List<StationDto> fetchStations(String stationType, String origin) {
		LOG.info(
				"JSONPusher/fetchStations",
				v(
						"parameters", Utils.mapOf(
								"stationType", stationType,
								"origin", origin)),
				v("provenance", provenance));
		StationDto[] object = restTemplate
				.getForObject(
						url + STATIONS + "{datasourceName}/?origin={origin}&prn={}&prv={}",
						StationDto[].class,
						stationType == null ? this.integreenTypology : stationType,
						origin,
						provenance.getDataCollector(),
						provenance.getDataCollectorVersion());
		return Arrays.asList(object);
	}

}
