package it.bz.idm.bdp.ninja.utils.querybuilder;

/**
 * Target
 *
 * An API consumer uses a Target to select columns or a set of columns. So, if
 * the Target has no column, it is a pointer to a TargetList instead.
 */
public class Target {
	private final String name;
	private final TargetList targetList;
	private final String column;
	private String sqlBefore;
	private String sqlAfter;
	private String alias;

	public Target(final String name, final String column) {
		if (name == null || name.isEmpty() || column == null || column.isEmpty()) {
			throw new RuntimeException("A target must have non-empty name and a non-empty column");
		}
		this.name = name;
		this.column = column;
		this.targetList = null;
	}

	public Target(final String name, final TargetList targetList) {
		if (name == null || name.isEmpty() || targetList == null) {
			throw new RuntimeException("A target must have non-empty name and a non-null pointer to a TargetList");
		}
		this.name = name;
		this.column = null;
		this.targetList = targetList;
	}

	public String getColumn() {
		return column;
	}

	public TargetList getTargetList() {
		return this.targetList;
	}

	public String getName() {
		return name;
	}

	public String getAlias() {
		return alias;
	}

	public Target alias(final String alias) {
		if (alias == null || alias.isEmpty()) {
			throw new RuntimeException("A target's alias must be non-empty!");
		}
		this.alias = alias;
		return this;
	}

	public String getSqlBefore() {
		return this.sqlBefore;
	}

	public String getSqlAfter() {
		return this.sqlAfter;
	}

	/**
	 * Get the final name for the current target entry, which is either the alias if present,
	 * or the name itself.
	 *
	 * @return final name to be shown to API users
	 */
	public String getFinalName() {
		return alias == null ? name : alias;
	}

	public Target sqlBefore(final String sqlBefore) {
		this.sqlBefore = sqlBefore;
		return this;
	}

	public Target sqlAfter(final String sqlAfter) {
		this.sqlAfter = sqlAfter;
		return this;
	}

	public boolean hasAlias() {
		return alias != null;
	}

	public boolean hasSqlBefore() {
		return sqlBefore != null;
	}

	public boolean hasSqlAfter() {
		return sqlAfter != null;
	}

	public boolean hasTargetList() {
		return targetList != null;
	}

	public boolean hasColumn() {
		return column != null;
	}
}
