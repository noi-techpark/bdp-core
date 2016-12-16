package it.bz.idm.bdp.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import it.bz.idm.bdp.dto.StationDto;

@JsonInclude(value=Include.NON_EMPTY)
public class StationDto implements Serializable{

	private static final long serialVersionUID = 7928534360551629831L;
	protected String id;
	protected String name;
	protected Double latitude;
	protected Double longitude;
	protected String crs;
	private String origin;
	
	public StationDto() {
	}
	public StationDto(String stationcode, String name, Double y, Double x) {
		this.id = stationcode;
		this.name = name;
		this.longitude = x;
		this.latitude = y;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Double getLatitude() {
		return latitude;
	}
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	public Double getLongitude() {
		return longitude;
	}
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
	public String getCrs() {
		return crs;
	}
	public void setCrs(String crs) {
		this.crs = crs;
	}
	public String getOrigin() {
		return origin;
	}
	public void setOrigin(String origin) {
		this.origin = origin;
	}
	public boolean checkIfValid() {
		return this.id != null && !this.id.isEmpty();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof StationDto){
			StationDto dto =(StationDto) obj;
			if (this.getId().equals(dto.getId()))
				return true;
		}
		return false;
	}
	@Override
	public int hashCode() {
		return 1;
	}
	
}
