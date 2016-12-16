package it.bz.idm.bdp.dto;

import java.io.Serializable;

public class ChildDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7990410431929026069L;
	private String identifier;	
	private String station;
	public String getIdentifier() {
		return identifier;
	}
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	public String getStation() {
		return station;
	}
	public void setStation(String station) {
		this.station = station;
	}
}
