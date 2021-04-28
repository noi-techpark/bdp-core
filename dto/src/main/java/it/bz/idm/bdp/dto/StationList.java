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

import java.util.ArrayList;
import java.util.Collection;

/**
 * StationList as a DTO got added due to the following reason:<br>
 * "There was an issue with the serialization of polymorphic objects when using generics, which
 * means the specific implementation of the StationDto did not get serialized correctly.
 * For example a CarsharingStationDto got serialized as StationDto instead."
 */
public class StationList extends ArrayList<StationDto>{

	public StationList(Collection<? extends StationDto> stations) {
		super(stations);
	}
	public StationList() {
	}

	private static final long serialVersionUID = 2408060694809964354L;
}
