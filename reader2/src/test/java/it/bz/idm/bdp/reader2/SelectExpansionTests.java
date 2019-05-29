package it.bz.idm.bdp.reader2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import it.bz.idm.bdp.reader2.utils.querybuilder.SelectExpansion;
import it.bz.idm.bdp.reader2.utils.querybuilder.SimpleException;
import it.bz.idm.bdp.reader2.utils.querybuilder.SelectExpansion.ErrorCode;
import it.bz.idm.bdp.reader2.utils.querybuilder.SelectExpansion.RecursionType;

public class SelectExpansionTests {

	@Test
	public void testFlatStructure() {
		SelectExpansion se = new SelectExpansion();
		se.addExpansion("A", "a", "A.a");
		se.addExpansion("A", "b", "A.b");
		se.addExpansion("B", "x", "B.x");
		se.addExpansion("C", "i", "C.i");

		se.build("a", "A", "C");

		// More select definitions than aliases
		List<String> res = se.getUsedAliases();
		assertEquals("a", res.get(0));
		assertTrue(res.size() == 1);

		// Alias that cannot be found
		try {
			se.build("a, i, x", "A", "C");
			se.getUsedAliases();
		} catch (SimpleException e) {
			assertEquals(ErrorCode.SELECT_EXPANSION_KEY_NOT_INSIDE_DEFLIST.toString(), e.getId());
			assertEquals("x", e.getData().get("alias"));
		}

	}

	@Test
	public void testNestedStructure() {
		SelectExpansion se = new SelectExpansion();
		se.addExpansion("A", "a", "a.A1");
		se.addExpansion("A", "b", "a.B1");
		se.addExpansion("B", "x", "kkk.B1");
		se.addSubExpansion("B", "y", "A");
		se.addExpansion("C", "i", "o.f");
		se.addSubExpansion("C", "j", "B");
		se.addSubExpansion("E", "j", "B");
		se.addSubExpansion("A", "j", "A");


		// Select definitions inside a sub-expansion
		se.build("a", "C");
		List<String> res = se.getUsedAliases();
		assertEquals("a", res.get(0));
		assertTrue(res.size() == 1);

	}

	@Test
	public void testgetDefNames() throws Exception {
		SelectExpansion se = new SelectExpansion();
		se.addExpansion("A", "a", "A.a");
		se.addSubExpansion("A", "c", "X");
		se.addExpansion("B", "x", "B.x");
		se.addSubExpansion("B", "y", "A");
		se.addExpansion("X", "h", "X.h");
		se.build("a", "A");
	}

	@Test
	public void testExpansion() throws Exception {
		SelectExpansion se = new SelectExpansion();
		se.addExpansion		("A", "a", "A.a");
		se.addExpansion		("A", "b", "A.b");
		se.addSubExpansion	("A", "c", "C");
		se.addExpansion		("B", "x", "B.x");
		se.addSubExpansion	("B", "y", "A");
		se.addExpansion		("C", "h", "C.h");


		se.build("a", "A");
		assertEquals("a", se.getUsedAliases().get(0));
		assertEquals("A", se.getUsedDefNames().get(0));
		assertEquals("A.a as a", se.getExpandedSelects().get("A"));
		assertTrue(se.getUsedAliases().size() == 1);
		assertTrue(se.getUsedDefNames().size() == 1);
		assertTrue(se.getExpandedSelects().size() == 1);

		se.build("a", "B");
		assertEquals("a", se.getUsedAliases().get(0));
		assertEquals("A", se.getUsedDefNames().get(0));
		assertEquals("A.a as a", se.getExpandedSelects().get("A"));
		assertTrue(se.getUsedAliases().size() == 1);
		assertTrue(se.getUsedDefNames().size() == 1);
		assertTrue(se.getExpandedSelects().size() == 1);

		se.build("a, b", "B");
		assertEquals("a", se.getUsedAliases().get(0));
		assertEquals("b", se.getUsedAliases().get(1));
		assertEquals("A", se.getUsedDefNames().get(0));
		assertEquals("A.a as a, A.b as b", se.getExpandedSelects().get("A"));
		assertTrue(se.getUsedAliases().size() == 2);
		assertTrue(se.getUsedDefNames().size() == 1);
		assertTrue(se.getExpandedSelects().size() == 1);

		se.build("x, y", RecursionType.SINGLE, "A", "B");
		assertEquals("a", se.getUsedAliases().get(0));
		assertEquals("b", se.getUsedAliases().get(1));
		assertEquals("c", se.getUsedAliases().get(2));
		assertEquals("x", se.getUsedAliases().get(3));
		assertEquals("h", se.getUsedAliases().get(4));
		assertEquals("y", se.getUsedAliases().get(5));
		assertEquals("A", se.getUsedDefNames().get(0));
		assertEquals("B", se.getUsedDefNames().get(1));
		assertEquals("C", se.getUsedDefNames().get(2));
		assertEquals("A.a as a, A.b as b", se.getExpandedSelects().get("A"));
		assertEquals("B.x as x", se.getExpandedSelects().get("B"));
		assertEquals("C.h as h", se.getExpandedSelects().get("C"));
		assertEquals(6, se.getUsedAliases().size());
		assertEquals(3, se.getUsedDefNames().size());
		assertEquals(3, se.getExpandedSelects().size());

	}

}
