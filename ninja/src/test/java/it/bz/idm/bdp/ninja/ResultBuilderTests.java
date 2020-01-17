package it.bz.idm.bdp.ninja;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import it.bz.idm.bdp.ninja.utils.querybuilder.SelectDefinition;
import it.bz.idm.bdp.ninja.utils.querybuilder.SelectExpansion;
import it.bz.idm.bdp.ninja.utils.querybuilder.TargetEntry;
import it.bz.idm.bdp.ninja.utils.resultbuilder.ResultBuilder;

@SuppressWarnings("serial")
public class ResultBuilderTests {

	SelectExpansion se;
	List<String> hierarchy;
	List<Map<String, Object>> queryResult;

	@Before
	public void setUpBefore() throws Exception {
		se = new SelectExpansion();
		SelectDefinition measurement = SelectDefinition.init("measurement")
				.addTargetEntry(new TargetEntry("mvalidtime", "me.timestamp"))
				.addTargetEntry(new TargetEntry("mtransactiontime", "me.created_on"))
				.addTargetEntry(new TargetEntry("mperiod", "me.period"));

		se.add(measurement);

		SelectDefinition measurementdouble = SelectDefinition.init("measurementdouble")
				.addTargetEntry(new TargetEntry("mvalue_double", "me.double_value")
						.sqlAfter("null::character varying as mvalue_string").alias("mvalue"));

		se.add(measurementdouble);

		SelectDefinition measurementstring = SelectDefinition.init("measurementstring")
				.addTargetEntry(new TargetEntry("mvalue_string", "me.string_value")
						.sqlBefore("null::double precision as mvalue_double").alias("mvalue"));

		se.add(measurementstring);

		SelectDefinition datatype = SelectDefinition.init("datatype")
				.addTargetEntry(new TargetEntry("tname", "t.cname")).addTargetEntry(new TargetEntry("tunit", "t.cunit"))
				.addTargetEntry(new TargetEntry("ttype", "t.rtype"))
				.addTargetEntry(new TargetEntry("tdescription", "t.description"))
				.addTargetEntry(new TargetEntry("tmeasurements", measurement));

		se.add(datatype);

		SelectDefinition parent = SelectDefinition.init("parent").addTargetEntry(new TargetEntry("pname", "p.name"))
				.addTargetEntry(new TargetEntry("ptype", "p.stationtype"))
				.addTargetEntry(new TargetEntry("pcode", "p.stationcode"))
				.addTargetEntry(new TargetEntry("porigin", "p.origin"))
				.addTargetEntry(new TargetEntry("pactive", "p.active"))
				.addTargetEntry(new TargetEntry("pavailable", "p.available"))
				.addTargetEntry(new TargetEntry("pcoordinate", "p.pointprojection"))
				.addTargetEntry(new TargetEntry("pmetadata", "pm.json"));

		se.add(parent);

		SelectDefinition station = SelectDefinition.init("station")
				.addTargetEntry(new TargetEntry("sname", "s.name"))
				.addTargetEntry(new TargetEntry("stype", "s.stationtype"))
				.addTargetEntry(new TargetEntry("scode", "s.stationcode"))
				.addTargetEntry(new TargetEntry("sorigin", "s.origin"))
				.addTargetEntry(new TargetEntry("sactive", "s.active"))
				.addTargetEntry(new TargetEntry("savailable", "s.available"))
				.addTargetEntry(new TargetEntry("scoordinate", "s.pointprojection"))
				.addTargetEntry(new TargetEntry("smetadata", "m.json"))
				.addTargetEntry(new TargetEntry("sparent", parent))
				.addTargetEntry(new TargetEntry("sdatatypes", datatype));

		se.add(station);

		SelectDefinition stationtype = SelectDefinition.init("stationtype")
				.addTargetEntry(new TargetEntry("stations", station));

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
