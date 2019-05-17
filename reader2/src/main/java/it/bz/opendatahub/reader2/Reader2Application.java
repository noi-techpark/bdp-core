package it.bz.opendatahub.reader2;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.jsoniter.output.JsonStream;

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

	public List<String> fetchStationTypes01() {
		log.info("/ -> getStationTypes");
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		return jdbcTemplate.queryForList("select jsonb_agg(stationtype) from (select stationtype from station group by stationtype) c", parameters, String.class);
	}

	public String fetchStationTypes02() {
		log.info("/ -> getStationTypes");
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		List<String> queryForList = jdbcTemplate.queryForList("select stationtype from station group by stationtype", parameters, String.class);
		return JsonStream.serialize(queryForList);
	}

	public String fetchStationTypes03() {
		log.info("/ -> getStationTypes");
		return QueryBuilder
				.init()
				.addSql("select stationtype from station group by stationtype")
				.buildJson(String.class);
	}

	// TODO stationTypeList should allow * for all stations
	//      Parse them accordingly (SQL-injection?)
	public List<Map<String, Object>> fetchStations01(String stationTypeList) {
		log.info("/ -> getStations");

		Set<String> stationTypeSet = new HashSet<String>();
		for (String stationType : stationTypeList.split(",")) {
			stationTypeSet.add(stationType.trim());
		}

		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("types", stationTypeSet);

		return jdbcTemplate.queryForList("SELECT row_to_json(c) FROM (select * from station WHERE stationtype in (:types)) c", parameters);
	}

	// TODO stationTypeList should allow * for all stations
	//      Parse them accordingly (SQL-injection?)
	public String fetchStations02(String stationTypeList) {
		log.info("/ -> getStations");

		Set<String> stationTypeSet = csvToSet(stationTypeList);

		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("types", stationTypeSet);
		List<Map<String, Object>> queryForList = jdbcTemplate.queryForList("select * from station WHERE stationtype in (:types)", parameters);
		return JsonStream.serialize(queryForList);
	}

	// TODO stationTypeList should allow * for all stations
	//      Parse them accordingly (SQL-injection?)
	public String fetchStations03(String stationTypeList) {
		log.info("/ -> getStations");

		Set<String> stationTypeSet = csvToSet(stationTypeList);

		return QueryBuilder
				.init()
				.addSql("select * from station")
				.setParameterIfNotEmptyAnd("stationtypes", stationTypeSet,
						"WHERE stationtype in (:stationtypes)", !stationTypeSet.contains("*"))
				.buildJson();
	}

	public String fetchStationsAndTypes(String stationTypeList, String dataTypeList) {
		Set<String> stationTypeSet = csvToSet(stationTypeList);
		Set<String> dataTypeSet = csvToSet(dataTypeList);

		return QueryBuilder
				.init()
				.addSql("select s.*, t.*, m.period, m.",
					 "from measurement m",
					 "join station s on m.station_id = s.id",
					 "join type t on m.type_id = t.id",
					 "where true")
				.setParameterIfNotEmptyAnd("stationtypes", stationTypeSet, "AND s.stationtype in (:stationtypes)", !stationTypeSet.contains("*"))
				.setParameterIfNotEmptyAnd("datatypes", dataTypeSet, "AND t.cname in (:datatypes)", !dataTypeSet.contains("*"))
				.addSql("limit 1")
				.buildJson();
	}


	@Override
	public void run(String... args) throws Exception {

//		List<String> fetchStationTypes = fetchStationTypes01();
//		fetchStationTypes.forEach(s -> log.info(s));

		QueryBuilder.setup(jdbcTemplate);

		JsonStream.setIndentionStep(4);
		JsonIterSqlTimestampSupport.enable("yyyy-MM-dd HH:mm:ss.SSSZ");
		JsonIterPostgresSupport.enable();

//		System.out.println(JsonStream.serialize(new Point(1,2)));
//
//		System.exit(0);

//		String stationTypes = fetchStationTypes03();
//		log.info(stationTypes);

//		String stations = fetchStations03("ParkingStation, Bicycle");
//		String stations = fetchStations03("Bicycle");

		String stations = fetchStationsAndTypes("ParkingStation", "occupied");
		log.info(stations);

		log.info("READY.");
	}

	private static Set<String> csvToSet(final String csv) {
		Set<String> resultSet = new HashSet<String>();
		for (String value : csv.split(",")) {
			value = value.trim();
			if (value.equals("*")) {
				resultSet.clear();
				resultSet.add(value);
				return resultSet;
			}
			resultSet.add(value);
		}
		return resultSet;
	}

}
