package it.bz.idm.bdp.reader2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import it.bz.idm.bdp.reader2.utils.SelectExpansion;
import it.bz.idm.bdp.reader2.utils.SimpleException;

public class SelectExpansionTests {

	@Test
	public void testFlatStructure() {
		SelectExpansion se = new SelectExpansion();
		se.addExpansion("A", "a", "a.A1");
		se.addExpansion("A", "b", "a.B1");
		se.addExpansion("B", "x", "kkk.B1");
		se.addExpansion("C", "i", "o.f");

		// More select definitions than aliases
		List<String> res = se.getColumnAliasesAsList("a", "A", "C");
		assertEquals("a", res.get(0));
		assertTrue(res.size() == 1);

		// Alias that cannot be found
		try {
			se.getColumnAliases("a, i, x", "A", "C");
		} catch (SimpleException e) {
			assertEquals("SELECT_EXPANSION_KEY_NOT_FOUND", e.getId());
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

		/*
		 * B: B A
		 */

		se.addExpansion("C", "i", "o.f");
		se.addSubExpansion("C", "j", "B");

		/*
		 * B: B A
		 * C: C B A
		 */

		se.addSubExpansion("E", "j", "B");

		/*
		 * B A
		 * C B
		 * C A
		 * E B
		 * E A
		 */

		se.addSubExpansion("A", "j", "A");

		// Select definitions inside a sub-expansion
		List<String> res = se.getColumnAliasesAsList("a", "C");
		assertEquals("a", res.get(0));
		assertTrue(res.size() == 1);

	}

}
