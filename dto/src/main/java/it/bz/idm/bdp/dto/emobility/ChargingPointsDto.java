package it.bz.idm.bdp.dto.emobility;

import java.io.Serializable;

public class ChargingPointsDto implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1317253632323366368L;
	private Integer plugId;
	private Integer bookable;
	private String status;
	private String chargeState;
	private String plugType;
	private Double maxCurrent;
	private Double minCurrent;
	private Double maxPower;
	public Integer getPlugId() {
		return plugId;
	}
	public void setPlugId(Integer plugId) {
		this.plugId = plugId;
	}

	public Integer getBookable() {
		return bookable;
	}
	public void setBookable(Integer bookable) {
		this.bookable = bookable;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getChargeState() {
		return chargeState;
	}
	public void setChargeState(String chargeState) {
		this.chargeState = chargeState;
	}
	public String getPlugType() {
		return plugType;
	}
	public void setPlugType(String plugType) {
		this.plugType = plugType;
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
	public Double getMaxPower() {
		return maxPower;
	}
	public void setMaxPower(Double maxPower) {
		this.maxPower = maxPower;
	}
}
