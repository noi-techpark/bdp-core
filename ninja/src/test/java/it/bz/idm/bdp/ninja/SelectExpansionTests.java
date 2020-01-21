package it.bz.idm.bdp.ninja;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import it.bz.idm.bdp.ninja.config.SelectExpansionConfig;
import it.bz.idm.bdp.ninja.utils.querybuilder.TargetDefList;
import it.bz.idm.bdp.ninja.utils.querybuilder.Schema;
import it.bz.idm.bdp.ninja.utils.querybuilder.SelectExpansion;
import it.bz.idm.bdp.ninja.utils.querybuilder.TargetDef;
import it.bz.idm.bdp.ninja.utils.querybuilder.SelectExpansion.ErrorCode;
import it.bz.idm.bdp.ninja.utils.simpleexception.SimpleException;

public class SelectExpansionTests {

	SelectExpansion seNested;
	SelectExpansion seFlat;
	SelectExpansion seNestedBig;
	SelectExpansion seNestedMain;
	SelectExpansion seMinimal;
	SelectExpansion seOpenDataHub;

	@Before
	public void setup() {
		Schema schemaNested = new Schema();
		seNested = new SelectExpansion();
		TargetDefList seNestedC = TargetDefList.init("C")
				.add(new TargetDef("h", "C.h"));
		TargetDefList seNestedA = TargetDefList.init("A")
				.add(new TargetDef("a", "A.a"))
				.add(new TargetDef("b", "A.b"))
				.add(new TargetDef("c", seNestedC));
		TargetDefList seNestedB = TargetDefList.init("B")
				.add(new TargetDef("x", "B.x"))
				.add(new TargetDef("y", seNestedA));
		schemaNested.add(seNestedA);
		schemaNested.add(seNestedB);
		schemaNested.add(seNestedC);
		seNested.setSchema(schemaNested);

		Schema schemaFlat = new Schema();
		seFlat = new SelectExpansion();
		TargetDefList seFlatA = TargetDefList.init("A")
				.add(new TargetDef("a", "A.a"))
				.add(new TargetDef("b", "A.b"));
		TargetDefList seFlatB = TargetDefList.init("B")
				.add(new TargetDef("x", "B.x"));
		TargetDefList seFlatC = TargetDefList.init("C")
				.add(new TargetDef("i", "C.i"));
		schemaFlat.add(seFlatA);
		schemaFlat.add(seFlatB);
		schemaFlat.add(seFlatC);
		seFlat.addOperator("null", "eq", "%c is %v");
		seFlat.addOperator("number", "eq", "%c = %v");
		seFlat.setSchema(schemaFlat);

		Schema schemaNestedBig = new Schema();
		seNestedBig = new SelectExpansion();
		TargetDefList seNestedBigA = TargetDefList.init("A")
				.add(new TargetDef("a", "A.a"))
				.add(new TargetDef("b", "A.b"));
		TargetDefList seNestedBigB = TargetDefList.init("B")
				.add(new TargetDef("x", "B.x").alias("x_replaced"))
				.add(new TargetDef("y", seNestedBigA));
		TargetDefList seNestedBigC = TargetDefList.init("C")
				.add(new TargetDef("i", "C.i"))
				.add(new TargetDef("j", seNestedBigB));
		TargetDefList seNestedBigE = TargetDefList.init("E")
				.add(new TargetDef("j", seNestedBigB));
		schemaNestedBig.add(seNestedBigA);
		schemaNestedBig.add(seNestedBigB);
		schemaNestedBig.add(seNestedBigC);
		schemaNestedBig.add(seNestedBigE);
		seNestedBig.setSchema(schemaNestedBig);

		Schema schemaMinimal = new Schema();
		seMinimal = new SelectExpansion();
		schemaMinimal.add(new TargetDefList("A").add(new TargetDef("a", "A.a")));
		seMinimal.setSchema(schemaMinimal);
		seMinimal.addOperator("string", "eq", "%c = %v");
		seMinimal.addOperator("string", "neq", "%c <> %v");
		seMinimal.addOperator("number", "eq", "%c = %v");
		seMinimal.addOperator("number", "neq", "%c <> %v");
		seMinimal.addOperator("null", "eq", "%c is %v");
		seMinimal.addOperator("null", "neq", "%c is not %v");
		seMinimal.addOperator("number", "lt", "%c < %v");
		seMinimal.addOperator("number", "gt", "%c > %v");
		seMinimal.addOperator("number", "lteq", "%c =< %v");
		seMinimal.addOperator("number", "gteq", "%c >= %v");
		seMinimal.addOperator("string", "re", "%c ~ %v");
		seMinimal.addOperator("string", "ire", "%c ~* %v");
		seMinimal.addOperator("string", "nre", "%c !~ %v");
		seMinimal.addOperator("string", "nire", "%c !~* %v");
		seMinimal.addOperator("list/number", "in", "%c in (%v)", t -> {
			return !(t.getChildCount() == 1 && (
					t.getChild("string") != null && t.getChild("string").getValue() == null ||
					t.getChild("number") != null && t.getChild("number").getValue() == null
					));
		});
		seMinimal.addOperator("list/null", "in", "%c in (%v)", t -> {
			return !(t.getChildCount() == 1 && (
					t.getChild("string") != null && t.getChild("string").getValue() == null ||
					t.getChild("number") != null && t.getChild("number").getValue() == null
					));
		});
		seMinimal.addOperator("list/string", "in", "%c in (%v)", t -> {
			return !(t.getChildCount() == 1 && (
					t.getChild("string") != null && t.getChild("string").getValue() == null ||
					t.getChild("number") != null && t.getChild("number").getValue() == null
					));
		});
		seMinimal.addOperator("list/mixed", "in", "%c in (%v)", t -> {
			return !(t.getChildCount() == 1 && (
					t.getChild("string") != null && t.getChild("string").getValue() == null ||
					t.getChild("number") != null && t.getChild("number").getValue() == null
					));
		});
		seMinimal.addOperator("list/number", "bbi", "%c && ST_MakeEnvelope(%v)", t -> {
			return t.getChildCount() == 4 || t.getChildCount() == 5;
		});
		seMinimal.addOperator("list/number", "bbc", "%c @ ST_MakeEnvelope(%v)", t -> {
			return t.getChildCount() == 4 || t.getChildCount() == 5;
		});

		seOpenDataHub = new SelectExpansionConfig().getSelectExpansion();
	}

