package it.bz.idm.bdp.ninja.utils.querybuilder;

import it.bz.idm.bdp.ninja.utils.miniparser.Consumer;

public class WhereClauseOperator {
	private String name;
	private String sqlSnippet;
	private Consumer operatorCheck;

	public WhereClauseOperator(String name, String sqlSnippet, Consumer operatorCheck) {
		super();
		this.name = name;
		if (!sqlSnippet.contains("%v") || !sqlSnippet.contains("%c")) {
			throw new RuntimeException("A WhereClauseOperator SQL snippet must contain a value (%v) and column (%c) part.");
		}
		this.sqlSnippet = sqlSnippet;
		this.operatorCheck = operatorCheck;
	}

	public WhereClauseOperator(String name, String sqlSnippet) {
		this(name, sqlSnippet, null);
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSqlSnippet() {
		return sqlSnippet;
	}
	public void setSqlSnippet(String sqlSnippet) {
		this.sqlSnippet = sqlSnippet;
	}
	public Consumer getOperatorCheck() {
		return operatorCheck;
	}
	public void setOperatorCheck(Consumer operatorCheck) {
		this.operatorCheck = operatorCheck;
	}

}
