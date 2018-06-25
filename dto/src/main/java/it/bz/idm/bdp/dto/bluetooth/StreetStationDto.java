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
