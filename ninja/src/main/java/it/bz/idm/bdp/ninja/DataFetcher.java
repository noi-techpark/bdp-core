package it.bz.idm.bdp.ninja;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.jsoniter.output.JsonStream;

import it.bz.idm.bdp.ninja.utils.miniparser.Token;
import it.bz.idm.bdp.ninja.utils.querybuilder.QueryBuilder;
import it.bz.idm.bdp.ninja.utils.querybuilder.Schema;
import it.bz.idm.bdp.ninja.utils.querybuilder.TargetDefList;
import it.bz.idm.bdp.ninja.utils.querybuilder.SelectExpansion;
import it.bz.idm.bdp.ninja.utils.querybuilder.TargetDef;
import it.bz.idm.bdp.ninja.utils.queryexecutor.ColumnMapRowMapper;
import it.bz.idm.bdp.ninja.utils.queryexecutor.QueryExecutor;
import it.bz.idm.bdp.ninja.utils.resultbuilder.ResultBuilder;
import it.bz.idm.bdp.ninja.utils.simpleexception.ErrorCodeInterface;
import it.bz.idm.bdp.ninja.utils.simpleexception.SimpleException;

@Component
public class DataFetcher {

	private static final Logger log = LoggerFactory.getLogger(DataFetcher.class);

	public static enum ErrorCode implements ErrorCodeInterface {
		WHERE_WRONG_DATA_TYPE ("'%s' can only be used with NULL, NUMBERS or STRINGS: '%s' given.");

		private final String msg;
		ErrorCode(String msg) {
			this.msg = msg;
		}

		@Override
		public String getMsg() {
			return "DATA FETCHING ERROR: " + msg;
		}
	}

	private QueryBuilder query;
	private long limit;
	private long offset;
	private List<String> roles;
	private boolean ignoreNull;
	private String select;
	private String where;
	private boolean distinct;

