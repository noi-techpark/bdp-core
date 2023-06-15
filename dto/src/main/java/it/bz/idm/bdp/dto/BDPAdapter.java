// Copyright © 2018 IDM Südtirol - Alto Adige (info@idm-suedtirol.com)
// Copyright © 2019 NOI Techpark - Südtirol / Alto Adige (info@opendatahub.com)
//
// SPDX-License-Identifier: GPL-3.0-only

package it.bz.idm.bdp.dto;

import java.util.List;

/**
 * converts acquired data to a format which the big data platform understands;
 * currently it only exists for stations and types
 * @author Patrick Bertolla
 *
 */
public interface BDPAdapter {

	public abstract StationDto convert2StationDto(Object station);
	public abstract List<DataTypeDto> convert2DatatypeDtos(List<? extends Object> types);

}
