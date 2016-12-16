package it.bz.idm.bdp.dto;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import it.bz.idm.bdp.dto.SimpleRecordDto;

public class TypeMapDto implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2027158353405740826L;
	private Map<String,Set<SimpleRecordDto>> recordsByType = new LinkedHashMap<String, Set<SimpleRecordDto>>();

	public Map<String, Set<SimpleRecordDto>> getRecordsByType() {
		return recordsByType;
	}
}