	@Test
	public void testFlatStructure() {
		seFlat.expand("a", "A", "C");

		// More select definitions than aliases
		List<String> res = seFlat.getUsedTargetNames();
		assertEquals("a", res.get(0));
		assertTrue(res.size() == 1);

		// Alias that cannot be found
		try {
			seFlat.expand("a, i, x", "A", "C");
			fail("Exception expected");
		} catch (SimpleException e) {
			assertEquals(ErrorCode.KEY_NOT_INSIDE_DEFLIST, e.getId());
			assertEquals("x", e.getData().get("targetName"));
		}
	}

	@Test
	public void testOpenDataHubConfigJSONList() {
		seOpenDataHub.setWhereClause("smetadata.aa.bbb.in.()");
		seOpenDataHub.expand("smetadata", "station");
		seOpenDataHub.setWhereClause("smetadata.aa.bbb.in.(null)");
		seOpenDataHub.expand("smetadata", "station");
		seOpenDataHub.setWhereClause("smetadata.aa.bbb.in.(null,null)");
		seOpenDataHub.expand("smetadata", "station");
		seOpenDataHub.setWhereClause("smetadata.aa.bbb.in.(null,x,null)");
		seOpenDataHub.expand("smetadata", "station");
		seOpenDataHub.setWhereClause("smetadata.aa.bbb.in.(1,2,3)");
		seOpenDataHub.expand("smetadata", "station");
		seOpenDataHub.setWhereClause("smetadata.aa.bbb.in.(1,hallo)");
		seOpenDataHub.expand("smetadata", "station");
	}

