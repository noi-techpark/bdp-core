package it.bz.idm.bdp.ninja.utils.miniparser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Token {
	String name;
	String value;
	Map<String, Object> payload;
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

	public boolean is(String name) {
		if (name == null || name.isEmpty())
			return false;
		return name.equalsIgnoreCase(this.name);
	}

	/**
	 * Null-safe version of {@link Token#is}
	 * @param token
	 * @param name
	 * @return
	 */
	public static boolean is(Token token, String name) {
		if (token == null) {
			return false;
		}
		return token.is(name);
	}

	/**
	 * Null-safe version of {@link Token#valueIs}
	 * @param token
	 * @param name
	 * @return
	 */
	public static boolean valueIs(Token token, String name) {
		if (token == null) {
			return false;
		}
		return token.valueIs(name);
	}

	public boolean valueIs(String value) {
		if (value == null) {
			if (this.value == null) {
				return true;
			} else {
				return false;
			}
		}
		return value.equals(this.value);
	}

	public void combine(Token child) {
		if (! child.name.equalsIgnoreCase(name)) {
			throw new RuntimeException("Cannot combine two different tokens: " + child.name + " and " + name);
		}
		combineForce(child);
	}

	public void combineForce(Token child) {
		children.addAll(child.children);
	}

	public String prettyFormat() {
		final StringBuilder res = new StringBuilder();
		walker(new ConsumerExtended() {
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
		walker(new ConsumerExtended() {

			@Override
			public boolean middle(Token t) {
				res.append("{" + t.name + (t.value == null ? "" : "=" + t.value) + "}");
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

	public void walker(ConsumerExtended c) {
		_walker(this, c);
	}

	private boolean _walker(Token token, ConsumerExtended c) {
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

	public void setName(String name) {
		if (name == null || name.isEmpty()) {
			throw new RuntimeException("Invalid Token name!");
		}
		this.name = name.toUpperCase();
	}

	public Token getChild(int idx) {
		return children.get(idx);
	}

	public Token getChild(String childName) {
		for (Token child : children) {
			if (childName.equalsIgnoreCase(child.getName())) {
				return child;
			}
		}
		return null;
	}

	public boolean hasOnlyChildrenOf(String childName) {
		if (children.isEmpty()) {
			return false;
		}
		for (Token child : children) {
			if (! childName.equalsIgnoreCase(child.getName())) {
				return false;
			}
		}
		return true;
	}

	public String getChildrenType() {
		if (children.isEmpty()) {
			return null;
		}
		Token firstChild = children.get(0);
		for (int i = 1; i < children.size(); i++) {
			if (! firstChild.is(children.get(i).getName())) {
				return "MIXED";
			}
		}
		return firstChild.getName();
	}


	public void addPayload(String key, Object value) {
		if (payload == null) {
			payload = new HashMap<String, Object>();
		}
		payload.put(key, value);
	}

	public Object getPayload(String key) {
		return payload.getOrDefault(key, null);
	}

	public int getChildCount() {
		return children.size();
	}

	public boolean equals(Token o) {
		return name.equalsIgnoreCase(o.getName());
	}

	public List<Token> getChildren() {
		return children;
	}
}
