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
package it.bz.idm.bdp.ninja.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import it.bz.idm.bdp.ninja.DataFetcher;
import it.bz.idm.bdp.ninja.security.SecurityUtils;
import it.bz.idm.bdp.ninja.utils.resultbuilder.ResultBuilder;
import it.bz.idm.bdp.ninja.utils.simpleexception.ErrorCodeInterface;
import it.bz.idm.bdp.ninja.utils.simpleexception.SimpleException;

/**
 * @author Peter Moser
 */
@RestController
@RequestMapping(value = "api")
@Api(value = "Data", produces = "application/json")
public class DataController {

	private static final String DATETIME_FORMAT_PATTERN = "yyyy-MM-dd['T'[HH][:mm][:ss][.SSS]][Z]";

	public static enum ErrorCode implements ErrorCodeInterface {
		WRONG_REPRESENTATION("Please choose 'flat' or 'tree' as representation. '%s' is not allowed."),
		DATE_PARSE_ERROR("Invalid date given. Format must be %s, where [] denotes optionality. Do not forget, single digits must be leaded by 0. Error message: %s.");

		private final String msg;

		ErrorCode(final String msg) {
			this.msg = msg;
		}

		@Override
		public String getMsg() {
			return "PARSING ERROR: " + msg;
		}
	}

	private static final String DOC_DISTINCT = "Remove duplicate entries.";
	private static final String DOC_REPRESENTATION = "Do you want to have the result in a <code>tree</code> or <code>flat</code> representation.";
	private static final String DOC_STATIONTYPES = "Station types or categories. Multiple types possible as comma-separated-values. All types with <code>*</code>.";
	private static final String DOC_DATATYPES = "Data types. Multiple types possible as comma-separated-values. All types with <code>*</code>.";
	private static final String DOC_LIMIT = "The limit of the response. Set it to -1 to disable it.";
	private static final String DOC_OFFSET = "The offset of the response list. To simulate pagination, together with limit.";
	private static final String DOC_SELECT = "Select <code>aliases</code>, which will be used to build the response. Multiple aliases possible as comma-separated-values. Example: <code>sname</code> or <code>smetadata.city.cap</code> for JSON. Functions can be set as <code>func(alias)</code> (Functions with JSON are not supported yet)";
	private static final String DOC_WHERE = "Filter the result with filter-triples, like <code>alias.operator.value_or_list</code>"
			+ "\n\n<code>values_or_list</code>\n"
			+ " -   value: Whatever you want, also a regular expression. However, you need to escape <code>,'\"</code> with a <code>\\\\</code>. Use url-encoded values, if your tool does not support certain characters.\n"
			+ " -   list: <code>(value,value,value)</code>" + "\n\n<code>operator</code>\n" + " -   eq: Equal\n"
			+ " -   neq: Not Equal\n" + " -   lt: Less Than\n" + " -   gt: Greater Than\n"
			+ " -   lteq: Less Than Or Equal\n" + " -   gteq: Greater Than Or Equal\n" + " -   re: Regular Expression\n"
			+ " -   ire: Insensitive Regular Expression\n" + " -   nre: Negated Regular Expression\n"
			+ " -   nire: Negated Insensitive Regular Expression\n"
			+ " -   bbi: Bounding box intersecting objects (ex., a street that is only partially covered by the box). Syntax? See below.\n"
			+ " -   bbc: Bounding box containing objects (ex., a station or street, that is completely covered by the box). Syntax? See below.\n"
			+ " -   in: True, if the value of the alias can be found within the given list. Example: name.in.(Peter,Patrick,Rudi)\n"
			+ " -   nin: False, if the value of the alias can be found within the given list. Example: name.nin.(Peter,Patrick,Rudi)\n"
			+ "\n\n<code>logical operations</code>\n"
			+ " -   and(alias.operator.value_or_list,...): Conjunction of filters (can be nested)\n"
			+ " -   or(alias.operator.value_or_list,...): Disjunction of filters (can be nested)\n"
			+ "\nMultiple conditions possible as comma-separated-values. <code>value</code>s will be casted to Double precision or <code>null</code>, if possible. Put them inside double quotes, if you want to prevent that.\n\n"
			+ " Example-syntax for bbi/bbc could be <code>coordinate.bbi.(11,46,12,47,4326)</code>, where the ordering inside the list is left-x, left-y, right-x, right-y and SRID (optional).";
	private static final String DOC_SHOWNULL = "Should JSON keys with null-values be returned, or removed from the response-JSON.";
	private static final String DOC_TIME = "Date or date-time format, that forms a half-open interval [from, to). The format is <code>" + DATETIME_FORMAT_PATTERN.toString().replace("'", "") + "</code>, where [] denotes optionality.";

	private static final String DEFAULT_LIMIT = "200";
	private static final String DEFAULT_OFFSET = "0";
	private static final String DEFAULT_SHOWNULL = "false";
	private static final String DEFAULT_DISTINCT = "true";
	private static final String DEFAULT_REPRESENTATION = "flat";

