package it.bz.idm.bdp.reader2.utils.querybuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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

//	private final Map<String, SelectDefinition> schema = new HashMap<String, SelectDefinition>();
	private final SelectSchema schema = new SelectSchema();
	private final Map<String, Set<String>> hierarchyDefsFull = new HashMap<String, Set<String>>();
	private final Map<String, Set<String>> hierarchyDefsSingle = new HashMap<String, Set<String>>();
//	private final Map<String, Set<String>> hierarchyAliasesFull = new HashMap<String, Set<String>>();
//	private final Map<String, Set<String>> hierarchyAliasesSingle = new HashMap<String, Set<String>>();
//	private final Map<String, String> aliasMap = new HashMap<String, String>();
//	private final Map<String, String> pointerMap = new HashMap<String, String>();

//	private Set<String> usedJSONAliases = new HashSet<String>();
//	private Set<String> usedJSONDefNames = new HashSet<String>();
//	private Map<String, String> expandedSelects = new HashMap<String, String>();

	private boolean dirty = true;


	public void addExpansion(final String defName, String alias, String column) {
		schema.addColumn(defName, alias, column);

//		if (alias == null || alias.isEmpty() || column == null || column.isEmpty()) {
//			throw new RuntimeException("Expansion alias and column must be set!");
//		}
//		_addExpansion(defName, alias, column);
//
//		for (Entry<String, String> p : pointerMap.entrySet()) {
//			if (p.getValue().equals(defName)) {
//				hierarchyAliasesSingle.get(p.getKey()).add(alias);
//			}
//		}
//
//		System.out.println(hierarchyAliasesSingle);
//		System.out.println(pointerMap);
	}

	public void addSubExpansion(final String defName, String alias, String subDefName) {
		schema.addSubSelectDefinition(defName, alias, subDefName);

//		SelectDefinition subdefinition = _addExpansion(subDefName, null, null);
//		SelectDefinition definition = _addExpansion(defName, alias, null);
//
//		definition.addPointer(alias, subdefinition);
//
//		pointerMap.put(alias, subDefName);
//
		for (Entry<String, Set<String>> e : hierarchyDefsFull.entrySet()) {
			if (e.getKey().equals(subDefName)) {
				hierarchyDefsFull.get(defName).addAll(e.getValue());
			}
		}
//
//		hierarchyDefsSingle.get(defName).add(subDefName);
//
//		for (String p : pointerMap.values()) {
//			if (p.equals(defName)) {
//				hierarchyAliasesSingle.get(p).add(alias);
//			}
//		}
//		System.out.println(hierarchyAliasesSingle);
//		System.out.println(pointerMap);
	}

	public SelectDefinition getExpansion(final String defName) {
		return schema.get(defName);
	}

	private Set<String> getAllKeys(Set<String> defNames) {
		Set<String> columnAliases = new HashSet<String>();
		for (String defName : defNames) {
			columnAliases.addAll(schema.get(defName).getAliases());
		}
		return columnAliases;
	}

//	private SelectDefinition _addExpansion(final String defName, String alias, String column) {
//		if (defName == null || defName.isEmpty()) {
//			throw new RuntimeException("Expansion definition name must be set!");
//		}
//		dirty = true;
//
//		SelectDefinition definition = schema.getOrDefault(defName, new SelectDefinition(defName));
//		if (alias != null) {
//			if (column != null) {
//				definition.addAlias(alias, column);
//			}
//			aliasMap.put(alias, defName);
//		}
//		schema.put(defName, definition);
//
//		Set<String> defSet = hierarchyDefsFull.getOrDefault(defName, new HashSet<String>());
//		defSet.add(defName);
//		hierarchyDefsFull.put(defName, defSet);
//
//		Set<String> defSetSingle = hierarchyDefsSingle.getOrDefault(defName, new HashSet<String>());
//		defSetSingle.add(defName);
//		hierarchyDefsSingle.put(defName, defSetSingle);
//
//		if (alias != null && column == null) {
//			Set<String> defAliasSingle = hierarchyAliasesSingle.getOrDefault(alias, new HashSet<String>());
//			hierarchyAliasesSingle.put(alias, defAliasSingle);
//
//			Set<String> defAliasFull = hierarchyAliasesFull.getOrDefault(alias, new HashSet<String>());
//			hierarchyAliasesFull.put(alias, defAliasFull);
//		}
//
//		return definition;
//	}

