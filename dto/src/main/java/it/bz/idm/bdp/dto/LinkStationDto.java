package it.bz.idm.bdp.dto;

import java.util.List;

import it.bz.idm.bdp.dto.CoordinateDto;
import it.bz.idm.bdp.dto.StationDto;



public class LinkStationDto extends StationDto {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2452746626453079659L;
	private String origin;
	
	private String destination;
	
	private Double length;
	
	private String street_ids_ref;
	
	private List<CoordinateDto> coordinates;
	
	private Integer elapsed_time_default;

	public LinkStationDto() {
	}

	public LinkStationDto(String stationcode, String name, Double y, Double x) {
		super(stationcode,name,y,x);
	}
	
	public Integer getElapsed_time_default() {
		return elapsed_time_default;
	}

	public void setElapsed_time_default(Integer elapsed_time_default) {
		this.elapsed_time_default = elapsed_time_default;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public Double getLength() {
		return length;
	}

	public void setLength(Double length) {
		this.length = length;
	}

	public String getStreet_ids_ref() {
		return street_ids_ref;
	}

	public void setStreet_ids_ref(String street_ids_ref) {
		this.street_ids_ref = street_ids_ref;
	}

	public List<CoordinateDto> getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(List<CoordinateDto> coordinates) {
		this.coordinates = coordinates;
	}

}
