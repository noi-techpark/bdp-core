package it.bz.idm.bdp.reader2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import it.bz.idm.bdp.reader2.utils.SelectExpansion;
import it.bz.idm.bdp.reader2.utils.SelectExpansion.ERROR_CODES;
import it.bz.idm.bdp.reader2.utils.SimpleException;

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
			assertEquals(ERROR_CODES.SELECT_EXPANSION_KEY_NOT_FOUND.toString(), e.getId());
			assertEquals("x", e.getData());
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
	public void testExpansion() throws Exception {
		SelectExpansion se = new SelectExpansion();
		se.addExpansion("A", "a", "a.A1");
		se.addExpansion("A", "b", "a.B1");
		se.addExpansion("B", "x", "kkk.B1");
		se.addSubExpansion("B", "y", "A");

		se.addExpansion("X", "h", "h.h");

		se.addSubExpansion("A", "c", "X");

		se.build("a", "A");
		System.out.println(se.getExpandedSelects());
		System.out.println(se.getUsedAliases());
		System.out.println(se.getUsedDefNames());

		se.build("a", "B");
		System.out.println(se.getExpandedSelects());
		System.out.println(se.getUsedAliases());
		System.out.println(se.getUsedDefNames());

		se.build("a, b", "B");
		System.out.println(se.getExpandedSelects());
		System.out.println(se.getUsedAliases());
		System.out.println(se.getUsedDefNames());

		se.build("x, y", "B");
		System.out.println(se.getExpandedSelects());
		System.out.println(se.getUsedAliases());
		System.out.println(se.getUsedDefNames());

	}

}
