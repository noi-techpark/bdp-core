package it.bz.idm.bdp.ninja.utils.querybuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A TargetDefList is a hierarchy of {@link TargetDef}, where a target definition
 * can either be a simple column, or a pointer to another {@link TargetDefList}.
 *
 * Example:
 * <pre>
 *    EMPLOYEE(ename->emp.fullname, emanager)
 *                                     `-------MANAGER(mname->mgr.fullname, mpos->mgr.position)
 * </pre>
 * Here {@link TargetDefList}s are EMPLOYEE and MANAGER, {@link TargetDef} names
 * are <code>ename</code>, <code>emanager</code> and <code>mname</code>.
 *
 * The final SQL select target lists looks then like this, depending on the choosen
 * final names:
 * <pre>
 * FINAL NAMES = [ename, emanager]
 * SQL SELECT  = emp.fullname as ename, mgr.fullname as mname, mgr.position as mpos
 *
 * FINAL NAMES = [ename]
 * SQL SELECT  = emp.fullname as ename
 *
 * FINAL NAMES = [ename, mname]
 * SQL SELECT  = emp.fullname as ename, mgr.fullname as mname
 * </pre>
 *
 * @author Peter Moser <p.moser@noi.bz.it>
 */
public class TargetDefList {

	private final String name;

	/**
	 * The key of this map is the result of {@link TargetDef#getFinalName()}.
	 *
	 * For example, mvalue_double --> measurements.double_value
	 * This will be rewritten to "measurements.double_value AS mvalue_double"
	 */
	private Map<String, TargetDef> finalNameMap = new HashMap<String, TargetDef>();

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
		if (finalNameMap.containsKey(targetDef.getFinalName())) {
			throw new RuntimeException("TargetDef '" + targetDef.getFinalName() + "' already exists. Aliases and names must be unique (also between each other)");
		}
		finalNameMap.put(targetDef.getFinalName(), targetDef);
		return this;
	}

	public Map<String, TargetDef> getAll() {
		return this.finalNameMap;
	}

	public TargetDef get(final String aliasOrName) {
		return this.finalNameMap.get(aliasOrName);
	}

	public Set<String> getFinalNames() {
		return finalNameMap.keySet();
	}

	@Override
	public String toString() {
		return "TargetDefList [name=" + name + ", targets=" + finalNameMap.toString() + "]";
	}

}