	public List<Map<String, Object>> fetchStations(String stationTypeList, boolean flat) {

		log.debug("FETCHING FROM STATIONS");

		Set<String> stationTypeSet = QueryBuilder.csvToSet(stationTypeList);

		long nanoTime = System.nanoTime();
		query = QueryBuilder
				.init(select, where, "station", "parent")
				.addSql("select")
				.addSqlIf("distinct", distinct)
				.addSqlIf("s.stationtype as _stationtype, s.stationcode as _stationcode", !flat)
				.expandSelectPrefix(", ", !flat)
				.addSql("from station s")
				.addSqlIfAlias("left join metadata m on m.id = s.meta_data_id", "smetadata")
				.addSqlIfDefinition("left join station p on s.parent_id = p.id", "parent")
				.addSqlIfAlias("left join metadata pm on pm.id = p.meta_data_id", "pmetadata")
				.addSql("where true")
				.setParameterIfNotEmptyAnd("stationtypes", stationTypeSet, "AND s.stationtype in (:stationtypes)", !stationTypeSet.contains("*"))
				.expandWhere()
				.expandGroupBy()
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
		query = QueryBuilder
				.init(select, where, "station", "parent", "measurementdouble", "measurement", "datatype");

		// FIXME Consider all possibilities and build a query with both mvalue types if strings and numbers are present
		List<Token> mvalueTokens = query.getSelectExpansion().getUsedAliasesInWhere().get("mvalue_double");
		Token mvalueToken = mvalueTokens == null ? null : mvalueTokens.get(0);
		boolean mvalueExists = mvalueToken != null;
		boolean hasFunctions = query.getSelectExpansion().hasFunctions();

		if (!mvalueExists || mvalueToken.is("number") || mvalueToken.is("null")) {
			query.addSql("select")
				 .addSqlIf("distinct", distinct)
				 .addSqlIf("s.stationtype as _stationtype, s.stationcode as _stationcode, t.cname as _datatypename", !flat)
				 .expandSelectPrefix(", ", !flat)
				 .addSqlIf("from measurementhistory me", from != null || to != null)
				 .addSqlIf("from measurement me", from == null && to == null)
				 .addSql("join bdppermissions pe on (",
						 "(me.station_id = pe.station_id OR pe.station_id is null)",
						 "AND (me.type_id = pe.type_id OR pe.type_id is null)",
						 "AND (me.period = pe.period OR pe.period is null)",
						 "AND pe.role_id in (select id from bdprole r where r.name in (:roles))",
						 ")",
						 "join station s on me.station_id = s.id")
				 .addSqlIfAlias("left join metadata m on m.id = s.meta_data_id", "smetadata")
				 .addSqlIfDefinition("left join station p on s.parent_id = p.id", "parent")
				 .addSqlIfAlias("left join metadata pm on pm.id = p.meta_data_id", "pmetadata")
				 .addSql("join type t on me.type_id = t.id")
				 .addSqlIfAlias("left join type_metadata tm on tm.id = t.meta_data_id", "tmetadata")
				 .addSql("where true")
				 .setParameterIfNotEmptyAnd("stationtypes", stationTypeSet, "and s.stationtype in (:stationtypes)", !stationTypeSet.contains("*"))
				 .setParameterIfNotEmptyAnd("datatypes", dataTypeSet, "and t.cname in (:datatypes)", !dataTypeSet.contains("*"))
				 .setParameterIfNotNull("from", from, "and timestamp >= :from")
				 .setParameterIfNotNull("to", to, "and timestamp < :to")
				 .setParameter("roles", roles)
				 .expandWhere()
				 .expandGroupBy();
		}

		if (!hasFunctions && (!mvalueExists || mvalueToken.is("null"))) {
			query.addSql("union all");
		}

		if (!hasFunctions && (!mvalueExists || mvalueToken.is("string") || mvalueToken.is("null"))) {
			query.reset(select, where, "station", "parent", "measurementstring", "measurement", "datatype")
				 .addSql("select")
				 .addSqlIf("distinct", distinct)
				 .addSqlIf("s.stationtype as _stationtype, s.stationcode as _stationcode, t.cname as _datatypename", !flat)
				 .expandSelectPrefix(", ", !flat)
				 .addSqlIf("from measurementstringhistory me", from != null || to != null)
				 .addSqlIf("from measurementstring me", from == null && to == null)
				 .addSql("join bdppermissions pe on (",
						 "(me.station_id = pe.station_id OR pe.station_id is null)",
						 "AND (me.type_id = pe.type_id OR pe.type_id is null)",
						 "AND (me.period = pe.period OR pe.period is null)",
						 "AND pe.role_id in (select id from bdprole r where r.name in (:roles))",
						 ")",
						 "join station s on me.station_id = s.id")
				 .addSqlIfAlias("left join metadata m on m.id = s.meta_data_id", "smetadata")
				 .addSqlIfDefinition("left join station p on s.parent_id = p.id", "parent")
				 .addSqlIfAlias("left join metadata pm on pm.id = p.meta_data_id", "pmetadata")
				 .addSql("join type t on me.type_id = t.id")
				 .addSqlIfAlias("left join type_metadata tm on tm.id = t.meta_data_id", "tmetadata")
				 .addSql("where true")
				 .setParameterIfNotEmptyAnd("stationtypes", stationTypeSet, "and s.stationtype in (:stationtypes)", !stationTypeSet.contains("*"))
				 .setParameterIfNotEmptyAnd("datatypes", dataTypeSet, "and t.cname in (:datatypes)", !dataTypeSet.contains("*"))
				 .setParameterIfNotNull("from", from, "and timestamp >= :from")
				 .setParameterIfNotNull("to", to, "and timestamp < :to")
				 .setParameter("roles", roles)
				 .expandWhere()
				 .expandGroupBy();
		}

		if (mvalueExists && !mvalueToken.is("string")
						 && !mvalueToken.is("number")
						 && !mvalueToken.is("null")) {
			throw new SimpleException(ErrorCode.WHERE_WRONG_DATA_TYPE, "mvalue", mvalueToken.getName());
		}

		query.addSqlIf("order by _stationtype, _stationcode, _datatypename", !flat)
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
				.build("select distinct stationtype from station order by 1", String.class));
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

	public void setRoles(List<String> roles) {
		if (roles == null) {
			roles = new ArrayList<String>();
			roles.add("GUEST");
		}
		this.roles = roles;
	}

	public void setIgnoreNull(boolean ignoreNull) {
		this.ignoreNull = ignoreNull;
	}

