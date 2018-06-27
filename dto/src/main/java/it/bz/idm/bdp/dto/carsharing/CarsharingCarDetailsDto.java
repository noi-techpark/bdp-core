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


public class CarsharingCarDetailsDto extends StationDto{

	private static final long serialVersionUID = -899771900803503862L;
	private String brand;
	private String parentStation;
	private String licensePlate;

	public CarsharingCarDetailsDto() {
	}

	public CarsharingCarDetailsDto(String stationcode, String name, Double y,
			Double x, String brand, String parentStation, String licensePlate) {
		super(stationcode, name, y, x);
		this.brand = brand;
		this.parentStation = parentStation;
		this.licensePlate = licensePlate;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getParentStation() {
		return parentStation;
	}

	public void setParentStation(String station) {
		this.parentStation = station;
	}

	public String getLicensePlate() {
		return licensePlate;
	}

	public void setLicensePlate(String licensePlate) {
		this.licensePlate = licensePlate;
	}
	

	
}
