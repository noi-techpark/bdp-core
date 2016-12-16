package it.bz.idm.bdp.dto;

import java.io.Serializable;

public class CoordinateDto implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4490883796363760913L;
	private double lon;
	private double lat;

	public CoordinateDto() {
	}
	public CoordinateDto(double longitude, double latitude) {
		this.lon=longitude;
		this.lat=latitude;
	}
	public double getLon() {
		return lon;
	}
	public void setLon(double lon) {
		this.lon = lon;
	}
	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	
}
