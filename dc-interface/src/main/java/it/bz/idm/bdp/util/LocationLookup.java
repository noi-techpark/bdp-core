// Copyright © 2018 IDM Südtirol - Alto Adige (info@idm-suedtirol.com)
// Copyright © 2019 NOI Techpark - Südtirol / Alto Adige (info@opendatahub.com)
//
// SPDX-License-Identifier: GPL-3.0-only

package it.bz.idm.bdp.util;

public interface LocationLookup {

	public String lookupLocation(Double longitude, Double latitude) throws NominatimException;
	public Double[] lookupCoordinates(String address) throws NominatimException;
}
