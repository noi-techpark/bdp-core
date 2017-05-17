package it.bz.idm.bdp.dto;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TypeMapDto implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2027158353405740826L;
	private Map<String,List<SimpleRecordDto>> recordsByType = new LinkedHashMap<String, List<SimpleRecordDto>>();

	public Map<String, List<SimpleRecordDto>> getRecordsByType() {
		return recordsByType;
	}
}
