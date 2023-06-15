// Copyright © 2018 IDM Südtirol - Alto Adige (info@idm-suedtirol.com)
// Copyright © 2019 NOI Techpark - Südtirol / Alto Adige (info@opendatahub.com)
//
// SPDX-License-Identifier: GPL-3.0-only

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

	@Override
	public StationList subList(int fromIndex, int toIndex) {
		return new StationList(super.subList(fromIndex, toIndex));
	}

	private static final long serialVersionUID = 2408060694809964354L;
}
