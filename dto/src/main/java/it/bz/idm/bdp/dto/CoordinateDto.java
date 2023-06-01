// Copyright © 2018 IDM Südtirol - Alto Adige (info@idm-suedtirol.com)
// Copyright © 2019 NOI Techpark - Südtirol / Alto Adige (info@opendatahub.bz.it)
//
// SPDX-License-Identifier: GPL-3.0-only

package it.bz.idm.bdp.dto;

import java.io.Serializable;


/**
 * Simple 2D representation of a point
 *
 * @author Patrick Bertolla
 */
public class CoordinateDto implements Serializable {

	private static final long serialVersionUID = -4490883796363760913L;
	private double lon;
	private double lat;

	public CoordinateDto() {
		super();
	}

	/**
	 * @param longitude expressed in m or degrees depending on used projection
	 * @param latitude expressed in m or degrees depending on used projection
	 */
	public CoordinateDto(double longitude, double latitude) {
		this.lon=longitude;
		this.lat=latitude;
	}
	public double getLon() {
		return lon;
	}
	public void setLon(double lon) {
		this.lon = lon;
	}
	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}

}
