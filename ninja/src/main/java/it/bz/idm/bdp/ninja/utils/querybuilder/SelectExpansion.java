package it.bz.idm.bdp.ninja.utils.querybuilder;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringJoiner;
import java.util.TreeMap;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.bz.idm.bdp.ninja.utils.miniparser.Consumer;
import it.bz.idm.bdp.ninja.utils.miniparser.ConsumerExtended;
import it.bz.idm.bdp.ninja.utils.miniparser.Token;
import it.bz.idm.bdp.ninja.utils.simpleexception.ErrorCodeInterface;
import it.bz.idm.bdp.ninja.utils.simpleexception.SimpleException;

/**
 * <pre>
 * A select expansion starts from a {@link SelectDefinition} and finds out which
 * tables or columns must be used, given a select targetlist and where clauses.
 * It is also a mapping from aliases to column names.
 *
 * Example:
 * Assume, that we have three select definitions with associated aliases:
 *
 *    A(a,b,c), B(x,y) and C(h),
 *
 * where c and y are foreign keys, referring to B and C respectively. This
 * gives the following hierarchy:
 *
 *    A(a,b,c)
 *          `-B(x,y)
 *                `-C(h)
 *
 *  If we want to select aliases [a,b,c] from definitions [A] now, we get the
 *  following result:
 *  - used aliases    : a, b   (not c, because it would be empty, since we do
 *                              not use the definition B)
 *  - used definitions: A
 *
 * Another example, selecting aliases [a,b,c] from defintions [A,B] gives:
 * - used aliases     : a, b, c, x
 * - used definitions : A, B
 *
 * In addition to a list of used aliases and definitions, that can be used to
 * determine, if we should join to a certain table or not, important for
 * conditional query building, we can retrieve a select expansion. This is, a
 * SQL snippet that contains a target list of columns and aliases.
 *
 * Assume a structure with columns as follows:
 *
 *    EMPLOYEE(ename->emp.fullname,emanager)
 *                                    `-------MANAGER(mname->mgr.fullname)
 *
 * In addition, we have a select [ename,emanager] with definitions [EMPLOYEE,MANAGER],
 * then the expansion to be inserted into your SQL is this map:
 *    {
 *      EMPLOYEE="emp.fullname as ename",
 *      MANAGER="mgr.fullname as mname"
 *    }
 *
 * NB: Do not confound definitions with tables, defintion names and aliases can be
 * used to create a hierarchical JSON structure, tables and columns are encoded
 * within the column string itself (ex., emp.fullname).
 * </pre>
 *
 * @author Peter Moser <p.moser@noi.bz.it>
 */
public class SelectExpansion {

	private static final Logger log = LoggerFactory.getLogger(SelectExpansion.class);
	private static final String ALIAS_VALIDATION = "[0-9a-zA-Z\\._]+"; // Do not forget to update ErrorCode.ALIAS_INVALID

	public static enum ErrorCode implements ErrorCodeInterface {
		KEY_NOT_FOUND("Key '%s' does not exist"),
		KEY_NOT_INSIDE_DEFLIST("Key '%s' is not reachable from the expanded select definition list: %s"),
		DEFINITION_NOT_FOUND("Select Definition '%s' not found! It must exist before we can point to it"),
		ADD_INVALID_DATA("A schema entry must have a name and a valid definition"),
		WHERE_ALIAS_VALUE_ERROR("Syntax Error in WHERE clause: '%s.<%s>' with value %s is not valid (checks failed)"),
		WHERE_ALIAS_NOT_FOUND("Syntax Error in WHERE clause: Alias '%s' does not exist"),
		WHERE_ALIAS_ALREADY_EXISTS("Syntax Error in WHERE clause: Alias '%s' cannot be used more than once"),
		WHERE_OPERATOR_NOT_FOUND("Syntax Error in WHERE clause: Operator '%s.<%s>' does not exist"),
		WHERE_SYNTAX_ERROR("Syntax Error in WHERE clause: %s"),
		DIRTY_STATE("We are in a dirty state. Run expand() to clean up"),
		EXPAND_INVALID_DATA("Provide valid alias and definition sets!"),
		ALIAS_INVALID("The given alias '%s' is not valid. Only the following characters are allowed: 'a-z', 'A-Z', '0-9', '_' and '.'"),
		SELECT_FUNC_NOJSON("It is currently not possible to use GROUPING with JSON fields. Remove '%s' from your SELECT, if you want to use functions.");

