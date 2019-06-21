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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;

/**
 * @author Peter Moser
 */
@RestController
@RequestMapping(value = "api/v2/")
@Api(value = "Hierarchy", produces = "application/json", tags = {"Hierarchy"})
@SwaggerDefinition(
	tags = {
		@Tag(
			name = "Hierarchy",
			description = "The response-JSON is a hierarchy of station-type -> station-name -> data-type -> measurements"
		)
	}
)
public class JsonController {

	private static final String DEFAULT_LIMIT = "200";
	private static final String DEFAULT_OFFSET = "0";
	private static final String DEFAULT_SHOWNULL = "false";

	private static DateTimeFormatter DATE_FORMAT = new DateTimeFormatterBuilder()
			.appendPattern("yyyy-MM-dd['T'[HH][:mm][:ss][.SSS]]")
			.parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
			.parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
			.parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
			.parseDefaulting(ChronoField.MILLI_OF_SECOND, 0)
			.toFormatter();


	@Autowired
	DataFetcher dataFetcher;

	@ApiOperation(value = "View a list of all station types (categories)")
	@GetMapping(value = "/", produces = "application/json")
	public @ResponseBody String requestStationTypes() {
		return new DataFetcher().fetchStationTypes();
	}

	@ApiOperation(
			value = "View details of all given station types",
			notes = "You can put multiple station types as comma-seperated list.")
	@GetMapping(value = "/{stationTypes}", produces = "application/json")
	public @ResponseBody String requestStations(@PathVariable String stationTypes,
											    @RequestParam(value="limit", required=false, defaultValue=DEFAULT_LIMIT) Long limit,
											    @RequestParam(value="offset", required=false, defaultValue=DEFAULT_OFFSET) Long offset,
											    @RequestParam(value="select", required=false) String select,
											    @RequestParam(value="where", required=false) String where,
												@RequestParam(value="shownull", required=false, defaultValue=DEFAULT_SHOWNULL) Boolean showNull) {
		return DataFetcher.serializeJSON(dataFetcher.fetchStations(stationTypes, limit, offset, select, "GUEST", !showNull, where));
	}

	@ApiOperation(
			value = "View details of all given station types including data types and most-recent measurements",
			notes = "You can put multiple station or data types as comma-seperated lists.")
	@GetMapping(value = "/{stationTypes}/{dataTypes}", produces = "application/json")
	public @ResponseBody String requestDataTypes(@PathVariable String stationTypes,
												 @PathVariable String dataTypes,
												 @RequestParam(value="limit", required=false, defaultValue=DEFAULT_LIMIT) Long limit,
												 @RequestParam(value="offset", required=false, defaultValue=DEFAULT_OFFSET) Long offset,
												 @RequestParam(value="select", required=false) String select,
												 @RequestParam(value="where", required=false) String where,
												 @RequestParam(value="shownull", required=false, defaultValue=DEFAULT_SHOWNULL) Boolean showNull) {
		return DataFetcher.serializeJSON(dataFetcher.fetchStationsTypesAndMeasurementHistory(stationTypes, dataTypes, limit, offset, select, "GUEST", !showNull, null, null, where));
	}

	@ApiOperation(
			value = "View details of all given station types including data types and historical measurements",
			notes = "You can put multiple station or data types as comma-seperated lists.")
	@GetMapping(value = "/{stationTypes}/{dataTypes}/{from}/{to}", produces = "application/json")
	public @ResponseBody String requestHistory(@PathVariable String stationTypes,
											   @PathVariable String dataTypes,
											   @PathVariable String from,
											   @PathVariable String to,
											   @RequestParam(value="limit", required=false, defaultValue=DEFAULT_LIMIT) Long limit,
											   @RequestParam(value="offset", required=false, defaultValue=DEFAULT_OFFSET) Long offset,
											   @RequestParam(value="select", required=false) String select,
											   @RequestParam(value="where", required=false) String where,
											   @RequestParam(value="shownull", required=false, defaultValue=DEFAULT_SHOWNULL) Boolean showNull) {

		LocalDateTime dateTimeFrom = LocalDateTime.from(DATE_FORMAT.parse(from));
		LocalDateTime dateTimeTo = LocalDateTime.from(DATE_FORMAT.parse(to));

		return DataFetcher.serializeJSON(dataFetcher.fetchStationsTypesAndMeasurementHistory(stationTypes, dataTypes, limit, offset, select, "GUEST", !showNull, dateTimeFrom, dateTimeTo, where));
	}
}
