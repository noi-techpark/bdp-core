package it.bz.idm.bdp.reader2.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class SelectExpansion {

	private final Map<String, Map<String, Object>> expansion = new HashMap<String, Map<String, Object>>();
	private final Map<String, Set<String>> hierarchy = new HashMap<String, Set<String>>();

	public void addExpansion(final String defName, String alias, String column) {
		Map<String, Object> definition = expansion.getOrDefault(defName, new HashMap<String, Object>());
		definition.put(alias, column);
		expansion.put(defName, definition);

		Set<String> defSet = hierarchy.getOrDefault(defName, new HashSet<String>());
		defSet.add(defName);
		hierarchy.put(defName, defSet);
	}

	public void addSubExpansion(final String defName, String alias, String subDefName) {
		Map<String, Object> subDefinition = expansion.getOrDefault(subDefName, new HashMap<String, Object>());
		Map<String, Object> definition = expansion.getOrDefault(defName, new HashMap<String, Object>());
		definition.put(alias, null);
		expansion.put(defName, definition);
		expansion.put(defName, subDefinition);

		Set<String> defSet = hierarchy.getOrDefault(defName, new HashSet<String>());
		defSet.add(subDefName);
		defSet.add(defName);
		hierarchy.put(defName, defSet);

		for (Entry<String, Set<String>> e : hierarchy.entrySet()) {
			if (e.getKey().equals(subDefName)) {
				defSet.addAll(hierarchy.get(subDefName));
			}
		}
	}

	private Set<String> expandDefNames(String... selectDefNames) {
		Set<String> res = new HashSet<String>();
		for (String defName : selectDefNames) {
			Set<String> list = hierarchy.get(defName);
			if (list != null)
				res.addAll(list);
		}
		return res;
	}

	public Map<String, Object> getExpansion(final String defName) {
		return expansion.get(defName);
	}

	private Set<String> getAllKeys(String... defNames) {
		Set<String> columnAliases = new HashSet<String>();
		for (String defName : defNames) {
			columnAliases.addAll(expansion.get(defName).keySet());
		}
		return columnAliases;
	}

	public List<String> getColumnAliasesAsList(String select, String...selectDefNames) {
		return new ArrayList<String>(getColumnAliases(select, selectDefNames));
	}

	public Set<String> getColumnAliases(String select, String... selectDefNames) {
		if (select == null || select.trim().equals("*")) {
			return getAllKeys(selectDefNames);
		}
		Set<String> aliases = csvToSet(select);

		String selectDefName = null;
		for (String alias : aliases) {
			for (String defName : expandDefNames(selectDefNames)) {
				Map<String, Object> selectDef = expansion.get(defName);
				if (selectDef.containsKey(alias)) {
					selectDefName = defName;
					break;
				}
			}
			if (selectDefName == null) {
				SimpleException ex = new SimpleException("SELECT_EXPANSION_KEY_NOT_FOUND", "Key '" + alias + "' does not exist!");
				ex.setData(alias);
				throw ex;
			}
		}

		return aliases;

	}

	public Map<String, String> _expandSelect(String select, String... selectDefNames) throws Exception {
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
}
