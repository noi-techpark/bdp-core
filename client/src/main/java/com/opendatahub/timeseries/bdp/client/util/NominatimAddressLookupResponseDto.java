// Copyright © 2018 IDM Südtirol - Alto Adige (info@idm-suedtirol.com)
// Copyright © 2019 NOI Techpark - Südtirol / Alto Adige (info@opendatahub.com)
//
// SPDX-License-Identifier: GPL-3.0-only

package com.opendatahub.timeseries.bdp.client.util;

import java.io.Serializable;

public class NominatimAddressLookupResponseDto implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = 5136628956589831385L;
	private String lat;
	private String lon;
	public String getLat() {
		return lat;
	}
	public void setLat(String lat) {
		this.lat = lat;
	}
	public String getLon() {
		return lon;
	}
	public void setLon(String lon) {
		this.lon = lon;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}



}
