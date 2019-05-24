package it.bz.idm.bdp.reader2;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import it.bz.idm.bdp.reader2.utils.QueryBuilder;

public class QueryBuilderTests {

	@Test
	public void testExpandSelect() {
		String res = QueryBuilder
				.init("sname, pname", "station", "parent")
				.expandSelect()
				.getSql();

		assertEquals(res, "p.name as pname, s.name as sname");

		res = QueryBuilder
				.init("sparent", "station", "parent")
				.expandSelect()
				.getSql();

		assertEquals(res, "s.pointprojection as pcoordinate, p.stationcode as pcode, p.name as pname, p.stationtype as ptype, p.origin as porigin");
	}

	@Before
	public void setUpBefore() throws Exception {
		// We load data from AppStartupDataLoader.java
	}

}
