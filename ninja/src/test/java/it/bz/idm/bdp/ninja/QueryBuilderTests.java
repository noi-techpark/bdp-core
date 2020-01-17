package it.bz.idm.bdp.ninja;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import it.bz.idm.bdp.ninja.utils.querybuilder.QueryBuilder;
import it.bz.idm.bdp.ninja.utils.querybuilder.SelectDefinition;
import it.bz.idm.bdp.ninja.utils.querybuilder.SelectExpansion;
import it.bz.idm.bdp.ninja.utils.querybuilder.TargetEntry;

public class QueryBuilderTests {

	@Test
	public void testExpandSelect() {
		String res = QueryBuilder
				.init("a, x", null, "A", "B")
				.expandSelect()
				.getSql();

		assertEquals("A.a as a, B.x as x", res.trim());

		res = QueryBuilder
				.init("y", null, "B", "C")
				.expandSelect()
				.getSql();

		assertEquals("C.d as d", res.trim());

		res = QueryBuilder
				.init("d", null, "C")
				.expandSelect()
				.getSql();

		assertEquals("C.d as d", res.trim());

		res = QueryBuilder
				.init("x, y", null, "A", "B")
				.expandSelect()
				.getSql();

		assertEquals("B.x as x", res.trim());

		res = QueryBuilder
				.init("a,b,c", null, "A", "B")
				.expandSelect("B")
				.getSql();

		assertEquals("B.x as x", res.trim());
	}

	@Before
	public void setUpBefore() throws Exception {
		SelectExpansion se = new SelectExpansion();
		SelectDefinition defC = SelectDefinition.init("C")
				.addTargetEntry(new TargetEntry("d", "C.d"));
		SelectDefinition defB = SelectDefinition.init("B")
				.addTargetEntry(new TargetEntry("x", "B.x"))
				.addTargetEntry(new TargetEntry("y", defC));
		SelectDefinition defA = SelectDefinition.init("A")
				.addTargetEntry(new TargetEntry("a", "A.a"))
				.addTargetEntry(new TargetEntry("b", defB))
				.addTargetEntry(new TargetEntry("c", "A.c"));
		se.add(defA);
		se.add(defB);
		se.add(defC);
		QueryBuilder.setup(se);
	}

}
