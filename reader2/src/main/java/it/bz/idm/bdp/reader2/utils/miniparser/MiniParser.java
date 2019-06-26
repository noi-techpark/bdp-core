package it.bz.idm.bdp.reader2.utils.miniparser;

public class MiniParser {

	private static final boolean DEBUG = false;

	private char c;
	private char la;
	private int i;
	private String input;

	protected MiniParser(String input) {
		if (input == null || input.isEmpty()) {
			throw new RuntimeException("No input to parse");
		}
		this.input = input + '\0';
		i = 0;
		c = input.charAt(0);
		la = c;
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
			if (DEBUG)
				System.out.println("C=" + c);
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

	protected char c() {
		return la(0);
	}

	private void error(String msg) {
		throw new RuntimeException("Syntax error at position " + i + " with character '" + c + "': " + msg);
	}

	protected void expect(char exp) {
		if (c != exp)
			error("'" + exp + "' expected");
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

	protected Token doSingle(String tokenName, SimpleConsumer c) {
		Token t = new Token(tokenName);
		c.middle(t);
		if (DEBUG)
			System.out.println("T=" + tokenName);
		return t;
	}

	protected Token doWhile(String tokenName, SimpleConsumer c) {
		Token t = new Token(tokenName);
		do {
			if(!c.middle(t)) {
				break;
			}
		} while (consume());
		if (DEBUG)
			System.out.println("T=" + tokenName);
		return t;
	}

//	public static void main(String[] args) {
//		MiniParser mp = new MiniParser("and,or,xx");
//		System.out.println(mp.matchConsume("and"));
//		System.out.println(mp.matchConsume(','));
//		System.out.println(mp.matchConsume("or"));
//		mp.expect('.');
//	}

}
