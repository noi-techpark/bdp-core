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