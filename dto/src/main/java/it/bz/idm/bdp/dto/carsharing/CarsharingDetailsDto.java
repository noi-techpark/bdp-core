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
package it.bz.idm.bdp.dto.carsharing;

import it.bz.idm.bdp.dto.StationDto;


public class CarsharingDetailsDto extends StationDto{

	private static final long serialVersionUID = -899771900803503862L;
	private String company;
	private Integer availableVehicles;
	private boolean bookahead;
	private boolean fixedParking;
	private boolean spontaneously;
	public CarsharingDetailsDto() {
	}
	public CarsharingDetailsDto(String stationcode, String name, Double y, Double x, String company, Integer availableVehicles, boolean bookahead, boolean fixedParking, boolean spontaneously) {
		super(stationcode, name, y, x);
		this.company = company;
		this.availableVehicles = availableVehicles;
		this.bookahead = bookahead;
		this.fixedParking = fixedParking;
		this.spontaneously = spontaneously;
	}
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public Integer getAvailableVehicles() {
		return availableVehicles;
	}
	public void setAvailableVehicles(Integer availableVehicles) {
		this.availableVehicles = availableVehicles;
	}
	public boolean isBookahead() {
		return bookahead;
	}
	public void setBookahead(boolean bookahead) {
		this.bookahead = bookahead;
	}
	public boolean isFixedParking() {
		return fixedParking;
	}
	public void setFixedParking(boolean fixedParking) {
		this.fixedParking = fixedParking;
	}
	public boolean isSpontaneously() {
		return spontaneously;
	}
	public void setSpontaneously(boolean spontaneously) {
		this.spontaneously = spontaneously;
	}
	
}
