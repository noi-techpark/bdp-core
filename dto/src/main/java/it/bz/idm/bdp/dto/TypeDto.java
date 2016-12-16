package it.bz.idm.bdp.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class TypeDto implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1224947780318447560L;
	private String id;
	private String unit;
	private Map<String,String> desc = new HashMap<String, String>();
	private String typeOfMeasurement;
	private Set<Integer> aquisitionIntervalls = new TreeSet<Integer>();
	
	public TypeDto() {
	}
	public TypeDto(String id, Integer interval) {
		this.id = id;
		if (interval!= null)
			this.aquisitionIntervalls.add(interval);
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public Map<String, String> getDesc() {
		return desc;
	}
	public void setDesc(Map<String, String> desc) {
		this.desc = desc;
	}
	public String getTypeOfMeasurement() {
		return typeOfMeasurement;
	}
	public void setTypeOfMeasurement(String typeOfMeasurement) {
		this.typeOfMeasurement = typeOfMeasurement;
	}
	public Set<Integer> getAquisitionIntervalls() {
		return aquisitionIntervalls;
	}
	public void setAquisitionIntervalls(Set<Integer> aquisitionIntervalls) {
		this.aquisitionIntervalls = aquisitionIntervalls;
	}
	
}
