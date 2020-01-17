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
public class TargetList {

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

	public TargetList(final String name) {
		if (name == null || name.isEmpty()) {
			throw new RuntimeException("A select definition's name must be set!");
		}
		this.name = name;
	}

	public static TargetList init(final String name) {
		return new TargetList(name);
	}

	public String getName() {
		return name;
	}

	public TargetList add(final TargetEntry targetEntry) {
		if (targetEntry == null) {
			throw new RuntimeException("TargetEntry must be non-null");
		}
		if (targetEntryMap.containsKey(targetEntry.getName())) {
			throw new RuntimeException("TargetEntry '" + targetEntry.getName() + "' already exists");
		}
		targetEntryMap.put(targetEntry.getName(), targetEntry);
		return this;
	}

	public Map<String,TargetEntry> getAll() {
		return this.targetEntryMap;
	}

	public TargetEntry get(final String targetEntryName) {
		return this.targetEntryMap.get(targetEntryName);
	}


	public Set<String> getNames() {
		return targetEntryMap.keySet();
	}

	public boolean exists(final String targetEntryName) {
		return targetEntryMap.containsKey(targetEntryName);
	}

	public Map<String, TargetList> getSelectDefinitionsOnly() {
		Map<String, TargetList> result = new HashMap<>();
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
