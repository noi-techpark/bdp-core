package it.bz.idm.bdp.ninja.utils.querybuilder;

import it.bz.idm.bdp.ninja.utils.miniparser.MiniParser;
import it.bz.idm.bdp.ninja.utils.miniparser.Token;
import it.bz.idm.bdp.ninja.utils.simpleexception.SimpleException;

public class WhereClauseParser extends MiniParser {

	public WhereClauseParser(String input) {
		super(input);
	}

	private Token clauseOrLogicalOp() {
		return doSingle("CLAUSE_OR_LOGICAL_OP", t -> {
			if (matchConsume("and(")) {
				t.add(logicalOpAnd());
			} else if (matchConsume("or(")) {
				t.add(logicalOpOr());
			} else {
				t.add(clause());
				if (clash(',') && clash(')') && clash(EOL)) {
					expect(",)" + EOL);
				}
			}
			if (matchConsume(',')) {
				t.combine(clauseOrLogicalOp());
			}
			return true;
		});
	}

	// XXX ALIAS is a terminal and could be describe with a lexer
	// XXX Use uppercase names for terminals, and lowercase for non-terminal values
	// XXX It seems that doWhile is about terminals, whereas doSingle is about non-terminals
	private Token alias() {
		Token res = doWhile("ALIAS", t -> {
			if (clashLetter() && clash('_')) {
				return false;
			}
			t.appendValue(c());
			return true;
		});
		if (res.getValue() == null || res.getValue().isEmpty()) {
			throw new SimpleException(ErrorCode.SYNTAX_ERROR, getPos() - 1, encode(la(-1)), "ALIAS expected");
		}
		return res;
	}

	private Token clause() {
		return doSingle("CLAUSE", t -> {
			t.add(alias());
			expectConsume('.');
			Token jsonSelector = jsonSelector();
			if (jsonSelector != null) {
				expectConsume('.');
				t.add(jsonSelector);
			}
			t.add(operator());
			expectConsume('.');
			t.combineForce(listOrValue());
			return true;
		});
	}

	private Token jsonSelector() {
		Token res = doWhile("JSONSEL", t -> {
			if (clashLetter() && clash('_') && clash('.') && clashDigit()) {
				return false;
			}
			t.appendValue(c());
			return true;
		});

		/*
		 * A CLAUSE-token is composed of four parts: alias, json selector, operator, and value.
		 * JSON selectors are optional, hence we must first check if they are present. For that,
		 * we eliminate first the value part, and try to find the dot which shows the start of the operator.
		 * A value could be a number and hence have a dot inside. If that happens, we need to jump another
		 * dot back. Strings are kept as they are. If a dot inside a string should be handled correctly, the
		 * user must use quotes.
		 */
		String value = res.getValue() == null ? "" : res.getValue();
		int indexBeforeOperator = value.lastIndexOf(".", value.lastIndexOf(".") - 1);
		try {
			String numberCandidate = value.substring(indexBeforeOperator + 1);
			Double.parseDouble(numberCandidate);
			indexBeforeOperator -= numberCandidate.length();
		} catch (NumberFormatException e) {
			/* nothing to do, the other tokenizers will handled possible errors */
		}

		if (value.isEmpty() || indexBeforeOperator < 0) {
			goBack(value.length());
			return null;
		}

		goBack(value.length() - indexBeforeOperator);
		res.setValue(value.substring(0, indexBeforeOperator));
		return res;
	}

	private Token operator() {
		Token res = doWhile("OP", t -> {
			if (clashLetter()) {
				return false;
			}
			t.appendValue(c());
			return true;
		});
		if (res.getValue() == null || res.getValue().isEmpty()) {
			throw new SimpleException(ErrorCode.SYNTAX_ERROR, getPos() - 1, encode(la(-1)), "OPERATOR expected");
		}
		return res;
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
		Token res = doSingle("LIST", t -> {
			t.add(value());
			if (matchConsume(',')) {
				t.combine(list());
			}
			return true;
		});
		expect(')');
		return res;
	}

	private Token value() {
		boolean quoted = matchConsume('"');
		Token res = doWhile("VALUE", t -> {
			if ((match(')') || match(',') || (match('"') && quoted)) && clash('\\', -1)) {
				return false;
			} else if ((match('(') || match('\'') || match('"')) && clash('\\', -1)) {
				error("Characters ('\" must be escaped within a filter VALUE");
			}
			matchConsume('\\');
			t.appendValue(c());
			return true;
		});

		Object typedValue = null;
		if (quoted) {
			expectConsume('"');
			res.setName("string");
		} else if (res.valueIs(null)) {
			res.setName("string");
			res.setValue("");
		} else if (res.valueIs("null")) {
			res.setName("null");
			res.setValue(null);
		} else if (res.getValue().equalsIgnoreCase("true") || res.getValue().equalsIgnoreCase("false")) {
			res.setName("boolean");
			typedValue = res.getValue().equalsIgnoreCase("true");
		} else {
			try {
				typedValue = Integer.parseInt(res.getValue());
				res.setName("number");
			} catch (NumberFormatException e) {
				/* it is not an integer, go ahead */
				try {
					typedValue = Double.parseDouble(res.getValue());
					res.setName("number");
				} catch (NumberFormatException ex) {
					/* nothing more to try, we will keep the given object
					 * type and let Postgres handle possible casting errors */
					res.setName("string");
				}
			}
		}
		res.addPayload("quoted", quoted);
		if (typedValue == null) {
			typedValue = res.getValue();
		}
		res.addPayload("typedvalue", typedValue);
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
			return matchConsume(',');
		});
		expect(EOL);
		return ast;
	}

	public static void main(String[] args) throws Exception {
		String input;
//		input = "a.b.c.eq.x=b";
//		input = "and(x.eq.3,y.bbi.(1,2,3,4,5),or(z.neq.null,abc.in.(ciao,ha\\,llo),t.ire.\\.*77|e3))";
//		input = "a.eq.1.and(a.eq.0)";
		input = "smetadata.outlets.0.maxPower.eq.\".*\",x.gt.7";
//		input = "a.3.c.in.(1,2)";
//		input = "a.eq.0,b.neq.3,or(a.eq.3,b.eq.5)";
//		input = "a.eq.0,b.neq.3,or(a.eq.3,b.eq.5),a.bbi.(1,2,3,4),d.eq.,f.in.()";
//		input = "f.eq.(null,null,null)";
//		input = "f_.eq.";//,or(a.eq.7,and(b.eq.9))";
//		input = "a.eq.1.and(a.eq.0)";
//		input = "or(scode.ire.TRENTO|rovere'to.*,mvalue.eq.0)";
		WhereClauseParser we = new WhereClauseParser(input);
		Token ast = we.parse();
		System.out.println(ast.prettyFormat());
		System.out.println(ast.format());

	}
}
