package it.bz.idm.bdp.ninja;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.Before;
import org.junit.Test;

import it.bz.idm.bdp.ninja.utils.querybuilder.TargetDefList;
import it.bz.idm.bdp.ninja.utils.querybuilder.Schema;
import it.bz.idm.bdp.ninja.utils.querybuilder.SelectExpansion;
import it.bz.idm.bdp.ninja.utils.querybuilder.TargetDef;
import it.bz.idm.bdp.ninja.utils.resultbuilder.ResultBuilder;

@SuppressWarnings("serial")
public class ResultBuilderTests {

	SelectExpansion se;
	List<String> hierarchy;
	List<Map<String, Object>> queryResult;
	private SelectExpansion seNestedMain;

	@Before
	public void setUpBefore() throws Exception {
		se = new SelectExpansion();
		Schema schema = new Schema();
		TargetDefList measurement = TargetDefList.init("measurement")
				.add(new TargetDef("mvalidtime", "me.timestamp"))
				.add(new TargetDef("mtransactiontime", "me.created_on"))
				.add(new TargetDef("mperiod", "me.period"));

		schema.add(measurement);

		TargetDefList measurementdouble = TargetDefList.init("measurementdouble")
				.add(new TargetDef("mvalue_double", "me.double_value")
						.sqlAfter("null::character varying as mvalue_string").alias("mvalue"));

		schema.add(measurementdouble);

		TargetDefList measurementstring = TargetDefList.init("measurementstring")
				.add(new TargetDef("mvalue_string", "me.string_value")
						.sqlBefore("null::double precision as mvalue_double").alias("mvalue"));

		schema.add(measurementstring);

		TargetDefList datatype = TargetDefList.init("datatype")
				.add(new TargetDef("tname", "t.cname"))
				.add(new TargetDef("tunit", "t.cunit"))
				.add(new TargetDef("ttype", "t.rtype"))
				.add(new TargetDef("tdescription", "t.description"))
				.add(new TargetDef("tmeasurements", measurement));

		schema.add(datatype);

		TargetDefList parent = TargetDefList.init("parent")
				.add(new TargetDef("pname", "p.name"))
				.add(new TargetDef("ptype", "p.stationtype"))
				.add(new TargetDef("pcode", "p.stationcode"))
				.add(new TargetDef("porigin", "p.origin"))
				.add(new TargetDef("pactive", "p.active"))
				.add(new TargetDef("pavailable", "p.available"))
				.add(new TargetDef("pcoordinate", "p.pointprojection"))
				.add(new TargetDef("pmetadata", "pm.json"));

		schema.add(parent);

		TargetDefList station = TargetDefList.init("station")
				.add(new TargetDef("sname", "s.name"))
				.add(new TargetDef("stype", "s.stationtype"))
				.add(new TargetDef("scode", "s.stationcode"))
				.add(new TargetDef("sorigin", "s.origin"))
				.add(new TargetDef("sactive", "s.active"))
				.add(new TargetDef("savailable", "s.available"))
				.add(new TargetDef("scoordinate", "s.pointprojection"))
				.add(new TargetDef("smetadata", "m.json"))
				.add(new TargetDef("sparent", parent))
				.add(new TargetDef("sdatatypes", datatype));

		schema.add(station);

		TargetDefList stationtype = TargetDefList.init("stationtype")
				.add(new TargetDef("stations", station));

		schema.add(stationtype);

		se.setSchema(schema);

		queryResult = new ArrayList<Map<String, Object>>();
		hierarchy = new ArrayList<String>();
		hierarchy.add("_stationtype");
		hierarchy.add("_stationcode");
		hierarchy.add("_datatypename");

		seNestedMain = new SelectExpansion();
		Schema schemaNestedMain = new Schema();
		TargetDefList defListC = new TargetDefList("C")
				.add(new TargetDef("h", "C.h").sqlBefore("before"));
		TargetDefList defListD = new TargetDefList("D")
				.add(new TargetDef("d", "D.d").sqlAfter("after"));
		TargetDefList defListB = new TargetDefList("B")
				.add(new TargetDef("x", "B.x").alias("x_replaced"))
				.add(new TargetDef("y", defListC));
		TargetDefList defListA = new TargetDefList("A")
				.add(new TargetDef("a", "A.a"))
				.add(new TargetDef("b", "A.b"))
				.add(new TargetDef("c", defListB));
		TargetDefList defListMain = new TargetDefList("main")
				.add(new TargetDef("t", defListA));
		schemaNestedMain.add(defListA);
		schemaNestedMain.add(defListB);
		schemaNestedMain.add(defListC);
		schemaNestedMain.add(defListD);
		schemaNestedMain.add(defListMain);
		seNestedMain.setSchema(schemaNestedMain);
	}

