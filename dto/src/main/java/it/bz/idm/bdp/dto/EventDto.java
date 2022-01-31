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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.uuid.Generators;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import io.swagger.annotations.ApiModelProperty;
import it.bz.idm.bdp.dto.utils.Constraints;

/**
 * Data transfer object representing an event
 *
 * @author Patrick Bertolla
 */
@JsonInclude(value=Include.NON_EMPTY)
public class EventDto implements Serializable {

	private static final long serialVersionUID = 7928534360551629831L;

	@ApiModelProperty (notes = "The unique UUID associated to the event.")
	@JsonProperty(required = true)
	@JsonPropertyDescription("Unique UUID describing a single event.")
	protected String uuid;

	@JsonPropertyDescription("Who provided the event?")
	@JsonProperty(required = true)
	private String origin;

	@ApiModelProperty (notes = "The event category")
	@JsonProperty(required = true)
	@JsonPropertyDescription("describes a group in which the event falls e.g. a car accident can be part of the category traffic jam ")
	protected String category;

	@ApiModelProperty (notes = "The event series UUID")
	@JsonProperty(required = true)
	@JsonPropertyDescription("concatenates equal events, that change over time")
	protected String eventSeriesUuid;

	@ApiModelProperty (notes = "The human-readable name associated to the event.")
	@JsonProperty(required = true)
	@JsonPropertyDescription("Unique name describing a single event inside the origin/category/event-series-id hierarchy.")
	protected String name;

	@ApiModelProperty (notes = "The event description")
	@JsonProperty(required = false)
	@JsonPropertyDescription("Describes the event in few words")
	protected String description;

	@JsonPropertyDescription("Meta data, describing additional features of the event")
	private Map<String, Object> metaData = new HashMap<>();

	@ApiModelProperty (notes = "The event location")
	@JsonProperty(required = false)
	@JsonPropertyDescription("A short text summarizing the location")
	protected String locationDescription;

	@ApiModelProperty (notes = "The geografic representation of the location using the projection EPSG:4326")
	@JsonProperty(required = false)
	@JsonPropertyDescription("Well-known Text representation of this Geometry(OpenGIS Simple Features Specification)")
	private String wktGeometry;

	@ApiModelProperty (notes = "The start time of the event (included, we have a half-open interval)")
	@JsonProperty(required = false)
	@JsonPropertyDescription("Start time as unix timestamp in milliseconds")
	private Long eventStart;

	@ApiModelProperty (notes = "The end time of the event (excluded, we have a half-open interval)")
	@JsonProperty(required = false)
	@JsonPropertyDescription("End time as unix timestamp in milliseconds (excluded)")
	private Long eventEnd;

	@ApiModelProperty (notes = "The data collector name and version that inserts this event")
	@JsonProperty(required = true)
	@JsonPropertyDescription("The UUID of a data collector name and version")
	private String provenance;

	public String getUuid() {
		return this.uuid;
	}

	public void setUuid(String uuid) {
		if (! Constraints.isUUID(uuid))
			throw new IllegalArgumentException("EventDto: Invalid UUID format given '" + uuid + "'.");
		this.uuid = uuid;
	}

    public void setUuid(Map<String, Object> uuidMap) throws JsonProcessingException {
		setUuid(uuidMap, null);
    }

	public void setUuid(Map<String, Object> uuidMap, UUID uuidNameSpace) throws JsonProcessingException {
		this.uuid = generateUuidByMap(uuidMap, uuidNameSpace);
    }

	private String generateUuidByMap(Map<String, Object> uuidMap, UUID uuidNameSpace) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String uuidNameJson = mapper.writer().writeValueAsString(uuidMap);
        return Generators.nameBasedGenerator(uuidNameSpace).generate(uuidNameJson).toString();
    }

	public String getEventSeriesUuid() {
		return this.eventSeriesUuid;
	}

	public void setEventSeriesUuid(String uuid) {
		if (! Constraints.isUUID(uuid))
			throw new IllegalArgumentException("EventDto: Invalid UUID format for event-series given '" + uuid + "'.");
		this.eventSeriesUuid = uuid;
	}

    public void setEventSeriesUuid(Map<String, Object> uuidMap) throws JsonProcessingException {
		setEventSeriesUuid(uuidMap, null);
    }

	public void setEventSeriesUuid(Map<String, Object> uuidMap, UUID uuidNameSpace) throws JsonProcessingException {
		this.eventSeriesUuid = generateUuidByMap(uuidMap, uuidNameSpace);
    }

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getWktGeometry() {
		return wktGeometry;
	}

	public void setWktGeometry(String wktGeometry) {
		this.wktGeometry = wktGeometry;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getEventStart() {
		return eventStart;
	}

	public void setEventStart(Long eventStart) {
		this.eventStart = eventStart;
	}

	public Long getEventEnd() {
		return eventEnd;
	}

	public void setEventEnd(Long eventEnd) {
		this.eventEnd = eventEnd;
	}

	public String getLocationDescription() {
		return locationDescription;
	}

	public void setLocationDescription(String locationDescription) {
		this.locationDescription = locationDescription;
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

	public String getProvenance() {
		return provenance;
	}

	public void setProvenance(String provenanceUuid) {
		this.provenance = provenanceUuid;
	}

	/**
	 * Generate a half-open interval string representation of this events start and end.
	 * If the start or end is null, we consider it to be unbound, that is "infinite" in
	 * that direction.
	 *
	 * @param dto
	 * @return Half-open interval, like [1,3)
	 */
	public String getEventIntervalAsString() {
		// SimpleDateFormat is not thread-safe, so we better do not mark it as "static"
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
		StringBuilder sb = new StringBuilder();
		if (getEventStart() == null) {
			sb.append("(");
		} else {
			sb.append("[");
			sb.append(dateFormat.format(new Date(getEventStart())));
		}
		sb.append(",");
		if (getEventEnd() != null) {
			sb.append(dateFormat.format(new Date(getEventEnd() + 1)));
		}
		sb.append(")");
		return sb.toString();
	}

	@JsonIgnore
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof EventDto){
			EventDto dto =(EventDto) obj;
			if (this.getUuid().equals(dto.getUuid()))
				return true;
		}
		return false;
	}
	@JsonIgnore
	@Override
	public int hashCode() {
		return 1;
	}

	public static boolean isValid(EventDto dto, boolean checkProvenance) {
		if (dto == null)
			return false;
		if (checkProvenance && Constraints.isEmpty(dto.getProvenance()))
			return false;
		return Constraints.noneEmpty(
			dto.getOrigin(),
			dto.getEventSeriesUuid(),
			dto.getUuid(),
			dto.getName()
		);
	}
}
