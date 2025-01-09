// Copyright © 2018 IDM Südtirol - Alto Adige (info@idm-suedtirol.com)
// Copyright © 2024 NOI Techpark - Südtirol / Alto Adige (info@opendatahub.com)
//
// SPDX-License-Identifier: GPL-3.0-only

package com.opendatahub.timeseries.bdp.dto.dto;

import java.io.Serializable;

public class ProvenanceDto implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 2627858984135614589L;

	protected String uuid;

	protected String lineage;

	protected String dataCollector;

	protected String dataCollectorVersion;

	protected String license;

	protected String source;

	protected String owner;

	public ProvenanceDto() {
	}

	public ProvenanceDto(String uuid, String dataCollector, String dataCollectorVersion, String lineage) {
		this.uuid = uuid;
		this.dataCollector = dataCollector;
		this.dataCollectorVersion = dataCollectorVersion;
		this.lineage = lineage;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getLineage() {
		return lineage;
	}

	public void setLineage(String lineage) {
		this.lineage = lineage;
	}

	public String getDataCollector() {
		return dataCollector;
	}

	public void setDataCollector(String dataCollector) {
		this.dataCollector = dataCollector;
	}

	public String getDataCollectorVersion() {
		return dataCollectorVersion;
	}

	public void setDataCollectorVersion(String dataCollectorVersion) {
		this.dataCollectorVersion = dataCollectorVersion;
	}

	public String getLicense() {
		return license;
	}

	public void setLicense(String license) {
		this.license = license;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public static boolean isValid(ProvenanceDto dto) {
		if (dto == null)
			return false;
		if (dto.lineage == null || dto.lineage.trim().isEmpty())
			return false;
		if (dto.dataCollector == null || dto.dataCollector.trim().isEmpty())
			return false;
		return true;
	}
}
