package it.bz.idm.bdp.ninja;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import it.bz.idm.bdp.ninja.utils.querybuilder.QueryBuilder;
import it.bz.idm.bdp.ninja.utils.querybuilder.Schema;
import it.bz.idm.bdp.ninja.utils.querybuilder.SelectExpansion;
import it.bz.idm.bdp.ninja.utils.querybuilder.TargetDef;
import it.bz.idm.bdp.ninja.utils.querybuilder.TargetDefList;

public class QueryBuilderTests {

	@Test
	public void testExpandSelect() {
		String res = QueryBuilder
				.init("a, x", null, true, "A", "B")
				.expandSelect()
				.getSql();

		assertEquals("A.a as a, B.x as x", res.trim());

		res = QueryBuilder
				.init("y", null, true, "B", "C")
				.expandSelect()
				.getSql();

		assertEquals("C.d as d", res.trim());

		res = QueryBuilder
				.init("d", null, true, "C")
				.expandSelect()
				.getSql();

		assertEquals("C.d as d", res.trim());

		res = QueryBuilder
				.init("x, y", null, true, "A", "B")
				.expandSelect()
				.getSql();

		assertEquals("B.x as x", res.trim());

		res = QueryBuilder
				.init("a,b,c", null, true, "A", "B")
				.expandSelect("B")
				.getSql();

		assertEquals("B.x as x", res.trim());
	}

	@Before
	public void setUpBefore() throws Exception {
		SelectExpansion se = new SelectExpansion();
		Schema schema = new Schema();
		TargetDefList defC = TargetDefList.init("C")
				.add(new TargetDef("d", "C.d"));
		TargetDefList defB = TargetDefList.init("B")
				.add(new TargetDef("x", "B.x"))
				.add(new TargetDef("y", defC));
		TargetDefList defA = TargetDefList.init("A")
				.add(new TargetDef("a", "A.a"))
				.add(new TargetDef("b", defB))
				.add(new TargetDef("c", "A.c"));
		schema.add(defA);
		schema.add(defB);
		schema.add(defC);
		se.setSchema(schema);
		QueryBuilder.setup(se);
	}

}
