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
import it.bz.idm.bdp.reader2.utils.resultbuilder.ResultBuilder;

@Component
public class DataFetcher {

	private static final Logger log = LoggerFactory.getLogger(DataFetcher.class);

	private QueryBuilder query;
	private long limit;
	private long offset;
	private String role;
	private boolean ignoreNull;
	private String select;
	private String where;


	public List<Map<String, Object>> fetchStations(String stationTypeList, boolean flat) {

		log.debug("FETCHING FROM STATIONS");

		Set<String> stationTypeSet = QueryBuilder.csvToSet(stationTypeList);

		long nanoTime = System.nanoTime();
		query = QueryBuilder
				.init(select == null ? "*" : select, where, "station", "parent")
				.addSql("select distinct")
				.addSqlIf("s.stationtype as _stationtype, s.stationcode as _stationcode", !flat)
				.expandSelectPrefix(", ", !flat)
				.addSql("from station s")
				.addSqlIfAlias("left join metadata m on m.id = s.meta_data_id", "smetadata")
				.addSqlIfDefinition("left join station p on s.parent_id = p.id", "parent")
				.addSqlIfAlias("left join metadata pm on pm.id = p.meta_data_id", "pmetadata")
				.addSql("where true")
				.setParameterIfNotEmptyAnd("stationtypes", stationTypeSet, "AND s.stationtype in (:stationtypes)", !stationTypeSet.contains("*"))
				.expandWhere()
				.addSqlIf("order by _stationtype, _stationcode", !flat)
				.addLimit(limit)
				.addOffset(offset);

		log.debug(query.getSql());

		log.debug("build query: " + Long.toString((System.nanoTime() - nanoTime) / 1000000));
		ColumnMapRowMapper.setIgnoreNull(ignoreNull);

		nanoTime = System.nanoTime();
		List<Map<String, Object>> queryResult = QueryExecutor
				.init()
				.addParameters(query.getParameters())
				.build(query.getSql());

		log.debug(queryResult.toString());

		log.debug("exec query: " + Long.toString((System.nanoTime() - nanoTime) / 1000000));

		return queryResult;
	}

	public static String serializeJSON(Object whatever) {
		long nanoTime = System.nanoTime();
		String serialize = JsonStream.serialize(whatever);
		log.debug("serialize json: " + Long.toString((System.nanoTime() - nanoTime) / 1000000));
		return serialize;
	}

	public List<Map<String, Object>> fetchStationsTypesAndMeasurementHistory(String stationTypeList, String dataTypeList, LocalDateTime from, LocalDateTime to, boolean flat) {
		if (from == null && to == null) {
			log.debug("FETCHING FROM MEASUREMENT");
		} else {
			log.debug("FETCHING FROM MEASUREMENTHISTORY");
		}
		Set<String> stationTypeSet = QueryBuilder.csvToSet(stationTypeList);
		Set<String> dataTypeSet = QueryBuilder.csvToSet(dataTypeList);

		long nanoTime = System.nanoTime();
		QueryBuilder query = QueryBuilder
				.init(select == null ? "*" : select, where, "station", "parent", "measurement", "datatype")
				.addSql("select distinct")
				.addSqlIf("s.stationtype as _stationtype, s.stationcode as _stationcode, t.cname as _datatypename", !flat)
				.expandSelectPrefix(", ", !flat)
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
				.addSqlIf("order by _stationtype, _stationcode, _datatypename", !flat)
				.addLimit(limit)
				.addOffset(offset);
		log.debug("build query: " + Long.toString((System.nanoTime() - nanoTime) / 1000000));
		ColumnMapRowMapper.setIgnoreNull(ignoreNull);

		nanoTime = System.nanoTime();
		List<Map<String, Object>> queryResult = QueryExecutor
				.init()
				.addParameters(query.getParameters())
				.build(query.getSql());

		log.debug("exec query: " + Long.toString((System.nanoTime() - nanoTime) / 1000000));

		return queryResult;
	}

	public String fetchStationTypes() {
		return JsonStream.serialize(QueryExecutor
				.init()
				.build("select stationtype from station group by stationtype order by 1", String.class));
	}

	public QueryBuilder getQuery() {
		return query;
	}

	public void setLimit(long limit) {
		this.limit = limit;
	}

	public void setOffset(long offset) {
		this.offset = offset;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public void setIgnoreNull(boolean ignoreNull) {
		this.ignoreNull = ignoreNull;
	}

	public void setSelect(String select) {
		this.select = select;
	}

	public void setWhere(String where) {
		this.where = where;
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

		System.out.println( JsonStream.serialize(ResultBuilder.build(true, queryResult, se, hierarchy)));
	}


}
