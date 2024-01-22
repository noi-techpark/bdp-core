// Copyright © 2018 IDM Südtirol - Alto Adige (info@idm-suedtirol.com)
// Copyright © 2019 NOI Techpark - Südtirol / Alto Adige (info@opendatahub.com)
//
// SPDX-License-Identifier: GPL-3.0-only

package com.opendatahub.timeseries.bdp.dto.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

/**
 * Most simple implementation of {@link RecordDtoImpl} which additionally to the
 * timestamp has a value which can be anything.
 *
 * @author Patrick Bertolla
 *
 */
@JsonInclude(value=Include.NON_EMPTY)
public class SimpleRecordDto extends RecordDtoImpl {

	private static final long serialVersionUID = 5703758724961079739L;

	@JsonProperty(required = true)
	@JsonPropertyDescription("The measurement value, either string or number")
	private Object value;

	@JsonPropertyDescription("Interval between one measurement and the consecutive one (seconds)")
	private Integer period;

	private Long created_on;

	public SimpleRecordDto() {
		super();
	}
	@Deprecated
	public SimpleRecordDto(Long timestamp, Object value) {
		this();
		this.timestamp = timestamp;
		this.value = value;
	}
	public SimpleRecordDto(Long timestamp, Double value) {
		this(timestamp, (Object) value);
	}
	public SimpleRecordDto(Long timestamp, Object value, Integer period) {
		this(timestamp, value);
		setPeriod(period);
	}
	public SimpleRecordDto(Long timestamp, Object value, Long created_on) {
		this(timestamp, value);
		setCreated_on(created_on);
	}
	public SimpleRecordDto(Long timestamp, Object value, Integer period, Long created_on) {
		this(timestamp, value, period);
		setCreated_on(created_on);
	}
	public Long getCreated_on() {
		return created_on;
	}
	public void setCreated_on(Long created_on) {
		this.created_on = created_on;
	}
	@Override
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
	public Integer getPeriod() {
		return period;
	}
	public void setPeriod(Integer period) {
		this.period = period;
	}
	@JsonIgnore
	public boolean isValid() {
		return value != null && timestamp != null && period != null;
	}

	@Override
	public String toString() {
		return "SimpleRecordDto [timestamp=" + timestamp + ", value=" + value + ", period=" + period + "]";
	}

}

