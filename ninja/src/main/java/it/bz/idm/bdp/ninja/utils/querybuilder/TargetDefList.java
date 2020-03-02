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
	 * The key of this map is the {@link TargetDef#getName()}.
	 *
	 * For example, mvalue_double & measurements.double_value
	 * This will be rewritten to "measurements.double_value AS mvalue_double"
	 */
	private Map<String, TargetDef> nameMap = new HashMap<String, TargetDef>();
	private Map<String, TargetDef> aliasMap = new HashMap<String, TargetDef>();
	private Map<String, TargetDef> fullNameMap = new HashMap<String, TargetDef>();

	public TargetDefList(final String name) {
		if (name == null || name.isEmpty()) {
			throw new RuntimeException("A TargetDefList must have a non-empty name!");
		}
		this.name = name;
	}

	public static TargetDefList init(final String name) {
		return new TargetDefList(name);
	}

	public String getName() {
		return name;
	}

	public TargetDefList add(final TargetDef targetDef) {
		if (targetDef == null) {
			throw new RuntimeException("TargetDef must be non-null");
		}
		if (fullNameMap.containsKey(targetDef.getFinalName())) {
			throw new RuntimeException("TargetDef '" + targetDef.getFinalName() + "' already exists. Aliases and names must be unique (also between each other)");
		}
		nameMap.put(targetDef.getName(), targetDef);
		fullNameMap.put(targetDef.getFinalName(), targetDef);
		if (targetDef.hasAlias()) {
			aliasMap.put(targetDef.getAlias(), targetDef);
		}
		return this;
	}

	public Map<String,TargetDef> getAll() {
		return this.nameMap;
	}

	public Map<String,TargetDef> getAllFullNames() {
		return this.fullNameMap;
	}

	public TargetDef get(final String targetName) {
		return this.nameMap.get(targetName);
	}

	public TargetDef getByFullName(final String aliasOrName) {
		return this.fullNameMap.get(aliasOrName);
	}

	public Set<String> getNames() {
		return nameMap.keySet();
	}

	public boolean exists(final String targetName) {
		return nameMap.containsKey(targetName);
	}

	public Map<String, TargetDefList> getPointerTargets() {
		Map<String, TargetDefList> result = new HashMap<>();
		for (TargetDef targetDef : nameMap.values()) {
			if (targetDef.hasTargetDefList()) {
				result.put(targetDef.getName(), targetDef.getTargetList());
			}
		}
		return result;
	}

	public Map<String, TargetDefList> getColumnTargets() {
		Map<String, TargetDefList> result = new HashMap<>();
		for (TargetDef targetDef : nameMap.values()) {
			if (targetDef.hasColumn()) {
				result.put(targetDef.getName(), targetDef.getTargetList());
			}
		}
		return result;
	}

	public TargetDef getByAlias(final String alias) {
		return aliasMap.get(alias);
	}

	@Override
	public String toString() {
		return "TargetList [name=" + name + ", targets=" + nameMap.toString() + "]";
	}

}
