package it.bz.idm.bdp.ninja.utils.querybuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Schema
 */
public class Schema {

	/* We use a tree map here, because we want to have elements naturally sorted */
	private Map<String, TargetDefList> schema = new TreeMap<String, TargetDefList>();

	public Schema add(final TargetDefList targetDefList) {
		if (targetDefList == null) {
			throw new RuntimeException("A Schema only contains non-null TargetDefLists");
		}
		if (schema.containsKey(targetDefList.getName())) {
			throw new RuntimeException(String.format("TargetDefList '%s' already exists", targetDefList.getName()));
		}
		schema.put(targetDefList.getName(), targetDefList);
		return this;
	}

	public TargetDefList get(final String targetDefListName) {
		TargetDefList targetDefList = getOrNull(targetDefListName);
		if (targetDefList == null) {
			throw new RuntimeException(String.format(
					"TargetDefList '%s' not found! It must exist before you can point to it", targetDefListName));
		}
		return targetDefList;
	}

	public TargetDefList findOrNull(final String targetDefName) {
		for (TargetDefList targetDefList : schema.values()) {
			if (targetDefList.get(targetDefName) != null) {
				return targetDefList;
			}
		}
		return null;
	}

	public TargetDefList findByAliasOrNull(final String alias) {
		for (TargetDefList targetDefList : schema.values()) {
			TargetDef targetDef = targetDefList.getByAlias(alias);
			if (targetDef != null) {
				return targetDefList;
			}
		}
		return null;
	}

	public TargetDefList findByAliasOrNull(final String alias, Set<String> targetDefListNames) {
		TargetDefList targetDefList = findByAliasOrNull(alias);
		if (targetDefList != null && targetDefListNames.contains(targetDefList.getName())) {
			return targetDefList;
		}
		return null;
	}

	public TargetDefList find(final String targetDefName) {
		TargetDefList result = findOrNull(targetDefName);
		if (result == null) {
			throw new RuntimeException(String.format("No TargetDefList that contains TargetDef '%s' found", targetDefName));
		}
		return result;
	}

	public TargetDefList findOrNull(final String targetDefName, Set<String> targetDefListNames) {
		TargetDefList targetDefList = findOrNull(targetDefName);
		if (targetDefList != null && targetDefListNames.contains(targetDefList.getName())) {
			return targetDefList;
		}
		return null;
	}

	public TargetDefList find(final String targetDefName, Set<String> targetDefListNames) {
		TargetDefList result = findOrNull(targetDefName, targetDefListNames);
		if (result == null) {
			throw new RuntimeException(String.format("No TargetDefList in '%s' that contains TargetDef '%s' found", targetDefListNames.toString(), targetDefName));
		}
		return result;
	}

	public Collection<TargetDefList> getAll() {
		return schema.values();
	}

	public List<TargetDefList> getAll(Set<String> targetDefListNames) {
		List<TargetDefList> result = new ArrayList<TargetDefList>();
		for (String targetDefListName : targetDefListNames) {
			TargetDefList targetDefList = get(targetDefListName);
			result.add(targetDefList);
		}
		return result;
	}

	public TargetDefList getOrNull(final String targetDefListName) {
		return schema.get(targetDefListName);
	}

	public TargetDefList getOrNew(final String targetDefListName) {
		return schema.getOrDefault(targetDefListName, new TargetDefList(targetDefListName));
	}

	public List<String> getListNames(Set<String> targetDefListNames) {
		List<String> result = new ArrayList<String>();
		for (String targetDefListName : targetDefListNames) {
			TargetDefList targetDefList = get(targetDefListName);
			result.addAll(targetDefList.getNames());
		}
		return result;
	}

	public List<String> getListNames() {
		List<String> result = new ArrayList<String>();
		for (TargetDefList targetDefList : schema.values()) {
			result.addAll(targetDefList.getNames());
		}
		return result;
	}

	public TargetDefList getTargetDefListParent(final String childTargetDefListName, Set<String> targetListNames) {
		for (TargetDefList def : getAll(targetListNames)) {
			for (TargetDefList child : def.getPointerTargets().values()) {
				if (child.getName().equals(childTargetDefListName))
					return def;
			}
		}
		return null;
	}

	// XXX do we need dirty flags here for performance needed?
	// private void _build() {
	// 	if (schema == null) {
	// 		throw new SimpleException(ErrorCode.SCHEMA_NULL);
	// 	}
	// 	dirty = true;
	// 	dirty = false;
	// }
}
