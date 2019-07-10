package it.bz.idm.bdp.reader2;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import it.bz.idm.bdp.reader2.utils.querybuilder.SelectExpansion;
import it.bz.idm.bdp.reader2.utils.resultbuilder.ObjectBuilder;

@SuppressWarnings("serial")
public class ResultBuilderTests {

	SelectExpansion se;
	List<String> hierarchy;
	List<Map<String, Object>> queryResult;

	@Before
	public void setUpBefore() throws Exception {
		se = new SelectExpansion();
		se.addColumn("measurement", "mvalidtime", "me.timestamp");
		se.addColumn("measurement", "mtransactiontime", "me.created_on");
		se.addColumn("measurement", "mperiod", "me.period");
		se.addColumn("measurement", "mvalue", "me.double_value");

		se.addColumn("datatype", "tname", "t.cname");
		se.addColumn("datatype", "tunit", "t.cunit");
		se.addColumn("datatype", "ttype", "t.rtype");
		se.addColumn("datatype", "tdescription", "t.description");
		se.addSubDef("datatype", "tmeasurements", "measurement");

		se.addColumn("parent", "pname", "p.name");
		se.addColumn("parent", "ptype", "p.stationtype");
		se.addColumn("parent", "pcoordinate", "p.pointprojection");
		se.addColumn("parent", "pcode", "p.stationcode");
		se.addColumn("parent", "porigin", "p.origin");
		se.addColumn("parent", "pmetadata", "pm.json");

		se.addColumn("station", "sname", "s.name");
		se.addColumn("station", "stype", "s.stationtype");
		se.addColumn("station", "scode", "s.stationcode");
		se.addColumn("station", "sorigin", "s.origin");
		se.addColumn("station", "sactive", "s.active");
		se.addColumn("station", "savailable", "s.available");
		se.addColumn("station", "scoordinate", "s.pointprojection");
		se.addColumn("station", "smetadata", "m.json");
		se.addSubDef("station", "sparent", "parent");
		se.addSubDef("station", "sdatatypes", "datatype");

		se.addSubDef("stationtype", "stations", "station");

		queryResult = new ArrayList<Map<String, Object>>();
		hierarchy = new ArrayList<String>();
		hierarchy.add("_stationtype");
		hierarchy.add("_stationcode");
		hierarchy.add("_datatypename");
	}

	@Test
	public void test() {
		se.expand("tname", "datatype");

		queryResult.add(new HashMap<String, Object>() {{
			put("_stationtype", "parking");
			put("_stationcode", "walther");
			put("_datatypename", "occ1");
			put("tname", "o");
		}});

		assertEquals("{parking={stations={walther={sdatatypes=[{tname=o}]}}}}",
				ObjectBuilder.build(true, queryResult, se, hierarchy).toString());

		queryResult.add(new HashMap<String, Object>() {{
			put("_stationtype", "parking");
			put("_stationcode", "walther");
			put("_datatypename", "occ1");
			put("tname", "o");
		}});

		/* We do not eliminate duplicates, if you want this you must write a corresponding query */
		assertEquals("{parking={stations={walther={sdatatypes=[{tname=o}, {tname=o}]}}}}",
				ObjectBuilder.build(true, queryResult, se, hierarchy).toString());

	}

}
