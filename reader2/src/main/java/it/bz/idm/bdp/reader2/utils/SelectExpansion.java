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
	private final Map<String, String> aliasMap = new HashMap<String, String>();

	private final Set<String> usedJSONAliases = new HashSet<String>();
	private final Set<String> usedJSONDefNames = new HashSet<String>();
	private final Map<String, String> expandedSelects = new HashMap<String, String>();

	private boolean dirty = true;
	private static final boolean RECURSION_DEFAULT = false;

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
			aliasMap.put(alias, defName);
		}
		expansion.put(defName, definition);

		Set<String> defSet = hierarchy.getOrDefault(defName, new HashSet<String>());
		defSet.add(defName);
		hierarchy.put(defName, defSet);

		return definition;
	}

	private Set<String> getColumnAliases(String select, String... selectDefNames) {
		if (select == null || select.trim().equals("*")) {
			return getAllKeys(selectDefNames);
		}
		Set<String> aliases = csvToSet(select);

		return aliases;
	}

	public void build(String select, String... selectDefNames) {
		build(select, RECURSION_DEFAULT, selectDefNames);
	}

	public void build(String select, boolean recursive, String... selectDefNames) {
		Set<String> columnAliases = getColumnAliases(select, selectDefNames);
		Map<String, StringJoiner> bufferMap = new HashMap<String, StringJoiner>();
		StringJoiner sb = null;

		for (String columnAlias : columnAliases) {

			String selectDefName = aliasMap.get(columnAlias);
			if (selectDefName == null) {
				SimpleException ex = new SimpleException(ERROR_CODES.SELECT_EXPANSION_KEY_NOT_FOUND.toString(), "Key '" + columnAlias + "' does not exist!");
				ex.setData(columnAlias);
				throw ex;
			}
			Map<String, Object> selectDef = expansion.get(selectDefName);
			usedJSONDefNames.add(selectDefName);

			sb = bufferMap.getOrDefault(selectDefName, new StringJoiner(", "));
			bufferMap.put(selectDefName, sb);

			Object def = selectDef.get(columnAlias);
			buildRec(def, sb, columnAlias, bufferMap, recursive);
		}

		expandedSelects.clear();
		for (Entry<String, StringJoiner> entry : bufferMap.entrySet()) {
			if (entry.getValue() != null && entry.getValue().length() > 0) {
				expandedSelects.put(entry.getKey(), entry.getValue().toString());
			}
		}

		dirty = false;
	}

	public List<String> getUsedAliases() {
		if (dirty)
			throw new RuntimeException("Select expansion definition changed, run build()!");

		return new ArrayList<String>(usedJSONAliases);
	}

	public List<String> getUsedDefNames() {
		if (dirty)
			throw new RuntimeException("Select expansion definition changed, run build()!");
		return new ArrayList<String>(usedJSONDefNames);
	}

	public Map<String, String> getExpandedSelects() {
		if (dirty)
			throw new RuntimeException("Select expansion definition changed, run build()!");
		return expandedSelects;
	}

	public Map<String, String> getExpandedSelects(final String... defNames) {
		Map<String, String> exp = getExpandedSelects();
		if (defNames == null || defNames.length == 0) {
			return exp;
		}
		Map<String, String> res = new HashMap<String, String>();
		for (String defName : defNames) {
			for (String subDefName : hierarchy.get(defName)) {
				if (exp.get(subDefName) != null) {
					res.put(subDefName, exp.get(subDefName));
				}
			}
		}
		return res;
	}

	@SuppressWarnings("unchecked")
	private void buildRec(Object def, StringJoiner sb, String alias, Map<String, StringJoiner> bufferMap, boolean recursive) {
		if (def == null)
			return;
		if (def instanceof String) {
			usedJSONAliases.add(alias);
			usedJSONDefNames.add(aliasMap.get(alias));
			bufferMap.put(aliasMap.get(alias), sb.add("" + def + " as " + alias));
		} else if (def instanceof Map) {
			usedJSONAliases.add(alias);
			for (Entry<String, Object> e : ((Map<String, Object>) def).entrySet()) {
				String selectDefName = aliasMap.get(e.getKey());
				sb = bufferMap.getOrDefault(selectDefName, new StringJoiner(", "));
				bufferMap.put(selectDefName, sb);
				if (recursive) {
					buildRec(e.getValue(), sb, e.getKey(), bufferMap, recursive);
				}
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

	public static void main(String[] args) throws Exception {
		SelectExpansion se = new SelectExpansion();
		se.addExpansion("A", "a", "a.A1");
		se.addExpansion("A", "b", "a.B1");
		se.addExpansion("B", "x", "kkk.B1");
		se.addSubExpansion("B", "y", "A");

		se.addExpansion("X", "h", "h.h");

		se.addSubExpansion("A", "c", "X");

//		res = se._expandSelect("a", "A");
//		System.out.println(res);
//
//		res = se._expandSelect("a", "B");
//		System.out.println(res);
//
//		res = se._expandSelect("a, b", "B");
//		System.out.println(res);

		se.build("y", true, "B");
		System.out.println(se.getExpandedSelects());
		System.out.println(se.getUsedAliases());
		System.out.println(se.getUsedDefNames());
	}

}
