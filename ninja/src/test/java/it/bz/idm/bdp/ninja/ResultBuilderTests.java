package it.bz.idm.bdp.ninja;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import it.bz.idm.bdp.ninja.utils.querybuilder.TargetDefList;
import it.bz.idm.bdp.ninja.utils.querybuilder.SelectExpansion;
import it.bz.idm.bdp.ninja.utils.querybuilder.TargetDef;
import it.bz.idm.bdp.ninja.utils.resultbuilder.ResultBuilder;

@SuppressWarnings("serial")
public class ResultBuilderTests {

	SelectExpansion se;
	List<String> hierarchy;
	List<Map<String, Object>> queryResult;

	@Before
	public void setUpBefore() throws Exception {
		se = new SelectExpansion();
		TargetDefList measurement = TargetDefList.init("measurement")
				.add(new TargetDef("mvalidtime", "me.timestamp"))
				.add(new TargetDef("mtransactiontime", "me.created_on"))
				.add(new TargetDef("mperiod", "me.period"));

		se.add(measurement);

		TargetDefList measurementdouble = TargetDefList.init("measurementdouble")
				.add(new TargetDef("mvalue_double", "me.double_value")
						.sqlAfter("null::character varying as mvalue_string").alias("mvalue"));

		se.add(measurementdouble);

		TargetDefList measurementstring = TargetDefList.init("measurementstring")
				.add(new TargetDef("mvalue_string", "me.string_value")
						.sqlBefore("null::double precision as mvalue_double").alias("mvalue"));

		se.add(measurementstring);

		TargetDefList datatype = TargetDefList.init("datatype")
				.add(new TargetDef("tname", "t.cname")).add(new TargetDef("tunit", "t.cunit"))
				.add(new TargetDef("ttype", "t.rtype"))
				.add(new TargetDef("tdescription", "t.description"))
				.add(new TargetDef("tmeasurements", measurement));

		se.add(datatype);

		TargetDefList parent = TargetDefList.init("parent").add(new TargetDef("pname", "p.name"))
				.add(new TargetDef("ptype", "p.stationtype"))
				.add(new TargetDef("pcode", "p.stationcode"))
				.add(new TargetDef("porigin", "p.origin"))
				.add(new TargetDef("pactive", "p.active"))
				.add(new TargetDef("pavailable", "p.available"))
				.add(new TargetDef("pcoordinate", "p.pointprojection"))
				.add(new TargetDef("pmetadata", "pm.json"));

		se.add(parent);

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

		se.add(station);

		TargetDefList stationtype = TargetDefList.init("stationtype")
				.add(new TargetDef("stations", station));

		se.add(stationtype);

		queryResult = new ArrayList<Map<String, Object>>();
		hierarchy = new ArrayList<String>();
		hierarchy.add("_stationtype");
		hierarchy.add("_stationcode");
		hierarchy.add("_datatypename");
	}

	@Test
	public void test() {
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
				ResultBuilder.build(true, queryResult, se, hierarchy).toString());

		queryResult.add(new HashMap<String, Object>() {
			{
				put("_stationtype", "parking");
				put("_stationcode", "walther");
				put("_datatypename", "occ1");
				put("tname", "o");
			}
		});

		assertEquals("{parking={stations={walther={sdatatypes={occ1={tname=o}}}}}}",
				ResultBuilder.build(true, queryResult, se, hierarchy).toString());

		queryResult.add(new HashMap<String, Object>() {
			{
				put("_stationtype", "parking");
				put("_stationcode", "walther");
				put("_datatypename", "occ2");
				put("tname", "x");
			}
		});

		assertEquals("{parking={stations={walther={sdatatypes={occ2={tname=x}, occ1={tname=o}}}}}}",
				ResultBuilder.build(true, queryResult, se, hierarchy).toString());

	}

}
