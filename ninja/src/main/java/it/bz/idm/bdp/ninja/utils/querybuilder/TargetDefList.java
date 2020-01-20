package it.bz.idm.bdp.ninja.utils.querybuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * <pre>
 * A TargetList is a hierarchy of definition names (categories), aliases
 * and table/columns.
 *
 * Example:
 *
 *    EMPLOYEE(ename->emp.fullname,emanager)
 *                                    `-------MANAGER(mname->mgr.fullname)
 *
 * Here definitions are EMPLOYEE and MANAGER, aliases are ename, emanager and
 * mname, and table/column binaries are emp.fullname and mgr.fullname.
 * TODO rewrite this to match TargetDef/targetlist
 * </pre>
 *
 * @author Peter Moser <p.moser@noi.bz.it>
 */
public class TargetDefList {

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
	private Map<String, TargetDef> targetMap = new HashMap<String, TargetDef>();

	public TargetDefList(final String name) {
		if (name == null || name.isEmpty()) {
			throw new RuntimeException("A TargetDefList's name must be set!");
		}
		this.name = name;
	}

	public static TargetDefList init(final String name) {
		return new TargetDefList(name);
	}

	public String getName() {
		return name;
	}

	public TargetDefList add(final TargetDef TargetDef) {
		if (TargetDef == null) {
			throw new RuntimeException("TargetDef must be non-null");
		}
		if (targetMap.containsKey(TargetDef.getName())) {
			throw new RuntimeException("TargetDef '" + TargetDef.getName() + "' already exists");
		}
		targetMap.put(TargetDef.getName(), TargetDef);
		return this;
	}

	public Map<String,TargetDef> getAll() {
		return this.targetMap;
	}

	public TargetDef get(final String targetName) {
		return this.targetMap.get(targetName);
	}


	public Set<String> getNames() {
		return targetMap.keySet();
	}

	public boolean exists(final String targetName) {
		return targetMap.containsKey(targetName);
	}

	public Map<String, TargetDefList> getTargetListsOnly() {
		Map<String, TargetDefList> result = new HashMap<>();
		for (TargetDef TargetDef : targetMap.values()) {
			if (TargetDef.hasTargetDefList()) {
				result.put(TargetDef.getName(), TargetDef.getTargetList());
			}
		}
		return result;
	}

	@Override
	public String toString() {
		return "TargetList [name=" + name + ", targets=" + targetMap.toString() + "]";
	}

}
