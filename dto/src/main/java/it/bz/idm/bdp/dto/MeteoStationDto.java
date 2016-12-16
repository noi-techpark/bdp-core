package it.bz.idm.bdp.dto;

import it.bz.idm.bdp.dto.StationDto;

public class MeteoStationDto extends StationDto {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4734857507387043315L;

	private String area;
	
	public MeteoStationDto() {
	}
	public MeteoStationDto(String stationcode, String name, double y, double x) {
		super(stationcode,name,y,x);
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

}
