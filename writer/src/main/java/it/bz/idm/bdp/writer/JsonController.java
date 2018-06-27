/**
 * Big data platform - Data Writer for the Big Data Platform, that writes changes to the database
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
package it.bz.idm.bdp.writer;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.StationDto;

@RequestMapping("/json")
@Controller
public class JsonController extends DataManager{
	@Override
	@RequestMapping(value = "/getDateOfLastRecord/{integreenTypology}/", method = RequestMethod.GET)
	public @ResponseBody Date getDateOfLastRecord(@PathVariable("integreenTypology") String stationType,@RequestParam("stationId") String stationId,
			@RequestParam(value="typeId") String typeId, @RequestParam(value="period",required=false) Integer period) {
		return (Date) super.getDateOfLastRecord(stationType, stationId, typeId, period);
	}

	@Override
	@RequestMapping(value = "/stationsWithoutMunicipality", method = RequestMethod.GET)
	public @ResponseBody List<StationDto> getStationsWithoutMunicipality() {
		return super.getStationsWithoutMunicipality();
	}

	@RequestMapping(value = "/pushRecords/{integreenTypology}", method = RequestMethod.POST)
	public @ResponseBody Object pushRecords(@RequestBody(required = true) DataMapDto<RecordDtoImpl> stationData,
			@PathVariable String integreenTypology) {
		return super.pushRecords(integreenTypology, stationData);
	}
	@Override
	@RequestMapping(value = "/patchStations", method = RequestMethod.POST)
	public @ResponseBody void patchStations(@RequestBody(required = true) List<StationDto> stations) {
		super.patchStations(stations);
	}
	@RequestMapping(value = "/syncStations/{integreenTypology}", method = RequestMethod.POST)
	public @ResponseBody Object syncStations(@RequestBody(required = true) List<StationDto> data,
			@PathVariable String integreenTypology) {
		return super.syncStations(integreenTypology, data);
	}

	@Override
	@RequestMapping(value = "/syncDataTypes", method = RequestMethod.POST)
	public @ResponseBody Object syncDataTypes(@RequestBody(required = true) List<DataTypeDto> data) {
		return super.syncDataTypes(data);
	}
}