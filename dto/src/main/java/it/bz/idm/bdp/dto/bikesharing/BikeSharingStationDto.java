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
 * along with this program (see LICENSES/GPL-3.0.txt). If not, see
 * <http://www.gnu.org/licenses/>.
 *
 * SPDX-License-Identifier: GPL-3.0
 */
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
