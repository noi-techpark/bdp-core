package it.bz.idm.bdp.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataMapDto extends HashMap<String, DataMapDto>{
	

	private static final long serialVersionUID = -6053193762265167013L;
	private List<RecordDto> data = new ArrayList<RecordDto>();
	
	/**
	 * This tree will always return data of the current branch, if that condition is not fullfilled it will search in it's branches and so on until the leafes are reached 
	 * 
	 */
	public List<RecordDto> getData(){
		if (data.isEmpty()) {
			if (this.isEmpty())
				return data;
			for (Map.Entry<String, DataMapDto> entry : this.entrySet()) {
				data.addAll(entry.getValue().getData());
			}
		}
		return data;
	}
	public void setData(List<RecordDto> records) {
		this.data = records;
	}

}
