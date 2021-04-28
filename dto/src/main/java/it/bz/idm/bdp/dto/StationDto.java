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
package it.bz.idm.bdp.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import io.swagger.annotations.ApiModelProperty;

/**
 * Data transfer object representing a station, which is a point (probably, on a map)
 * that measures data.
 *
 * @author Patrick Bertolla
 */
@JsonInclude(value=Include.NON_EMPTY)
public class StationDto implements Serializable {

	private static final long serialVersionUID = 7928534360551629831L;

	@ApiModelProperty (notes = "The unique ID associated to the station.")
	@JsonProperty(required = true)
	@JsonPropertyDescription("Unique station code (ex., bz:noi01)")
	protected String id;

	@ApiModelProperty (notes = "The type of station")
	@JsonProperty(required = true)
	@JsonPropertyDescription("Station type or category (ex., Environment)")
	private String stationType;

	@ApiModelProperty (notes = "The name of the station")
	@JsonProperty(required = true)
	@JsonPropertyDescription("Natural station name (ex., Primary NOI Station)")
	protected String name;

	@ApiModelProperty (notes = "The latitude where this station is located.")
	protected Double latitude;

	@ApiModelProperty (notes = "The longitude where this station is located.")
	protected Double longitude;

	protected Double elevation;
	protected String coordinateReferenceSystem;

	@JsonPropertyDescription("Who provided this station?")
	private String origin;

	@ApiModelProperty (notes = "The town or city wehre the station is located.")
	private String municipality;

	@JsonPropertyDescription("Station code to which this station belongs (ex., bz:noi)")
	private String parentStation;

	@JsonPropertyDescription("Meta data, that describes this station (can be any valid JSON string)")
	private Map<String, Object> metaData = new HashMap<>();

	public StationDto() {
		super();
	}

	/**
	 * @param id unique identifier
	 * @param name well chosen name, english is the preferred language
	 * @param latitude of the point in space
	 * @param longitude of the point in space
	 */
	public StationDto(String id, String name, Double latitude, Double longitude ) {
		this.id = id;
		this.name = name;
		this.longitude = longitude ;
		this.latitude = latitude;
	}


	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public String getParentStation() {
		return parentStation;
	}

	public void setParentStation(String parentId) {
		this.parentStation = parentId;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Double getLatitude() {
		return latitude;
	}
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	public Double getLongitude() {
		return longitude;
	}
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public Double getElevation() {
		return elevation;
	}

	public void setElevation(Double elevation) {
		this.elevation = elevation;
	}

	public String getCoordinateReferenceSystem() {
		return coordinateReferenceSystem;
	}

	public void setCoordinateReferenceSystem(String coordinateReferenceSystem) {
		this.coordinateReferenceSystem = coordinateReferenceSystem;
	}

	public String getOrigin() {
		return origin;
	}
	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public Map<String, Object> getMetaData() {
		return metaData;
	}

	public void setMetaData(Map<String, Object> metaData) {
		if (metaData == null) {
			this.metaData = null;
			return;
		}


		for (Entry<String, Object> entry : metaData.entrySet()) {
			if (entry.getValue() != null && entry.getKey() != null) {
				this.metaData.put(entry.getKey(), entry.getValue());
			}
		}
	}

	@JsonIgnore
	public boolean isValid() {
		return id != null && !id.isEmpty()
						  && stationType != null && !stationType.isEmpty()
						  && name != null && !name.isEmpty();
	}

	@JsonIgnore
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof StationDto){
			StationDto dto =(StationDto) obj;
			if (this.getId().equals(dto.getId()))
				return true;
		}
		return false;
	}
	@JsonIgnore
	@Override
	public int hashCode() {
		return 1;
	}

	@Override
	public String toString() {
		return "StationDto [id=" + id + ", name=" + name + ", latitude=" + latitude + ", longitude=" + longitude
				+ ", crs=" + coordinateReferenceSystem + ", origin=" + origin + "]";
	}

	public String getStationType() {
		return stationType;
	}

	public void setStationType(String stationType) {
		this.stationType = stationType;
	}

}
