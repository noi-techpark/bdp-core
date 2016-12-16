package it.bz.idm.bdp.dto.emobility;

import java.util.ArrayList;
import java.util.List;

import it.bz.idm.bdp.dto.StationDto;

public class EchargingPlugDto extends StationDto{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7770495430287952644L;
	private Double maxCurrent;
	private Double minCurrent;
	private String plugType;
	private Double maxPower;
	private String parentStation;
	private List<OutletDtoV2> outlets = new ArrayList<OutletDtoV2>();
	
	public EchargingPlugDto() {
	}
	public EchargingPlugDto(String stationcode, String name, Double y,
			Double x, Double maxCurrent, Double minCurrent, String plugType,
			Double maxPower,String parent) {
		super(stationcode, name, y, x);
		this.maxCurrent = maxCurrent;
		this.minCurrent = minCurrent;
		this.plugType = plugType;
		this.maxPower = maxPower;
		this.parentStation = parent;
	}
	public EchargingPlugDto(String stationcode, String name, Double y,
			Double x, String parent, List<OutletDtoV2> outlets) {
		super(stationcode, name, y, x);
		this.parentStation = parent;
		this.outlets = outlets;

	}
	public Double getMaxCurrent() {
		return maxCurrent;
	}
	public void setMaxCurrent(Double maxCurrent) {
		this.maxCurrent = maxCurrent;
	}
	public Double getMinCurrent() {
		return minCurrent;
	}
	public void setMinCurrent(Double minCurrent) {
		this.minCurrent = minCurrent;
	}
	public String getPlugType() {
		return plugType;
	}
	public void setPlugType(String plugType) {
		this.plugType = plugType;
	}
	public Double getMaxPower() {
		return maxPower;
	}
	public void setMaxPower(Double maxPower) {
		this.maxPower = maxPower;
	}
	public String getParentStation() {
		return parentStation;
	}
	public void setParentStation(String parent) {
		this.parentStation = parent;
	}
	public List<OutletDtoV2> getOutlets() {
		return outlets;
	}
	public void setOutlets(List<OutletDtoV2> outlets) {
		this.outlets = outlets;
	}
	
}
