// Copyright © 2018 IDM Südtirol - Alto Adige (info@idm-suedtirol.com)
// Copyright © 2019 NOI Techpark - Südtirol / Alto Adige (info@opendatahub.com)
//
// SPDX-License-Identifier: GPL-3.0-only

package com.opendatahub.timeseries.bdp.dto.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@Deprecated
@JsonInclude(value=Include.NON_EMPTY)
public class SlimRecordDto {
	private Long timestamp;
	private Long created_on;
	private Object value;
	private Integer period;
	
	public SlimRecordDto() {
	}
	
	public SlimRecordDto(Long timestamp, Object value, Integer period, Long created_on) {
		super();
		this.timestamp = timestamp;
		this.value = value;
		this.period = period;
		this.created_on = created_on;
	}
	public Long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}
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
	public Long getCreated_on() {
		return created_on;
	}

	public void setCreated_on(Long created_on) {
		this.created_on = created_on;
	}
	
}
