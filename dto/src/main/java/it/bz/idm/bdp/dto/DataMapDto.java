package it.bz.idm.bdp.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public class DataMapDto implements Serializable{
	

	private static final long serialVersionUID = -6053193762265167013L;
	private List<SimpleRecordDto> data = new ArrayList<SimpleRecordDto>();
	private Map<String,DataMapDto> branch = new TreeMap<String,DataMapDto>();
	
	public DataMapDto() {
	}
	public DataMapDto(List<SimpleRecordDto> data) {
		this.data = data;
	}
	/**
	 * This tree will always return data of the current branch, if the current branch has no data it will search in it's child branches and so on until the leafes are reached or data is found 
	 * 
	 */
	public List<SimpleRecordDto> getData(){
		if (data.isEmpty()) {
			if (this.branch.isEmpty())
				return data;
			for (Map.Entry<String, DataMapDto> entry : this.branch.entrySet()) {
				data.addAll(entry.getValue().getData());
			}
		}
		return data;
	}
	public void setData(List<SimpleRecordDto> records) {
		this.data = records;
	}
	
	public Map<String, DataMapDto> getBranch() {
		return branch;
	}

}
