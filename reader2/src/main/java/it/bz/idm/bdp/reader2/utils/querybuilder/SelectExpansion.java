package it.bz.idm.bdp.reader2.utils.querybuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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

	private Map<String, SelectDefinition> schema = new HashMap<String, SelectDefinition>();
	private Map<String, String> aliases = new HashMap<String, String>();
	private Map<String, String> pointers = new HashMap<String, String>();
	private Map<String, String> expandedSelects = new HashMap<String, String>();
	private Set<String> usedJSONAliases = new HashSet<String>();
	private Set<String> usedJSONDefNames = new HashSet<String>();
	private boolean dirty = true;

	public void add(final String name, final SelectDefinition selDef) {
		if (name == null || name.isEmpty() || selDef == null) {
			throw new RuntimeException("A schema entry must have a name and a valid definition!");
		}
		schema.put(name, selDef);
		dirty = true;
	}

	public SelectDefinition get(final String name) {
		dirty = true;
		return schema.get(name);
	}

	public SelectDefinition getOrNew(final String name) {
		SelectDefinition res = schema.get(name);
		if (res == null)
			return new SelectDefinition(name);
		return res;
	}

	public void addColumn(final String name, final String alias, final String column) {
		SelectDefinition selDef = getOrNew(name);
		selDef.addAlias(alias, column);
		schema.put(name, selDef);
		dirty = true;
	}

	public void addSubDef(final String name, final String alias, final String subName) {
		SelectDefinition subSelDef = get(subName);
		if (subSelDef == null) {
			throw new RuntimeException("Select Definition '" + subName + "' not found! It must exist before we can point to it!");
		}
		SelectDefinition selDef = getOrNew(name);
		selDef.addPointer(alias, subSelDef);
		schema.put(name, selDef);
		dirty = true;
	}

	public Map<String, String> getAliasMap() {
		if (!dirty) {
			return aliases;
		}
		aliases.clear();
		for (SelectDefinition defs : schema.values()) {
			for (String alias : defs.getAliases()) {
				aliases.put(alias, defs.getName());
			}
		}
		return aliases;
	}

	public Map<String, String> getPointerMap() {
		if (!dirty) {
			return pointers;
		}
		pointers.clear();
		for (SelectDefinition defs : schema.values()) {
			for (String alias : defs.getPointersOnly().keySet()) {
				pointers.put(alias, defs.getName());
			}
		}
		return pointers;
	}

	public Set<String> getAliases(Set<String> defNames) {
		Set<String> res = new HashSet<String>();
		for (SelectDefinition def : getDefinition(defNames)) {
			 res.addAll(def.getAliases());
		}
		return res;
	}

	public SelectDefinition getDefinition(final String alias, Set<String> defNames) {
		for (SelectDefinition def : getDefinition(defNames)) {
			if (def.getAliases().contains(alias))
				return def;
		}
		return null;
	}

	public Set<SelectDefinition> getDefinition(Set<String> defNames) {
		Set<SelectDefinition> res = new HashSet<SelectDefinition>();
		for (String defName : defNames) {
			SelectDefinition def = schema.get(defName);
			if (def == null) {
				throw new RuntimeException("Select definition with name '" + defName + "' does not exist!");
			}
			res.add(def);
		}
		return res;
	}

	public void expand(final Set<String> aliases, final Set<String> defNames) {
		if (aliases == null || aliases.isEmpty() || defNames == null || defNames.isEmpty()) {
			throw new RuntimeException("EXPAND: Provide valid alias and definition sets!");
		}
		List<String> candidateAliases = null;
		if (aliases.size() == 1 && aliases.contains("*")) {
			candidateAliases = new ArrayList<String>(getAliases(defNames));
		} else {
			candidateAliases = new ArrayList<String>(aliases);
		}
		int curPos = 0;
		while (curPos < candidateAliases.size()) {
			String alias = candidateAliases.get(curPos);
			SelectDefinition def = getDefinition(alias, defNames);
			if (def == null) {
				throw new RuntimeException("Not found");
			}

			if (def.isColumn(alias)) {
				usedJSONAliases.add(alias);
				String defName = getAliasMap().get(alias);
				usedJSONDefNames.add(defName);
				String sqlSelect = expandedSelects.getOrDefault(defName, "");
				expandedSelects.put(defName, sqlSelect + def.getColumn(alias) + " as " + alias);
			} else {
				SelectDefinition pointsTo = def.getPointersOnly().get(alias);
				if (defNames.contains(pointsTo.getName())) {
					usedJSONAliases.add(alias);
					usedJSONDefNames.add(getAliasMap().get(alias));
					candidateAliases.addAll(pointsTo.getAliases());
				}
			}
			curPos++;
		}
		dirty = false;
	}

	public void expand(final String aliases, String... defNames) {
		expand(_csvToSet(aliases), new HashSet<String>(Arrays.asList(defNames)));
	}

	public Set<String> getUsedAliases() {
		if (dirty) {
			throw new RuntimeException("We are in a dirty state. You need to run expand() before getting used aliases!");
		}
		return usedJSONAliases;
	}

	public Set<String> getUsedDefNames() {
		if (dirty) {
			throw new RuntimeException("We are in a dirty state. You need to run expand() before getting used definition names!");
		}
		return usedJSONDefNames;
	}

	public Map<String, String> getExpansion() {
		if (dirty) {
			throw new RuntimeException("We are in a dirty state. You need to run expand() before getting an expanded select statement!");
		}
		return expandedSelects;
	}

	public String getExpansion(String defName) {
		return getExpansion().get(defName);
	}

	public Map<String, String> getExpansion(Set<String> defNames) {
		Map<String, String> res = new HashMap<String, String>();
		for (String defName : defNames) {
			 String exp = getExpansion(defName);
			 if (exp == null) {
				 throw new RuntimeException("Select definition with name '" + defName + "' does not exist!");
			 }
			 res.put(defName, exp);
		}
		return res;
	}

	@Override
	public String toString() {
		return "SelectSchema [schema=" + schema + "]";
	}

	private static Set<String> _csvToSet(final String csv) {
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
		se.addColumn("C", "h", "C.h");
		se.addColumn("B", "x", "B.x");
		se.addSubDef("B", "y", "C");
		se.addColumn("A", "a", "A.a");
		se.addColumn("A", "b", "A.b");
		se.addSubDef("A", "c", "B");

//		System.out.println(se.schema);

//		String select = "c";
//		String defs = "A, B";
//		Set<String> defNameSet = QueryBuilder.csvToSet(defs);
//		Set<String> selectSet = QueryBuilder.csvToSet(select);
//
//		se.expand(selectSet, defNameSet);
//		System.out.println(se.getUsedAliases());
//		System.out.println(se.getUsedDefNames());
//		System.out.println(se.getExpansion());

//		res = se._expandSelect("a", "A");
//		System.out.println(res);
//
//		res = se._expandSelect("a", "B");
//		System.out.println(res);
//
//		res = se._expandSelect("a, b", "B");
//		System.out.println(res);


//		{}
//		[]
//		[]
		se.expand("y", "B");
		System.out.println(se.getExpansion());
		System.out.println(se.getUsedAliases());
		System.out.println(se.getUsedDefNames());

//		{B=B.x as x}
//		[x, y]
//		[B]
		se.expand("x, y", "B");
		System.out.println(se.getExpansion());
		System.out.println(se.getUsedAliases());
		System.out.println(se.getUsedDefNames());

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
