/**
 * reader - Data Reader for the Big Data Platform, that queries the database for web-services
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
package it.bz.idm.bdp.reader2;

import java.sql.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Peter Moser
 */
@RestController
@RequestMapping(value = "api/v2/")
public class JsonController {

	@Autowired
    NamedParameterJdbcTemplate jdbcTemplate;

	@GetMapping(value = "/", produces = "application/json")
	public @ResponseBody String requestStationTypes() {
		return new DataFetcher().fetchStationTypes();
	}

	@GetMapping(value = "/{stationTypes}", produces = "application/json")
	public @ResponseBody String requestStations(@PathVariable String stationTypes,
											    @RequestParam(value="limit", required=false, defaultValue="100") Long limit,
											    @RequestParam(value="offset", required=false, defaultValue="0") Long offset,
											    @RequestParam(value="select", required=false) String select,
											    @RequestParam(value="where", required=false) String where,
												@RequestParam(value="shownull", required=false, defaultValue="false") Boolean showNull) {
		return DataFetcher.serializeJSON(new DataFetcher().fetchStations(stationTypes, limit, offset, select, "GUEST", !showNull, where));
	}

	@GetMapping(value = "/{stationTypes}/{dataTypes}", produces = "application/json")
	public @ResponseBody String requestDataTypes(@PathVariable String stationTypes,
												 @PathVariable String dataTypes,
												 @RequestParam(value="limit", required=false, defaultValue="100") Long limit,
												 @RequestParam(value="offset", required=false, defaultValue="0") Long offset,
												 @RequestParam(value="select", required=false) String select,
												 @RequestParam(value="where", required=false) String where,
												 @RequestParam(value="shownull", required=false, defaultValue="false") Boolean showNull) {
		return DataFetcher.serializeJSON(new DataFetcher().fetchStationsTypesAndMeasurements(stationTypes, dataTypes, limit, offset, select, "GUEST", !showNull, where));
	}

	@GetMapping(value = "/{stationTypes}/{dataTypes}/{from}/{to}", produces = "application/json")
	public @ResponseBody String requestHistory(@PathVariable String stationTypes,
												 @PathVariable String dataTypes,
												 @PathVariable Date from,
												 @PathVariable Date to,
												 @RequestParam(value="limit", required=false, defaultValue="100") Long limit,
												 @RequestParam(value="offset", required=false, defaultValue="0") Long offset,
												 @RequestParam(value="select", required=false) String select,
												 @RequestParam(value="where", required=false) String where,
												 @RequestParam(value="shownull", required=false, defaultValue="false") Boolean showNull) {
		return DataFetcher.serializeJSON(new DataFetcher().fetchStationsTypesAndMeasurementHistory(stationTypes, dataTypes, limit, offset, select, "GUEST", !showNull, from, to, where));
	}
}
