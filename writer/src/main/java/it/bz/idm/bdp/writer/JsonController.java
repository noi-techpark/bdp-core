/**
 * writer - Data Writer for the Big Data Platform
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
package it.bz.idm.bdp.writer;

import java.net.URI;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import it.bz.idm.bdp.dal.util.JPAException;
import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.EventDto;
import it.bz.idm.bdp.dto.ProvenanceDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.StationDto;

/**
 * Spring controller handling JSON requests to the writer API.
 * For more documentation on the single API calls refer to {@link DataManager}
 *
 * @author Patrick Bertolla
 * @author Peter Moser
 */
@RestController
@RequestMapping("/json")
public class JsonController {

	@Autowired
	DataManager dataManager;

	@PostMapping(value = "/provenance")
	@ResponseBody
	public ResponseEntity<String> createProvenance(
		@RequestBody ProvenanceDto provenance
	) {
		return dataManager.addProvenance(provenance);
	}

	@GetMapping(value = "/provenance")
	@ResponseBody
	public List<ProvenanceDto> getProvenance(
		@RequestParam(value = "uuid", required = false) String uuid,
		@RequestParam(value = "dataCollector", required = false) String name,
		@RequestParam(value = "dataCollectorVersion", required = false) String version,
		@RequestParam(value = "lineage", required = false) String lineage
	) {
		return dataManager.findProvenance(uuid, name, version, lineage);
	}


	@GetMapping(value = "/getDateOfLastRecord")
	@ResponseBody
	public ResponseEntity<Date> dateOfLastRecordMissingTopology() {
		throw new JPAException("Missing station type. For example set MyStationType: .../getDateOfLastRecord/MyStationType?stationId=X&typeId=Y");
	}

	@GetMapping(value = "/getDateOfLastRecord/{integreenTypology}")
	@ResponseBody
	public ResponseEntity<Date> dateOfLastRecord(
		@PathVariable("integreenTypology") String stationType,
		@RequestParam("stationId") String stationId,
		@RequestParam(value="typeId", required=false) String typeId,
		@RequestParam(value="period", required=false) Integer period
	) {
		return ResponseEntity.ok(dataManager.getDateOfLastRecord(stationType, stationId, typeId, period));
	}

	@GetMapping(value = "/stations/{integreenTypology}")
	@ResponseBody
	public Object stationsGetList(
		@PathVariable("integreenTypology") String stationType,
		@RequestParam(value="origin", required=false) String origin
	) {
		return dataManager.getStationsNative(stationType, origin);
	}

	@GetMapping(value = "/stations")
	@ResponseBody
	public List<String> stationsGetTypes() {
		return dataManager.getStationTypes();
	}

	@GetMapping(value = "/types")
	@ResponseBody
	public List<String> dataTypes() {
		return dataManager.getDataTypes();
	}

	@PostMapping(value = "/pushRecords")
	@ResponseBody
	public ResponseEntity<Object> pushRecordsMissingTopology() {
		throw new JPAException("Missing station type. For example set MyStationType: .../pushRecords/MyStationType");
	}

	@PostMapping(value = "/pushRecords/{stationType}")
	@ResponseBody
	public Object pushRecords(
		@RequestBody(required = true) DataMapDto<RecordDtoImpl> dataMap,
		@PathVariable String stationType
	) {
		return dataManager.pushRecords(stationType, null, dataMap);
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	@PostMapping(value = "/patchStations")
	@ResponseBody
	public void patchStations(
		@RequestBody(required = true) List<StationDto> stations
	) {
		dataManager.patchStations(stations);
	}

	@PostMapping(value = "/syncStations")
	@ResponseBody
	public ResponseEntity<Object> syncStationsMissingTopology() {
		throw new JPAException("Missing station type. For example set MyStationType: .../syncStations/MyStationType");
	}

	@PostMapping(value = "/syncStations/{stationType}")
	@ResponseBody
	public ResponseEntity<Object> syncStations(
		@PathVariable String stationType,
		@RequestBody(required = true) List<StationDto> stationDtos
	) {
		return dataManager.syncStations(
			stationType,
			stationDtos,
			getURIMapping("/stations/{stationType}", stationType)
		);
	}

	@PostMapping(value = "/syncDataTypes")
	@ResponseBody
	public ResponseEntity<Object> syncDataTypes(
		@RequestBody(required = true) List<DataTypeDto> data
	) {
		return dataManager.syncDataTypes(data, getURIMapping("/types"));
	}

	@PostMapping(value = "/event")
	@ResponseBody
	public ResponseEntity<Object> syncEvents(
		@RequestBody(required = true) List<EventDto> eventDtos
	) {
		return dataManager.addEvents(eventDtos, getURIMapping("/events"));
	}

	private URI getURIMapping(String mapping, Object... uriVariableValues) {
		if (mapping == null)
			mapping = "";
		else if (mapping.length() > 0)
			mapping = "/" + mapping;
		String mappingController = this.getClass().getAnnotation(RequestMapping.class).value()[0];
		return ServletUriComponentsBuilder
			.fromCurrentContextPath()
			.path(mappingController + mapping)
			.buildAndExpand(uriVariableValues)
			.toUri();
	}

}
