package it.bz.idm.bdp.reader2;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.jsoniter.output.JsonStream;

import it.bz.idm.bdp.reader2.utils.querybuilder.QueryBuilder;
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
		ColumnMapRowMapper.setIgnoreNull(ignoreNull);

		nanoTime = System.nanoTime();
		List<Map<String, Object>> queryResult = QueryExecutor
				.init()
				.addParameters(query.getParameters())
				.build(query.getSql());

		log.debug(queryResult.toString());

		log.info("exec query: " + Long.toString((System.nanoTime() - nanoTime) / 1000000));

		List<String> hierarchy = new ArrayList<String>();
		hierarchy.add("_stationtype");
		hierarchy.add("_stationcode");

		return buildResultMaps(ignoreNull, queryResult, query.getSelectExpansion(), hierarchy);
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
		ColumnMapRowMapper.setIgnoreNull(ignoreNull);

		nanoTime = System.nanoTime();
		List<Map<String, Object>> queryResult = QueryExecutor
				.init()
				.addParameters(query.getParameters())
				.build(query.getSql());

		log.info("exec query: " + Long.toString((System.nanoTime() - nanoTime) / 1000000));

		List<String> hierarchy = new ArrayList<String>();
		hierarchy.add("_stationtype");
		hierarchy.add("_stationcode");
		hierarchy.add("_datatypename");

		return buildResultMaps(ignoreNull, queryResult, query.getSelectExpansion(), hierarchy);
	}

	@SuppressWarnings("unchecked")
	private static Map<String, Object> buildResultMaps(boolean ignoreNull, List<Map<String, Object>> queryResult, SelectExpansion se, List<String> hierarchy) {

		if (queryResult == null || queryResult.isEmpty()) {
			return new HashMap<String, Object>();
		}

		List<String> currValues = new ArrayList<String>();
		List<String> prevValues = new ArrayList<String>();

		for (int i = 0; i < hierarchy.size(); i++) {
			prevValues.add("");
		}

		Map<String, Object> stationTypes = new HashMap<String, Object>();
		Map<String, Object> stations = null;
		List<Object> datatypes = null;
		List<Object> measurements = null;

		Map<String, Object> stationType = null;
		Map<String, Object> station = null;
		Map<String, Object> parent = null;
		Map<String, Object> datatype = null;
		Map<String, Object> measurement = null;

		for (Map<String, Object> rec : queryResult) {

			currValues.clear();
			int i = 0;
			boolean levelSet = false;
			int renewLevel = hierarchy.size();
			for (String alias : hierarchy) {
				String value = (String) rec.get(alias);
				if (value == null) {
					throw new RuntimeException(alias + " not found in select. Unable to build hierarchy.");
				}
				currValues.add(value);
				if (!levelSet && !value.equals(prevValues.get(i))) {
					renewLevel = i;
					levelSet = true;
				}
				i++;
			}

			switch (renewLevel) {
				case 0:
					stationType = se.makeObj(rec, "stationtype", false);
					if (!stationType.isEmpty()) {
						stationTypes.put(currValues.get(0), stationType);
					}
				case 1:
					station = se.makeObj(rec, "station", ignoreNull);
					parent = se.makeObj(rec, "parent", ignoreNull);
					if (!parent.isEmpty()) {
						station.put("sparent", parent);
					}

					if (!station.isEmpty()) {
						stations = (Map<String, Object>) stationType.get("stations");
						if (stations == null) {
							stations = new HashMap<String, Object>();
							stationType.put("stations", stations);
						}
						stations.put(currValues.get(1), station);
					}
				case 2:
					datatype = se.makeObj(rec, "datatype", ignoreNull);
					if (!datatype.isEmpty()) {
						datatypes = (List<Object>) station.get("sdatatypes");
						if (datatypes == null) {
							datatypes = new ArrayList<Object>();
							station.put("sdatatypes", datatypes);
						}
						datatypes.add(datatype);
					}
				default:
					measurement = se.makeObj(rec, "measurement", ignoreNull);
					if (!measurement.isEmpty()) {
						measurements = (List<Object>) datatype.get("tmeasurements");
						if (measurements == null) {
							measurements = new ArrayList<Object>();
							datatype.put("tmeasurements", measurements);
						}
						measurements.add(measurement);
					}
			}

			prevValues.clear();
			prevValues.addAll(currValues);
		}
		return stationTypes;
	}

	public String fetchStationTypes() {
		return JsonStream.serialize(QueryExecutor
				.init()
				.build("select stationtype from station group by stationtype", String.class));
	}

	public static void main(String[] args) throws Exception {
		SelectExpansion se = new SelectExpansion();
		se.addColumn("measurement", "mvalidtime", "me.timestamp");
		se.addColumn("measurement", "mtransactiontime", "me.created_on");
		se.addColumn("measurement", "mperiod", "me.period");
		se.addColumn("measurement", "mvalue", "me.double_value");

		se.addColumn("datatype", "tname", "t.cname");
		se.addColumn("datatype", "tunit", "t.cunit");
		se.addColumn("datatype", "ttype", "t.rtype");
		se.addColumn("datatype", "tdescription", "t.description");
		se.addSubDef("datatype", "tmeasurements", "measurement");

		se.addColumn("parent", "pname", "p.name");
		se.addColumn("parent", "ptype", "p.stationtype");
		se.addColumn("parent", "pcoordinate", "p.pointprojection");
		se.addColumn("parent", "pcode", "p.stationcode");
		se.addColumn("parent", "porigin", "p.origin");
		se.addColumn("parent", "pmetadata", "pm.json");

		se.addColumn("station", "sname", "s.name");
		se.addColumn("station", "stype", "s.stationtype");
		se.addColumn("station", "scode", "s.stationcode");
		se.addColumn("station", "sorigin", "s.origin");
		se.addColumn("station", "sactive", "s.active");
		se.addColumn("station", "savailable", "s.available");
		se.addColumn("station", "scoordinate", "s.pointprojection");
		se.addColumn("station", "smetadata", "m.json");
		se.addSubDef("station", "sparent", "parent");
		se.addSubDef("station", "sdatatypes", "datatype");

		se.addSubDef("stationtype", "stations", "station");

		se.expand("*", "stationtype", "station", "parent", "datatype", "measurement");

		List<Map<String, Object>> queryResult = new ArrayList<>();
		Map<String, Object> rec1 = new HashMap<String, Object>();
		rec1.put("_stationtype", "parking");
		rec1.put("_stationcode", "walther-code");
		rec1.put("_datatypename", "occ1");
		rec1.put("stype", "parking");
		rec1.put("sname", "walther");
		rec1.put("pname", "bolzano1");
		rec1.put("tname", "o");
		rec1.put("mvalue", 1);
		queryResult.add(rec1);

		Map<String, Object> rec2 = new HashMap<String, Object>();
		rec2.put("_stationtype", "parking");
		rec2.put("_stationcode", "walther-code");
		rec2.put("_datatypename", "occ1");
		rec2.put("stype", "parking");
		rec2.put("sname", "walther");
		rec2.put("pname", "bolzano2");
		rec2.put("tname", "o");
		rec2.put("mvalue", 2);
		queryResult.add(rec2);

//		System.out.println(se.getExpansion());
//		System.out.println(se.getUsedAliases());
//		System.out.println(se.getUsedDefNames());
//		System.out.println(se.getWhereSql());

//		System.out.println(se.makeObjectOrEmptyMap(rec1, false, "stationtype").toString());

		List<String> hierarchy = new ArrayList<String>();
		hierarchy.add("_stationtype");
		hierarchy.add("_stationcode");
		hierarchy.add("_datatypename");

		System.out.println( JsonStream.serialize(buildResultMaps(true, queryResult, se, hierarchy)));
	}


}