		private final String msg;

		ErrorCode(String msg) {
			this.msg = msg;
		}

		@Override
		public String getMsg() {
			return "SELECT EXPANSION ERROR: " + msg;
		}
	}

	/* We use tree sets and maps here, because we want to have elements naturally sorted */
	private Map<String, SelectDefinition> schema = new TreeMap<String, SelectDefinition>();
	private Map<String, String> aliases = new TreeMap<String, String>();
	private Map<String, String> pointers = new TreeMap<String, String>();
	private Map<String, String> expandedSelects = new TreeMap<String, String>();
	private Set<String> usedJSONAliases = new TreeSet<String>();
	private Map<String, List<String>> functionsAndGroups = new TreeMap<String, List<String>>();
	private List<String> groupByCandidates = new ArrayList<String>();
	private Set<String> usedJSONDefNames = new TreeSet<String>();
	private Map<String, List<Token>> usedJSONAliasesInWhere = new TreeMap<String, List<Token>>();
	private Map<String, WhereClauseOperator> whereClauseOperatorMap = new TreeMap<String, WhereClauseOperator>();

	private Map<String, Object> whereParameters = null;
	private String whereSQL = null;
	private String whereClause = null;
	private boolean dirty = true;

	public void addOperator(String tokenType, String operator, String sqlSnippet) {
		addOperator(tokenType, operator, sqlSnippet, null);
	}

	public void addOperator(String tokenType, String operator, String sqlSnippet, Consumer check) {
		String opName = tokenType.toUpperCase() + "/" + operator.toUpperCase();
		whereClauseOperatorMap.put(opName, new WhereClauseOperator(opName, sqlSnippet, check));
	}

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

	public void addColumn(final String name, final String alias, final String column, final String sqlBefore, final String sqlAfter) {
		SelectDefinition selDef = getOrNew(name);
		selDef.addAlias(alias, column, sqlBefore, sqlAfter);
		schema.put(name, selDef);
		dirty = true;
	}

	public void addColumn(final String name, final String alias, final String column) {
		addColumn(name, alias, column, null, null);
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
		for (SelectDefinition def : getDefinitions(defNames)) {
			res.addAll(def.getAliases());
		}
		return res;
	}

	public SelectDefinition getDefinitionByAlias(final String alias, Set<String> defNames) {
		for (SelectDefinition def : getDefinitions(defNames)) {
			if (def.getAliases().contains(alias))
				return def;
		}
		return null;
	}

	public SelectDefinition getDefinitionByAlias(final String alias) {
		for (SelectDefinition def : schema.values()) {
			if (def.getAliases().contains(alias))
				return def;
		}
		return null;
	}

	public SelectDefinition getDefinition(final String defName) {
		SelectDefinition def = schema.get(defName);
		if (def == null) {
			throw new SimpleException(ErrorCode.DEFINITION_NOT_FOUND, defName);
		}
		return def;
	}

	public SelectDefinition getParentDefinition(final String defName, Set<String> defNames) {
		for (SelectDefinition def : getDefinitions(defNames)) {
			for (SelectDefinition child : def.getPointersOnly().values()) {
				if (child.getName().equals(defName))
					return def;
			}
		}
		return null;
	}

	public SelectDefinition getParentDefinition(final String defName) {
		return getParentDefinition(defName, null);
	}

	public String getParentAlias(final String defName) {
		return getParentAlias(defName, null);
	}

	public String getParentAlias(final String defName, Set<String> defNames) {
		SelectDefinition parent = getParentDefinition(defName, defNames);
		if (parent == null) {
			return null;
		}
		for (Entry<String, SelectDefinition> child : parent.getPointersOnly().entrySet()) {
			if (child.getValue().getName().equals(defName)) {
				return child.getKey();
			}
		}
		return null;
	}

