package it.bz.idm.bdp.dto.bikesharing;

import java.util.Set;

import it.bz.idm.bdp.dto.StationDto;


public class BikeSharingStationDto extends StationDto {
	/**
	 * 
	 */
	private static final long serialVersionUID = 902769333357686586L;
	private Integer status;
	private Set<String> availableBikeTypes;

	/**
	 * 
	 */
	public BikeSharingStationDto() {
	}

	public BikeSharingStationDto(String stationcode, String name, Double lon,
			Double lat,Integer status, Set<String> availableBikeTypes) {
		super(stationcode, name, lon, lat);
		this.availableBikeTypes = availableBikeTypes;
		this.status = status;
	}
	

	public Set<String> getAvailableBikeTypes() {
		return availableBikeTypes;
	}

	public void setAvailableBikeTypes(Set<String> availableBikeTypes) {
		this.availableBikeTypes = availableBikeTypes;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Integer getStatus() {
		return status;
	}
	
	
}
