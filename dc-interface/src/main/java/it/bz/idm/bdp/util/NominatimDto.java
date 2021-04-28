/**
 * dc-interface - Data Collector Interface for the Big Data Platform
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
