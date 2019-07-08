package it.bz.idm.bdp.reader2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import it.bz.idm.bdp.reader2.utils.querybuilder.SelectExpansion;
import it.bz.idm.bdp.reader2.utils.querybuilder.SelectExpansion.ErrorCode;
import it.bz.idm.bdp.reader2.utils.simpleexception.SimpleException;

public class SelectExpansionTests {

	SelectExpansion seNested;
	SelectExpansion seFlat;
	SelectExpansion seNestedBig;
	SelectExpansion seMinimal;

	@Before
	public void setup() {
		seNested = new SelectExpansion();
		seNested.addColumn("C", "h", "C.h");
		seNested.addColumn("A", "a", "A.a");
		seNested.addColumn("A", "b", "A.b");
		seNested.addSubDef("A", "c", "C");
		seNested.addColumn("B", "x", "B.x");
		seNested.addSubDef("B", "y", "A");

		seFlat = new SelectExpansion();
		seFlat.addColumn("A", "a", "A.a");
		seFlat.addColumn("A", "b", "A.b");
		seFlat.addColumn("B", "x", "B.x");
		seFlat.addColumn("C", "i", "C.i");

		seNestedBig = new SelectExpansion();
		seNestedBig.addColumn("A", "a", "A.a");
		seNestedBig.addColumn("A", "b", "A.b");
		seNestedBig.addColumn("B", "x", "B.x");
		seNestedBig.addSubDef("B", "y", "A");
		seNestedBig.addColumn("C", "i", "C.i");
		seNestedBig.addSubDef("C", "j", "B");
		seNestedBig.addSubDef("E", "j", "B");
		seNestedBig.addSubDef("A", "j", "A");

		seMinimal = new SelectExpansion();
		seMinimal.addColumn("A", "a", "A.a");
	}

	@Test
	public void testFlatStructure() {
		seFlat.expand("a", "A", "C");

		// More select definitions than aliases
		List<String> res = seFlat.getUsedAliases();
		assertEquals("a", res.get(0));
		assertTrue(res.size() == 1);

		// Alias that cannot be found
		try {
			seFlat.expand("a, i, x", "A", "C");
			fail("Exception expected");
		} catch (SimpleException e) {
			assertEquals(ErrorCode.KEY_NOT_INSIDE_DEFLIST, e.getId());
			assertEquals("x", e.getData().get("alias"));
		}

	}

	@Test
	public void testNestedStructure() {
		// Select definitions inside a sub-expansion
		seNestedBig.expand("a", "A", "C");
		List<String> res = seNestedBig.getUsedAliases();
		assertEquals("a", res.get(0));
		assertTrue(res.size() == 1);

	}

	@Test
	public void testgetDefNames() throws Exception {
		seNested.expand("a", "A");

		List<String> res = seNested.getUsedDefNames();
		assertEquals("A", res.get(0));
		assertTrue(res.size() == 1);
	}

	@Test
	public void testExpansion() throws Exception {
		seNested.expand("a", "A");
		assertEquals("a", seNested.getUsedAliases().get(0));
		assertEquals("A", seNested.getUsedDefNames().get(0));
		assertEquals("A.a as a", seNested.getExpansion().get("A"));
		assertTrue(seNested.getUsedAliases().size() == 1);
		assertTrue(seNested.getUsedDefNames().size() == 1);
		assertTrue(seNested.getExpansion().size() == 1);

		try {
			seNested.expand("a", "B");
			fail("Exception expected");
		} catch (Exception e) {
			// nothing to do
		}

		seNested.expand("a, b", "A", "B");
		assertEquals("a", seNested.getUsedAliases().get(0));
		assertEquals("b", seNested.getUsedAliases().get(1));
		assertEquals("A", seNested.getUsedDefNames().get(0));
		assertEquals("A.a as a, A.b as b", seNested.getExpansion().get("A"));
		assertTrue(seNested.getUsedAliases().size() == 2);
		assertTrue(seNested.getUsedDefNames().size() == 1);
		assertTrue(seNested.getExpansion().size() == 1);

		seNested.expand("x, y", "A", "B", "C");
		assertEquals("a", seNested.getUsedAliases().get(0));
		assertEquals("b", seNested.getUsedAliases().get(1));
		assertEquals("c", seNested.getUsedAliases().get(2));
		assertEquals("x", seNested.getUsedAliases().get(3));
		assertEquals("h", seNested.getUsedAliases().get(4));
		assertEquals("y", seNested.getUsedAliases().get(5));
		assertEquals("A", seNested.getUsedDefNames().get(0));
		assertEquals("B", seNested.getUsedDefNames().get(1));
		assertEquals("C", seNested.getUsedDefNames().get(2));
		assertEquals("A.a as a, A.b as b", seNested.getExpansion().get("A"));
		assertEquals("B.x as x", seNested.getExpansion().get("B"));
		assertEquals("C.h as h", seNested.getExpansion().get("C"));
		assertEquals(6, seNested.getUsedAliases().size());
		assertEquals(3, seNested.getUsedDefNames().size());
		assertEquals(3, seNested.getExpansion().size());
	}

