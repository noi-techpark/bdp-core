// Copyright © 2018 IDM Südtirol - Alto Adige (info@idm-suedtirol.com)
// Copyright © 2019 NOI Techpark - Südtirol / Alto Adige (info@opendatahub.com)
//
// SPDX-License-Identifier: GPL-3.0-only

package com.opendatahub.timeseries.bdp;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.opendatahub.timeseries.bdp.util.NominatimException;
import com.opendatahub.timeseries.bdp.util.NominatimLocationLookupUtil;

public class NominatimLookupITTest {

	private NominatimLocationLookupUtil util = new NominatimLocationLookupUtil();

	@Test
	public void testLocationLookup() throws NominatimException {
		String lookupLocation = util.lookupLocation(11.45, 49.45);
		assertNotNull(lookupLocation);
		assertFalse(lookupLocation.isEmpty());
	}
	@Test
	public void testCoordinateLookup() throws NominatimException {
		Double[] coordinates = util.lookupCoordinates("via Fago 26 Bolzano");
		assertNotNull(coordinates);
	}
}