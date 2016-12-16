package it.bz.idm.bdp.dto.emobility;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(value=Include.NON_EMPTY)
public class OutletDtoV2 implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1925244922468895580L;
	private String id;
	private String outletTypeCode;
	private Double minCurrent;
	private Double maxCurrent;
	private Double maxPower;
	private Boolean	hasFixedCable;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getOutletTypeCode() {
		return outletTypeCode;
	}
	public void setOutletTypeCode(String outletTypeCode) {
		this.outletTypeCode = outletTypeCode;
	}
	public Double getMinCurrent() {
		return minCurrent;
	}
	public void setMinCurrent(Double minCurrent) {
		this.minCurrent = minCurrent;
	}
	public Double getMaxCurrent() {
		return maxCurrent;
	}
	public void setMaxCurrent(Double maxCurrent) {
		this.maxCurrent = maxCurrent;
	}
	public Double getMaxPower() {
		return maxPower;
	}
	public void setMaxPower(Double maxPower) {
		this.maxPower = maxPower;
	}
	public Boolean getHasFixedCable() {
		return hasFixedCable;
	}
	public void setHasFixedCable(Boolean hasFixedCable) {
		this.hasFixedCable = hasFixedCable;
	}
}
