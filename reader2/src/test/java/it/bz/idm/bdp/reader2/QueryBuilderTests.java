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
				.init("a, x", "A", "B")
				.expandSelect()
				.getSql();

		assertEquals("A.a as a, B.x as x", res);

		res = QueryBuilder
				.init("showA", "C")
				.expandSelect()
				.getSql();

		assertEquals("A.a as a, A.b as b", res);

		res = QueryBuilder
				.init("showB", "C")
				.expandSelect()
				.getSql();

		assertEquals("A.a as a, A.b as b, B.x as x", res);

		res = QueryBuilder
				.init("showB", "C")
				.expandSelectPrefix("C")
				.getSql();

		assertEquals("A.a as a, A.b as b, B.x as x", res);
	}

	@Before
	public void setUpBefore() throws Exception {
		SelectExpansion se = new SelectExpansion();
		se.addExpansion("A", "a", "A.a");
		se.addExpansion("A", "b", "A.b");

		se.addExpansion("B", "x", "B.x");
		se.addSubExpansion("B", "IwantA", "A");

		se.addExpansion("C", "i", "C.i");
		se.addSubExpansion("C", "showA", "A");
		se.addSubExpansion("C", "showB", "B");


		QueryBuilder.setup(se);
	}

}
