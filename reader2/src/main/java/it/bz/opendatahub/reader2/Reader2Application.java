package it.bz.opendatahub.reader2;

import java.util.HashSet;
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

	public String fetchStations(String stationTypeList, long limit, long offset, String select) {
		Set<String> stationTypeSet = csvToSet(stationTypeList);
		return QueryBuilder
				.init()
				.addSql("select s.name, s.origin, s.pointprojection coord, s.stationcode, s.stationtype, m.json metadata from station s",
						"left join metadata m on m.station_id = s.id",
						"left join station p on s.parent_id = p.id",
						"left join metadata pm on pm.station_id = p.id",
						"where true")
				.setParameterIfNotEmptyAnd("stationtypes", stationTypeSet,
						"and s.stationtype in (:stationtypes)", !stationTypeSet.contains("*"))
				.setParameterIf("limit", new Long(limit), "limit :limit", limit > 0)
				.setParameterIf("offset", new Long(offset), "offset :offset", offset >= 0)
				.buildJson();
	}

	public String fetchStationsAndTypes(String stationTypeList, String dataTypeList) {
		Set<String> stationTypeSet = csvToSet(stationTypeList);
		Set<String> dataTypeSet = csvToSet(dataTypeList);

		return QueryBuilder
				.init()
				.addSql("select s.*, t.*, m.period, m.timestamp",
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

		/* Set the query builder, JDBC template's row mapper and JSON parser up */
		QueryBuilder.setup(jdbcTemplate);
		ColumnMapRowMapper.setIgnoreNull(true);
		JsonStream.setIndentionStep(4);
		JsonIterSqlTimestampSupport.enable("yyyy-MM-dd HH:mm:ss.SSSZ");
		JsonIterPostgresSupport.enable();

		/* Run queries */
//		String fetchStationTypes = fetchStationTypes();
//		System.out.println(fetchStationTypes);

		String stations = fetchStations("ParkingStation, Bicycle", -1, 10, "");
//		String stations = fetchStations("Bicycle");
//		String stations = fetchStations("*");

		System.out.println(stations);

//		String stations = fetchStationsAndTypes("ParkingStation", "occupied");
//		log.info(stations);

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