	@Test
	public void testWhereClauseExpansion() {
		seMinimal.addOperator("value", "eq", "= %s");
		seMinimal.addOperator("value", "neq", "<> %s");
		seMinimal.addOperator("null", "eq", "is null");
		seMinimal.addOperator("null", "neq", "is not null");
		seMinimal.addOperator("value", "lt", "< %s");
		seMinimal.addOperator("value", "gt", "> %s");
		seMinimal.addOperator("value", "lteq", "=< %s");
		seMinimal.addOperator("value", "gteq", ">= %s");
		seMinimal.addOperator("value", "re", "~ %s");
		seMinimal.addOperator("value", "ire", "~* %s");
		seMinimal.addOperator("value", "nre", "!~ %s");
		seMinimal.addOperator("value", "nire", "!~* %s");
		seMinimal.addOperator("list", "in", "in (%s)", t -> {
			return !(t.getChildCount() == 1 && t.getChild("value").getValue() == null);
		});
		seMinimal.addOperator("list", "bbi", "&& ST_MakeEnvelope(%s)", t -> {
			return t.getChildCount() == 4 || t.getChildCount() == 5;
		});
		seMinimal.addOperator("list", "bbc", "@ ST_MakeEnvelope(%s)", t -> {
			return t.getChildCount() == 4 || t.getChildCount() == 5;
		});

		seMinimal.setWhereClause("a.bbi.(1,2,3,4,5,6)");
		try {
			seMinimal.expand("a", "A");
			fail("Exception expected; where clause bbi.<list> must have 4 or 5 elements");
		} catch (SimpleException e) {
			// nothing to do
		}

		seMinimal.setWhereClause("a.bbi.(1,2,3,4)");
		seMinimal.expand("a", "A");
		assertEquals("(A.a && ST_MakeEnvelope('1','2','3','4'))", seMinimal.getWhereSql());

		seMinimal.setWhereClause("a.in.()");
		seMinimal.expand("a", "A");
		assertEquals("(A.a in (''))", seMinimal.getWhereSql());

		seMinimal.setWhereClause("a.in.(null,null)");
		seMinimal.expand("a", "A");
		assertEquals("(A.a in (null,null))", seMinimal.getWhereSql());

		seMinimal.setWhereClause("a.eq.1.and(a.eq.0)");
		try {
			seMinimal.expand("a", "A");
			fail("Exception expected; syntax error after a.eq.1");
		} catch (SimpleException e) {
			// nothing to do
			e.printStackTrace();
		}

		seMinimal.setWhereClause("a.bbi.(1,2,3,4,5,6).and(a.eq.0)");
		try {
			seMinimal.expand("a", "A");
			fail("Exception expected; syntax error after a.bbi.(1,2,3,4,5,6)");
		} catch (SimpleException e) {
			// nothing to do
			e.printStackTrace();
		}
	}

	@Test
	public void testMakeObject() {

	}

}