	@Test
	public void testOpenDataHubConfigJSON() {
		seOpenDataHub.setWhereClause("smetadata.aa.bbb.eq.");
		seOpenDataHub.expand("smetadata", "station");
		seOpenDataHub.setWhereClause("smetadata.aa.bbb.eq.\"\"");
		seOpenDataHub.expand("smetadata", "station");
		seOpenDataHub.setWhereClause("smetadata.aa.bbb.eq.null");
		seOpenDataHub.expand("smetadata", "station");
		seOpenDataHub.setWhereClause("smetadata.aa.bbb.eq.-1");
		seOpenDataHub.expand("smetadata", "station");
		seOpenDataHub.setWhereClause("smetadata.aa.bbb.eq.hallo");
		seOpenDataHub.expand("smetadata", "station");
		seOpenDataHub.setWhereClause("smetadata.aa.bbb.eq.\".......\"");
		seOpenDataHub.expand("smetadata", "station");
	}

	@Test
	public void testNestedStructure() {
		// Select definitions inside a sub-expansion
		seNestedBig.expand("a", "A", "C");
		List<String> res = seNestedBig.getUsedTargetNames();
		assertEquals("a", res.get(0));
		assertTrue(res.size() == 1);
	}

	@Test
	public void testAlias() {
		seNestedBig.expand("x", "B");
		List<String> res = seNestedBig.getUsedTargetNames();
		assertEquals("x", res.get(0));
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
		assertEquals("a", seNested.getUsedTargetNames().get(0));
		assertEquals("A", seNested.getUsedDefNames().get(0));
		assertEquals("A.a as a", seNested.getExpansion().get("A"));
		assertTrue(seNested.getUsedTargetNames().size() == 1);
		assertTrue(seNested.getUsedDefNames().size() == 1);
		assertTrue(seNested.getExpansion().size() == 1);

		try {
			seNested.expand("a", "B");
			fail("Exception expected");
		} catch (Exception e) {
			// nothing to do
		}

		seNested.expand("a, b", "A", "B");
		assertEquals("a", seNested.getUsedTargetNames().get(0));
		assertEquals("b", seNested.getUsedTargetNames().get(1));
		assertEquals("A", seNested.getUsedDefNames().get(0));
		assertEquals("A.a as a, A.b as b", seNested.getExpansion().get("A"));
		assertTrue(seNested.getUsedTargetNames().size() == 2);
		assertTrue(seNested.getUsedDefNames().size() == 1);
		assertTrue(seNested.getExpansion().size() == 1);

		seNested.expand("x, y", "A", "B", "C");
		assertEquals("a", seNested.getUsedTargetNames().get(0));
		assertEquals("b", seNested.getUsedTargetNames().get(1));
		assertEquals("c", seNested.getUsedTargetNames().get(2));
		assertEquals("h", seNested.getUsedTargetNames().get(3));
		assertEquals("x", seNested.getUsedTargetNames().get(4));
		assertEquals("y", seNested.getUsedTargetNames().get(5));
		assertEquals("A", seNested.getUsedDefNames().get(0));
		assertEquals("B", seNested.getUsedDefNames().get(1));
		assertEquals("C", seNested.getUsedDefNames().get(2));
		assertEquals("A.a as a, A.b as b", seNested.getExpansion().get("A"));
		assertEquals("B.x as x", seNested.getExpansion().get("B"));
		assertEquals("C.h as h", seNested.getExpansion().get("C"));
		assertEquals(6, seNested.getUsedTargetNames().size());
		assertEquals(3, seNested.getUsedDefNames().size());
		assertEquals(3, seNested.getExpansion().size());
	}

