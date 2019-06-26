package it.bz.idm.bdp.reader2.utils.querybuilder;

import it.bz.idm.bdp.reader2.utils.miniparser.MiniParser;
import it.bz.idm.bdp.reader2.utils.miniparser.Token;

public class WhereClauseParser extends MiniParser {

	public WhereClauseParser(String input) {
		super(input);
	}

	private Token clauseOrLogicalOp() {
		Token clauseOrLogicalOp = new Token("clause_or_logical_op");
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
		return doWhile("alias", t -> {
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
		Token clause = new Token("clause");
		clause.add(alias);
		clause.add(operator);
		clause.add(listOrValue);
		return clause;
	}

	private Token operator() {
		return doWhile("operator", t -> {
			if (!Character.isLetter(c())) {
				return false;
			}
			t.appendValue(c());
			return true;
		});
	}

	private Token listOrValue() {
		return doSingle("list_or_value", t -> {
			if (matchConsume('(')) {
				t.add(list());
				expectConsume(')');
			} else {
				t.add(value());
			}
			return true;
		});
	}

	private Token list() {
		return doSingle("list", t -> {
			t.add(value());
			if (matchConsume(',')) {
				t.combine(list());
			}
			return true;
		});
	}

	private Token value() {
		return doWhile("value", t -> {
			if ((match(')') || match(',')) && clash('\\', -1)) {
				return false;
			}
			matchConsume('\\');
			t.appendValue(c());
			return true;
		});
	}

	private Token logicalOpAnd() {
		Token res = doWhile("logical_op_and", t -> {
			t.add(clauseOrLogicalOp());
			if (matchConsume(',')) {
				t.add(clauseOrLogicalOp());
			}
			return clashConsume(')');
		});
		expectConsume(')');
		return res;
	}

	private Token logicalOpOr() {
		Token res = doWhile("logical_op_or", t -> {
			t.add(clauseOrLogicalOp());
			if (matchConsume(',')) {
				t.add(clauseOrLogicalOp());
			}
			return clashConsume(')');
		});
		expectConsume(')');
		return res;
	}

	public Token parse() {
		return doWhile("where", t -> {
			t.add(clauseOrLogicalOp());
			return true;
		});
	}

	public static void main(String[] args) throws Exception {
		String input;
//		input = "x.eq.e";
//		input = "and(x.eq.3,y.bbi.(1,2,3,4,5),or(z.neq.null,abc.in.(ciao,ha\\,llo),t.ire..*77|e3))";
//		input = "xy.in.(1,2)";
//		input = "a.eq.0,b.neq.3,or(a.eq.3,b.eq.5)";
		input = "a.eq.0,b.neq.3,or(a.eq.3,b.eq.5),a.bbi.(1,2,3,4)";
		WhereClauseParser we = new WhereClauseParser(input);
		System.out.println(we.parse().prettyPrint());

	}
}
