package it.bz.idm.bdp.reader2;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.jsoniter.output.JsonStream;

import it.bz.idm.bdp.reader2.utils.querybuilder.QueryBuilder;
import it.bz.idm.bdp.reader2.utils.querybuilder.SelectDefinition;
import it.bz.idm.bdp.reader2.utils.querybuilder.SelectExpansion;
import it.bz.idm.bdp.reader2.utils.queryexecutor.ColumnMapRowMapper;
import it.bz.idm.bdp.reader2.utils.queryexecutor.QueryExecutor;

@Component
public class DataFetcher {

	private static final Logger log = LoggerFactory.getLogger(DataFetcher.class);

	public Map<String, Object> fetchStations(String stationTypeList, long limit, long offset, String select, String role, boolean ignoreNull, String where) {

		log.info("FETCHING FROM STATIONS");

		Set<String> stationTypeSet = QueryBuilder.csvToSet(stationTypeList);

		long nanoTime = System.nanoTime();
		QueryBuilder query = QueryBuilder
				.init(select == null ? "*" : select, where, "station", "parent")
				.addSql("select s.stationtype as _stationtype, s.stationcode as _stationcode")
				.expandSelectPrefix(", ")
				.addSql("from station s")
				.addSqlIfAlias("left join metadata m on m.id = s.meta_data_id", "smetadata")
				.addSqlIfDefinition("left join station p on s.parent_id = p.id", "parent")
				.addSqlIfAlias("left join metadata pm on pm.id = p.meta_data_id", "pmetadata")
				.addSql("where true")
				.setParameterIfNotEmptyAnd("stationtypes", stationTypeSet, "AND s.stationtype in (:stationtypes)", !stationTypeSet.contains("*"))
				.expandWhere()
				.addSql("order by _stationtype, _stationcode")
				.addLimit(limit)
				.addOffset(offset);

		log.info(query.getSql());

		log.info("build query: " + Long.toString((System.nanoTime() - nanoTime) / 1000000));

		nanoTime = System.nanoTime();
		List<Map<String, Object>> queryResult = QueryExecutor
				.init()
				.addParameters(query.getParameters())
				.build(query.getSql());

		log.debug(queryResult.toString());

		log.info("exec query: " + Long.toString((System.nanoTime() - nanoTime) / 1000000));

		ColumnMapRowMapper.setIgnoreNull(ignoreNull);
		return buildResultMaps(ignoreNull, queryResult, query.getSelectExpansion());
	}

	public static String serializeJSON(Map<String, Object> resultMap) {
		long nanoTime = System.nanoTime();
		String serialize = JsonStream.serialize(resultMap);
		log.info("serialize json: " + Long.toString((System.nanoTime() - nanoTime) / 1000000));
		return serialize;
	}

	public Map<String, Object> fetchStationsTypesAndMeasurementHistory(String stationTypeList, String dataTypeList, long limit,
			long offset, String select, String role, boolean ignoreNull, LocalDateTime from, LocalDateTime to, String where) {
		if (from == null && to == null) {
			log.info("FETCHING FROM MEASUREMENT");
		} else {
			log.info("FETCHING FROM MEASUREMENTHISTORY");
		}
		Set<String> stationTypeSet = QueryBuilder.csvToSet(stationTypeList);
		Set<String> dataTypeSet = QueryBuilder.csvToSet(dataTypeList);

		long nanoTime = System.nanoTime();
		QueryBuilder query = QueryBuilder
				.init(select == null ? "*" : select, where, "station", "parent", "measurement", "datatype")
				.addSql("select s.stationtype as _stationtype, s.stationcode as _stationcode, t.cname as _datatypename")
				.expandSelectPrefix(", ")
				.addSqlIf("from measurementhistory me", from != null || to != null)
				.addSqlIf("from measurement me", from == null && to == null)
				.addSql("join bdppermissions pe on (",
								"(me.station_id = pe.station_id OR pe.station_id is null)",
								"AND (me.type_id = pe.type_id OR pe.type_id is null)",
								"AND (me.period = pe.period OR pe.period is null)",
								"AND pe.role_id = (select id from bdprole r where r.name = '" +
								(role == null ? role = "GUEST" : role) + "')",
							")",
						"join station s on me.station_id = s.id")
				.addSqlIfAlias("left join metadata m on m.id = s.meta_data_id", "smetadata")
				.addSqlIfDefinition("left join station p on s.parent_id = p.id", "parent")
				.addSqlIfAlias("left join metadata pm on pm.id = p.meta_data_id", "pmetadata")
				.addSql("join type t on me.type_id = t.id",
						"where true")
				.setParameterIfNotEmptyAnd("stationtypes", stationTypeSet, "and s.stationtype in (:stationtypes)", !stationTypeSet.contains("*"))
				.setParameterIfNotEmptyAnd("datatypes", dataTypeSet, "and t.cname in (:datatypes)", !dataTypeSet.contains("*"))
				.setParameterIfNotNull("from", from, "and timestamp >= :from")
				.setParameterIfNotNull("to", to, "and timestamp < :to")
				.expandWhere()
				.addSql("order by _stationtype, _stationcode, _datatypename")
				.addLimit(limit)
				.addOffset(offset);
		log.info("build query: " + Long.toString((System.nanoTime() - nanoTime) / 1000000));

		nanoTime = System.nanoTime();
		List<Map<String, Object>> queryResult = QueryExecutor
				.init()
				.addParameters(query.getParameters())
				.build(query.getSql());

		log.info("exec query: " + Long.toString((System.nanoTime() - nanoTime) / 1000000));

		ColumnMapRowMapper.setIgnoreNull(ignoreNull);
		return buildResultMaps(ignoreNull, queryResult, query.getSelectExpansion());
	}

