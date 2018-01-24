package it.bz.idm.bdp.dto.bluetooth;

import it.bz.idm.bdp.dto.StationDto;

public class StreetStationDto extends StationDto {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7409560798539761207L;
 
	private Double length;
	
	private String description;
	
	private Integer speed_default;
	
	private Short old_idstr;

	public StreetStationDto() {
	}
	public StreetStationDto(String stationcode, String name, Double y, Double x) {
		super(stationcode,name,y,x);
	}

	public Double getLength() {
		return length;
	}

	public void setLength(Double length) {
		this.length = length;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getSpeed_default() {
		return speed_default;
	}

	public void setSpeed_default(Integer speed_default) {
		this.speed_default = speed_default;
	}

	public Short getOld_idstr() {
		return old_idstr;
	}

	public void setOld_idstr(Short old_idstr) {
		this.old_idstr = old_idstr;
	}
}
