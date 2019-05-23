package it.bz.idm.bdp.reader2.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class SelectExpansion {
	final Map<String, Map<String, Object>> expansion = new HashMap<String, Map<String, Object>>();

	public void addExpansion(final String defName, Map<String, Object> definition) {
		expansion.put(defName, definition);
	}

	// Needed?
	public void addSubExpansion(final String defName, final String jsonName, Map<String, Object> definition) throws Exception {
		Map<String, Object> exp = expansion.get(defName);
		if (exp == null) {
			throw new Exception("Expansion with name '" + defName + "' not found!");
		}
		exp.put(jsonName, definition);
	}

	public Map<String, Object> getExpansion(final String defName) {
		return expansion.get(defName);
	}

	private Set<String> getKeys(String... defNames) {
		Set<String> columnAliases = new HashSet<String>();
		for (String defName : defNames) {
			columnAliases.addAll(expansion.get(defName).keySet());
		}
		return columnAliases;
	}

	public Set<String> getColumnAliases(String select, String... selectDefNames) {
		if (select == null || select.trim().equals("*")) {
			return getKeys(selectDefNames);
		}
		return csvToSet(select);
	}

	public Map<String, String> _expandSelect(String select, String... selectDefNames) {
		Set<String> columnAliases = getColumnAliases(select, selectDefNames);
		return _expandSelect(columnAliases, selectDefNames);
	}

	@SuppressWarnings("unchecked")
	public Map<String, String> _expandSelect(Set<String> columnAliases, String... selectDefNames) {

		Map<String, StringBuffer> bufferMap = new HashMap<String, StringBuffer>();
		StringBuffer sb = null;

		for (String columnAlias : columnAliases) {

			String selectDefName = null;

			Map<String, Object> selectDef = null;
			for (String defName : selectDefNames) {
				selectDef = expansion.get(defName);
				if (selectDef.containsKey(columnAlias)) {
					selectDefName = defName;
					break;
				}
			}

			if (selectDefName == null) {
				throw new RuntimeException("Key '" + columnAlias + "' does not exist!");
			}

			if (bufferMap.containsKey(selectDefName)) {
				sb = bufferMap.get(selectDefName);
			} else {
				sb = new StringBuffer();
				bufferMap.put(selectDefName, sb);
			}

			// TODO make this recursive in a separate method
			Object def = selectDef.get(columnAlias);
			if (def == null)
				continue;
			if (def instanceof String) {
				sb.append(def)
				  .append(" as ")
				  .append(columnAlias)
				  .append(", ");
			} else if (def instanceof Map) {
				for (Entry<String, String> e : ((Map<String, String>) def).entrySet()) {
					sb.append(e.getValue())
					  .append(" as ")
					  .append(e.getKey())
					  .append(", ");
				}
			} else {
				throw new RuntimeException("A select definition must contain either Strings or Maps!");
			}

		}

		Map<String, String> result = new HashMap<String, String>();
		for (Entry<String, StringBuffer> entry : bufferMap.entrySet()) {
			StringBuffer buffer = entry.getValue();
			result.put(entry.getKey(), buffer.length() >= 3 ? buffer.substring(0, buffer.length() - 2) : buffer.toString());
		}

		return result;
	}

	public static Set<String> csvToSet(final String csv) {
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

	public static void main(String[] args) {
		SelectExpansion se = new SelectExpansion();

		Map<String, Object> seMeasurement = new HashMap<String, Object>() {
			private static final long serialVersionUID = 1L;
			{
				put("mvalidtime", "me.timestamp");
				put("mtransactiontime", "me.created_on");
				put("mperiod", "me.period");
				put("mvalue", "me.double_value");
			}
		};

		Map<String, Object> seDatatype = new HashMap<String, Object>() {
			private static final long serialVersionUID = 1L;
			{
				put("tname", "t.cname");
				put("tunit", "t.cunit");
				put("ttype", "t.rtype");
				put("tdescription", "t.description");
				put("tlastmeasurement", seMeasurement);
			}
		};

		Map<String, Object> seParent = new HashMap<String, Object>() {
			private static final long serialVersionUID = 1L;
			{
				put("pname", "p.name");
				put("ptype", "p.stationtype");
				put("pcoordinate", "s.pointprojection");
				put("pcode", "p.stationcode");
				put("porigin", "p.origin");
			}
		};

		Map<String, Object> seStation = new HashMap<String, Object>() {
			private static final long serialVersionUID = 1L;
			{
				put("sname", "s.name");
				put("stype", "s.stationtype");
				put("scode", "s.stationcode");
				put("sorigin", "s.origin");
				put("scoordinate", "s.pointprojection");
				put("smetadata", "m.json");
				put("sparent", seParent);
				put("sdatatypes", seDatatype);
			}
		};

		se.addExpansion("station", seStation);
		se.addExpansion("parent", seParent);
		se.addExpansion("datatype", seDatatype);
		se.addExpansion("measurement", seMeasurement);

		try {
			System.out.println(se._expandSelect("scode,sname,pname", "station", "parent"));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}


}
