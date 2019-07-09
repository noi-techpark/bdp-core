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
package it.bz.idm.bdp.reader2.controller;

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
import io.swagger.annotations.ApiParam;
import it.bz.idm.bdp.reader2.DataFetcher;

/**
 * @author Peter Moser
 */
@RestController
@RequestMapping(value = "api/v2/hierarchy")
@Api(value = "Hierarchy", produces = "application/json")
public class HierarchyController {

	private static final String DOC_STATIONTYPES = "Station types or categories. Multiple types possible as comma-separated-values. All types with <code>*</code>.";
	private static final String DOC_DATATYPES = "Data types. Multiple types possible as comma-separated-values. All types with <code>*</code>.";
	private static final String DOC_LIMIT = "The limit of the response. Set it to -1 to disable it.";
	private static final String DOC_OFFSET = "The offset of the response list. To simulate pagination, together with limit.";
	private static final String DOC_SELECT = "Select JSON keys (a.k.a. column-aliases), which will be used to build the response. Multiple aliases possible as comma-separated-values. All of them start with a single character prefix, ex., <code>sname</code>.";
	private static final String DOC_WHERE = "Filter the result with filter-triples, like <code>alias.operator.value_or_list</code>" +
			"\n\n<code>values_or_list</code>\n" +
			" -   value: Whatever you want, also a regular expression. However, you need to escape <code>,</code> and <code>'</code> with a <code>\\\\</code>. Use url-encoded values, if your tool does not support certain characters.\n" +
			" -   list: <code>(value,value,value)</code>" +
			"\n\n<code>operator</code>\n" +
			" -   eq: Equal\n" +
			" -   neq: Not Equal\n" +
			" -   lt: Less Than\n" +
			" -   gt: Greater Than\n" +
			" -   lteq: Less Than Or Equal\n" +
			" -   gteq: Greater Than Or Equal\n" +
			" -   re: Regular Expression\n" +
			" -   ire: Insensitive Regular Expression\n" +
			" -   nre: Negated Regular Expression\n" +
			" -   nire: Negated Insensitive Regular Expression\n" +
			" -   bbi: Bounding box intersecting objects (ex., a street that is only partially covered by the box). Syntax? See below.\n" +
			" -   bbc: Bounding box containing objects (ex., a station or street, that is completely covered by the box). Syntax? See below.\n" +
			" -   in: True, if the value of the alias can be found within the given list. Example: name.in.(Peter,Patrick,Rudi)\n" +
			" -   and(alias.operator.value_or_list,...): Conjunction of filters (can be nested)\n" +
			" -   or(alias.operator.value_or_list,...): Disjunction of filters (can be nested)\n" +
			"\nMultiple conditions possible as comma-separated-values.\n\n" +
			" Example-syntax for bbi/bbc could be <code>coordinate.bbi.(11,46,12,47,4326)</code>, where the ordering inside the list is left-x, left-y, right-x, right-y and SRID (optional).";
	private static final String DOC_SHOWNULL = "Should JSON keys with null-values be returned, or removed from the response-JSON.";
	private static final String DOC_TIME = "Date or date-time format, that forms a half-open interval [from, to). The format is <code>yyyy-MM-dd['T'[HH][:mm][:ss][.SSS]]</code>, where [] denotes optionality.";

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
			notes = "You can put multiple station types as comma-seperated list.<br>The response is a hierarchy of <code>station-type / station-name</code>."
			)
	@GetMapping(value = "/{stationTypes}", produces = "application/json")
	public @ResponseBody String requestStations(@ApiParam(value=DOC_STATIONTYPES, defaultValue="*") @PathVariable String stationTypes,
											    @ApiParam(value=DOC_LIMIT) @RequestParam(value="limit", required=false, defaultValue=DEFAULT_LIMIT) Long limit,
											    @ApiParam(value=DOC_OFFSET) @RequestParam(value="offset", required=false, defaultValue=DEFAULT_OFFSET) Long offset,
											    @ApiParam(value=DOC_SELECT) @RequestParam(value="select", required=false) String select,
											    @ApiParam(value=DOC_WHERE) @RequestParam(value="where", required=false) String where,
											    @ApiParam(value=DOC_SHOWNULL) @RequestParam(value="shownull", required=false, defaultValue=DEFAULT_SHOWNULL) Boolean showNull) {
		return DataFetcher.serializeJSON(dataFetcher.fetchStations(stationTypes, limit, offset, select, "GUEST", !showNull, where));
	}

	@ApiOperation(
			value = "View details of all given station types including data types and most-recent measurements",
			notes = "You can put multiple station or data types as comma-seperated lists.<br>The response is a hierarchy of <code>station-type / station-name / data-type / measurements</code>.")
	@GetMapping(value = "/{stationTypes}/{dataTypes}", produces = "application/json")
	public @ResponseBody String requestDataTypes(@ApiParam(value=DOC_STATIONTYPES, defaultValue="*") @PathVariable String stationTypes,
												 @ApiParam(value=DOC_DATATYPES, defaultValue="*") @PathVariable String dataTypes,
												 @ApiParam(value=DOC_LIMIT) @RequestParam(value="limit", required=false, defaultValue=DEFAULT_LIMIT) Long limit,
												 @ApiParam(value=DOC_OFFSET) @RequestParam(value="offset", required=false, defaultValue=DEFAULT_OFFSET) Long offset,
												 @ApiParam(value=DOC_SELECT) @RequestParam(value="select", required=false) String select,
												 @ApiParam(value=DOC_WHERE) @RequestParam(value="where", required=false) String where,
												 @ApiParam(value=DOC_SHOWNULL) @RequestParam(value="shownull", required=false, defaultValue=DEFAULT_SHOWNULL) Boolean showNull) {
		return DataFetcher.serializeJSON(dataFetcher.fetchStationsTypesAndMeasurementHistory(stationTypes, dataTypes, limit, offset, select, "GUEST", !showNull, null, null, where));
	}

	@ApiOperation(
			value = "View details of all given station types including data types and historical measurements",
			notes = "You can put multiple station or data types as comma-seperated lists.<br>The response is a hierarchy of <code>station-type / station-name / data-type / measurements</code>.")
	@GetMapping(value = "/{stationTypes}/{dataTypes}/{from}/{to}", produces = "application/json")
	public @ResponseBody String requestHistory(@ApiParam(value=DOC_STATIONTYPES, defaultValue="*") @PathVariable String stationTypes,
											   @ApiParam(value=DOC_DATATYPES, defaultValue="*") @PathVariable String dataTypes,
											   @ApiParam(value=DOC_TIME) @PathVariable String from,
											   @ApiParam(value=DOC_TIME) @PathVariable String to,
											   @ApiParam(value=DOC_LIMIT) @RequestParam(value="limit", required=false, defaultValue=DEFAULT_LIMIT) Long limit,
											   @ApiParam(value=DOC_OFFSET) @RequestParam(value="offset", required=false, defaultValue=DEFAULT_OFFSET) Long offset,
											   @ApiParam(value=DOC_SELECT) @RequestParam(value="select", required=false) String select,
											   @ApiParam(value=DOC_WHERE) @RequestParam(value="where", required=false) String where,
											   @ApiParam(value=DOC_SHOWNULL) @RequestParam(value="shownull", required=false, defaultValue=DEFAULT_SHOWNULL) Boolean showNull) {

		LocalDateTime dateTimeFrom = LocalDateTime.from(DATE_FORMAT.parse(from));
		LocalDateTime dateTimeTo = LocalDateTime.from(DATE_FORMAT.parse(to));

		return DataFetcher.serializeJSON(dataFetcher.fetchStationsTypesAndMeasurementHistory(stationTypes, dataTypes, limit, offset, select, "GUEST", !showNull, dateTimeFrom, dateTimeTo, where));
	}
}
