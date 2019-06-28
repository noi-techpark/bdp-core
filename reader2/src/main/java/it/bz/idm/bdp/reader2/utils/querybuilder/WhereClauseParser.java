package it.bz.idm.bdp.reader2.utils.querybuilder;

import it.bz.idm.bdp.reader2.utils.miniparser.MiniParser;
import it.bz.idm.bdp.reader2.utils.miniparser.Token;

public class WhereClauseParser extends MiniParser {

	public WhereClauseParser(String input) {
		super(input);
	}

	private Token clauseOrLogicalOp() {
		Token clauseOrLogicalOp = new Token("CLAUSE_OR_LOGICAL_OP");
		if (matchConsume("and(")) {
			clauseOrLogicalOp.add(logicalOpAnd());
		} else if (matchConsume("or(")) {
			clauseOrLogicalOp.add(logicalOpOr());
		} else {
			clauseOrLogicalOp.add(clause());
		}
		if (matchConsume(',')) {
			clauseOrLogicalOp.combine(clauseOrLogicalOp());
		}
		return clauseOrLogicalOp;
	}

	private Token alias() {
		return doWhile("ALIAS", t -> {
			if (!Character.isLetter(c())) {
				return false;
			}
			t.appendValue(c());
			return true;
		});
	}

	private Token clause() {
		Token alias = alias();
		expectConsume('.');
		Token operator = operator();
		expectConsume('.');
		Token listOrValue = listOrValue();
		Token clause = new Token("CLAUSE");
		clause.add(alias);
		clause.add(operator);
		clause.combineForce(listOrValue);
		return clause;
	}

	private Token operator() {
		return doWhile("OP", t -> {
			if (!Character.isLetter(c())) {
				return false;
			}
			t.appendValue(c());
			return true;
		});
	}

	private Token listOrValue() {
		return doSingle("LIST_OR_VALUE", t -> {
			if (matchConsume('(')) {
				t.add(list());
				expectConsume(')');
			} else  {
				t.add(value());
			}
			return true;
		});
	}

	private Token list() {
		return doSingle("LIST", t -> {
			t.add(value());
			if (matchConsume(',')) {
				t.combine(list());
			}
			return true;
		});
	}

	private Token value() {
		Token res = doWhile("VALUE", t -> {
			if ((match(')') || match(',') || match('\'')) && clash('\\', -1)) {
				return false;
			}
			matchConsume('\\');
			t.appendValue(c());
			return true;
		});
		if (res.valueIs(null)) {
			res.setValue("");
		} else if (res.valueIs("null")) {
			res.setName("null");
			res.setValue(null);
		}
		return res;
	}

	private Token logicalOpAnd() {
		Token res = doWhile("AND", t -> {
			t.combineForce(clauseOrLogicalOp());
			if (matchConsume(',')) {
				t.combineForce(clauseOrLogicalOp());
			}
			return clashConsume(')');
		});
		expectConsume(')');
		return res;
	}

	private Token logicalOpOr() {
		Token res = doWhile("OR", t -> {
			t.combineForce(clauseOrLogicalOp());
			if (matchConsume(',')) {
				t.combineForce(clauseOrLogicalOp());
			}
			return clashConsume(')');
		});
		expectConsume(')');
		return res;
	}

	public Token parse() {
		if (ast != null)
			return ast;
		ast = doWhile("AND", t -> {
			t.combineForce(clauseOrLogicalOp());
			return true;
		});
		return ast;
	}

	public static void main(String[] args) throws Exception {
		String input;
		input = "x.eq.e";
//		input = "and(x.eq.3,y.bbi.(1,2,3,4,5),or(z.neq.null,abc.in.(ciao,ha\\,llo),t.ire..*77|e3))";
//		input = "xy.in.(1,2)";
//		input = "a.eq.0,b.neq.3,or(a.eq.3,b.eq.5)";
//		input = "a.eq.0,b.neq.3,or(a.eq.3,b.eq.5),a.bbi.(1,2,3,4),d.eq.,f.in.()";
//		input = "f.eq.(null,null,null)";
		input = "f.eq.,or(a.eq.7,and(b.eq.9))";
		WhereClauseParser we = new WhereClauseParser(input);
		Token ast = we.parse();
		System.out.println(ast.prettyFormat());
		System.out.println(ast.format());

	}
}
