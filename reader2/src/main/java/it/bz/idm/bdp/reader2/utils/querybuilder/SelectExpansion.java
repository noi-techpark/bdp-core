package it.bz.idm.bdp.reader2.utils.querybuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;

/**
 * <p>A select expansion starts from a {@link SelectDefinition} and finds out which
 * tables or columns must be used, given a select targetlist and where clauses.
 * It is also a mapping from aliases to column names.
 *
 * <p>Example:
 * Assume, that we have three select definitions with associated aliases:
 *    A(a,b,c), B(x,y) and C(h),
 *
 * <p>...where c and y are foreign keys, referring to B and C respectively. This gives the
 * following hierarchy:
 * <pre>
 *    A(a,b,c)
 *          `-B(x,y)
 *                `-C(h)
 * </pre>
 *
 *  <p>If we want to select aliases [a,b,c] from definitions [A] now, we get the following result:
 *  - used aliases    : a, b   (not c, because it would be empty, since we do not use the definition B)
 *  - used definitions: A
 *
 * <p>Another example, selecting aliases [a,b,c] from defintions [A,B] gives:
 * - used aliases     : a, b, c, x
 * - used definitions : A, B
 *
 * <p>In addition to a list of used aliases and definitions, that can be used to determine, if we should
 * join to a certain table or not, important for conditional query building, we can retrieve a select
 * expansion. This is, a SQL snippet that contains a target list of columns and aliases.
 *
 * <p>Assume a structure with columns as follows:
 * <pre>
 *    EMPLOYEE(ename->emp.fullname,emanager)
 *                                    `-------MANAGER(mname->mgr.fullname)
 * </pre>
 *
 * <p>...and a select [ename,emanager] with definitions [EMPLOYEE,MANAGER], then the expansion to be inserted
 * into your SQL is this map:
 *
 * <pre>
 *    {
 *      EMPLOYEE="emp.fullname as ename",
 *      MANAGER="mgr.fullname as mname"
 *    }
 * </pre>
 *
 * <p>NB: Do not confound definitions with tables, defintion names and aliases can be used to create a hierarchical
 * JSON structure, tables and columns are encoded within the column string itself (ex., emp.fullname).
 *
 * @author Peter Moser <p.moser@noi.bz.it>
 */
public class SelectExpansion {

	public static enum ErrorCode implements ErrorCodeInterface {
		KEY_NOT_FOUND 	         ("Key '%s' does not exist"),
		KEY_NOT_INSIDE_DEFLIST   ("Key '%s' is not reachable from the expanded select definition list: %s"),
		DEFINITION_NOT_FOUND     ("Select Definition '%s' not found! It must exist before we can point to it"),
		ADD_INVALID_DATA         ("A schema entry must have a name and a valid definition"),
		WHERE_SYNTAX_ERROR       ("Syntax Error in WHERE clause: '%s' is not a triple: alias.operator.value"),
		WHERE_ALIAS_NOT_FOUND    ("Syntax Error in WHERE clause: Alias '%s' does not exist"),
		WHERE_OPERATOR_NOT_FOUND ("Syntax Error in WHERE clause: Operator '%s' does not exist"),
		DIRTY_STATE              ("We are in a dirty state. Run expand() to clean up"),
		EXPAND_INVALID_DATA      ("Provide valid alias and definition sets!");

		private final String msg;
		ErrorCode(String msg) {
			this.msg = msg;
		}

		@Override
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
	private String sqlWhere = null;
	private String whereClause = null;
	private boolean dirty = true;

	public void add(final String name, final SelectDefinition selDef) {
		if (name == null || name.isEmpty() || selDef == null) {
			throw new SimpleException(ErrorCode.ADD_INVALID_DATA);
		}
		schema.put(name, selDef);
		dirty = true;
	}

	public SelectDefinition get(final String name) {
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
			throw new SimpleException(ErrorCode.DEFINITION_NOT_FOUND, subName);
		}
		SelectDefinition selDef = getOrNew(name);
		selDef.addPointer(alias, subSelDef);
		schema.put(name, selDef);
		dirty = true;
	}

	public Map<String, String> getAliasMap() {
		if (dirty) {
			aliases.clear();
			for (SelectDefinition defs : schema.values()) {
				for (String alias : defs.getAliases()) {
					aliases.put(alias, defs.getName());
				}
			}
		}
		return aliases;
	}

	public Map<String, String> getPointerMap() {
		if (dirty) {
			pointers.clear();
			for (SelectDefinition defs : schema.values()) {
				for (String alias : defs.getPointersOnly().keySet()) {
					pointers.put(alias, defs.getName());
				}
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

	public SelectDefinition getDefinition(final String alias) {
		for (SelectDefinition def : schema.values()) {
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
				throw new SimpleException(ErrorCode.DEFINITION_NOT_FOUND, defName);
			}
			res.add(def);
		}
		return res;
	}

	private void _build() {
		dirty = true;
		getAliasMap();
		getPointerMap();
		dirty = false;
	}

	public void expand(final Set<String> aliases, final Set<String> defNames) {
		if (aliases == null || aliases.isEmpty() || defNames == null || defNames.isEmpty()) {
			throw new SimpleException(ErrorCode.EXPAND_INVALID_DATA);
		}
		if (dirty) {
			_build();
		}
		usedJSONAliases.clear();
		usedJSONDefNames.clear();
		expandedSelects.clear();

		/*
		 * We cannot use a HashSet here, because we need a get by position within the while loop.
		 * This is, because we cannot use an iterator here, since we add elements to it while processing.
		 */
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
				SimpleException se = new SimpleException(ErrorCode.KEY_NOT_INSIDE_DEFLIST, alias, defNames);
				se.addData("alias", alias);
				throw se;
			}

			if (def.isColumn(alias)) {
				usedJSONAliases.add(alias);
				String defName = getAliasMap().get(alias);
				usedJSONDefNames.add(defName);
				String sqlSelect = expandedSelects.getOrDefault(defName, null);
				expandedSelects.put(defName, (sqlSelect == null ? "" : sqlSelect + ", ") + def.getColumn(alias) + " as " + alias);
			} else {
				SelectDefinition pointsTo = def.getPointersOnly().get(alias);
				if (defNames.contains(pointsTo.getName())) {
					usedJSONAliases.add(alias);
					usedJSONDefNames.add(getAliasMap().get(alias));
					for (String subAlias : pointsTo.getAliases()) {
						if (!candidateAliases.contains(subAlias)) {
							candidateAliases.add(subAlias);
						}
					}

				}
			}
			curPos++;
		}

		_expandWhere(whereClause);
	}

