package it.bz.idm.bdp.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public class DataMapDto <X extends RecordDtoImpl> implements Serializable{

	private static final long serialVersionUID = -6053193762265167013L;
	private String name = "(default)";
	private List<RecordDtoImpl> data = new ArrayList<RecordDtoImpl>();
	private Map<String,DataMapDto<RecordDtoImpl>> branch = new TreeMap<String,DataMapDto<RecordDtoImpl>>();

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public DataMapDto() {
	}
	public DataMapDto(List<RecordDtoImpl> data) {
		this.data = data;
	}
	public List<RecordDtoImpl> getData(){
		return data;
	}
	public void setData(List<RecordDtoImpl> records) {
		this.data = records;
	}
	public Map<String, DataMapDto<RecordDtoImpl>> getBranch() {
		return branch;
	}

	/**
	 * Insert a new branch if the key does not exist, otherwise give the existing branch back.
	 *
	 * @param key
	 *            to find the branch inside the data map
	 * @return the existing branch associated to the given key, or a new branch with that key
	 */
	public DataMapDto<RecordDtoImpl> upsertBranch(String key) {
		DataMapDto<RecordDtoImpl> existingMap = branch.get(key);

		if (existingMap == null) {
			existingMap = new DataMapDto<RecordDtoImpl>();
			branch.put(key, existingMap);
		}

		return existingMap;
	}

}
