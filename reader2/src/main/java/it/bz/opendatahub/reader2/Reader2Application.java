package it.bz.opendatahub.reader2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.jsoniter.output.JsonStream;

import it.bz.opendatahub.reader2.utils.ColumnMapRowMapper;
import it.bz.opendatahub.reader2.utils.JsonIterPostgresSupport;
import it.bz.opendatahub.reader2.utils.JsonIterSqlTimestampSupport;
import it.bz.opendatahub.reader2.utils.QueryBuilder;

@SpringBootApplication
public class Reader2Application implements CommandLineRunner {

	private static final Logger log = LoggerFactory.getLogger(Reader2Application.class);

	@Autowired
    NamedParameterJdbcTemplate jdbcTemplate;

	public static void main(String[] args) {
		SpringApplication.run(Reader2Application.class, args);
	}

	public String fetchStationTypes() {
		return QueryBuilder
				.init()
				.addSql("select stationtype from station group by stationtype")
				.buildJson(String.class);
	}

	final static Map<String, Object> COLUMN_EXPANSION_MEASUREMENT = new HashMap<String, Object>() {
		private static final long serialVersionUID = 1L;
		{
			put("mvalidtime", "me.timestamp");
			put("mtransactiontime", "me.created_on");
			put("mperiod", "me.period");
			put("mvalue", "me.double_value");
		}
	};

	final static Map<String, Object> COLUMN_EXPANSION_DATATYPE = new HashMap<String, Object>() {
		private static final long serialVersionUID = 1L;
		{
			put("tname", "t.cname");
			put("tunit", "t.cunit");
			put("ttype", "t.rtype");
			put("tdescription", "t.description");
			put("tlastmeasurement", COLUMN_EXPANSION_MEASUREMENT);
		}
	};

	final static Map<String, Object> COLUMN_EXPANSION_STATION = new HashMap<String, Object>() {
		private static final long serialVersionUID = 1L;
		{
			put("sname", "s.name");
			put("sorigin", "s.origin");
			put("scoordinate", "s.pointprojection");
			put("stype", "s.stationtype");
			put("sparent", "p.stationcode");
			put("smetadata", "m.json");
			put("sdatatypes", COLUMN_EXPANSION_DATATYPE);
		}
	};

	final static Map<String, Object> COLUMN_EXPANSION = new HashMap<String, Object>() {
		private static final long serialVersionUID = 1L;
		{
			putAll(COLUMN_EXPANSION_STATION);
			putAll(COLUMN_EXPANSION_DATATYPE);
			putAll(COLUMN_EXPANSION_MEASUREMENT);
		}
	};

	@SuppressWarnings("unchecked")
	public String fetchStations(String stationTypeList, long limit, long offset, String select) {
		Set<String> stationTypeSet = QueryBuilder.csvToSet(stationTypeList);

		QueryBuilder query = QueryBuilder
				.init()
				.addSql("select s.stationtype as _stationtype, s.stationcode as _stationcode, ")
				.expandSelect(select, COLUMN_EXPANSION, "")
				.addSql("from station s",
						"left join metadata m on m.station_id = s.id",
						"left join station p on s.parent_id = p.id",
						"where true")
				.setParameterIfNotEmptyAnd("stationtypes", stationTypeSet,
						"and s.stationtype in (:stationtypes)", !stationTypeSet.contains("*"))
				.addSql("order by _stationtype, _stationcode")
				.addLimit(limit)
				.addOffset(offset);

		List<Map<String, Object>> queryResult = null;

		try {
			queryResult = query.build();
		} catch (Exception e) {
			e.printStackTrace();
		}

		Map<String, Object> result = new HashMap<String, Object>();

		String stationTypePrev = "";
		for (Map<String, Object> rec : queryResult) {
			String stationTypeAct = (String) rec.getOrDefault("_stationtype", "");
			if (! stationTypePrev.equalsIgnoreCase(stationTypeAct)) {
				result.put(stationTypeAct, null);
				stationTypePrev = stationTypeAct;
				result.put(stationTypeAct, new ArrayList<Object>());
			}
			rec.remove("_stationtype");
			rec.remove("_stationcode");

			((List<Map<String, ?>>) result.get(stationTypeAct)).add(rec);
		}

		return JsonStream.serialize(result);
	}

