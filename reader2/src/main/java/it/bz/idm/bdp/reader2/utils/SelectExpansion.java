package it.bz.idm.bdp.reader2.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringJoiner;

public class SelectExpansion {

	public static enum ErrorCode {
		SELECT_EXPANSION_KEY_NOT_FOUND 			("Key '%s' does not exist!"),
		SELECT_EXPANSION_KEY_NOT_INSIDE_DEFLIST ("Key '%s' is not reachable from the expanded select definition list: %s"),
		SELECT_EXPANSION_DEFINITION_NOT_FOUND   ("Select definition contains an invalid name: %s");

		private final String msg;
		ErrorCode(String msg) {
			this.msg = msg;
		}
		public String getMsg() {
			return "SELECT EXPANSION ERROR: " + msg;
		}
	}

	public static enum RecursionType {
		NONE	(0),
		SINGLE	(1),
		FULL	(8);

		private final int id;
		RecursionType(int id) {
			this.id = id;
		}
		public int getValue() {
			return id;
		}
		public static RecursionType getDefault() {
			return FULL;
		}
	}

	private final Map<String, Map<String, Object>> expansion = new HashMap<String, Map<String, Object>>();
	private final Map<String, Set<String>> hierarchyFull = new HashMap<String, Set<String>>();
	private final Map<String, Set<String>> hierarchySingle = new HashMap<String, Set<String>>();
	private final Map<String, String> aliasMap = new HashMap<String, String>();

	private Set<String> usedJSONAliases = new HashSet<String>();
	private Set<String> usedJSONDefNames = new HashSet<String>();
	private Map<String, String> expandedSelects = new HashMap<String, String>();

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

		for (Entry<String, Set<String>> e : hierarchyFull.entrySet()) {
			if (e.getKey().equals(subDefName)) {
				hierarchyFull.get(defName).addAll(e.getValue());
			}
		}