	@Test
	public void testExpansionWithFunctionsAndTargets() throws Exception {
		seNested.expand("a, min(a), max(a)", "A");
		assertEquals("a", seNested.getUsedTargetNames().get(0));
		assertEquals("A", seNested.getUsedDefNames().get(0));
		assertEquals("max(A.a) as \"max(a)\", min(A.a) as \"min(a)\", A.a as a", seNested.getExpansion().get("A"));
		assertTrue(seNested.getUsedTargetNames().size() == 1);
		assertTrue(seNested.getUsedDefNames().size() == 1);
		assertTrue(seNested.getExpansion().size() == 1);
	}

	@Test
	public void testExpansionWithFunctionsOnly() throws Exception {
		seNested.expand("min(a), max(a)", "A");
		assertEquals("a", seNested.getUsedTargetNames().get(0));
		assertEquals("A", seNested.getUsedDefNames().get(0));
		assertEquals("max(A.a) as \"max(a)\", min(A.a) as \"min(a)\"", seNested.getExpansion().get("A"));
		assertTrue(seNested.getUsedTargetNames().size() == 1);
		assertTrue(seNested.getUsedDefNames().size() == 1);
		assertTrue(seNested.getExpansion().size() == 1);
	}

	@Test
	public void testWhereClauseExpansion() {
		seMinimal.setWhereClause("a.bbi.(1,2,3,4,5,6)");
		try {
			seMinimal.expand("a", "A");
			fail("Exception expected; where clause bbi.<list> must have 4 or 5 elements");
		} catch (SimpleException e) {
			// nothing to do
		}

		seMinimal.setWhereClause("a.bbi.(1,2,3,4)");
		seMinimal.expand("a", "A");
		assertEquals("(A.a && ST_MakeEnvelope(:pwhere_0))", seMinimal.getWhereSql());
		assertEquals("{pwhere_0=[1, 2, 3, 4]}", seMinimal.getWhereParameters().toString());

		seMinimal.setWhereClause("a.in.()");
		seMinimal.expand("a", "A");
		assertEquals("(A.a in (:pwhere_0))", seMinimal.getWhereSql());
		assertEquals("{pwhere_0=[]}", seMinimal.getWhereParameters().toString());

		seMinimal.setWhereClause("a.in.(null,null)");
		seMinimal.expand("a", "A");
		assertEquals("(A.a in (:pwhere_0))", seMinimal.getWhereSql());
		assertEquals("{pwhere_0=[null, null]}", seMinimal.getWhereParameters().toString());

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
		}

		seFlat.setWhereClause("and(or(a.eq.null,b.eq.5))");
		seFlat.expand("a", "A");
		assertEquals("(((A.a is null OR A.b = :pwhere_0)))", seFlat.getWhereSql());
		assertEquals(5, seFlat.getWhereParameters().get("pwhere_0"));

		seMinimal.setWhereClause("a.eq.3");
		seMinimal.expand("a", "A");
		assertTrue(seMinimal.getUsedAliasesInWhere().containsKey("a"));
		assertEquals("NUMBER", seMinimal.getUsedAliasesInWhere().get("a").get(0).getName());
		assertTrue(seMinimal.getUsedAliasesInWhere().get("a").get(0).getPayload("typedvalue") instanceof Integer);

		seMinimal.setWhereClause("a.in.(1,3.2,a,null)");
		seMinimal.expand("a", "A");
		assertTrue(seMinimal.getUsedAliasesInWhere().containsKey("a"));
		assertEquals("LIST", seMinimal.getUsedAliasesInWhere().get("a").get(0).getName());
		assertTrue(seMinimal.getUsedAliasesInWhere().get("a").get(1).getPayload("typedvalue") instanceof Integer);
		assertTrue(seMinimal.getUsedAliasesInWhere().get("a").get(2).getPayload("typedvalue") instanceof Double);
		assertTrue(seMinimal.getUsedAliasesInWhere().get("a").get(3).getPayload("typedvalue") instanceof String);
		assertTrue(seMinimal.getUsedAliasesInWhere().get("a").get(4).getPayload("typedvalue") == null);
	}

}