//	private Set<String> getDefNames(Set<String> selectDefNames, RecursionType recType) {
////		if (!hierarchyDefsFull.keySet().containsAll(selectDefNames)) {
////			ErrorCode code = ErrorCode.SELECT_EXPANSION_DEFINITION_NOT_FOUND;
////			SimpleException ex = new SimpleException(code.toString(), String.format(code.getMsg(), selectDefNames.toString()));
////			ex.addData("select definitions", selectDefNames);
////			throw ex;
////		}
//
//		if (recType == RecursionType.NONE) {
//			return selectDefNames;
//		}
//
//		Map<String, Set<String>> curHierarchy = recType == RecursionType.FULL ? hierarchyDefsFull : hierarchyDefsSingle;
//
//		Set<String> defNames = new HashSet<String>();
//		for (String givenDefName : selectDefNames) {
//			defNames.addAll(curHierarchy.get(givenDefName));
//		}
//		return defNames;
//	}

	private Set<String> getColumnAliases(String select, Set<String> selectDefNames, RecursionType recType) {
		if (select == null || select.trim().equals("*")) {
			return getAllKeys(selectDefNames);
		}

		Set<String> aliases = csvToSet(select);
		for (String alias : aliases) {
			String selectDefName = schema.getAliasMap().get(alias);

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
//		build(select, RecursionType.getDefault(), selectDefNames);
		schema.expand(QueryBuilder.csvToSet(select), new HashSet<String>(Arrays.asList(selectDefNames)));
	}

//	public void build(String select, RecursionType recType, String... selectDefNames) {
//		Set<String> selectDefNameSet = getDefNames(new HashSet<String>(Arrays.asList(selectDefNames)), recType);
//		Set<String> columnAliases = getColumnAliases(select, selectDefNameSet, recType);
//		Map<String, StringJoiner> bufferMap = new HashMap<String, StringJoiner>();
//		StringJoiner sb = null;
//
//		for (String columnAlias : columnAliases) {
//			String selectDefName = schema.getAliasMap().get(columnAlias);
//			sb = bufferMap.getOrDefault(selectDefName, new StringJoiner(", "));
//			Object def = schema.get(selectDefName).isColumn(columnAlias);
//			usedJSONDefNames.add(selectDefName);
//			usedJSONAliases.add(columnAlias);
//			if (def instanceof String) {
//				bufferMap.put(selectDefName, sb);
//				usedJSONDefNames.add(schema.getAliasMap().get(columnAlias));
//				bufferMap.put(schema.getAliasMap().get(columnAlias), sb.add("" + def + " as " + columnAlias));
////				buildRec(def, sb, columnAlias, bufferMap, recType.getValue(), selectDefNameSet);
//			}
//		}
//
//		expandedSelects.clear();
//		for (Entry<String, StringJoiner> entry : bufferMap.entrySet()) {
//			if (entry.getValue() != null && entry.getValue().length() > 0) {
//				expandedSelects.put(entry.getKey(), entry.getValue().toString());
//			}
//		}
//
//		dirty = false;
//	}

	public List<String> getUsedAliases() {
		if (dirty)
			throw new RuntimeException("Select expansion definition changed, run build()!");

		return new ArrayList<String>(schema.getUsedAliases());
	}

	public List<String> getUsedDefNames() {
		if (dirty)
			throw new RuntimeException("Select expansion definition changed, run build()!");
		return new ArrayList<String>(schema.getUsedDefNames());
	}

	public Map<String, String> getExpandedSelects() {
		if (dirty)
			throw new RuntimeException("Select expansion definition changed, run build()!");
		return schema.getExpansion();
	}

	public Map<String, String> getExpandedSelects(final String... defNames) {
		Map<String, String> exp = getExpandedSelects();
		if (defNames == null || defNames.length == 0) {
			return exp;
		}
		Map<String, String> res = new HashMap<String, String>();
		for (String defName : defNames) {
			for (String subDefName : hierarchyDefsFull.get(defName)) {
				if (exp.get(subDefName) != null) {
					res.put(subDefName, exp.get(subDefName));
				}
			}
		}
		return res;
	}

//	@SuppressWarnings("unchecked")
//	private void buildRec(Object def, StringJoiner sb, String alias, Map<String, StringJoiner> bufferMap, int recursionLevel, Set<String> selectDefNames) {
//		if (def == null)
//			return;
//		if (def instanceof String) {
//			usedJSONAliases.add(alias);
//			usedJSONDefNames.add(schema.getAliasMap().get(alias));
//			bufferMap.put(schema.getAliasMap().get(alias), sb.add("" + def + " as " + alias));
//		} else if (def instanceof Map) {
//			usedJSONAliases.add(alias);
//			for (Entry<String, Object> e : ((Map<String, Object>) def).entrySet()) {
//				String selectDefName = schema.getAliasMap().get(e.getKey());
//				sb = bufferMap.getOrDefault(selectDefName, new StringJoiner(", "));
//				bufferMap.put(selectDefName, sb);
//				if (recursionLevel > 0 || selectDefNames.contains(selectDefName)) {
//					buildRec(e.getValue(), sb, e.getKey(), bufferMap, recursionLevel - 1, selectDefNames);
//				}
//			}
//		} else {
//			throw new RuntimeException("A select definition must contain either Strings or Maps!");
//		}
//	}

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
		se.addExpansion("C", "h", "C.h");
		se.addExpansion("B", "x", "B.x");
		se.addSubExpansion("B", "y", "C");
		se.addExpansion("A", "a", "A.a");
		se.addExpansion("A", "b", "A.b");
		se.addSubExpansion("A", "c", "B");

		System.out.println(se.schema);

		String select = "c";
		String defs = "A, B";
		Set<String> defNames = QueryBuilder.csvToSet(defs);

		String result = "";
		List<String> candidateAliases = new ArrayList<String>(QueryBuilder.csvToSet(select));
		int curPos = 0;
		while (curPos < candidateAliases.size()) {
			String alias = candidateAliases.get(curPos);
			System.out.println("Alias = " + alias + "; candidates = " + candidateAliases);

			SelectDefinition def = se.schema.getDefinition(alias, defNames);
			if (def == null) {
				throw new RuntimeException("Not found");
			}

			if (def.isColumn(alias)) {
				result += alias + ", ";
			} else {
				SelectDefinition pointsTo = def.getPointersOnly().get(alias);
				if (defNames.contains(pointsTo.getName())) {
					result += alias + ", ";
					candidateAliases.addAll(pointsTo.getAliases());
				}
			}
			curPos++;
		}

		System.out.println(result);

		se.schema.expand(QueryBuilder.csvToSet(select), defNames);
		System.out.println(se.schema.getUsedAliases());
		System.out.println(se.schema.getUsedDefNames());
		System.out.println(se.schema.getExpansion());

//		res = se._expandSelect("a", "A");
//		System.out.println(res);
//
//		res = se._expandSelect("a", "B");
//		System.out.println(res);
//
//		res = se._expandSelect("a, b", "B");
//		System.out.println(res);


//		{}
//		[y]
//		[B]
		se.schema.expand("y", "B");
		System.out.println(se.schema.getUsedAliases());
		System.out.println(se.schema.getUsedDefNames());
		System.out.println(se.schema.getExpansion());
		System.out.println(se.getExpandedSelects());
		System.out.println(se.getUsedAliases());
		System.out.println(se.getUsedDefNames());
//
////		{B=B.x as x}
////		[x, y]
////		[B]
//		se.build("x, y", RecursionType.NONE, "B");
//		System.out.println(se.getExpandedSelects());
//		System.out.println(se.getUsedAliases());
//		System.out.println(se.getUsedDefNames());

////		{A=A.a as a, A.b as b, B=B.x as x}
////		[a, b, c, x, y]
////		[A, B]
//		se.build("x, y", RecursionType.SINGLE, "B");
//		System.out.println(se.getExpandedSelects());
//		System.out.println(se.getUsedAliases());
//		System.out.println(se.getUsedDefNames());
//
//		se.build("*", RecursionType.NONE, "A");
//		System.out.println(se.getExpandedSelects());
//		System.out.println(se.getUsedAliases());
//		System.out.println(se.getUsedDefNames());
//
//		se.build("*", RecursionType.NONE, "A", "B");
//		System.out.println(se.getExpandedSelects());
//		System.out.println(se.getUsedAliases());
//		System.out.println(se.getUsedDefNames());
//
//
////		{A=A.a as a, A.b as b, B=B.x as x, X=X.h as h}
////		[a, b, c, x, h, y]
////		[A, B, X]
//		se.build("x, y", RecursionType.FULL, "B");
//		System.out.println(se.getExpandedSelects());
//		System.out.println(se.getUsedAliases());
//		System.out.println(se.getUsedDefNames());

	}

}