	@Test
	public void testOpenDataHubMobility() {
		se.expand("tname", "datatype");

		queryResult.add(new HashMap<String, Object>() {
			{
				put("_stationtype", "parking");
				put("_stationcode", "walther");
				put("_datatypename", "occ1");
				put("tname", "o");
			}
		});

		assertEquals("{parking={stations={walther={sdatatypes={occ1={tname=o}}}}}}",
				ResultBuilder.build(true, queryResult, se.getSchema(), hierarchy, 0).toString());

		queryResult.add(new HashMap<String, Object>() {
			{
				put("_stationtype", "parking");
				put("_stationcode", "walther");
				put("_datatypename", "occ1");
				put("tname", "o");
			}
		});

		assertEquals("{parking={stations={walther={sdatatypes={occ1={tname=o}}}}}}",
				ResultBuilder.build(true, queryResult, se.getSchema(), hierarchy, 0).toString());

		queryResult.add(new HashMap<String, Object>() {
			{
				put("_stationtype", "parking");
				put("_stationcode", "walther");
				put("_datatypename", "occ2");
				put("tname", "x");
			}
		});

		assertEquals("{parking={stations={walther={sdatatypes={occ2={tname=x}, occ1={tname=o}}}}}}",
				ResultBuilder.build(true, queryResult, se.getSchema(), hierarchy, 0).toString());

	}

	@Test
	public void testMakeObject() {
		AtomicLong size = new AtomicLong(0);
		seNestedMain.expand("*", "main", "A", "B", "C", "D");
		Map<String, Object> rec = new HashMap<String, Object>();
		rec.put("a", "3");
		rec.put("b", "7");
		rec.put("d", "DDD");
		rec.put("x_replaced", "0");
		rec.put("h", "v");

		assertEquals("a", seNestedMain.getUsedTargetNames().get(0));
		assertEquals("b", seNestedMain.getUsedTargetNames().get(1));
		assertEquals("c", seNestedMain.getUsedTargetNames().get(2));
		assertEquals("d", seNestedMain.getUsedTargetNames().get(3));
		assertEquals("h", seNestedMain.getUsedTargetNames().get(4));
		assertEquals("t", seNestedMain.getUsedTargetNames().get(5));
		assertEquals("x_replaced", seNestedMain.getUsedTargetNames().get(6));
		assertEquals("y", seNestedMain.getUsedTargetNames().get(7));

		assertEquals("A", seNestedMain.getUsedDefNames().get(0));
		assertEquals("B", seNestedMain.getUsedDefNames().get(1));
		assertEquals("C", seNestedMain.getUsedDefNames().get(2));
		assertEquals("D", seNestedMain.getUsedDefNames().get(3));
		assertEquals("main", seNestedMain.getUsedDefNames().get(4));

		assertEquals("A.a as a, A.b as b", seNestedMain.getExpansion().get("A"));
		assertEquals("B.x as x_replaced", seNestedMain.getExpansion().get("B"));
		assertEquals("before, C.h as h", seNestedMain.getExpansion().get("C"));
		assertEquals("D.d as d, after", seNestedMain.getExpansion().get("D"));

		System.out.println(ResultBuilder.makeObj(seNestedMain.getSchema(), rec, "A", false, size).toString());
		System.out.println(ResultBuilder.makeObj(seNestedMain.getSchema(), rec, "A", true, size).toString());
		System.out.println();
		System.out.println(ResultBuilder.makeObj(seNestedMain.getSchema(), rec, "B", false, size).toString());
		System.out.println(ResultBuilder.makeObj(seNestedMain.getSchema(), rec, "B", true, size).toString());
		System.out.println();
		System.out.println(ResultBuilder.makeObj(seNestedMain.getSchema(), rec, "C", false, size).toString());
		System.out.println(ResultBuilder.makeObj(seNestedMain.getSchema(), rec, "C", true, size).toString());
		System.out.println();
	}

	@Test
	public void testMakeObjectJSON() {
		AtomicLong size = new AtomicLong(0);
		seNestedMain.expand("x_replaced.address.cap, x_replaced.address.city", "A", "B");
		Map<String, Object> rec = new HashMap<String, Object>();
		rec.put("x_replaced.address.cap", 39100);
		rec.put("x_replaced.address.city", "BZ");

		assertEquals("x_replaced", seNestedMain.getUsedTargetNames().get(0));
		assertEquals("B", seNestedMain.getUsedDefNames().get(0));

		List<String> expB = Arrays.asList(seNestedMain.getExpansion().get("B").split(", "));
		assertTrue(expB.contains("B.x#>'{address,city}' as \"x_replaced.address.city\""));
		assertTrue(expB.contains("B.x#>'{address,cap}' as \"x_replaced.address.cap\""));

		System.out.println(ResultBuilder.makeObj(seNestedMain.getSchema(), rec, "B", false, size).toString());
	}

}