	public String fetchStationsAndTypes(String stationTypeList, String dataTypeList, long limit, long offset, String select, String role) {
		Set<String> stationTypeSet = QueryBuilder.csvToSet(stationTypeList);
		Set<String> dataTypeSet = QueryBuilder.csvToSet(dataTypeList);
		List<Map<String, Object>> queryResult = QueryBuilder
				.init()
				.addSql("select s.stationtype as _stationtype, s.stationcode as _stationcode, t.cname as _datatypename, ")
				.expandSelect(select, COLUMN_EXPANSION, "")
				.addSql("from measurement me",
						"join bdppermissions pe on (",
								"(me.station_id = pe.station_id OR pe.station_id is null)",
								"AND (me.type_id = pe.type_id OR pe.type_id is null)",
								"AND (me.period = pe.period OR pe.period is null)",
								"AND pe.role_id = (select id from bdprole r where r.name = '" +
								(role == null ? role = "GUEST" : role) + "')",
							")",
						"join station s on me.station_id = s.id",
						"left join metadata m on m.id = s.meta_data_id",
						"left join station p on s.parent_id = p.id",
						"join type t on me.type_id = t.id",
						"where true")
				.setParameterIfNotEmptyAnd("stationtypes", stationTypeSet, "AND s.stationtype in (:stationtypes)", !stationTypeSet.contains("*"))
				.setParameterIfNotEmptyAnd("datatypes", dataTypeSet, "AND t.cname in (:datatypes)", !dataTypeSet.contains("*"))
				.addSql("order by _stationtype, _stationcode, _datatypename")
				.addLimit(limit)
				.addOffset(offset)
				.build();

		String stationTypePrev = "";
		String stationTypeAct = "";
		String stationCodePrev = "";
		String stationCodeAct = "";
		Map<String, Object> stations = null;
		List<Object> datatypes = null;

		ListIterator<Map<String, Object>> it = queryResult.listIterator();

		Map<String, Object> rec = it.hasNext() ? it.next() : null;
		stationTypeAct = (String) rec.getOrDefault("_stationtype", "");
		stationCodeAct = (String) rec.getOrDefault("_stationcode", "");
		System.out.println(rec);

		/* Accumulate station types */
		Map<String, Object> stationTypes = new HashMap<String, Object>();
		while (rec != null) {

			/* Accumulate stations */
			stations = new HashMap<String, Object>();
			stationTypePrev = stationTypeAct;
			do {
				Map<String, Object> station = makeObjectOrNull(rec, COLUMN_EXPANSION_STATION);
				stations.put(stationCodeAct, station);
				System.out.println("S = " + stationCodeAct);

				/* Accumulate data types */
				datatypes = new ArrayList<Object>();
				stationCodePrev = stationCodeAct;
				do {
					Map<String, Object> dataType = makeObjectOrNull(rec, COLUMN_EXPANSION_DATATYPE);
					if (dataType != null) {
						datatypes.add(dataType);
					}

					if (it.hasNext()) {
						rec = it.next();
						stationTypeAct = (String) rec.getOrDefault("_stationtype", "");
						stationCodeAct = (String) rec.getOrDefault("_stationcode", "");
						System.out.println(rec);
					} else {
						rec = null;
					}
				} while (rec != null && stationCodePrev.equalsIgnoreCase(stationCodeAct));
				station.put("sdatatypes", datatypes);

			} while (rec != null && stationTypePrev.equalsIgnoreCase(stationTypeAct));
			stationTypes.put(stationTypePrev, stations);
		}

		return JsonStream.serialize(stationTypes);
	}

	private static Map<String, Object> makeObjectOrNull(Map<String, Object> record, Map<String, Object> objDefinition) {
		Map<String, Object> result = new HashMap<String, Object>();
		for (Entry<String, Object> entry : record.entrySet()) {
			if (objDefinition.containsKey(entry.getKey())) {
				result.put(entry.getKey(), entry.getValue());
			}
		}
		return result.isEmpty() ? null : result;
	}


	@Override
	public void run(String... args) throws Exception {

		/* Set the query builder, JDBC template's row mapper and JSON parser up */
		QueryBuilder.setup(jdbcTemplate);

		// The API should have a flag to remove null values (what should be default?)
		ColumnMapRowMapper.setIgnoreNull(false);
		JsonStream.setIndentionStep(4);
//		JsonIterUnicodeSupport.enable();
		JsonIterSqlTimestampSupport.enable("yyyy-MM-dd HH:mm:ss.SSSZ");
		JsonIterPostgresSupport.enable();

//		String x = "T\u00f6ll";
//		String y = "Töll";
//		String z = JsonStream.serialize(y);
//
//		for (byte c : x.getBytes()) {
//			System.out.println(c + " : " + Character.toString((char) c));
//		}
//		System.out.println();
//
//		for (byte c : z.getBytes()) {
//			System.out.println(c + " : " + Character.toString((char) c));
//		}
//		System.out.println();
//
//		System.out.println(new String(z.getBytes(), "UTF-8"));
//		System.out.println(new String(x.getBytes("UTF-8"), "UTF-8"));
//
//		System.exit(0);

		/* Run queries */
//		String fetchStationTypes = fetchStationTypes();
//		System.out.println(fetchStationTypes);

//		String stations = fetchStations("ParkingStation, Bicycle", 120, -10, "sorigin, sname, smetadata");
//		String stations = fetchStations("Bicycle");
//		String stations = fetchStations("*", 20, -1, "name, metadata");
//		String stations = fetchStations("ParkingStation, Bicycle", 20, -1, "name, type, metadata");

//		System.out.println(stations);

//		String stations = fetchStationsAndTypes("ParkingStation, Bicycle", "occupied, availability", 20, 0, "sorigin, sname, tunit, ttype");
//		System.out.println(stations);
//
//		stations = fetchStationsAndTypes("ParkingStation, Bicycle", "occupied, availability", 20, 20, "sorigin, sname, tunit, ttype");
//		System.out.println(stations);
//
//		stations = fetchStationsAndTypes("ParkingStation, Bicycle", "occupied, availability", 20, 40, "sorigin, sname, tunit, ttype");
//		System.out.println(stations);

//		String stations = fetchStationsAndTypes("ParkingStation", "occupied, availability", 2, 10, null);//"sorigin, sname, tunit, ttype");
//		String stations = fetchStationsAndTypes("ParkingStation, Bicycle", "occupied, availability", 10, 0, "sorigin, sname, tname, tperiod, tlastmeasurement", "GUEST");
//		String stations = fetchStationsAndTypes("ParkingStation, Bicycle", "*", 30, 0, "sname, sdatatypes", "ADMIN");
		String stations = fetchStationsAndTypes("*", "*", 30, 0, "*", "ADMIN");
		System.out.println(stations);

		log.info("READY.");
	}


}
