package it.bz.idm.bdp.reader2.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringJoiner;

public class SelectExpansion {

	public static enum ERROR_CODES {
		SELECT_EXPANSION_KEY_NOT_FOUND
	}
	private final Map<String, Map<String, Object>> expansion = new HashMap<String, Map<String, Object>>();
	private final Map<String, Set<String>> hierarchy = new HashMap<String, Set<String>>();

	private boolean dirty = true;

	public void addExpansion(final String defName, String alias, String column) {
		if (alias == null || alias.isEmpty() || column == null || column.isEmpty()) {
			throw new RuntimeException("Expansion alias and column must be set!");
		}
		_addExpansion(defName, alias, column);
	}

	public void addSubExpansion(final String defName, String alias, String subDefName) {
		Map<String, Object> subdefinition = _addExpansion(subDefName, null, null);
		Map<String, Object> definition = _addExpansion(defName, alias, null);

		definition.put(alias, subdefinition);

		for (Entry<String, Set<String>> e : hierarchy.entrySet()) {
			if (e.getKey().equals(subDefName)) {
				hierarchy.get(defName).addAll(e.getValue());
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

	private Map<String, Object> _addExpansion(final String defName, String alias, String column) {
		if (defName == null || defName.isEmpty()) {
			throw new RuntimeException("Expansion definition name must be set!");
		}
		dirty = true;
		Map<String, Object> definition = expansion.getOrDefault(defName, new HashMap<String, Object>());
		if (alias != null) {
			definition.put(alias, column);
		}
		expansion.put(defName, definition);

		Set<String> defSet = hierarchy.getOrDefault(defName, new HashSet<String>());
		defSet.add(defName);
		hierarchy.put(defName, defSet);

		return definition;
	}

	public List<String> getColumnAliasesAsList(String select, String...selectDefNames) {
		return new ArrayList<String>(getColumnAliases(select, selectDefNames));
	}

	public Set<String> getColumnAliases(String select, String... selectDefNames) {
		if (select == null || select.trim().equals("*")) {
			return getAllKeys(selectDefNames);
		}
		Set<String> aliases = csvToSet(select);

		// XXX Just a check if exists, remove it
		for (String alias : aliases) {
			_findDefinitionContainingAlias(alias, selectDefNames);
		}

		return aliases;
	}

	private Map<String, Map<String, Object>> _findDefinitionContainingAlias(String alias, String... selectDefNames) {
		Map<String, Map<String, Object>> res = new HashMap<String, Map<String, Object>>();
		for (String defName : expandDefNames(selectDefNames)) {
			Map<String, Object> selectDef = expansion.get(defName);
			if (selectDef.containsKey(alias)) {
				res.put(defName, selectDef);
			}
		}
		return res;

		// XXX bring this snippet back?
//		SimpleException ex = new SimpleException(ERROR_CODES.SELECT_EXPANSION_KEY_NOT_FOUND.toString(), "Key '" + alias + "' does not exist!");
//		ex.setData(alias);
//		throw ex;
	}

	public Map<String, String> _expandSelect(String select, String... selectDefNames) throws Exception {
		Set<String> columnAliases = getColumnAliases(select, selectDefNames);
		return _expandSelect(columnAliases, selectDefNames);
	}

	public Map<String, String> _expandSelect(Set<String> columnAliases, String... selectDefNames) {
		Map<String, StringJoiner> bufferMap = new HashMap<String, StringJoiner>();
		StringJoiner sb = null;

		for (String columnAlias : columnAliases) {

			Map<String, Map<String, Object>> selectDefWithName = _findDefinitionContainingAlias(columnAlias, selectDefNames);
			String selectDefName = (String) selectDefWithName.keySet().toArray()[0];
			Map<String, Object> selectDef = selectDefWithName.get(selectDefName);

			sb = bufferMap.getOrDefault(selectDefName, new StringJoiner(", "));
			bufferMap.put(selectDefName, sb);

			Object def = selectDef.get(columnAlias);
			buildSelectRec(def, sb, columnAlias);
		}

		Map<String, String> result = new HashMap<String, String>();
		for (Entry<String, StringJoiner> entry : bufferMap.entrySet()) {
			result.put(entry.getKey(), entry.getValue().toString());
		}

		dirty = false;
		return result;
	}

	@SuppressWarnings("unchecked")
	private void buildSelectRec(Object def, StringJoiner sb, String alias) {
		if (def == null)
			return;
		if (def instanceof String) {
			sb.add((String) def)
			  .add(" as ")
			  .add(alias);
		} else if (def instanceof Map) {
			for (Entry<String, Object> e : ((Map<String, Object>) def).entrySet()) {
				buildSelectRec(e.getValue(), sb, e.getKey());
			}
		} else {
			throw new RuntimeException("A select definition must contain either Strings or Maps!");
		}
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
