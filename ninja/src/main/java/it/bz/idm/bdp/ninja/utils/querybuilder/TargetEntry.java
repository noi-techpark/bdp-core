package it.bz.idm.bdp.ninja.utils.querybuilder;

/**
 * TargetEntry
 *
 * An API consumer uses a TargetEntry to select columns or a pointer that creates , if he wants to select a certain
 * column, or a set of columns. So, if the TargetEntry has no column set, it is
 * just a pointer to a SelectDefinition, which contains addtional sets of
 * columns.
 */
public class TargetEntry {
	private final String name;
	private final TargetList selectDefinition;
	private final String column;
	private String sqlBefore;
	private String sqlAfter;
	private String alias;

	public TargetEntry(final String name, final String column) {
		if (name == null || name.isEmpty() || column == null || column.isEmpty()) {
			throw new RuntimeException("A TargetEntry must have non-empty name and a non-empty column");
		}
		this.name = name;
		this.column = column;
		this.selectDefinition = null;
	}

	public TargetEntry(final String name, final TargetList selectDefinition) {
		if (name == null || name.isEmpty() || selectDefinition == null) {
			throw new RuntimeException("A TargetEntry must have non-empty name and a non-null pointer to a SelectDefinition");
		}
		this.name = name;
		this.column = null;
		this.selectDefinition = selectDefinition;
	}

	public String getColumn() {
		return column;
	}

	public TargetList getSelectDefinition() {
		return this.selectDefinition;
	}

	public String getName() {
		return name;
	}

	public String getAlias() {
		return alias;
	}

	public TargetEntry alias(final String alias) {
		if (alias == null || alias.isEmpty()) {
			throw new RuntimeException("A TargetEntry's alias must be non-empty!");
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

	public TargetEntry sqlBefore(final String sqlBefore) {
		this.sqlBefore = sqlBefore;
		return this;
	}

	public TargetEntry sqlAfter(final String sqlAfter) {
		this.sqlAfter = sqlAfter;
		return this;
	}

	public boolean isSelectDefinition() {
		return selectDefinition != null;
	}

	public boolean isColumn() {
		return column != null;
	}
}
