package it.bz.idm.bdp.reader2.utils.resultbuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.bz.idm.bdp.reader2.utils.querybuilder.SelectExpansion;

public class ResultBuilder {

	@SuppressWarnings("unchecked")
	public static Map<String, Object> build(boolean ignoreNull, List<Map<String, Object>> queryResult, SelectExpansion se, List<String> hierarchy) {

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
		Map<String, Object> datatypes = null;
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
				case 1:
					station = se.makeObj(rec, "station", ignoreNull);
					parent = se.makeObj(rec, "parent", ignoreNull);
				case 2:
					if (hierarchy.size() > 2) {
						datatype = se.makeObj(rec, "datatype", ignoreNull);
					}
				default:
					if (hierarchy.size() > 2) {
						measurement = se.makeObj(rec, "measurement", ignoreNull);
					}
			}

			if (measurement != null && !measurement.isEmpty()) {
				measurements = (List<Object>) datatype.get("tmeasurements");
				if (measurements == null) {
					measurements = new ArrayList<Object>();
					datatype.put("tmeasurements", measurements);
				}
				measurements.add(measurement);
			}
			if (datatype != null && !datatype.isEmpty()) {
				datatypes = (Map<String, Object>) station.get("sdatatypes");
				if (datatypes == null) {
					datatypes = new HashMap<String, Object>();
					station.put("sdatatypes", datatypes);
				}
				datatypes.put(currValues.get(2), datatype);
			}
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
			if (!stationType.isEmpty()) {
				stationTypes.put(currValues.get(0), stationType);
			}

			prevValues.clear();
			prevValues.addAll(currValues);
		}
		return stationTypes;
	}

}
