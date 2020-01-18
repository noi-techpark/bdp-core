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
 * TODO rewrite this to match target/targetlist
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
	private Map<String, Target> targetMap = new HashMap<String, Target>();

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

	public TargetList add(final Target target) {
		if (target == null) {
			throw new RuntimeException("Target must be non-null");
		}
		if (targetMap.containsKey(target.getName())) {
			throw new RuntimeException("Target '" + target.getName() + "' already exists");
		}
		targetMap.put(target.getName(), target);
		return this;
	}

	public Map<String,Target> getAll() {
		return this.targetMap;
	}

	public Target get(final String targetName) {
		return this.targetMap.get(targetName);
	}


	public Set<String> getNames() {
		return targetMap.keySet();
	}

	public boolean exists(final String targetName) {
		return targetMap.containsKey(targetName);
	}

	public Map<String, TargetList> getTargetListsOnly() {
		Map<String, TargetList> result = new HashMap<>();
		for (Target target : targetMap.values()) {
			if (target.hasTargetList()) {
				result.put(target.getName(), target.getTargetList());
			}
		}
		return result;
	}

	@Override
	public String toString() {
		return "TargetList [name=" + name + ", targets=" + targetMap.toString() + "]";
	}

}
