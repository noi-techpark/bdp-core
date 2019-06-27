package it.bz.idm.bdp.reader2.utils.miniparser;

import java.util.ArrayList;
import java.util.List;

public class Token {
	String name;
	String value;
	List<Token> children = new ArrayList<Token>();

	public Token(String name, String value) {
		super();
		this.name = name;
		this.value = value;
	}

	public Token(String name) {
		this(name, null);
	}

	public void add(Token child) {
		children.add(child);
	}

	public void combine(Token child) {
		if (! child.name.equalsIgnoreCase(name)) {
			throw new RuntimeException("Cannot combine two different tokens: " + child.name + " and " + name);
		}
		children.addAll(child.children);
	}

	public String prettyFormat() {
		final StringBuilder res = new StringBuilder();
		walker(new Consumer() {
			int indent = 0;

			@Override
			public boolean middle(Token t) {
				res.append(_indent(indent) + t.toString() + "\n");
				return true;
			}

			@Override
			public boolean before(Token t) {
				res.append(_indent(indent++) + t.name + " [\n");
				return true;
			}

			@Override
			public boolean after(Token t) {
				res.append(_indent(--indent) + "] //" + t.name + "\n");
				return true;
			}
		});
		return res.toString();
	}

	public String format() {
		final StringBuilder res = new StringBuilder();
		walker(new Consumer() {

			@Override
			public boolean middle(Token t) {
				res.append("{" + t.name + "=" + t.value + "}");
				return true;
			}

			@Override
			public boolean before(Token t) {
				res.append(t.name + "{");
				return true;
			}

			@Override
			public boolean after(Token t) {
				res.append("}");
				return true;
			}
		});
		return res.toString();
	}

	private String _indent(int indent) {
		StringBuilder sb = new StringBuilder();
        if (indent > 0) {
            for (int cnt = 0; cnt < indent; cnt++) {
            	sb.append("  ");
            }
        }
        return sb.toString();
	}

	public void walker(Consumer c) {
		_walker(this, c);
	}

	private boolean _walker(Token token, Consumer c) {
		if (token.children.isEmpty()) {
			return c.middle(token);
		}

		if(!c.before(token)) {
			return false;
		}
		for (Token child : token.children) {
			if (!_walker(child, c))
				return false;
		}

		if(!c.after(token)) {
			return false;
		}

		return true;
	}

	@Override
	public String toString() {
		return name + (value == null ? "" : " '" + value + "'");
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void appendValue(String value) {
		this.value += value;
	}

	public void appendValue(char value) {
		if (value == '\0')
			return;
		if (this.value == null)
			this.value = "";
		this.value += value;
	}

	public String getName() {
		return name;
	}

	public List<Token> getChildren() {
		return children;
	}

	public Token getChild(String childName) {
		for (Token child : children) {
			if (childName.equals(child.getName())) {
				return child;
			}
		}
		return null;
	}


}