	public Set<SelectDefinition> getDefinitions() {
		return new HashSet<SelectDefinition>(schema.values());
	}

	public Set<SelectDefinition> getDefinitions(Set<String> defNames) {
		if (defNames == null) {
			return getDefinitions();
		}
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
		usedJSONAliasesInWhere.clear();
		functionsAndGroups.clear();
		groupByCandidates.clear();

		/*
		 * We cannot use a HashSet here, because we need a get by position within the while loop.
		 * This is, because we cannot use an iterator here, since we add elements to it while processing.
		 */
		List<String> candidateAliases = null;
		Map<String, List<String>> aliasToJSONPath = new TreeMap<String, List<String>>();
		if (aliases.size() == 1 && aliases.contains("*")) {
			candidateAliases = new ArrayList<String>(getAliases(defNames));
		} else {
			candidateAliases = new ArrayList<String>();
			for (String aliasOrFunc : aliases) {
				String func = null;
				String alias = null;
				if (aliasOrFunc.matches("[a-zA-Z_]+\\(" + ALIAS_VALIDATION + "\\)")) {
					int idx = aliasOrFunc.indexOf('(');
					func = aliasOrFunc.substring(0, idx);
					alias = aliasOrFunc.substring(idx + 1, aliasOrFunc.length() - 1);
				} else {
					alias = aliasOrFunc;
					if (alias.contains(".")) {
						throw new SimpleException(ErrorCode.SELECT_FUNC_NOJSON, alias);
					}
					groupByCandidates.add(alias);
				}
				if (!alias.matches(ALIAS_VALIDATION)) {
					throw new SimpleException(ErrorCode.ALIAS_INVALID, alias);
				}
				_addFunctions(alias, func);
				int index = alias.indexOf('.');
				String mainAlias = index > 0 ? alias.substring(0, index) : alias;
				if (!candidateAliases.contains(mainAlias)) {
					candidateAliases.add(mainAlias);
				}

				if (index > 0) {
					List<String> aliasList = aliasToJSONPath.getOrDefault(mainAlias, new ArrayList<String>());
					aliasList.add(alias.substring(index + 1));
					aliasToJSONPath.putIfAbsent(mainAlias, aliasList);
				}
			}
		}
		Collections.sort(candidateAliases);
		int curPos = 0;
		while (curPos < candidateAliases.size()) {
			String alias = candidateAliases.get(curPos);
			SelectDefinition def = getDefinitionByAlias(alias, defNames);
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

				String before = def.getSqlBefore(alias) == null ? "" : def.getSqlBefore(alias) + ", ";
				String after = def.getSqlAfter(alias) == null ? "" : ", " + def.getSqlAfter(alias);
				StringJoiner sj = new StringJoiner(", ");

				/* Look, if it is a JSON selector */
				if (aliasToJSONPath.containsKey(alias)) {
					for (String jsonSelector : aliasToJSONPath.get(alias)) {
						List<String> functions = functionsAndGroups.get(alias + "." + jsonSelector);
						if (functions == null ) {
							sj.add(String.format("%s#>'{%s}' as \"%s.%s\"", def.getColumn(alias), jsonSelector.replace(".", ","), alias, jsonSelector));
						} else {
							String func = functions.remove(0);
							sj.add(String.format("%s((%s#>'{%s}')::double precision) as \"%s(%s.%s)\"", func, def.getColumn(alias), jsonSelector.replace(".", ","), func, alias, jsonSelector));
						}
					}
				} else { /* Regular column */
					List<String> functions = functionsAndGroups.get(alias);
					if (functions == null) {
						sj.add(String.format("%s as %s", def.getColumn(alias), alias));
					} else {
						for (String func : functions) {
							sj.add(String.format("%s(%s) as \"%s(%s)\"", func, def.getColumn(alias), func, alias));
						}
						functionsAndGroups.put(alias, null);
					}
				}
				expandedSelects.put(defName, (sqlSelect == null ? "" : sqlSelect + ", ") + before + sj + after);
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

	private class Context {
		int clauseCnt;
		String logicalOp;

		public Context(int clauseCnt, String logicalOp) {
			super();
			this.clauseCnt = clauseCnt;
			this.logicalOp = logicalOp;
		}

		@Override
		public String toString() {
			return "Context [clauseCnt=" + clauseCnt + ", logicalOp=" + logicalOp + "]";
		}

	}

	private void _addAliasesInWhere(final String alias, Token token) {
		List<Token> tokens = usedJSONAliasesInWhere.getOrDefault(alias, new ArrayList<Token>());
		tokens.add(token);
		usedJSONAliasesInWhere.put(alias, tokens);
	}

	private void _addFunctions(final String alias, String func) {
		if (func == null || func.isEmpty()) {
			return;
		}
		List<String> functions = functionsAndGroups.getOrDefault(alias, new ArrayList<String>());
		functions.add(func);
		functionsAndGroups.put(alias, functions);
	}

	public boolean hasFunctions() {
		return functionsAndGroups.size() > 0;
	}

	private void _expandWhere(String where) {
		if (where == null || where.isEmpty()) {
			whereSQL = null;
			whereParameters = null;
			return;
		}

		WhereClauseParser whereParser = new WhereClauseParser(where);
		Token whereAST;
		try {
			whereAST = whereParser.parse();
		} catch (SimpleException e) {
			e.setDescription("Syntax error in WHERE-clause");
			e.addData("hint", "You need to escape the following characters ()', within the value part of your filters");
			throw e;
		}

		StringBuilder sbFull = new StringBuilder();
		whereParameters = new TreeMap<String, Object>();

		whereAST.walker(new ConsumerExtended() {

			/* A stack implementation */
			Deque<Context> context = new ArrayDeque<Context>();
			Context ctx;

			@Override
			public boolean middle(Token t) {
				return true;
			}

			@Override
			public boolean before(Token t) {
				switch (t.getName()) {
				case "AND":
				case "OR":
					sbFull.append("(");
					context.push(new Context(t.getChildCount(), t.getName()));
					log.debug("AND/OR" + context.getFirst());
					break;
				case "CLAUSE": {
					log.debug("CLAUSE");
					String alias = t.getChild("ALIAS").getValue();
					String column = getColumn(alias);
					if (column == null) {
						throw new SimpleException(ErrorCode.WHERE_ALIAS_NOT_FOUND, alias);
					}
					String operator = t.getChild("OP").getValue();

					usedJSONAliases.add(alias);
					usedJSONDefNames.add(getAliasMap().get(alias));
					Token jsonSel = t.getChild("JSONSEL");
					Token clauseOrValueToken = t.getChild(t.getChildCount() - 1);
					_addAliasesInWhere(alias, clauseOrValueToken);
					sbFull.append(whereClauseItem(column, alias, operator, clauseOrValueToken, jsonSel));
					ctx = context.getFirst();
					ctx.clauseCnt--;
					if (ctx.clauseCnt > 0)
						sbFull.append(" " + ctx.logicalOp + " ");
					}
					break;
				}
				return true;
			}

			@Override
			public boolean after(Token t) {
				switch (t.getName()) {
				case "AND":
				case "OR":
					sbFull.append(")");
					context.pop();
					ctx = context.peekFirst();
					if (ctx == null)
						break;
					ctx.clauseCnt--;
					if (ctx.clauseCnt > 0)
						sbFull.append(" " + ctx.logicalOp + " ");
					break;
				}
				return true;
			}

		});

		whereSQL = sbFull.toString();
	}

	private String whereClauseItem(String column, String alias, String operator, Token clauseValueToken, Token jsonSel) {
		operator = operator.toUpperCase();

		/* Search for a definition of this operator for a the given value input type (list, null or values) */
		StringJoiner operatorID = new StringJoiner("/");
		if (jsonSel != null) {
			operatorID.add("JSON");
		}
		operatorID.add(clauseValueToken.getName());
		String listElementTypes = clauseValueToken.getChildrenType();
		if (listElementTypes != null) {
			operatorID.add(listElementTypes.toUpperCase());
		}

		WhereClauseOperator whereClauseOperator = whereClauseOperatorMap.get(operatorID.toString() + "/" + operator);
		if (whereClauseOperator == null) {
			throw new SimpleException(ErrorCode.WHERE_OPERATOR_NOT_FOUND, operator, operatorID);
		}

		/* Build the value, or error out if the value type does not exist */
		String paramName = null;
		Object value = null;
		switch (clauseValueToken.getName()) {
		case "LIST":
			List<Object> listItems = new ArrayList<Object>();
			for (Token listItem : clauseValueToken.getChildren()) {
				listItems.add(listItem.getPayload("typedvalue"));
				_addAliasesInWhere(alias, listItem); //XXX Needed?
			}
			value = listItems;
			break;
		case "NULL":
		case "NUMBER":
		case "STRING":
		case "BOOLEAN":
			value = clauseValueToken.getPayload("typedvalue");
			break;
		default:
			// FIXME give the whole where-clause from user input to generate a better error response
			throw new SimpleException(ErrorCode.WHERE_ALIAS_VALUE_ERROR, operator);
		}

		if (value != null) {
			paramName = "pwhere_" + whereParameters.size();
			whereParameters.put(paramName, value);
		}

		/*
		 * Search for a check-function for this operator/value-type combination. Execute it, if present
		 * and error-out on failure. For instance, check if a list has exactly 3 elements. This cannot
		 * be done during parsing.
		 */
		if (whereClauseOperator.getOperatorCheck() != null) {
			if (!whereClauseOperator.getOperatorCheck().middle(clauseValueToken)) {
				throw new SimpleException(ErrorCode.WHERE_ALIAS_VALUE_ERROR, operator, clauseValueToken.getName(), value);
			}
		}

		value = (value == null) ? "null" : ":" + paramName;
		String sqlSnippet = whereClauseOperator.getSqlSnippet();
		StringBuffer result = new StringBuffer();
		int i = 0;
		while(i < sqlSnippet.length()) {
		   char c = sqlSnippet.charAt(i);
		   if (c == '%' && i < sqlSnippet.length() - 1) {
			   switch (sqlSnippet.charAt(i + 1)) {
			   case 'v':
				   result.append(value);
				   i++;
				   break;
			   case 'c':
				   result.append(column);
				   i++;
				   break;
			   case 'j':
				   result.append(jsonSel.getValue().replace(".", ","));
				   i++;
				   break;
			   case '%':
				   result.append('%');
				   i++;
				   break;
			   }
		   } else {
			   result.append(c);
		   }
		   i++;
		}

		return result.toString();
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

	public List<String> getGroupByColumns() {
		if (dirty) {
			throw new SimpleException(ErrorCode.DIRTY_STATE);
		}
		return groupByCandidates;
	}

	public Map<String, List<Token>> getUsedAliasesInWhere() {
		if (dirty) {
			throw new SimpleException(ErrorCode.DIRTY_STATE);
		}
		return usedJSONAliasesInWhere;
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
		SelectDefinition definition = getDefinitionByAlias(alias);
		if (definition == null)
			return null;
		return definition.getColumn(alias);
	}

	public Map<String, String> getExpansion(Set<String> defNames) {
		if (defNames == null) {
			return getExpansion();
		}
		Map<String, String> res = new TreeMap<String, String>();
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
		return getExpansion(new TreeSet<String>(Arrays.asList(defNames)));
	}

	public String getWhereSql() {
		if (dirty) {
			throw new SimpleException(ErrorCode.DIRTY_STATE);
		}
		return whereSQL;
	}

	public Map<String, Object> getWhereParameters() {
		if (dirty) {
			throw new SimpleException(ErrorCode.DIRTY_STATE);
		}
		return whereParameters;
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

	public Map<String, Object> makeObj(Map<String, Object> record, String defName, boolean ignoreNull) {
		SelectDefinition def = getDefinition(defName);
		Map<String, Object> result = new TreeMap<String, Object>();
		for (String alias : def.getAliases()) {
			Object value = record.get(alias);
			if (ignoreNull && value == null)
				continue;
			result.put(alias, value);
		}
		return result;
	}

	public Map<String, Object> makeObjectOrNull(Map<String, Object> record, boolean ignoreNull, Set<String> defNames) {
		Map<String, Object> result = makeObjectOrEmptyMap(record, ignoreNull, defNames);
		return result.isEmpty() ? null : result;
	}

	public Map<String, Object> makeObjectOrNull(Map<String, Object> record, boolean ignoreNull, String... defNames) {
		return makeObjectOrNull(record, ignoreNull, new HashSet<String>(Arrays.asList(defNames)));
	}

	public Map<String, Object> makeObjectOrEmptyMap(Map<String, Object> record, boolean ignoreNull, Set<String> defNames) {
		Map<String, Map<String, Object>> result = new TreeMap<String, Map<String, Object>>();
		List<String> usedAliases = getUsedAliases();

		List<String> rootDefinition = new ArrayList<String>();
		for (String defName : getUsedDefNames()) {
			result.put(defName, new TreeMap<String, Object>());
			rootDefinition.add(defName);
		}

		for (String alias : usedAliases) {
			SelectDefinition def = getDefinitionByAlias(alias);
			Map<String, Object> curMap = result.get(def.getName());
			if (def.isColumn(alias)) {
				/*
				 * record.get cannot distinguish between "not-found" and "found-but-null", therefore
				 * we need to use containsKey here
				 */
				if (!record.containsKey(alias)) {
					continue;
				}
				Object value = record.get(alias);
				if (value == null && ignoreNull) {
					continue;
				}
				curMap.put(alias, value);
			} else {
				String pointingTo = def.getPointersOnly().get(alias).getName();
				rootDefinition.remove(pointingTo);
				curMap.put(alias, result.get(pointingTo));
			}
		}

		if (rootDefinition.size() != 1) {
			throw new RuntimeException("Result Object is not unique");
		}

		return result.get(rootDefinition.get(0));
	}

	public Map<String, Object> makeObjectOrEmptyMap(Map<String, Object> record, boolean ignoreNull, String... defNames) {
		return makeObjectOrEmptyMap(record, ignoreNull, new HashSet<String>(Arrays.asList(defNames)));
	}

	public static void main(String[] args) throws Exception {
		SelectExpansion se = new SelectExpansion();
		se.addColumn("C", "h", "C.h", "before", null);
		se.addColumn("D", "d", "D.d", null, "after");
		se.addColumn("B", "x", "B.x");
		se.addSubDef("B", "y", "C");
		se.addColumn("A", "a", "A.a");
		se.addColumn("A", "b", "A.b");
		se.addSubDef("A", "c", "B");
		se.addSubDef("main", "t", "A");

		// se.addRewrite("measurement", "mvalue", "measurementdouble", "mvalue_double");
		// se.addRewrite("measurement", "mvalue", "measurementstring", "mvalue_string");

		//// {}
		//// []
		//// []
		// se.expand("y", "B");
		// System.out.println(se.getExpansion());
		// System.out.println(se.getUsedAliases());
		// System.out.println(se.getUsedDefNames());
		//
		//// {B=B.x as x}
		//// [x]
		//// [B]
		// se.expand("x, y", "B");
		// System.out.println(se.getExpansion());
		// System.out.println(se.getUsedAliases());
		// System.out.println(se.getUsedDefNames());
		//
		//// {A=A.a as a, A.b as b, B=B.x as x}
		//// [a, b, c, x]
		//// [A, B]
		// se.expand("a, b, c", "A", "B");
		// System.out.println(se.getExpansion());
		// System.out.println(se.getUsedAliases());
		// System.out.println(se.getUsedDefNames());
		//
//		se.addOperator("number", "gt", "> %s");
//		se.addOperator("string", "eq", "= %s");
//		se.addOperator("string", "neq", "<> %s");
//		se.addOperator("number", "eq", "= %s");
//		se.addOperator("boolean", "eq", "= %s");
//		se.addOperator("number", "neq", "<> %s");
//		se.addOperator("null", "eq", "is null");
//		se.addOperator("null", "neq", "is not null");
//		se.addOperator("list", "in", "in (%s)");
//		se.addOperator("list", "bbi", "&& st_envelope(%s)");

//		se.setWhereClause("b.eq.true,and(or(a.eq.null,b.eq.5),a.bbi.(1,2,3,4),b.in.(lo,la,xx))");

//		se.addOperator("boolean", "eq", "%c = %v");
//		se.setWhereClause("a.eq.true");
//		se.expand("h", "A", "C", "B");

//		se.addOperator("json/string", "eq", "%c#>>'{%j}' = %v");
//		se.addOperator("json/boolean", "eq", "%c#>'{%j}' = %v");
//		se.addOperator("json/number", "eq", "(%c#>'{%j}')::double precision = %%v");
//		se.addOperator("json/list/number", "eq", "(%c#>'{%j}')::double precision = %v");
//		se.setWhereClause("a.b.c.eq.-.2");
//		se.setWhereClause("a.b.c.eq.\"\"");
//		se.expand("min(h.a.b),max(h.a.b),h.a,h", "A", "C", "B");
		se.expand("min(h.a),max(h.a),min(x),h.a,h.a,count(h.a),y,a.b.c.d,x", "A", "C", "B");

		System.out.println(se.getExpansion());
		System.out.println(se.getUsedAliases());
		System.out.println(se.getUsedDefNames());
		System.out.println(se.getWhereSql());
		System.out.println(se.getWhereParameters());
		System.out.println(se.getGroupByColumns());

		// se.setWhereClause("");
		// se.expand("mvalue", "A", "D", "B");
		// System.out.println(se.getExpansion());
		// System.out.println(se.getUsedAliases());
		// System.out.println(se.getUsedDefNames());
		// System.out.println(se.getWhereSql());
		// System.out.println(se.getWhereParameters());

		// se.expand("*", "A", "B");
		// se.expand("*", "main", "A", "B", "C");
		// Map<String, Object> rec = new HashMap<String, Object>();
		// rec.put("a", "3");
		// rec.put("b", "7");
		// rec.put("x", "0");
		// rec.put("h", "v");
		// System.out.println(se.getExpansion());
		// System.out.println(se.getUsedAliases());
		// System.out.println(se.getUsedDefNames());
		// System.out.println(se.getWhereSql());
		// System.out.println();
		// System.out.println(se.makeObjectOrEmptyMap(rec, false, "A").toString());
		// System.out.println(se.makeObjectOrEmptyMap(rec, true, "A").toString());
		// System.out.println();
		// System.out.println(se.makeObjectOrEmptyMap(rec, false, "B").toString());
		// System.out.println(se.makeObjectOrEmptyMap(rec, true, "B").toString());
		// System.out.println();
		// System.out.println(se.makeObjectOrEmptyMap(rec, false, "C").toString());
		// System.out.println(se.makeObjectOrEmptyMap(rec, true, "C").toString());
		// System.out.println();
		// System.out.println(se.makeObjectOrEmptyMap(rec, false, "main", "A", "C").toString());
		// System.out.println(se.makeObjectOrEmptyMap(rec, true, "A", "C").toString());
		// System.out.println();
		//// System.out.println(se.makeObjectOrEmptyMap(rec, false, "B", "C").toString());
		//// System.out.println(se.makeObjectOrEmptyMap(rec, true, "B", "C").toString());
		//// System.out.println();
		//// System.out.println(se.makeObjectOrEmptyMap(rec, false, "A", "B").toString());
		//// System.out.println(se.makeObjectOrEmptyMap(rec, true, "A", "B").toString());
		// System.out.println();
		// System.out.println(se.makeObjectOrEmptyMap(rec, false, "A", "B", "C").toString());
		// System.out.println(se.makeObjectOrEmptyMap(rec, true, "A", "B", "C").toString());

		//// {A=A.a as a, A.b as b, B=B.x as x, X=X.h as h}
		//// [a, b, c, x, h, y]
		//// [A, B, X]
		// se.build("x, y", RecursionType.FULL, "B");
		// System.out.println(se.getExpandedSelects());
		// System.out.println(se.getUsedAliases());
		// System.out.println(se.getUsedDefNames());

	}
}
