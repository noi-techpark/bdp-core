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
