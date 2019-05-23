package it.bz.idm.bdp.reader2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jsoniter.output.JsonStream;

import it.bz.idm.bdp.reader2.utils.QueryBuilder;
import it.bz.idm.bdp.reader2.utils.SelectExpansion;

public class DataFetcher {

	private static final Logger log = LoggerFactory.getLogger(DataFetcher.class);

	public String fetchStations(String stationTypeList, long limit, long offset, String select, String role, boolean ignoreNull) {
		Set<String> stationTypeSet = QueryBuilder.csvToSet(stationTypeList);

		long nanoTime = System.nanoTime();
		QueryBuilder query = QueryBuilder
				.init(select, "station", "parent")
				.addSql("select s.stationtype as _stationtype, s.stationcode as _stationcode")
				.expandSelect("station", "parent")
				.addSql("from station s")
				.addSqlIfAlias("left join metadata m on m.id = s.meta_data_id", "smetadata")
				.addSqlIfDefinition("left join station p on s.parent_id = p.id", "parent")
				.addSql("where true")
				.setParameterIfNotEmptyAnd("stationtypes", stationTypeSet, "AND s.stationtype in (:stationtypes)", !stationTypeSet.contains("*"))
				.addSql("order by _stationtype, _stationcode")
				.addLimit(limit)
				.addOffset(offset);

		System.out.println(query.getSql());

		log.info("query building: " + Long.toString(System.nanoTime() - nanoTime));

		nanoTime = System.nanoTime();
		List<Map<String, Object>> queryResult = query.build();
		log.info("query exec: " + Long.toString(System.nanoTime() - nanoTime));

		Map<String, Object> stationTypes = buildResultMaps(ignoreNull, queryResult, query.getSelectExpansion());

		nanoTime = System.nanoTime();
		String serialize = JsonStream.serialize(stationTypes);
		log.info("json: " + Long.toString(System.nanoTime() - nanoTime));
		return serialize;
	}

	public String fetchStationsAndTypes(String stationTypeList, String dataTypeList, long limit,
			long offset, String select, String role, boolean ignoreNull) {
		log.info("FETCHSTATIONSANDTYPES");
		Set<String> stationTypeSet = QueryBuilder.csvToSet(stationTypeList);
		Set<String> dataTypeSet = QueryBuilder.csvToSet(dataTypeList);

		long nanoTime = System.nanoTime();
		QueryBuilder query = QueryBuilder
				.init(select)
				.addSql("select s.stationtype as _stationtype, s.stationcode as _stationcode, t.cname as _datatypename, ")
//				.expandSelect(select, COLUMN_EXPANSION, true, false)
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
				.addOffset(offset);
		log.info("query building: " + Long.toString(System.nanoTime() - nanoTime));

		nanoTime = System.nanoTime();
		List<Map<String, Object>> queryResult = query.build();
		log.info("query exec: " + Long.toString(System.nanoTime() - nanoTime));

		Map<String, Object> stationTypes = buildResultMaps(ignoreNull, queryResult, query.getSelectExpansion());

		nanoTime = System.nanoTime();
		String serialize = JsonStream.serialize(stationTypes);
		log.info("json: " + Long.toString(System.nanoTime() - nanoTime));
		return serialize;
	}

	private static Map<String, Object> buildResultMaps(boolean ignoreNull, List<Map<String, Object>> queryResult, SelectExpansion se) {
		long nanoTime;
		nanoTime = System.nanoTime();
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
		log.debug(rec.toString());

		/* Accumulate station types */
		Map<String, Object> stationTypes = new HashMap<String, Object>();
		while (rec != null) {

			/* Accumulate stations */
			stations = new HashMap<String, Object>();
			stationTypePrev = stationTypeAct;
			do {
				Map<String, Object> station = makeObjectOrEmptyMap(rec, se.getExpansion("station"), ignoreNull);
				stations.put(stationCodeAct, station);
				log.debug("S = " + stationCodeAct);

				/* Accumulate data types */
				datatypes = new ArrayList<Object>();
				if (station.get("sdatatypes") != null) {
					datatypes.add(station.get("sdatatypes"));
				}
				stationCodePrev = stationCodeAct;
				do {
					if (it.hasNext()) {
						rec = it.next();
						stationTypeAct = (String) rec.getOrDefault("_stationtype", "");
						stationCodeAct = (String) rec.getOrDefault("_stationcode", "");
						log.debug(rec.toString());
						Map<String, Object> dataType = makeObjectOrNull(rec, se.getExpansion("datatype"), ignoreNull);
						if (dataType != null) {
							datatypes.add(dataType);
						}
					} else {
						rec = null;
					}
				} while (rec != null && stationCodePrev.equalsIgnoreCase(stationCodeAct));

				if (!datatypes.isEmpty())
					station.put("sdatatypes", datatypes);

			} while (rec != null && stationTypePrev.equalsIgnoreCase(stationTypeAct));
			stationTypes.put(stationTypePrev, stations);
		}
		log.info("loop: " + Long.toString(System.nanoTime() - nanoTime));
		return stationTypes;
	}

	private static Map<String, Object> makeObjectOrNull(Map<String, Object> record, Map<String, Object> objDefinition, boolean ignoreNull) {
		Map<String, Object> result = makeObjectOrEmptyMap(record, objDefinition, ignoreNull);
		return result.isEmpty() ? null : result;
	}

	private static Map<String, Object> makeObjectOrEmptyMap(Map<String, Object> record, Map<String, Object> objDefinition, boolean ignoreNull) {
		Map<String, Object> result = new HashMap<String, Object>();
		for (Entry<String, Object> def : objDefinition.entrySet()) {
			if (record.containsKey(def.getKey())) {
				result.put(def.getKey(), record.get(def.getKey()));
			} else if (def.getValue() instanceof Map) {
				@SuppressWarnings("unchecked")
				Map<String, Object> subObject = makeObjectOrNull(record, (Map<String, Object>) def.getValue(), ignoreNull);
				if (subObject != null || !ignoreNull) {
					result.put(def.getKey(), subObject);
				}
			}
		}
		return result;
	}

	public String fetchStationTypes() {
		return QueryBuilder
				.init(null)
				.addSql("select stationtype from station group by stationtype")
				.buildJson(String.class);
	}



}
