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

	private boolean dirty = true;

	/* We use a tree map here, because we want to have elements naturally sorted */
	private Map<String, TargetDefList> schema = new TreeMap<String, TargetDefList>();
	private Map<String, String> targetDefNameToAliasMap = new TreeMap<String, String>();
	private Map<String, List<TargetDef>> aliasOrNameToTargetDefMap = new TreeMap<String, List<TargetDef>>();

	public Schema add(final TargetDefList targetDefList) {
		if (targetDefList == null) {
			throw new RuntimeException("A Schema only contains non-null TargetDefLists");
		}
		if (schema.containsKey(targetDefList.getName())) {
			throw new RuntimeException(String.format("TargetDefList '%s' already exists", targetDefList.getName()));
		}
		schema.put(targetDefList.getName(), targetDefList);
		dirty = true;
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

	public TargetDefList get(final String targetDefListName) {
		TargetDefList targetDefList = getOrNull(targetDefListName);
		if (targetDefList == null) {
			throw new RuntimeException(String.format(
					"TargetDefList '%s' not found! It must exist before you can point to it", targetDefListName));
		}
		return targetDefList;
	}

	public TargetDefList findOrNull(final String targetName) {
		for (TargetDefList targetDefList : schema.values()) {
			if (targetDefList.get(targetName) != null) {
				return targetDefList;
			}
		}
		return null;
	}

	public TargetDefList find(final String targetName) {
		TargetDefList result = findOrNull(targetName);
		if (result == null) {
			throw new RuntimeException(String.format("No TargetDefList that contains TargetDef with alias or name '%s' found", targetName));
		}
		return result;
	}

	/** FIXME aliases could be used more than ones, we must therefore return an array of TargetDefLists */
	public TargetDefList findOrNull(final String targetName, Set<String> targetDefListNames) {
		for (String targetDefListName : targetDefListNames) {
			TargetDefList targetDefList = schema.get(targetDefListName);
			if (targetDefList == null) {
				return null;
			}
			TargetDef targetDef = targetDefList.get(targetName);
			if (targetDef != null) {
				return targetDefList;
			}
		}
		return null;
	}

	public TargetDefList find(final String targetName, Set<String> targetDefListNames) {
		TargetDefList result = findOrNull(targetName, targetDefListNames);
		if (result == null) {
			throw new RuntimeException(String.format("No TargetDefList that contains TargetDef with alias or name '%s' found", targetDefListNames.toString(), targetName));
		}
		return result;
	}

	public List<TargetDef> getTargetDefs(final String targetName) {
		if (dirty) {
			compile();
		}
		return aliasOrNameToTargetDefMap.get(targetName);
	}

	public Map<String, String> getTargetDefNameToAliasMap() {
		if (dirty) {
			compile();
		}
		return this.targetDefNameToAliasMap;
	}

	public Schema compile() {
		for (TargetDefList targetDefList : schema.values()) {
			for (TargetDef targetDef : targetDefList.getAll().values()) {
				if (targetDef.hasAlias()) {
					targetDefNameToAliasMap.put(targetDef.getName(), targetDef.getAlias());
				}
			}
		}

		for (TargetDefList targetDefList : schema.values()) {
			for (TargetDef targetDef : targetDefList.getAll().values()) {
				_addToMapList(targetDef.getName(), targetDef);
				if(targetDef.hasAlias()) {
					_addToMapList(targetDef.getAlias(), targetDef);
				}
			}
		}
		dirty = false;
		return this;
	}

	private void _addToMapList(final String nameOrAlias, TargetDef targetDef) {
		List<TargetDef> list = aliasOrNameToTargetDefMap.get(nameOrAlias);
		if (list == null) {
			list = new ArrayList<TargetDef>();
			list.add(targetDef);
			aliasOrNameToTargetDefMap.put(nameOrAlias, list);
		}  else {
			list.add(targetDef);
		}
	}

}
