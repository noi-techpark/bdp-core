package it.bz.idm.bdp.reader2.utils.miniparser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.bz.idm.bdp.reader2.utils.simpleexception.ErrorCodeInterface;
import it.bz.idm.bdp.reader2.utils.simpleexception.SimpleException;

public class MiniParser {

	private static final Logger log = LoggerFactory.getLogger(MiniParser.class);
	protected static final char EOL = '\0';
	private char c;
	private char la;
	private int i;
	private String input;
	protected Token ast = null;

	public static enum ErrorCode implements ErrorCodeInterface {
		SYNTAX_ERROR 	         ("Syntax error at position %d with character %s: %s");

		private final String msg;
		ErrorCode(String msg) {
			this.msg = msg;
		}

		@Override
		public String getMsg() {
			return "PARSING ERROR: " + msg;
		}
	}

	protected MiniParser(String input) {
		setInput(input);
	}

	private boolean get(int i) {
		try {
			la = input.charAt(i);
			return true;
		} catch (IndexOutOfBoundsException e) {
			la = 0;
			return false;
		}
	}

	protected boolean consume() {
		boolean laExists = get(i + 1);
		if (laExists) {
			log.debug("C=" + c);
			c = la;
			i++;
		}
		return laExists;
	}

	protected boolean consume(int times) {
		for (int i = 0; i < times; i++) {
			if (!consume()) {
				return false;
			}
		}
		return true;
	}

	protected char la(int hops) {
		get(i + hops);
		return la;
	}

	protected int getPos() {
		return i;
	}

	protected char c() {
		return la(0);
	}

	protected String encode(char c) {
		switch (c) {
			case EOL: return "<EOL>";
		}
		return "" + c;
	}

	protected String encode(String chars) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < chars.length(); i++) {
			sb.append(encode(chars.charAt(i)));
		}
		return sb.toString();
	}


	protected void expect(char exp) {
		if (c != exp) {
			error(exp + " expected");
		}
	}

	protected void expect(String expChars) {
		boolean found = false;
		for (int i = 0; i < expChars.length(); i++) {
			if (c == expChars.charAt(i)) {
				found = true;
				break;
			}
		}
		if (!found) {
			error("One of the following characters " + expChars + " expected");
		}
	}

	protected void error(String msg) {
		SimpleException ex = new SimpleException(ErrorCode.SYNTAX_ERROR, i, encode(c), encode(msg));
		ex.addData("position", i);
		ex.addData("input_marked", input.substring(0, i)
								   + "--->" + encode(c) + "<---"
								   + (input.length() > i+1 ? input.substring(i+1, input.length() - 1) : ""));
		ex.addData("input_origin", input.substring(0, input.length() - 1));
		throw ex;
	}

	protected boolean match(char exp, int pos) {
		return la(pos) == exp;
	}

	protected boolean match(char exp) {
		return match(exp, 0);
	}

	protected boolean match(String exp) {
		for (int i = 0; i < exp.length(); i++) {
			if (!match(exp.charAt(i), i)) {
				return false;
			}
		}
		return true;
	}

	protected boolean clash(char exp, int pos) {
		return !match(exp, pos);
	}

	protected boolean clash(char exp) {
		return !match(exp);
	}

	protected boolean clash(String exp) {
		return !match(exp);
	}

	protected void expectConsume(char exp) {
		expect(exp);
		consume();
	}

	protected boolean matchConsume(char exp) {
		return consumeIf(match(exp));
	}

	protected boolean matchConsume(String exp) {
		boolean res = match(exp);
		if (res)
			consume(exp.length());
		return res;
	}

	protected boolean clashConsume(char exp) {
		return consumeIf(! match(exp));
	}

	protected boolean clashConsume(String exp) {
		boolean res = match(exp);
		if (!res)
			consume(exp.length());
		return res;
	}

	protected boolean consumeIf(boolean condition) {
		if (condition)
			consume();
		return condition;
	}

	protected Token doSingle(String tokenName, Consumer c) {
		Token t = new Token(tokenName);
		log.debug("S=" + tokenName);
		c.middle(t);
		log.debug("E=" + tokenName + "; value = " + t.getValue());
		return t;
	}

	protected Token doWhile(String tokenName, Consumer c) {
		Token t = new Token(tokenName);
		log.debug("S=" + tokenName);
		do {
			if(!c.middle(t)) {
				break;
			}
		} while (consume());
		log.debug("E=" + tokenName + "; value = " + t.getValue());
		return t;
	}

	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		if (input == null) {
			input = "";
		}

		if (input.equals(this.input)) {
			return;
		}

		this.input = input + EOL;
		i = 0;
		c = input.charAt(0);
		la = c;
		ast = null;
	}

	public Token getAst() {
		return ast;
	}

//	public static void main(String[] args) {
//		MiniParser mp = new MiniParser("and,or,xx");
//		System.out.println(mp.matchConsume("and"));
//		System.out.println(mp.matchConsume(','));
//		System.out.println(mp.matchConsume("or"));
//		mp.expect('.');
//	}

}
