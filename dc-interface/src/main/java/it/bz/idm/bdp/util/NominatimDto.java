// Copyright © 2018 IDM Südtirol - Alto Adige (info@idm-suedtirol.com)
// Copyright © 2019 NOI Techpark - Südtirol / Alto Adige (info@opendatahub.com)
//
// SPDX-License-Identifier: GPL-3.0-only

package it.bz.idm.bdp.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Minimal implementation of the DTO returned by Nominatim (a TIM service, which provides
 * address data giving coordinates.)
 *
 * @author Patrick Bertolla
 */
public class NominatimDto implements Serializable {

	private static final long serialVersionUID = -3369918936655035312L;

	/**
	 * containing multiple fields describing the coordinate location
	 */
	private Map<String,String> address = new HashMap<>();

	public Map<String, String> getAddress() {
		return address;
	}

	public void setAddress(Map<String, String> address) {
		this.address = address;
	}
}
