/**
 * dto - Data Transport Objects for the Big Data Platform
 * Copyright © 2018 IDM Südtirol - Alto Adige (info@idm-suedtirol.com)
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
package it.bz.idm.bdp.dto;

import java.io.Serializable;

public class DataTypeDto implements Serializable{

	private static final long serialVersionUID = -2577340085858167829L;
	public static final String NUMBER_AVAILABE = "number-available";
	public static final String AVAILABILITY = "availability";
	public static final String FUTURE_AVAILABILITY = "future-availability";
	public static final String PARKING_FORECAST = "parking-forecast";

	private String name;
	private String unit;
	private String description;
	private String rtype;
	private Integer period;

	public DataTypeDto() {
	}
	public DataTypeDto(String name, String unit, String description, String rtype) {
		super();
		this.name = name;
		this.unit = unit;
		this.description = description;
		this.rtype = rtype;
	}

	public DataTypeDto(String name, String unit, String description, String rtype, Integer period) {
		super();
		this.name = name;
		this.unit = unit;
		this.description = description;
		this.rtype = rtype;
		this.period = period;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getRtype() {
		return rtype;
	}
	public void setRtype(String rtype) {
		this.rtype = rtype;
	}
	public Integer getPeriod() {
		return period;
	}
	public void setPeriod(Integer period) {
		this.period = period;
	}
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DataTypeDto){
			DataTypeDto dto =(DataTypeDto) obj;
			if (this.getName().equals(dto.getName()))
				return true;
		}
		return false;
	}
	@Override
	public int hashCode() {
		return 1;
	}

	@Override
	public String toString() {
		return "DataTypeDto [name=" + name + ", unit=" + unit + ", description=" + description + ", rtype=" + rtype + ", period=" + period + "]";
	}
}