	@SuppressWarnings("unchecked")
	private static Map<String, Object> buildResultMaps(boolean ignoreNull, List<Map<String, Object>> queryResult, SelectExpansion se) {

		if (queryResult == null || queryResult.isEmpty()) {
			return new HashMap<String, Object>();
		}

		long nanoTime;
		nanoTime = System.nanoTime();
		String stationTypePrev = "";
		String stationTypeAct = "";
		String stationCodePrev = "";
		String stationCodeAct = "";
		String datatypePrev = "";
		String datatypeAct = "";
		Map<String, Object> stationTypes = new HashMap<String, Object>();
		Map<String, Object> stations = null;
		List<Object> datatypes = null;
		List<Object> measurements = null;

		Map<String, Object> station = null;
		Map<String, Object> parent = null;
		Map<String, Object> datatype = null;
		Map<String, Object> measurement = null;

		int renewLevel = 3;
		for (Map<String, Object> rec : queryResult) {

			stationTypeAct = (String) rec.getOrDefault("_stationtype", "");
			stationCodeAct = (String) rec.getOrDefault("_stationcode", "");
			datatypeAct = (String) rec.getOrDefault("_datatypename", "");

			if (!stationTypeAct.equals(stationTypePrev)) {
				renewLevel = 3;
			} else if (!stationCodeAct.equals(stationCodePrev)) {
				renewLevel = 2;
			} else if (!datatypeAct.equals(datatypePrev)) {
				renewLevel = 1;
			} else {
				renewLevel = 0;
			}

			switch (renewLevel) {
				case 3:
					stationTypes.put(stationTypeAct, new HashMap<String, Object>());
				case 2:
					station = makeObjectOrEmptyMap(rec, se, ignoreNull, "station");
					parent = makeObjectOrNull(rec, se, ignoreNull, "parent");
					if (parent != null) {
						station.put("sparent", parent);
					}
					stations = (Map<String, Object>) stationTypes.get(stationTypeAct);
					stations.put(stationCodeAct, station);
					if (se.getUsedDefNames().contains("datatype") || se.getUsedDefNames().contains("measurement")) {
						station.put("sdatatypes", new ArrayList<Object>());
					} else {
						break;
					}
				case 1:
					datatype = makeObjectOrEmptyMap(rec, se, ignoreNull, "datatype");
					if (datatype.isEmpty() && !se.getUsedDefNames().contains("measurement")) {
						break;
					}
					datatypes = (List<Object>) ((Map<String, Object>) stations.get(stationCodeAct)).get("sdatatypes");
					datatypes.add(datatype);
					if (se.getUsedDefNames().contains("measurement")) {
						datatype.put("tmeasurements", new ArrayList<Object>());
					} else {
						break;
					}
				case 0:
					measurement = makeObjectOrNull(rec, se, ignoreNull, "measurement");
					if (measurement == null) {
						break;
					}
					measurements = (List<Object>) ((Map<String, Object>) datatypes.get(datatypes.size() - 1)).get("tmeasurements");
					measurements.add(measurement);
			}

			stationTypePrev = stationTypeAct;
			stationCodePrev = stationCodeAct;
			datatypePrev = datatypeAct;
		}
		log.info("build result map: " + Long.toString((System.nanoTime() - nanoTime) / 1000000));
		return stationTypes;
	}

	private static Map<String, Object> makeObjectOrNull(Map<String, Object> record, SelectExpansion se, boolean ignoreNull, Set<String> defNames) {
		Map<String, Object> result = makeObjectOrEmptyMap(record, se, ignoreNull, defNames);
		return result.isEmpty() ? null : result;
	}

	private static Map<String, Object> makeObjectOrNull(Map<String, Object> record, SelectExpansion se, boolean ignoreNull, String... defNames) {
		return makeObjectOrNull(record, se, ignoreNull, new HashSet<String>(Arrays.asList(defNames)));
	}

	private static Map<String, Object> makeObjectOrEmptyMap(Map<String, Object> record, SelectExpansion se, boolean ignoreNull, Set<String> defNames) {
		Map<String, Object> result = new HashMap<String, Object>();
		for (Entry<String, Object> e : record.entrySet()) {
			SelectDefinition def = se.getDefinition(e.getKey(), defNames);
			if (def != null) {
				if (def.isColumn(e.getKey())) {
					result.put(e.getKey(), e.getValue());
				} else {
					Map<String, Object> subObject = makeObjectOrNull(record, se, ignoreNull, defNames);
					if (subObject != null || !ignoreNull) {
						result.put(e.getKey(), subObject);
					}
				}
			}
		}
		return result;
	}

	private static Map<String, Object> makeObjectOrEmptyMap(Map<String, Object> record, SelectExpansion se, boolean ignoreNull, String... defNames) {
		return makeObjectOrEmptyMap(record, se, ignoreNull, new HashSet<String>(Arrays.asList(defNames)));
	}

	public String fetchStationTypes() {
		return JsonStream.serialize(QueryExecutor
				.init()
				.build("select stationtype from station group by stationtype", String.class));
	}



}
