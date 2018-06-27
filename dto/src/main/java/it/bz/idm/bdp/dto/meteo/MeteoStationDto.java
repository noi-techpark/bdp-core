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
package it.bz.idm.bdp.dto.meteo;

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
