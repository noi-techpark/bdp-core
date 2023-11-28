// Copyright © 2018 IDM Südtirol - Alto Adige (info@idm-suedtirol.com)
// Copyright © 2019 NOI Techpark - Südtirol / Alto Adige (info@opendatahub.com)
//
// SPDX-License-Identifier: GPL-3.0-only

package com.opendatahub.timeseries.bdp.dto.dto;

import java.io.Serializable;

/**
 * <p>
 * Defines a relationship between two stations.
 * The identifiers in here are taken from {@link StationDto#id}
 * </p>
 *
 * @author Patrick Bertolla
 */
public class ChildDto implements Serializable {

	private static final long serialVersionUID = -7990410431929026069L;
	private String identifier;
	private String station;

	public String getIdentifier() {
		return identifier;
	}
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	public String getStation() {
		return station;
	}
	public void setStation(String station) {
		this.station = station;
	}
}
