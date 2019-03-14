package it.bz.idm.bdp;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import it.bz.idm.bdp.util.LocationLookupUtil;

public class NominatimLookupTest {

	private LocationLookupUtil util = new LocationLookupUtil();

	@Test
	public void testLocationLookup() {
		String lookupLocation = util.lookupLocation(11.45, 49.45);
		assertNotNull(lookupLocation);
		assertFalse(lookupLocation.isEmpty());
	}
}