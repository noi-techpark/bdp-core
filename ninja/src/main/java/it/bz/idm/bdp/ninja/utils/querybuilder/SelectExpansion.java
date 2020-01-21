package it.bz.idm.bdp.ninja.utils.querybuilder;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.TreeMap;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.bz.idm.bdp.ninja.utils.miniparser.Consumer;
import it.bz.idm.bdp.ninja.utils.miniparser.ConsumerExtended;
import it.bz.idm.bdp.ninja.utils.miniparser.Token;
import it.bz.idm.bdp.ninja.utils.resultbuilder.ResultBuilder;
import it.bz.idm.bdp.ninja.utils.simpleexception.ErrorCodeInterface;
import it.bz.idm.bdp.ninja.utils.simpleexception.SimpleException;

/**
 * <pre>
 * A select expansion starts from a {@link TargetDefList} and finds out which
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
		SCHEMA_NULL("No valid non-null schema provided"),
		ADD_INVALID_DATA("A schema entry must have a name and a valid definition"),
		WHERE_ALIAS_VALUE_ERROR("Syntax Error in WHERE clause: '%s.<%s>' with value %s is not valid (checks failed)"),
		WHERE_ALIAS_NOT_FOUND("Syntax Error in WHERE clause: Alias '%s' does not exist"),
		WHERE_ALIAS_ALREADY_EXISTS("Syntax Error in WHERE clause: Alias '%s' cannot be used more than once"),
		WHERE_OPERATOR_NOT_FOUND("Syntax Error in WHERE clause: Operator '%s.<%s>' does not exist"),
		WHERE_SYNTAX_ERROR("Syntax Error in WHERE clause: %s"),
		DIRTY_STATE("We are in a dirty state. Run expand() to clean up"),
		EXPAND_INVALID_DATA("Provide valid alias and definition sets!"),
		ALIAS_INVALID("The given alias '%s' is not valid. Only the following characters are allowed: 'a-z', 'A-Z', '0-9', '_' and '.'"),
		SELECT_FUNC_NOJSON("It is currently not possible to use GROUPING with JSON fields. Remove any JSON selectors from your SELECT, if you want to use functions.");

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
	private Schema schema;
	private Map<String, String> expandedSelects = new TreeMap<String, String>();
	private Set<String> usedTargetDefs = new TreeSet<String>();
	private Map<String, List<String>> functionsAndGroups = new TreeMap<String, List<String>>();
	private List<String> groupByCandidates = new ArrayList<String>();
	private Set<String> usedTargetDefListNames = new TreeSet<String>();
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

	public SelectExpansion setSchema(final Schema schema) {
		if (schema == null) {
			throw new SimpleException(ErrorCode.SCHEMA_NULL);
		}
		this.schema = schema;
		dirty = true;
		return this;
	}

	public Schema getSchema() {
		return schema;
	}

	private void _build() {
		if (schema == null) {
			throw new SimpleException(ErrorCode.SCHEMA_NULL);
		}
		// TODO Move dirty flags to Schema, or do we need it also here?
		dirty = true;
		dirty = false;
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

					usedTargetDefs.add(alias);
					usedTargetDefListNames.add(schema.find(alias).getName());
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
		Set<String> targetEntries = _targetStringToSet(aliases);
		Set<String> targetListNames = new HashSet<String>(Arrays.asList(defNames));

		if (targetEntries == null || targetEntries.isEmpty() || targetListNames == null || targetListNames.isEmpty()) {
			throw new SimpleException(ErrorCode.EXPAND_INVALID_DATA);
		}
		if (dirty) {
			_build();
		}
		usedTargetDefs.clear();
		usedTargetDefListNames.clear();
		expandedSelects.clear();
		usedJSONAliasesInWhere.clear();
		functionsAndGroups.clear();
		groupByCandidates.clear();

		boolean hasJSONSelectors = false;

		/*
		 * We cannot use a HashSet here, because we need a get by position within the while loop.
		 * This is, because we cannot use an iterator here, since we add elements to it while processing.
		 */
		List<String> candidateTargetNamesOrAliases = null;
		Map<String, List<String>> aliasToJSONPath = new TreeMap<String, List<String>>();
		if (targetEntries.size() == 1 && targetEntries.contains("*")) {
			candidateTargetNamesOrAliases = schema.getListNames(targetListNames);
		} else {
			candidateTargetNamesOrAliases = new ArrayList<String>();
			for (String targetOrFunc : targetEntries) {
				String func = null;
				String targetName = null;
				if (targetOrFunc.matches("[a-zA-Z_]+\\(" + ALIAS_VALIDATION + "\\)")) {
					int idx = targetOrFunc.indexOf('(');
					func = targetOrFunc.substring(0, idx);
					targetName = targetOrFunc.substring(idx + 1, targetOrFunc.length() - 1);
				} else {
					targetName = targetOrFunc;
					if (targetName.contains(".")) {
						hasJSONSelectors = true;
					}
				}
				if (!targetName.matches(ALIAS_VALIDATION)) {
					throw new SimpleException(ErrorCode.ALIAS_INVALID, targetName);
				}
				_addFunctions(targetName, func);
				int index = targetName.indexOf('.');
				String mainAlias = index > 0 ? targetName.substring(0, index) : targetName;
				candidateTargetNamesOrAliases.add(mainAlias);

				if (index > 0) {
					List<String> aliasList = aliasToJSONPath.getOrDefault(mainAlias, new ArrayList<String>());
					aliasList.add(targetName.substring(index + 1));
					aliasToJSONPath.putIfAbsent(mainAlias, aliasList);
				}
			}
		}

		/*
		 * Currently it is not possible to use functions together with JSON selectors.
		 */
		if (! functionsAndGroups.isEmpty() && hasJSONSelectors) {
			throw new SimpleException(ErrorCode.SELECT_FUNC_NOJSON);
		}

		Collections.sort(candidateTargetNamesOrAliases);
		int curPos = 0;
		while (curPos < candidateTargetNamesOrAliases.size()) {
			String targetNameOrAlias = candidateTargetNamesOrAliases.get(curPos);
			TargetDefList targetList = schema.findOrNull(targetNameOrAlias, targetListNames);
			if (targetList == null) {
				targetList = schema.findByAliasOrNull(targetNameOrAlias, targetListNames);
			}
			if (targetList == null) {
				SimpleException se = new SimpleException(ErrorCode.KEY_NOT_INSIDE_DEFLIST, targetNameOrAlias, targetListNames);
				se.addData("targetName", targetNameOrAlias);
				throw se;
			}

			TargetDef target = targetList.get(targetNameOrAlias);
			if (target == null) {
				target = targetList.getByAlias(targetNameOrAlias);
			}

			/* We cannot be sure, if targetNameOrAlias is an alias or name, hence we use
			 * target.getName(), target.getAlias() and target.getFullName() from here.
			 */
			targetNameOrAlias = null;

			usedTargetDefListNames.add(targetList.getName());

			/* It is a column target */
			if (target.hasColumn()) {
				usedTargetDefs.add(target.getName());
				String defName = targetList.getName();
				usedTargetDefListNames.add(defName);

				String sqlSelect = expandedSelects.getOrDefault(defName, null);
				String before = target.hasSqlBefore() ? target.getSqlBefore() + ", " : "";
				String after = target.hasSqlAfter() ? ", " + target.getSqlAfter() : "";

				StringJoiner sj = new StringJoiner(", ");

				/* Three types for column-targets exist:
				 * (1) Target with a JSON selector (ex., address.city)
				 * (2) Regular column (ex., name)
				 * (3) Function (ex., min(value))
				 */

				/* (1) Target with a JSON selector */
				if (aliasToJSONPath.containsKey(target.getName())) {
					for (String jsonSelector : aliasToJSONPath.get(target.getName())) {
						List<String> functions = functionsAndGroups.get(target.getName() + "." + jsonSelector);
						if (functions == null) {
							sj.add(String.format("%s#>'{%s}' as \"%s.%s\"", target.getColumn(), jsonSelector.replace(".", ","), target.getFinalName(), jsonSelector));
							candidateTargetNamesOrAliases.remove(0);
							curPos--;
						} else {
							String func = functions.remove(0);
							sj.add(String.format("%s((%s#>'{%s}')::double precision) as \"%s(%s.%s)\"", func, target.getColumn(), jsonSelector.replace(".", ","), func, target.getFinalName(), jsonSelector));
						}
					}
				} else {
					List<String> functions = functionsAndGroups.get(target.getFinalName());

					/* (2) Regular column */
					if (functions == null) {
						sj.add(String.format("%s as %s", target.getColumn(), target.getFinalName()));
						if (! groupByCandidates.contains(target.getName())) {
							groupByCandidates.add(target.getName());
						}
					} else { /* (3) Function */
						for (String func : functions) {
							sj.add(String.format("%s(%s) as \"%s(%s)\"", func, target.getColumn(), func, target.getFinalName()));
							candidateTargetNamesOrAliases.remove(0);
							curPos--;
						}
						functionsAndGroups.put(target.getFinalName(), null);
					}
				}
				expandedSelects.put(defName, (sqlSelect == null ? "" : sqlSelect + ", ") + before + sj + after);
			} else { /* the current target is not a column, but a pointer to a targetList */
				TargetDefList pointsTo = target.getTargetList();
				if (targetListNames.contains(pointsTo.getName())) {
					usedTargetDefs.add(target.getName());
					usedTargetDefListNames.add(pointsTo.getName());
					for (String subAlias : pointsTo.getNames()) {
						if (!candidateTargetNamesOrAliases.contains(subAlias)) {
							candidateTargetNamesOrAliases.add(subAlias);
						}
					}
				}
			}
			curPos = curPos < 0 ? 0 : curPos + 1;
		}

		_expandWhere(whereClause);
	}

	public List<String> getUsedTargetNames() {
		if (dirty) {
			throw new SimpleException(ErrorCode.DIRTY_STATE);
		}
		return new ArrayList<String>(usedTargetDefs);
	}

	public List<String> getGroupByTargetNames() {
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
		return new ArrayList<String>(usedTargetDefListNames);
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

	public String getColumn(String targetDefName) {
		TargetDefList targetDefList = schema.findOrNull(targetDefName);
		if (targetDefList == null)
			return null;
		return targetDefList.get(targetDefName).getColumn();
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

	private static Set<String> _targetStringToSet(final String csv) {
		Set<String> resultSet = new HashSet<String>();
		if (csv == null) {
			resultSet.add("*");
			return resultSet;
		}
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

		se.setSchema(schema);

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
		// {B=B.x as x}
		// [x]
		// [B]
		se.expand("x, y", "B");
		System.out.println(se.getExpansion());
		System.out.println(se.getUsedTargetNames());
		System.out.println(se.getUsedDefNames());
		System.out.println(se.getWhereSql());
		System.out.println(se.getWhereParameters());
		System.out.println(se.getGroupByTargetNames());
		//
		//// {A=A.a as a, A.b as b, B=B.x as x}
		//// [a, b, c, x]
		//// [A, B]
		se.expand("a, b, c", "A", "B");
		System.out.println(se.getExpansion());
		System.out.println(se.getUsedTargetNames());
		System.out.println(se.getUsedDefNames());

		se.addOperator("number", "gt", "> %s");
		se.addOperator("string", "eq", "= %s");
		se.addOperator("string", "neq", "<> %s");
		se.addOperator("number", "eq", "= %s");
		se.addOperator("boolean", "eq", "= %s");
		se.addOperator("number", "neq", "<> %s");
		se.addOperator("null", "eq", "is null");
		se.addOperator("null", "neq", "is not null");
		se.addOperator("list", "in", "in (%s)");
		se.addOperator("list", "bbi", "&& st_envelope(%s)");

		se.setWhereClause("b.eq.true,and(or(a.eq.null,b.eq.5),a.bbi.(1,2,3,4),b.in.(lo,la,xx))");

		se.addOperator("boolean", "eq", "%c = %v");
		se.setWhereClause("a.eq.true");
		se.expand("h", "A", "C", "B");

		se.addOperator("json/string", "eq", "%c#>>'{%j}' = %v");
		se.addOperator("json/boolean", "eq", "%c#>'{%j}' = %v");
		se.addOperator("json/number", "eq", "(%c#>'{%j}')::double precision = %%v");
		se.addOperator("json/list/number", "eq", "(%c#>'{%j}')::double precision = %v");
		se.setWhereClause("a.b.c.eq.-.2");
		se.setWhereClause("a.b.c.eq.\"\"");
		// se.expand("min(h.a.b),max(h.a.b),h.a,h", "A", "C", "B");
		// se.expand("min(h.a),max(h.a),min(x),h.a,h.a,count(h.a),y,a.b.c.d,x", "A", "C", "B");

		// System.out.println(se.getExpansion());
		// System.out.println(se.getUsedAliases());
		// System.out.println(se.getUsedDefNames());
		// System.out.println(se.getWhereSql());
		// System.out.println(se.getWhereParameters());
		// System.out.println(se.getGroupByColumns());

		// se.setWhereClause("");
		// se.expand("mvalue", "A", "D", "B");
		// System.out.println(se.getExpansion());
		// System.out.println(se.getUsedAliases());
		// System.out.println(se.getUsedDefNames());
		// System.out.println(se.getWhereSql());
		// System.out.println(se.getWhereParameters());

		// se.expand("*", "A", "B");
		se.expand("*", "main", "A", "B", "C");
		Map<String, Object> rec = new HashMap<String, Object>();
		rec.put("a", "3");
		rec.put("b", "7");
		rec.put("x", "0");
		rec.put("h", "v");
		System.out.println(se.getExpansion());
		System.out.println(se.getUsedTargetNames());
		System.out.println(se.getUsedDefNames());
		System.out.println(se.getWhereSql());
		System.out.println();
		System.out.println(ResultBuilder.makeObj(se.getSchema(), rec, "A", false).toString());
		System.out.println(ResultBuilder.makeObj(se.getSchema(), rec, "A", true).toString());
		System.out.println();
		System.out.println(ResultBuilder.makeObj(se.getSchema(), rec, "B", false).toString());
		System.out.println(ResultBuilder.makeObj(se.getSchema(), rec, "B", true).toString());
		System.out.println();
		System.out.println(ResultBuilder.makeObj(se.getSchema(), rec, "C", false).toString());
		System.out.println(ResultBuilder.makeObj(se.getSchema(), rec, "C", true).toString());

		//// {A=A.a as a, A.b as b, B=B.x as x, X=X.h as h}
		//// [a, b, c, x, h, y]
		//// [A, B, X]
		se.expand("x, y", "B");
		System.out.println(se.getExpansion());
		System.out.println(se.getUsedTargetNames());
		System.out.println(se.getUsedDefNames());

	}

}
