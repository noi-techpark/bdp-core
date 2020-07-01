package it.bz.idm.bdp.ninja.utils.resultbuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicLong;

import it.bz.idm.bdp.ninja.utils.querybuilder.Schema;
import it.bz.idm.bdp.ninja.utils.querybuilder.Target;
import it.bz.idm.bdp.ninja.utils.querybuilder.TargetDef;
import it.bz.idm.bdp.ninja.utils.querybuilder.TargetDefList;
import it.bz.idm.bdp.ninja.utils.simpleexception.ErrorCodeInterface;
import it.bz.idm.bdp.ninja.utils.simpleexception.SimpleException;

// TODO Make this generic, create a makeObjRecursive method
public class ResultBuilder {

	public static enum ErrorCode implements ErrorCodeInterface {
		RESPONSE_SIZE("Response size of %d MB exceeded. Please rephrase your request. Use a flat representation, WHERE, SELECT, LIMIT with OFFSET or a narrow time interval.");

		private final String msg;

		ErrorCode(final String msg) {
			this.msg = msg;
		}

		@Override
		public String getMsg() {
			return "TREE BUILDING: " + msg;
		}
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> build(boolean ignoreNull, List<Map<String, Object>> queryResult, Schema schema,
			List<String> hierarchy, int maxAllowedSizeInMB) {
		AtomicLong size = new AtomicLong(0);

		long maxAllowedSize = maxAllowedSizeInMB > 0 ? maxAllowedSizeInMB * 1000000 : 0;

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
		Map<String, Object> mvalueAndFunctions = null;

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
					stationType = makeObj(schema, rec, "stationtype", false, size);
				case 1:
					station = makeObj(schema, rec, "station", ignoreNull, size);
					parent = makeObj(schema, rec, "parent", ignoreNull, size);
				case 2:
					if (hierarchy.size() > 2) {
						datatype = makeObj(schema, rec, "datatype", ignoreNull, size);
					}
				default:
					if (hierarchy.size() > 3) {
						measurement = makeObj(schema, rec, "measurement", ignoreNull, size);

						/*
						 * We only need one measurement-type here ("measurementdouble"), since we look
						 * only for final names, that is we do not consider mvalue_double and
						 * mvalue_string here, but reduce both before handling to mvalue. See makeObj
						 * for details.
						 */
						mvalueAndFunctions = makeObj(schema, rec, "measurementdouble", ignoreNull, size);

						for (Entry<String, Object> entry : mvalueAndFunctions.entrySet()) {
							if (entry.getValue() != null || !ignoreNull) {
								measurement.put(entry.getKey(), entry.getValue());
							}
						}
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

			if (maxAllowedSize > 0 && maxAllowedSize < size.get()) {
				throw new SimpleException(ErrorCode.RESPONSE_SIZE, maxAllowedSizeInMB);
			}
		}
		System.out.println(size);
		return stationTypes;
	}

	public static Map<String, Object> makeObj(Schema schema, Map<String, Object> record, String defName, boolean ignoreNull, AtomicLong sizeEstimate) {
		TargetDefList def = schema.getOrNull(defName);
		Map<String, Object> result = new TreeMap<String, Object>();
		int size = 0;
		for (Entry<String, Object> entry : record.entrySet()) {
			if (ignoreNull && entry.getValue() == null)
				continue;

			Target target = new Target(entry.getKey());

			if (def.getFinalNames().contains(target.getName())) {
				if (target.hasJson()) {
					@SuppressWarnings("unchecked")
					Map<String, Object> jsonObj = (Map<String, Object>) result.getOrDefault(target.getName(), new TreeMap<String, Object>());
					jsonObj.put(target.getJson(), entry.getValue());
					size += target.getJson().length();
					if (jsonObj.size() == 1) {
						result.put(target.getName(), jsonObj);
						size += target.getName().length();
					}
				} else {
					result.put(target.getName(), entry.getValue());
					size += target.getName().length();
				}
				size += entry.getValue() == null ? 0 : entry.getValue().toString().length();
			}
		}
		sizeEstimate.getAndAdd(size);
		return result;
	}


	public static void main(String[] args) throws Exception {
		Schema schema = new Schema();
		TargetDefList defListC = new TargetDefList("C")
				.add(new TargetDef("h", "C.h").sqlBefore("before"));
		TargetDefList defListD = new TargetDefList("D")
				.add(new TargetDef("d", "D.d").sqlAfter("after"));
		TargetDefList defListB = new TargetDefList("B")
				.add(new TargetDef("x", "B.x").alias("x_replaced"))
				.add(new TargetDef("y", defListC));
		TargetDefList defListA = new TargetDefList("A")
				.add(new TargetDef("a", "A.a"))
				.add(new TargetDef("b", "A.b"))
				.add(new TargetDef("c", defListB));
		TargetDefList defListMain = new TargetDefList("main")
				.add(new TargetDef("t", defListA));
		schema.add(defListA);
		schema.add(defListB);
		schema.add(defListC);
		schema.add(defListD);
		schema.add(defListMain);

		AtomicLong size = new AtomicLong(0);

		Map<String, Object> rec = new HashMap<String, Object>();
		rec.put("a", "3");
		rec.put("b", null);
		rec.put("x.abc", "0");
		rec.put("h", "v");
		System.out.println(makeObj(schema, rec, "A", false, size).toString());
		System.out.println(makeObj(schema, rec, "A", true, size).toString());
		System.out.println();
		System.out.println(makeObj(schema, rec, "B", false, size).toString());
		System.out.println(makeObj(schema, rec, "B", true, size).toString());
		System.out.println();
		System.out.println(makeObj(schema, rec, "C", false, size).toString());
		System.out.println(makeObj(schema, rec, "C", true, size).toString());
		System.out.println(size);
	}

}
