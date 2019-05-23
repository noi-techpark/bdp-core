package it.bz.idm.bdp.reader2;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import it.bz.idm.bdp.reader2.utils.QueryBuilder;
import it.bz.idm.bdp.reader2.utils.SelectExpansion;

public class QueryBuilderTests {

	@Test
	public void testExpandSelect() {
		String res = QueryBuilder
				.init("sname, pname", "station", "parent")
				.expandSelect()
				.getSql();

		assertEquals(res, "p.name as pname, s.name as sname");

		res = QueryBuilder
				.init("sparent", "station", "parent")
				.expandSelect()
				.getSql();

		assertEquals(res, "s.pointprojection as pcoordinate, p.stationcode as pcode, p.name as pname, p.stationtype as ptype, p.origin as porigin");
	}

	@Before
	public void setUpBefore() throws Exception {
		// We load data from AppStartupDataLoader.java
		SelectExpansion se = new SelectExpansion();
		Map<String, Object> seMeasurement = new HashMap<String, Object>() {
			private static final long serialVersionUID = 1L;
			{
				put("mvalidtime", "me.timestamp");
				put("mtransactiontime", "me.created_on");
				put("mperiod", "me.period");
				put("mvalue", "me.double_value");
			}
		};

		Map<String, Object> seDatatype = new HashMap<String, Object>() {
			private static final long serialVersionUID = 1L;
			{
				put("tname", "t.cname");
				put("tunit", "t.cunit");
				put("ttype", "t.rtype");
				put("tdescription", "t.description");
				put("tlastmeasurement", seMeasurement);
			}
		};

		Map<String, Object> seParent = new HashMap<String, Object>() {
			private static final long serialVersionUID = 1L;
			{
				put("pname", "p.name");
				put("ptype", "p.stationtype");
				put("pcoordinate", "s.pointprojection");
				put("pcode", "p.stationcode");
				put("porigin", "p.origin");
			}
		};

		Map<String, Object> seStation = new HashMap<String, Object>() {
			private static final long serialVersionUID = 1L;
			{
				put("sname", "s.name");
				put("stype", "s.stationtype");
				put("scode", "s.stationcode");
				put("sorigin", "s.origin");
				put("scoordinate", "s.pointprojection");
				put("smetadata", "m.json");
				put("sparent", seParent);
				put("sdatatypes", seDatatype);
			}
		};

		se.addExpansion("station", seStation);
		se.addExpansion("parent", seParent);
		se.addExpansion("datatype", seDatatype);
		se.addExpansion("measurement", seMeasurement);

		QueryBuilder.setup(se);

//		boolean ignoreNull = false;
//		// The API should have a flag to remove null values (what should be default? <-- true)
//		ColumnMapRowMapper.setIgnoreNull(ignoreNull );
//		JsonStream.setIndentionStep(4);
////		JsonIterUnicodeSupport.enable();
//		JsonIterSqlTimestampSupport.enable("yyyy-MM-dd HH:mm:ss.SSSZ");
//		JsonIterPostgresSupport.enable();

	}

}
