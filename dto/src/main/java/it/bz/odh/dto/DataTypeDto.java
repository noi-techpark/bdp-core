/**
 * dto - Data Transport Objects for an object-relational mapping
 *
 * Copyright © 2018 IDM Südtirol - Alto Adige (info@idm-suedtirol.com)
 * Copyright © 2019 NOI Techpark - Südtirol / Alto Adige (info@opendatahub.bz.it)
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
package it.bz.odh.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

/**
 * Data transfer object representing a specific data type, which each measurement has a relation with
 *
 * @author Patrick Bertolla
 * @author Peter Moser
 */
public class DataTypeDto implements Serializable {

	private static final long serialVersionUID = -2577340085858167829L;
	public static final String NUMBER_AVAILABE = "number-available";
	public static final String AVAILABILITY = "availability";
	public static final String FUTURE_AVAILABILITY = "future-availability";
	public static final String PARKING_FORECAST = "parking-forecast";

	@JsonProperty(required = true)
	@JsonPropertyDescription("Unique name (ex., air-temperature)")
	private String name;

	@JsonProperty(required = false)
	@JsonPropertyDescription("Unit of the data type (ex., °C)")
	private String unit;

	@JsonProperty(required = false)
	@JsonPropertyDescription("Description of the data type (ex., Air temperature)")
	private String description;

	@JsonProperty(required = false)
	@JsonPropertyDescription("Metric of the data type (ex., mean or instantaneous)")
	private String rtype;

	@JsonProperty(required = false)
	@JsonPropertyDescription("Interval on how often a measurement with this data type gets collected (seconds)")
	private Integer period;

	@JsonProperty(required = false)
	@JsonPropertyDescription("detail information about given datatype")
	private Map<String,Object> metaData = new HashMap<String, Object>();

	public DataTypeDto() {
		super();
	}

	/**
	 * @param name unique identifier
	 * @param unit of the given data type, e.g., m²,°C, km/h
	 * @param description a well chosen description, please use English if you can
	 * @param rtype metric of a specific measurements, e.g., max, min, mean
	 */
	public DataTypeDto(String name, String unit, String description, String rtype) {
		super();
		this.name = name;
		this.unit = unit;
		this.description = description;
		this.rtype = rtype;
	}

	/**
	 * @param name unique identifier
	 * @param unit of the given data type, e.g., m²,°C, km/h
	 * @param description a well chosen description, please use English if you can
	 * @param rtype metric of a specific measurements, e.g., max, min, mean
	 * @param period interval in which this data type is normally provided
	 */
	public DataTypeDto(String name, String unit, String description, String rtype, Integer period) {
		this(name, unit, description, rtype);
		this.period = period;
	}
	public DataTypeDto(String name, String unit, String description, String rtype, Integer period,Map<String,Object> metaData) {
		this(name, unit, description, rtype,period);
		this.metaData = metaData;
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
	public Map<String, Object> getMetaData() {
		return metaData;
	}
	public void setMetaData(Map<String, Object> metaData) {
		this.metaData = metaData;
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

	@JsonIgnore
	public boolean isValid() {
		return getName() != null && !getName().isEmpty();
	}
}
