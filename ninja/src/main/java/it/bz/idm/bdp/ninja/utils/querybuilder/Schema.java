package it.bz.idm.bdp.ninja.utils.querybuilder;

import java.util.ArrayList;
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
			result.addAll(targetDefList.getFinalNames());
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

	public TargetDefList get(final String targetDefListName) {
		TargetDefList targetDefList = getOrNull(targetDefListName);
		if (targetDefList == null) {
			throw new RuntimeException(String.format(
					"TargetDefList '%s' not found! It must exist before you can point to it", targetDefListName));
		}
		return targetDefList;
	}

	public TargetDefList findOrNull(final String aliasOrName) {
		for (TargetDefList targetDefList : schema.values()) {
			if (targetDefList.getByFullName(aliasOrName) != null) {
				return targetDefList;
			}
		}
		return null;
	}

	public TargetDefList find(final String aliasOrName) {
		TargetDefList result = findOrNull(aliasOrName);
		if (result == null) {
			throw new RuntimeException(String.format("No TargetDefList that contains TargetDef with alias or name '%s' found", aliasOrName));
		}
		return result;
	}

	public TargetDefList findOrNull(final String aliasOrName, Set<String> targetDefListNames) {
		for (String targetDefListName : targetDefListNames) {
			TargetDefList targetDefList = schema.get(targetDefListName);
			if (targetDefList == null) {
				return null;
			}
			TargetDef targetDef = targetDefList.getByFullName(aliasOrName);
			if (targetDef != null) {
				return targetDefList;
			}
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

}
