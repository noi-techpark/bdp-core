/**
 * dto - Data Transport Objects for an object-relational mapping
 * Copyright © 2018 IDM Südtirol - Alto Adige (info@idm-suedtirol.com)
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
package it.bz.idm.bdp.dto.bikesharing;

import java.util.HashMap;
import java.util.Map;

import it.bz.idm.bdp.dto.StationDto;


public class BikesharingDetailsDto extends StationDto{

	private static final long serialVersionUID = -899771900803503862L;
	private Map<String , Integer> bikes = new HashMap<String, Integer>();

	public Map<String, Integer> getBikes() {
		return bikes;
	}

	public void setBikes(Map<String, Integer> bikes) {
		this.bikes = bikes;
	}
	public BikesharingDetailsDto() {
	}
	public BikesharingDetailsDto(String stationcode, String name, Double y, Double x, Map<String , Integer> bikes) {
		super(stationcode, name, y, x);
		this.bikes = bikes;
	}

}
