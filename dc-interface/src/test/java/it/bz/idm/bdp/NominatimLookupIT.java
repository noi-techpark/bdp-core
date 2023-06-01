// Copyright © 2018 IDM Südtirol - Alto Adige (info@idm-suedtirol.com)
// Copyright © 2019 NOI Techpark - Südtirol / Alto Adige (info@opendatahub.bz.it)
//
// SPDX-License-Identifier: GPL-3.0-only

package it.bz.idm.bdp;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import it.bz.idm.bdp.util.NominatimException;
import it.bz.idm.bdp.util.NominatimLocationLookupUtil;

public class NominatimLookupIT {

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