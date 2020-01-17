package it.bz.idm.bdp.ninja;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import it.bz.idm.bdp.ninja.utils.querybuilder.TargetList;
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
		TargetList measurement = TargetList.init("measurement")
				.add(new TargetEntry("mvalidtime", "me.timestamp"))
				.add(new TargetEntry("mtransactiontime", "me.created_on"))
				.add(new TargetEntry("mperiod", "me.period"));

		se.add(measurement);

		TargetList measurementdouble = TargetList.init("measurementdouble")
				.add(new TargetEntry("mvalue_double", "me.double_value")
						.sqlAfter("null::character varying as mvalue_string").alias("mvalue"));

		se.add(measurementdouble);

		TargetList measurementstring = TargetList.init("measurementstring")
				.add(new TargetEntry("mvalue_string", "me.string_value")
						.sqlBefore("null::double precision as mvalue_double").alias("mvalue"));

		se.add(measurementstring);

		TargetList datatype = TargetList.init("datatype")
				.add(new TargetEntry("tname", "t.cname")).add(new TargetEntry("tunit", "t.cunit"))
				.add(new TargetEntry("ttype", "t.rtype"))
				.add(new TargetEntry("tdescription", "t.description"))
				.add(new TargetEntry("tmeasurements", measurement));

		se.add(datatype);

		TargetList parent = TargetList.init("parent").add(new TargetEntry("pname", "p.name"))
				.add(new TargetEntry("ptype", "p.stationtype"))
				.add(new TargetEntry("pcode", "p.stationcode"))
				.add(new TargetEntry("porigin", "p.origin"))
				.add(new TargetEntry("pactive", "p.active"))
				.add(new TargetEntry("pavailable", "p.available"))
				.add(new TargetEntry("pcoordinate", "p.pointprojection"))
				.add(new TargetEntry("pmetadata", "pm.json"));

		se.add(parent);

		TargetList station = TargetList.init("station")
				.add(new TargetEntry("sname", "s.name"))
				.add(new TargetEntry("stype", "s.stationtype"))
				.add(new TargetEntry("scode", "s.stationcode"))
				.add(new TargetEntry("sorigin", "s.origin"))
				.add(new TargetEntry("sactive", "s.active"))
				.add(new TargetEntry("savailable", "s.available"))
				.add(new TargetEntry("scoordinate", "s.pointprojection"))
				.add(new TargetEntry("smetadata", "m.json"))
				.add(new TargetEntry("sparent", parent))
				.add(new TargetEntry("sdatatypes", datatype));

		se.add(station);

		TargetList stationtype = TargetList.init("stationtype")
				.add(new TargetEntry("stations", station));

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
