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

import java.util.Date;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

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
 */
@RestController
@RequestMapping("/json")
public class JsonController extends DataManager {

	@RequestMapping(value="/provenance", method=RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> createProvenance(@RequestBody ProvenanceDto provenance) {
		return DataManager.addProvenance(provenance);
	}

	@RequestMapping(value = "/provenance", method = RequestMethod.GET)
	public @ResponseBody List<ProvenanceDto> getProvenance(
			@RequestParam(value = "uuid", required = false) String uuid,
			@RequestParam(value = "dataCollector", required = false) String name,
			@RequestParam(value = "dataCollectorVersion", required = false) String version,
			@RequestParam(value = "lineage", required = false) String lineage) {
		return DataManager.findProvenance(uuid,name,version,lineage);
	}


	@RequestMapping(value = "/getDateOfLastRecord", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Date> dateOfLastRecordMissingTopology() {
		throw new JPAException("Missing station type. For example set MyStationType: .../getDateOfLastRecord/MyStationType?stationId=X&typeId=Y");
	}

	@RequestMapping(value = "/getDateOfLastRecord/{integreenTypology}", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Date> dateOfLastRecord(@PathVariable("integreenTypology") String stationType,
															   @RequestParam("stationId") String stationId,
															   @RequestParam(value="typeId", required=false) String typeId,
															   @RequestParam(value="period", required=false) Integer period) {
		return ResponseEntity.ok(DataManager.getDateOfLastRecord(stationType, stationId, typeId, period));
	}

	@RequestMapping(value = "/stations/{integreenTypology}", method = RequestMethod.GET)
	public @ResponseBody List<StationDto> stationsGetList(@PathVariable("integreenTypology") String stationType,
														  @RequestParam(value="origin", required=false) String origin) {
		return DataManager.getStations(stationType, origin);
	}

	@RequestMapping(value = "/stations", method = RequestMethod.GET)
	public @ResponseBody List<String> stationsGetTypes() {
		return DataManager.getStationTypes();
	}

	@RequestMapping(value = "/types", method = RequestMethod.GET)
	public @ResponseBody List<String> dataTypes() {
		return DataManager.getDataTypes();
	}

	@RequestMapping(value = "/pushRecords", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<Object> pushRecordsMissingTopology() {
		throw new JPAException("Missing station type. For example set MyStationType: .../pushRecords/MyStationType");
	}

	@RequestMapping(value = "/pushRecords/{stationType}", method = RequestMethod.POST)
	public @ResponseBody Object pushRecords(@RequestBody(required = true) DataMapDto<RecordDtoImpl> dataMap,
											@PathVariable String stationType) {
		return DataManager.pushRecords(stationType, null, dataMap);
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	@Override
	@RequestMapping(value = "/patchStations", method = RequestMethod.POST)
	public @ResponseBody void patchStations(@RequestBody(required = true) List<StationDto> stations) {
		super.patchStations(stations);
	}

	@RequestMapping(value = "/syncStations", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<Object> syncStationsMissingTopology() {
		throw new JPAException("Missing station type. For example set MyStationType: .../syncStations/MyStationType");
	}

	@RequestMapping(value = "/syncStations/{stationType}", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<Object> syncStations(@PathVariable String stationType,
														@RequestBody(required = true) List<StationDto> stationDtos) {
		return DataManager.syncStations(stationType, stationDtos, getURIMapping("/stations/{stationType}", stationType));
	}

	@RequestMapping(value = "/syncDataTypes", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<Object> syncDataTypes(@RequestBody(required = true) List<DataTypeDto> data) {
		return DataManager.syncDataTypes(data, getURIMapping("/types"));
	}

	@RequestMapping(value = "/event", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<Object> syncEvents(@RequestBody(required = true) List<EventDto> eventDtos) {
		return DataManager.addEvents(eventDtos,getURIMapping("/events"));
	}
}
