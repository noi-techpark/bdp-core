package it.bz.idm.bdp.reader2;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

//@SpringBootApplication
public class Reader2Application implements CommandLineRunner {

	private static final Logger log = LoggerFactory.getLogger(Reader2Application.class);

	@Autowired
    NamedParameterJdbcTemplate jdbcTemplate;

	public static void main(String[] args) {
		SpringApplication.run(Reader2Application.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

//		AppStartupDataLoader startup = new AppStartupDataLoader();
		DataFetcher df = new DataFetcher();

		boolean ignoreNull = true;

//		/* Set the query builder, JDBC template's row mapper and JSON parser up */
//		QueryBuilder.setup(jdbcTemplate, se);
//
//		// The API should have a flag to remove null values (what should be default? <-- true)
//		ColumnMapRowMapper.setIgnoreNull(ignoreNull);
//		JsonStream.setIndentionStep(4);
////		JsonIterUnicodeSupport.enable();
//		JsonIterSqlTimestampSupport.enable("yyyy-MM-dd HH:mm:ss.SSSZ");
//		JsonIterPostgresSupport.enable();

//		String x = "T\u00f6ll";
//		String y = "Töll";
//		String z = JsonStream.serialize(y);
//
//		for (byte c : x.getBytes()) {
//			log.debug(c + " : " + Character.toString((char) c));
//		}
//		log.debug();
//
//		for (byte c : z.getBytes()) {
//			log.debug(c + " : " + Character.toString((char) c));
//		}
//		log.debug();
//
//		log.debug(new String(z.getBytes(), "UTF-8"));
//		log.debug(new String(x.getBytes("UTF-8"), "UTF-8"));
//
//		System.exit(0);

		/* Run queries */
		String fetchStationTypes = df.fetchStationTypes();
		log.debug(fetchStationTypes);

//		String stations = fetchStations("ParkingStation, Bicycle", 120, -10, "sorigin, sname, smetadata");
//		String stations = fetchStations("Bicycle");
//		String stations = fetchStations("*", 20, -1, "name, metadata");
//		String stations = fetchStations("ParkingStation, Bicycle", 20, -1, "name, type, metadata");

//		log.debug(stations);

//		String stations = fetchStationsAndTypes("ParkingStation, Bicycle", "occupied, availability", 20, 0, "sorigin, sname, tunit, ttype");
//		log.debug(stations);
//
//		stations = fetchStationsAndTypes("ParkingStation, Bicycle", "occupied, availability", 20, 20, "sorigin, sname, tunit, ttype");
//		log.debug(stations);
//
//		stations = fetchStationsAndTypes("ParkingStation, Bicycle", "occupied, availability", 20, 40, "sorigin, sname, tunit, ttype");
//		log.debug(stations);

//		String stations = fetchStationsAndTypes("ParkingStation", "occupied, availability", 2, 10, null);//"sorigin, sname, tunit, ttype");
//		String stations = fetchStationsAndTypes("ParkingStation, Bicycle", "occupied, availability", 10, 0, "sorigin, sname, tname, tperiod, tlastmeasurement", "GUEST");
//		String stations = fetchStationsAndTypes("ParkingStation, Bicycle", "*", 30, 0, "sname, sdatatypes", "ADMIN");
		Map<String, Object> stations = df.fetchStationsTypesAndMeasurements("EChargingPlug, EChargingStation", "*", 1, 0, "sname, tname, mvalue", "ADMIN", ignoreNull, "");
		log.info(DataFetcher.serializeJSON(stations));

		log.info("READY.");
	}


}
