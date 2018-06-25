/**
 * dto - Data Transport Objects for an object-relational mapping
 * Copyright © 2018 OpenDataHub (info@opendatahub.bz.it)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program (see LICENSE/GPLv3). If not, see
 * <http://www.gnu.org/licenses/>.
 */
package it.bz.idm.bdp.dto.bluetooth;

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