	public void setSelect(String select) {
		/* No need to check for null, since the QueryBuilder
		 * will handle this with a "SELECT * ..."
		 */
		this.select = select;
	}

	public void setWhere(String where) {
		this.where = where;
	}

	public void setDistinct(Boolean distinct) {
		this.distinct = distinct;
	}

	public static void main(String[] args) throws Exception {
		SelectExpansion se = new SelectExpansion();
		Schema schema = new Schema();

		TargetDefList measurement = TargetDefList.init("measurement")
				.add(new TargetDef("mvalidtime", "me.timestamp"))
				.add(new TargetDef("mtransactiontime", "me.created_on"))
				.add(new TargetDef("mperiod", "me.period"));

		schema.add(measurement);

		TargetDefList measurementdouble = TargetDefList.init("measurementdouble")
				.add(new TargetDef("mvalue_double", "me.double_value")
						.sqlAfter("null::character varying as mvalue_string").alias("mvalue"));

		schema.add(measurementdouble);

		TargetDefList measurementstring = TargetDefList.init("measurementstring")
				.add(new TargetDef("mvalue_string", "me.string_value")
						.sqlBefore("null::double precision as mvalue_double").alias("mvalue"));

		schema.add(measurementstring);

		TargetDefList datatype = TargetDefList.init("datatype")
				.add(new TargetDef("tname", "t.cname"))
				.add(new TargetDef("tunit", "t.cunit"))
				.add(new TargetDef("ttype", "t.rtype"))
				.add(new TargetDef("tdescription", "t.description"))
				.add(new TargetDef("tmetadata", "tm.json"))
				.add(new TargetDef("tmeasurements", measurement));

		schema.add(datatype);

		TargetDefList parent = TargetDefList.init("parent").add(new TargetDef("pname", "p.name"))
				.add(new TargetDef("ptype", "p.stationtype"))
				.add(new TargetDef("pcode", "p.stationcode"))
				.add(new TargetDef("porigin", "p.origin"))
				.add(new TargetDef("pactive", "p.active"))
				.add(new TargetDef("pavailable", "p.available"))
				.add(new TargetDef("pcoordinate", "p.pointprojection"))
				.add(new TargetDef("pmetadata", "pm.json"));

		schema.add(parent);

		TargetDefList station = TargetDefList.init("station").add(new TargetDef("sname", "s.name"))
				.add(new TargetDef("stype", "s.stationtype"))
				.add(new TargetDef("scode", "s.stationcode"))
				.add(new TargetDef("sorigin", "s.origin"))
				.add(new TargetDef("sactive", "s.active"))
				.add(new TargetDef("savailable", "s.available"))
				.add(new TargetDef("scoordinate", "s.pointprojection"))
				.add(new TargetDef("smetadata", "m.json"))
				.add(new TargetDef("sparent", parent))
				.add(new TargetDef("sdatatypes", datatype));

		schema.add(station);

		TargetDefList stationtype = TargetDefList.init("stationtype")
				.add(new TargetDef("stations", station));

		schema.add(stationtype);

		se.setSchema(schema);

		se.expand("*", "station", "parent", "measurementdouble");
		System.out.println(se.getExpansion());
		System.out.println(se.getUsedTargetNames());
		System.out.println(se.getUsedDefNames());
		System.out.println(se.getWhereSql());

		se.setWhereClause("");
		se.expand("*", "station", "parent", "measurementstring");
		System.out.println(se.getExpansion());
		System.out.println(se.getUsedTargetNames());
		System.out.println(se.getUsedDefNames());
		System.out.println(se.getWhereSql());

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

		System.out.println(se.getExpansion());
		System.out.println(se.getUsedTargetNames());
		System.out.println(se.getUsedDefNames());
		System.out.println(se.getWhereSql());

//		System.out.println(se.makeObjectOrEmptyMap(rec1, false, "stationtype").toString());

		List<String> hierarchy = new ArrayList<String>();
		hierarchy.add("_stationtype");
		hierarchy.add("_stationcode");
		hierarchy.add("_datatypename");

		System.out.println(JsonStream.serialize(ResultBuilder.build(true, queryResult, se.getSchema(), hierarchy)));
	}

}
