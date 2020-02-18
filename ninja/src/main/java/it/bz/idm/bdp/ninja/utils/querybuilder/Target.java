package it.bz.idm.bdp.ninja.utils.querybuilder;


/**
 * Target
 */
public class Target implements Comparable<Target> {
	// Do not forget to update ErrorCode.ALIAS_INVALID in SelectExpansion
	private static final String TARGET_FQN_VALIDATION = "[0-9a-zA-Z\\._]+";

	private String name;
	private String func;
	private String json;
	private TargetDef targetDef;
	private String defListName;

	public Target(final String plainText) {
		func = null;
		json = null;

		if (plainText == null || plainText.trim().equals("*")) {
			name = "*";
			return;
		}

		name = plainText.trim();

		if (name.matches("[a-zA-Z_]+\\(" + TARGET_FQN_VALIDATION + "\\)")) {
			int idx = name.indexOf('(');
			func = name.substring(0, idx);
			name = name.substring(idx + 1, name.length() - 1);
		}

		if (! name.matches(TARGET_FQN_VALIDATION)) {
			throw new RuntimeException("The target '" + name + "' is invalid.");
		}

		int index = name.indexOf('.');
		if (index > 0) {
			json = name.substring(index + 1);
			name = name.substring(0, index);
		}
	}

	@Override
	public String toString() {
		return "{Target: " +
			"name='" + getName() + "'" +
			", func='" + getFunc() + "'" +
			", json='" + getJson() + "'" +
			"}";
	}

	public String getJson() {
		return json;
	}

	public String getFunc() {
		return func;
	}

	public String getName() {
		return name;
	}

	public String getFullName() {
		if (hasJson()) {
			return name + "." + json;
		}
		return name;
	}

	public boolean hasFunction() {
		return func != null;
	}

	public boolean hasJson() {
		return json != null;
	}

	public void setTargetDef(TargetDef targetDef) {
		this.targetDef = targetDef;
	}

	public TargetDef getTargetDef() {
		return targetDef;
	}

	public void setTargetDefListName(String name) {
		this.defListName = name;
	}

	public String getTargetDefListName() {
		return defListName;
	}

	@Override
	public int compareTo(Target o) {
		return name.compareTo(o.getName());
	}

	@Override
    public boolean equals(Object other)
    {
		if (other == null)
			return false;

		if (this == other)
			return true;

		return other instanceof Target && name.equals(((Target)other).getName());
    }

}
