package it.bz.idm.bdp.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public class DataMapDto <X extends RecordDtoImpl> implements Serializable{
	

	private static final long serialVersionUID = -6053193762265167013L;
	private List<RecordDtoImpl> data = new ArrayList<RecordDtoImpl>();
	private Map<String,DataMapDto<RecordDtoImpl>> branch = new TreeMap<String,DataMapDto<RecordDtoImpl>>();
	
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

}
