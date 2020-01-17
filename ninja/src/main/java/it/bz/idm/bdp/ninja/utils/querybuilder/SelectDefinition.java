package it.bz.idm.bdp.ninja.utils.querybuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * <pre>
 * A select definition is a hierarchy of definition names (categories), aliases
 * and table/columns.
 *
 * Example:
 *
 *    EMPLOYEE(ename->emp.fullname,emanager)
 *                                    `-------MANAGER(mname->mgr.fullname)
 *
 * Here definitions are EMPLOYEE and MANAGER, aliases are ename, emanager and
 * mname, and table/column binaries are emp.fullname and mgr.fullname.
 * </pre>
 *
 * @author Peter Moser <p.moser@noi.bz.it>
 */
public class SelectDefinition {

	private final String name;

	/**
	 * <pre>
	 * Column names in SQL, where the key is the ALIAS and the value is
	 * the COLUMN name.
	 *
	 * For example, mvalue -> measurements.double_value
	 * This could be rewritten into: "measurements.double_value AS mvalue"
	 * </pre>
	 */
	private Map<String, TargetEntry> targetEntryMap = new HashMap<String, TargetEntry>();

	public SelectDefinition(final String name) {
		if (name == null || name.isEmpty()) {
			throw new RuntimeException("A select definition's name must be set!");
		}
		this.name = name;
	}

	public static SelectDefinition init(final String name) {
		return new SelectDefinition(name);
	}

	public String getName() {
		return name;
	}

	public SelectDefinition addTargetEntry(final TargetEntry targetEntry) {
		if (targetEntry == null) {
			throw new RuntimeException("TargetEntry must be non-null");
		}
		if (targetEntryMap.containsKey(targetEntry.getName())) {
			throw new RuntimeException("TargetEntry '" + targetEntry.getName() + "' already exists");
		}
		targetEntryMap.put(targetEntry.getName(), targetEntry);
		return this;
	}

	public Map<String,TargetEntry> getTargetEntryMap() {
		return this.targetEntryMap;
	}

	public Set<String> getTargetEntryNames() {
		return targetEntryMap.keySet();
	}

	public void addPointer(final String targetEntryName, final SelectDefinition selectDefinition) {
		if (targetEntryName == null || targetEntryName.isEmpty() || selectDefinition == null) {
			throw new RuntimeException("A select definition's pointer must have a name and a valid sub-definition!");
		}
		if (targetEntryMap.containsKey(targetEntryName)) {
			throw new RuntimeException("TargetEntry with name '" + targetEntryName + "' already exists!");
		}
		targetEntryMap.put(targetEntryName, new TargetEntry(targetEntryName, selectDefinition));
	}

	public SelectDefinition getSelectDefinition(final String targetEntryName) {
		return targetEntryMap.get(targetEntryName).getSelectDefinition();
	}

	public String getColumn(final String targetEntryName) {
		return targetEntryMap.get(targetEntryName).getColumn();
	}

	public boolean exists(final String targetEntryName) {
		return targetEntryMap.containsKey(targetEntryName);
	}

	public boolean isSelectDefinition(final String targetEntryName) {
		return targetEntryMap.get(targetEntryName).isSelectDefinition();
	}

	public boolean isColumn(final String targetEntryName) {
		return targetEntryMap.get(targetEntryName).isColumn();
	}

	public Map<String, SelectDefinition> getSelectDefinitionsOnly() {
		Map<String, SelectDefinition> result = new HashMap<>();
		for (TargetEntry targetEntry : targetEntryMap.values()) {
			if (targetEntry.isSelectDefinition()) {
				result.put(targetEntry.getName(), targetEntry.getSelectDefinition());
			}
		}
		return result;
	}

	@Override
	public String toString() {
		return "SelectDefinition [name=" + name + ", targetEntries=" + targetEntryMap.toString() + "]";
	}

}