	private void _expandWhere(String where) {
		if (where == null || where.isEmpty()) {
			sqlWhere = null;
			return;
		}
		StringJoiner result = new StringJoiner(" and ", "and ", "");
		for (String and : where.split("(?<!\\\\),")) {
			String[] sqlWhereClause = and.split("\\.", 3);
			if (sqlWhereClause.length != 3) {
				throw new SimpleException(ErrorCode.WHERE_SYNTAX_ERROR, and);
			}
			String alias = sqlWhereClause[0];
			String operator = sqlWhereClause[1];
			String value = sqlWhereClause[2].replace("'", "").replaceAll("\\\\,", ",");
			String column = getColumn(alias);
			if (column == null) {
				throw new SimpleException(ErrorCode.WHERE_ALIAS_NOT_FOUND, alias);
			}

			usedJSONAliases.add(alias);
			usedJSONDefNames.add(getAliasMap().get(alias));

			String sqlOperator = null;
			switch (operator) {
				case "eq":
					if (value.equalsIgnoreCase("null")) {
						sqlOperator = "is null";
						value = null;
					} else {
						sqlOperator = "=";
					}
					break;
				case "neq":
					if (value.equalsIgnoreCase("null")) {
						sqlOperator = "is not null";
						value = null;
					} else {
						sqlOperator = "<>";
					}
					break;
				case "lt":
					sqlOperator = "<";
					break;
				case "gt":
					sqlOperator = ">";
					break;
				case "lteq":
					sqlOperator = "=<";
					break;
				case "gteq":
					sqlOperator = ">=";
					break;
				case "re":
					sqlOperator = "~";
					break;
				case "ire":
					sqlOperator = "~*";
					break;
				case "nre":
					sqlOperator = "!~";
					break;
				case "nire":
					sqlOperator = "!~*";
					break;
				default:
					throw new SimpleException(ErrorCode.WHERE_OPERATOR_NOT_FOUND, operator);
			}
			result.add(column + " " + sqlOperator + (value == null ? "" : " '" + value + "' "));
		}
		sqlWhere = result.toString();
	}

	public void expand(final String aliases, String... defNames) {
		expand(_csvToSet(aliases), new HashSet<String>(Arrays.asList(defNames)));
	}

	public List<String> getUsedAliases() {
		if (dirty) {
			throw new SimpleException(ErrorCode.DIRTY_STATE);
		}
		return new ArrayList<String>(usedJSONAliases);
	}

	public List<String> getUsedDefNames() {
		if (dirty) {
			throw new SimpleException(ErrorCode.DIRTY_STATE);
		}
		return new ArrayList<String>(usedJSONDefNames);
	}

	public Map<String, String> getExpansion() {
		if (dirty) {
			throw new SimpleException(ErrorCode.DIRTY_STATE);
		}
		return expandedSelects;
	}

	public String getExpansion(String defName) {
		return getExpansion().get(defName);
	}

	public String getColumn(String alias) {
		return getDefinition(alias).getColumn(alias);
	}

	public Map<String, String> getExpansion(Set<String> defNames) {
		if (defNames == null) {
			return getExpansion();
		}
		Map<String, String> res = new HashMap<String, String>();
		for (String defName : defNames) {
			 String exp = getExpansion(defName);
			 if (exp == null) {
				 throw new SimpleException(ErrorCode.DEFINITION_NOT_FOUND, defName);
			 }
			 res.put(defName, exp);
		}
		return res;
	}

	public Map<String, String> getExpansion(String... defNames) {
		if (defNames == null) {
			return getExpansion();
		}
		return getExpansion(new HashSet<String>(Arrays.asList(defNames)));
	}

	public String getWhereSql() {
		if (dirty) {
			throw new SimpleException(ErrorCode.DIRTY_STATE);
		}
		return sqlWhere;
	}

	public void setWhereClause(String where) {
		dirty = true;
		whereClause = where;
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

//		{}
//		[]
//		[]
		se.expand("y", "B");
		System.out.println(se.getExpansion());
		System.out.println(se.getUsedAliases());
		System.out.println(se.getUsedDefNames());

//		{B=B.x as x}
//		[x]
//		[B]
		se.expand("x, y", "B");
		System.out.println(se.getExpansion());
		System.out.println(se.getUsedAliases());
		System.out.println(se.getUsedDefNames());

//		{A=A.a as a, A.b as b, B=B.x as x}
//		[a, b, c, x]
//		[A, B]
		se.expand("a, b, c", "A", "B");
		System.out.println(se.getExpansion());
		System.out.println(se.getUsedAliases());
		System.out.println(se.getUsedDefNames());

		se.expand("*", "A", "B", "C");
		System.out.println(se.getExpansion());
		System.out.println(se.getUsedAliases());
		System.out.println(se.getUsedDefNames());

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