	private static final List<String> TREE_PARTIAL = new ArrayList<String>() {
		private static final long serialVersionUID = -1699134802805589710L;
		{
			add("_stationtype");
			add("_stationcode");
		}
	};

	private static final List<String> TREE_FULL = new ArrayList<String>() {
		private static final long serialVersionUID = -1699134802805589710L;
		{
			addAll(TREE_PARTIAL);
			add("_datatypename");
		}
	};

	private static DateTimeFormatter DATE_FORMAT = new DateTimeFormatterBuilder().appendPattern(DATETIME_FORMAT_PATTERN)
			.parseDefaulting(ChronoField.HOUR_OF_DAY, 0).parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
			.parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0).parseDefaulting(ChronoField.NANO_OF_SECOND, 0)
			.toFormatter();

	protected static LocalDateTime getDateTime(final String dateString) {
		try {
			return LocalDateTime.from(DATE_FORMAT.parse(dateString));
		} catch (final DateTimeParseException e) {
			throw new SimpleException(ErrorCode.DATE_PARSE_ERROR, DATETIME_FORMAT_PATTERN.toString().replace("'", ""),
					e.getMessage());
		}
	}

	@Autowired
	DataFetcher dataFetcher;

	@ApiOperation(value = "View a list of all station types (categories)")
	@GetMapping(value = "", produces = "application/json")
	public @ResponseBody String requestStationTypes() {
		return new DataFetcher().fetchStationTypes();
	}

	@ApiOperation(value = "View details of all given station types", notes = "You can put multiple station types as comma-seperated list.<br>The response is a tree of <code>station-type / station-name</code>.")
	@GetMapping(value = "/{representation}/{stationTypes}", produces = "application/json")
	public @ResponseBody String requestStations(
			@ApiParam(value = DOC_REPRESENTATION, defaultValue = DEFAULT_REPRESENTATION) @PathVariable final String representation,
			@ApiParam(value = DOC_STATIONTYPES, defaultValue = "*") @PathVariable final String stationTypes,
			@ApiParam(value = DOC_LIMIT) @RequestParam(value = "limit", required = false, defaultValue = DEFAULT_LIMIT) final Long limit,
			@ApiParam(value = DOC_OFFSET) @RequestParam(value = "offset", required = false, defaultValue = DEFAULT_OFFSET) final Long offset,
			@ApiParam(value = DOC_SELECT) @RequestParam(value = "select", required = false) final String select,
			@ApiParam(value = DOC_WHERE) @RequestParam(value = "where", required = false) final String where,
			@ApiParam(value = DOC_SHOWNULL) @RequestParam(value = "shownull", required = false, defaultValue = DEFAULT_SHOWNULL) final Boolean showNull,
			@ApiParam(value = DOC_DISTINCT) @RequestParam(value = "distinct", required = false, defaultValue = DEFAULT_DISTINCT) final Boolean distinct) {

		final boolean flat = isFlatRepresentation(representation);

		dataFetcher.setIgnoreNull(!showNull);
		dataFetcher.setLimit(limit);
		dataFetcher.setOffset(offset);
		dataFetcher.setWhere(where);
		dataFetcher.setSelect(select);
		dataFetcher.setDistinct(distinct);

		final List<Map<String, Object>> queryResult = dataFetcher.fetchStations(stationTypes, flat);
		final Map<String, Object> result = buildResult(queryResult, offset, limit, flat, showNull, TREE_PARTIAL);
		return DataFetcher.serializeJSON(result);
	}

	@ApiOperation(value = "View details of all given station types including data types and most-recent measurements", notes = "You can put multiple station or data types as comma-seperated lists.<br>The response is a tree of <code>station-type / station-name / data-type / measurements</code>.")
	@GetMapping(value = "/{representation}/{stationTypes}/{dataTypes}", produces = "application/json")
	public @ResponseBody String requestDataTypes(
			@ApiParam(value = DOC_REPRESENTATION, defaultValue = DEFAULT_REPRESENTATION) @PathVariable final String representation,
			@ApiParam(value = DOC_STATIONTYPES, defaultValue = "*") @PathVariable final String stationTypes,
			@ApiParam(value = DOC_DATATYPES, defaultValue = "*") @PathVariable final String dataTypes,
			@ApiParam(value = DOC_LIMIT) @RequestParam(value = "limit", required = false, defaultValue = DEFAULT_LIMIT) final Long limit,
			@ApiParam(value = DOC_OFFSET) @RequestParam(value = "offset", required = false, defaultValue = DEFAULT_OFFSET) final Long offset,
			@ApiParam(value = DOC_SELECT) @RequestParam(value = "select", required = false) final String select,
			@ApiParam(value = DOC_WHERE) @RequestParam(value = "where", required = false) final String where,
			@ApiParam(value = DOC_SHOWNULL) @RequestParam(value = "shownull", required = false, defaultValue = DEFAULT_SHOWNULL) final Boolean showNull,
			@ApiParam(value = DOC_DISTINCT) @RequestParam(value = "distinct", required = false, defaultValue = DEFAULT_DISTINCT) final Boolean distinct) {

		final boolean flat = isFlatRepresentation(representation);

		final Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		dataFetcher.setIgnoreNull(!showNull);
		dataFetcher.setLimit(limit);
		dataFetcher.setOffset(offset);
		dataFetcher.setWhere(where);
		dataFetcher.setSelect(select);
		dataFetcher.setRoles(SecurityUtils.getRolesFromAuthentication(auth));
		dataFetcher.setDistinct(distinct);

		final List<Map<String, Object>> queryResult = dataFetcher.fetchStationsTypesAndMeasurementHistory(stationTypes,
				dataTypes, null, null, flat);
		final Map<String, Object> result = buildResult(queryResult, offset, limit, flat, showNull, TREE_FULL);
		return DataFetcher.serializeJSON(result);
	}

	@ApiOperation(value = "View details of all given station types including data types and historical measurements", notes = "You can put multiple station or data types as comma-seperated lists.<br>The response is a tree of <code>station-type / station-name / data-type / measurements</code>.")
	@GetMapping(value = "/{representation}/{stationTypes}/{dataTypes}/{from}/{to}", produces = "application/json")
	public @ResponseBody String requestHistory(
			@ApiParam(value = DOC_REPRESENTATION, defaultValue = DEFAULT_REPRESENTATION) @PathVariable final String representation,
			@ApiParam(value = DOC_STATIONTYPES, defaultValue = "*") @PathVariable final String stationTypes,
			@ApiParam(value = DOC_DATATYPES, defaultValue = "*") @PathVariable final String dataTypes,
			@ApiParam(value = DOC_TIME) @PathVariable final String from,
			@ApiParam(value = DOC_TIME) @PathVariable final String to,
			@ApiParam(value = DOC_LIMIT) @RequestParam(value = "limit", required = false, defaultValue = DEFAULT_LIMIT) final Long limit,
			@ApiParam(value = DOC_OFFSET) @RequestParam(value = "offset", required = false, defaultValue = DEFAULT_OFFSET) final Long offset,
			@ApiParam(value = DOC_SELECT) @RequestParam(value = "select", required = false) final String select,
			@ApiParam(value = DOC_WHERE) @RequestParam(value = "where", required = false) final String where,
			@ApiParam(value = DOC_SHOWNULL) @RequestParam(value = "shownull", required = false, defaultValue = DEFAULT_SHOWNULL) final Boolean showNull,
			@ApiParam(value = DOC_DISTINCT) @RequestParam(value = "distinct", required = false, defaultValue = DEFAULT_DISTINCT) final Boolean distinct) {

		final boolean flat = isFlatRepresentation(representation);

		final Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		final LocalDateTime dateTimeFrom = getDateTime(from);
		final LocalDateTime dateTimeTo = getDateTime(to);

		dataFetcher.setIgnoreNull(!showNull);
		dataFetcher.setLimit(limit);
		dataFetcher.setOffset(offset);
		dataFetcher.setWhere(where);
		dataFetcher.setSelect(select);
		dataFetcher.setRoles(SecurityUtils.getRolesFromAuthentication(auth));
		dataFetcher.setDistinct(distinct);

		final List<Map<String, Object>> queryResult = dataFetcher.fetchStationsTypesAndMeasurementHistory(stationTypes,
				dataTypes, dateTimeFrom, dateTimeTo, flat);
		final Map<String, Object> result = buildResult(queryResult, offset, limit, flat, showNull, TREE_FULL);
		return DataFetcher.serializeJSON(result);
	}

	private boolean isFlatRepresentation(final String representation) {
		if (representation.equalsIgnoreCase("flat")) {
			return true;
		}
		if (representation.equalsIgnoreCase("tree")) {
			return false;
		}
		throw new SimpleException(ErrorCode.WRONG_REPRESENTATION, representation);
	}

	private Map<String, Object> buildResult(final List<Map<String, Object>> queryResult, final long offset,
			final long limit, final boolean flat, final boolean showNull, final List<String> tree) {
		final Map<String, Object> result = new HashMap<String, Object>();
		result.put("offset", offset);
		result.put("limit", limit);

		if (flat) {
			replaceMixedValueKeys(queryResult);
			result.put("data", queryResult);
		} else {
			result.put("data",
					ResultBuilder.build(!showNull, queryResult, dataFetcher.getQuery().getSelectExpansion(), tree));
		}
		return result;
	}

	/**
	 * Depending whether we get a string or double measurement, we have different
	 * columns in our record due to an UNION ALL query. We unify these two fields
	 * into a single "mvalue" to hide internals from the API consumers. XXX This
	 * could later maybe be integrated into select expansion or in a generic way
	 * into the result builder.
	 */
	private void replaceMixedValueKeys(final List<Map<String, Object>> queryResult) {
		for (final Map<String, Object> row : queryResult) {
			Object value = row.remove("mvalue_string");
			if (value == null) {
				value = row.remove("mvalue_double");
			}
			if (value == null) {
				break;
			} else {
				row.put("mvalue", value);
			}
		}
	}
}
