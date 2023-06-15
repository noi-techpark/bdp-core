// Copyright © 2018 IDM Südtirol - Alto Adige (info@idm-suedtirol.com)
// Copyright © 2019 NOI Techpark - Südtirol / Alto Adige (info@opendatahub.com)
//
// SPDX-License-Identifier: GPL-3.0-only

package it.bz.idm.bdp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

/**
 * Minimal measurement data transfer object to be interesting at all.
 * Each measurement should inherit from this
 * @author Patrick Bertolla
 *
 */
public abstract class RecordDtoImpl implements RecordDto,Comparable<RecordDtoImpl>{

	private static final long serialVersionUID = -1124149647267291299L;

	@JsonProperty(required = true)
	@JsonPropertyDescription("Time stamp in milliseconds")
	protected Long timestamp;

	public Long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public abstract Object getValue();

	@Override
	public boolean validate() {
		return this.timestamp != null && this.getValue() != null;
	}

	@Override
	public int compareTo(RecordDtoImpl o) {
		return this.timestamp != null && o.timestamp != null
			? this.timestamp.compareTo(o.timestamp) : -1;
	}


}
