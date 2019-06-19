package it.bz.idm.bdp.reader2;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import it.bz.idm.bdp.reader2.utils.querybuilder.QueryBuilder;
import it.bz.idm.bdp.reader2.utils.querybuilder.SelectExpansion;

public class QueryBuilderTests {

	@Test
	public void testExpandSelect() {
		String res = QueryBuilder
				.init("a, x", null, "A", "B")
				.expandSelect()
				.getSql();

		assertEquals("A.a as a, B.x as x", res);

		res = QueryBuilder
				.init("y", null, "B", "C")
				.expandSelect()
				.getSql();

		assertEquals("C.d as d", res);

		res = QueryBuilder
				.init("d", null, "C")
				.expandSelect()
				.getSql();

		assertEquals("C.d as d", res);

		res = QueryBuilder
				.init("x, y", null, "A", "B")
				.expandSelect()
				.getSql();

		assertEquals("B.x as x", res);

		res = QueryBuilder
				.init("a,b,c", null, "A", "B")
				.expandSelect("B")
				.getSql();

		assertEquals("B.x as x", res);
	}

	@Before
	public void setUpBefore() throws Exception {
		SelectExpansion se = new SelectExpansion();
		se.addColumn("C", "d", "C.d");
		se.addColumn("B", "x", "B.x");
		se.addSubDef("B", "y", "C");
		se.addColumn("A", "a", "A.a");
		se.addSubDef("A", "b", "B");
		se.addColumn("A", "c", "A.c");
		QueryBuilder.setup(se);
	}

}