		hierarchySingle.get(defName).add(subDefName);
	}

	public Map<String, Object> getExpansion(final String defName) {
		return expansion.get(defName);
	}

	private Set<String> getAllKeys(Set<String> defNames) {
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

		Set<String> defSet = hierarchyFull.getOrDefault(defName, new HashSet<String>());
		defSet.add(defName);
		hierarchyFull.put(defName, defSet);

		Set<String> defSetSingle = hierarchySingle.getOrDefault(defName, new HashSet<String>());
		defSetSingle.add(defName);
		hierarchySingle.put(defName, defSetSingle);

		return definition;
	}

	private Set<String> getDefNames(Set<String> selectDefNames, RecursionType recType) {
		if (!hierarchyFull.keySet().containsAll(selectDefNames)) {
			ErrorCode code = ErrorCode.SELECT_EXPANSION_DEFINITION_NOT_FOUND;
			SimpleException ex = new SimpleException(code.toString(), String.format(code.getMsg(), selectDefNames.toString()));
			ex.addData("select definitions", selectDefNames);
			throw ex;
		}
		Set<String> defNames = new HashSet<String>();
		for (String givenDefName : selectDefNames) {
			switch (recType) {
				case FULL:
					defNames.addAll(hierarchyFull.get(givenDefName));
				break;
				case SINGLE:
					defNames.addAll(hierarchySingle.get(givenDefName));
				break;
				case NONE:
					// nothing to do
				break;
			}
		}
		return defNames;
	}

	private Set<String> getColumnAliases(String select, Set<String> selectDefNames, RecursionType recType) {
		if (select == null || select.trim().equals("*")) {
			return getAllKeys(selectDefNames);
		}

		Set<String> aliases = csvToSet(select);
		for (String alias : aliases) {
			String selectDefName = aliasMap.get(alias);

			// Check if alias exists
			if (selectDefName == null) {
				ErrorCode code = ErrorCode.SELECT_EXPANSION_KEY_NOT_FOUND;
				SimpleException ex = new SimpleException(code.toString(), String.format(code.getMsg(), alias));
				ex.addData("alias", alias);
				throw ex;
			}

			// Check if the found select definition is within the given set of definitions
			if (!selectDefNames.contains(selectDefName)) {
				ErrorCode code = ErrorCode.SELECT_EXPANSION_KEY_NOT_INSIDE_DEFLIST;
				SimpleException ex = new SimpleException(code.toString(), String.format(code.getMsg(), alias, selectDefNames.toString()));
				ex.addData("alias", alias);
				ex.addData("select definitions", selectDefNames);
				throw ex;
			}
		}
		return aliases;
	}

	public void build(String select, String... selectDefNames) {
		build(select, RecursionType.getDefault(), selectDefNames);
	}

	public void build(String select, RecursionType recType, String... selectDefNames) {
		Set<String> selectDefNameSet = getDefNames(new HashSet<String>(Arrays.asList(selectDefNames)), recType);
		Set<String> columnAliases = getColumnAliases(select, selectDefNameSet, recType);
		Map<String, StringJoiner> bufferMap = new HashMap<String, StringJoiner>();
		StringJoiner sb = null;

		for (String columnAlias : columnAliases) {
			String selectDefName = aliasMap.get(columnAlias);
			usedJSONDefNames.add(selectDefName);
			sb = bufferMap.getOrDefault(selectDefName, new StringJoiner(", "));
			bufferMap.put(selectDefName, sb);
			Object def = expansion.get(selectDefName).get(columnAlias);
			buildRec(def, sb, columnAlias, bufferMap, recType.getValue(), selectDefNameSet);
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
			for (String subDefName : hierarchyFull.get(defName)) {
				if (exp.get(subDefName) != null) {
					res.put(subDefName, exp.get(subDefName));
				}
			}
		}
		return res;
	}

	@SuppressWarnings("unchecked")
	private void buildRec(Object def, StringJoiner sb, String alias, Map<String, StringJoiner> bufferMap, int recursionLevel, Set<String> selectDefNames) {
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
				if (recursionLevel > 0 || selectDefNames.contains(selectDefName)) {
					buildRec(e.getValue(), sb, e.getKey(), bufferMap, recursionLevel - 1, selectDefNames);
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
		se.addExpansion("A", "a", "A.a");
		se.addExpansion("A", "b", "A.b");
		se.addSubExpansion("A", "c", "X");
		se.addExpansion("B", "x", "B.x");
		se.addSubExpansion("B", "y", "A");
		se.addExpansion("X", "h", "X.h");

//		res = se._expandSelect("a", "A");
//		System.out.println(res);
//
//		res = se._expandSelect("a", "B");
//		System.out.println(res);
//
//		res = se._expandSelect("a, b", "B");
//		System.out.println(res);


////		{}
////		[y]
////		[B]
//		se.build("y", RecursionType.NONE, "B");
//		System.out.println(se.getExpandedSelects());
//		System.out.println(se.getUsedAliases());
//		System.out.println(se.getUsedDefNames());
//
////		{B=B.x as x}
////		[x, y]
////		[B]
//		se.build("x, y", RecursionType.NONE, "B");
//		System.out.println(se.getExpandedSelects());
//		System.out.println(se.getUsedAliases());
//		System.out.println(se.getUsedDefNames());

//		{A=A.a as a, A.b as b, B=B.x as x}
//		[a, b, c, x, y]
//		[A, B]
		se.build("x, y", RecursionType.SINGLE, "A");
		System.out.println(se.getExpandedSelects());
		System.out.println(se.getUsedAliases());
		System.out.println(se.getUsedDefNames());

////		{A=A.a as a, A.b as b, B=B.x as x, X=X.h as h}
////		[a, b, c, x, h, y]
////		[A, B, X]
//		se.build("x, y", RecursionType.FULL, "B");
//		System.out.println(se.getExpandedSelects());
//		System.out.println(se.getUsedAliases());
//		System.out.println(se.getUsedDefNames());

	}

}
