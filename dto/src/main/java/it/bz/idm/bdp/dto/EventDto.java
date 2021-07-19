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

import org.geojson.GeoJsonObject;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import io.swagger.annotations.ApiModelProperty;

/**
 * Data transfer object representing an event
 *
 * @author Patrick Bertolla
 */
@JsonInclude(value=Include.NON_EMPTY)
public class EventDto implements Serializable {

	private static final long serialVersionUID = 7928534360551629831L;

	@ApiModelProperty (notes = "The unique ID associated to the event.")
	@JsonProperty(required = true)
	@JsonPropertyDescription("Unique event code describing a single event (ex. fog1458)")
	protected String id;

	@ApiModelProperty (notes = "The event category")
	@JsonProperty(required = true)
	@JsonPropertyDescription("")
	protected String category;
	
	@ApiModelProperty (notes = "The events subcategory")
	@JsonProperty(required = true)
	@JsonPropertyDescription("")
	protected String subCategory;

	@JsonPropertyDescription("Who provided the event?")
	private String origin;

	@JsonPropertyDescription("Meta data, that describes this station (can be any valid JSON string)")
	private Map<String, Object> metaData = new HashMap<>();
	
	private GeoJsonObject geoJson;

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getSubCategory() {
		return subCategory;
	}

	public void setSubCategory(String subCategory) {
		this.subCategory = subCategory;
	}

	public GeoJsonObject getGeoJson() {
		return geoJson;
	}

	public void setGeoJson(GeoJsonObject geoJson) {
		this.geoJson = geoJson;
	}

	public EventDto() {
		super();
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
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
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof EventDto){
			EventDto dto =(EventDto) obj;
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
}
