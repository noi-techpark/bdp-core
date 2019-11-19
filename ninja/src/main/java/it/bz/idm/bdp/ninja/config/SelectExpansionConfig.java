package it.bz.idm.bdp.ninja.config;

import it.bz.idm.bdp.ninja.utils.miniparser.Consumer;
import it.bz.idm.bdp.ninja.utils.miniparser.Token;
import it.bz.idm.bdp.ninja.utils.querybuilder.SelectExpansion;

public class SelectExpansionConfig {

	private SelectExpansion se;

	public SelectExpansionConfig() {
		super();

		se = new SelectExpansion();

		se.addColumn("measurement", "mvalidtime", "me.timestamp");
		se.addColumn("measurement", "mtransactiontime", "me.created_on");
		se.addColumn("measurement", "mperiod", "me.period");

		se.addColumn("measurementdouble", "mvalue_double", "me.double_value", null, "null::character varying as mvalue_string");
		se.addColumn("measurementstring", "mvalue_string", "me.string_value", "null::double precision as mvalue_double", null);

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

		/*
		 * Define where-clause items and their mappings to SQL. Some operators need
		 * checks of their values or list items. These can be defined with Lambda
		 * functions.
		 */

		/* Primitive operators */
		se.addOperator("NULL", "eq", "%c is %v");
		se.addOperator("NULL", "neq", "%c is not %v");

		se.addOperator("BOOLEAN", "eq", "%c = %v");
		se.addOperator("BOOLEAN", "neq", "%c <> %v");

		se.addOperator("NUMBER", "eq", "%c = %v");
		se.addOperator("NUMBER", "neq", "%c <> %v");
		se.addOperator("NUMBER", "lt", "%c < %v");
		se.addOperator("NUMBER", "gt", "%c > %v");
		se.addOperator("NUMBER", "lteq", "%c =< %v");
		se.addOperator("NUMBER", "gteq", "%c >= %v");

		se.addOperator("STRING", "eq", "%c = %v");
		se.addOperator("STRING", "neq", "%c <> %v");
		se.addOperator("STRING", "re", "%c ~ %v");
		se.addOperator("STRING", "ire", "%c ~* %v");
		se.addOperator("STRING", "nre", "%c !~ %v");
		se.addOperator("STRING", "nire", "%c !~* %v");

		/* JSON operators */
		se.addOperator("JSON/NULL", "eq", "%c#>'{%j}' is %v");
		se.addOperator("JSON/NULL", "neq", "%c#>'{%j}' is not %v");

		se.addOperator("JSON/BOOLEAN", "eq", "%c#>'{%j}' = %v");
		se.addOperator("JSON/BOOLEAN", "neq", "%c#>'{%j}' <> %v");

		se.addOperator("JSON/NUMBER", "eq", "(%c#>'{%j}')::double precision = %v");
		se.addOperator("JSON/NUMBER", "neq", "(%c#>'{%j}')::double precision <> %v");
		se.addOperator("JSON/NUMBER", "lt", "(%c#>'{%j}')::double precision < %v");
		se.addOperator("JSON/NUMBER", "gt", "(%c#>'{%j}')::double precision > %v");
		se.addOperator("JSON/NUMBER", "lteq", "(%c#>'{%j}')::double precision =< %v");
		se.addOperator("JSON/NUMBER", "gteq", "(%c#>'{%j}')::double precision >= %v");

		se.addOperator("JSON/STRING", "eq", "%c#>>'{%j}' = %v");
		se.addOperator("JSON/STRING", "neq", "%c#>>'{%j}' <> %v");
		se.addOperator("JSON/STRING", "re", "%c#>>'{%j}' ~ %v");
		se.addOperator("JSON/STRING", "ire", "%c#>>'{%j}' ~* %v");
		se.addOperator("JSON/STRING", "nre", "%c#>>'{%j}' !~ %v");
		se.addOperator("JSON/STRING", "nire", "%c#>>'{%j}' !~* %v");

		/* LIST operators */
		se.addOperator("LIST/NUMBER", "in", "%c in (%v)");
		se.addOperator("LIST/STRING", "in", "%c in (%v)");
		se.addOperator("LIST/NULL", "in", "%c in (%v)");

		se.addOperator("LIST/NUMBER", "nin", "%c not in (%v)");
		se.addOperator("LIST/STRING", "nin", "%c not in (%v)");
		se.addOperator("LIST/NULL", "nin", "%c not in (%v)");

		Consumer checkMakeEnvelope = new Consumer() {
			@Override
			public boolean middle(Token t) {
				return t.getChildCount() == 4 || t.getChildCount() == 5;
			}
		};

		se.addOperator("LIST/NUMBER", "bbi", "%c && ST_MakeEnvelope(%v)", checkMakeEnvelope);
		se.addOperator("LIST/NUMBER", "bbc", "%c @ ST_MakeEnvelope(%v)", checkMakeEnvelope);

		/* JSON/LIST operators */
		se.addOperator("JSON/LIST/STRING", "in", "%c#>>'{%j}' in (%v)");
		se.addOperator("JSON/LIST/NUMBER", "in", "(%c#>'{%j}')::double precision in (%v)");
		se.addOperator("JSON/LIST/NULL", "in", "%c#>'{%j}' in (%v)");
		se.addOperator("JSON/LIST/MIXED", "in", "%c#>'{%j}' in (%v)");

		se.addOperator("JSON/LIST/STRING", "nin", "%c#>>'{%j}' not in (%v)");
		se.addOperator("JSON/LIST/NUMBER", "nin", "(%c#>'{%j}')::double precision not in (%v)");
		se.addOperator("JSON/LIST/NULL", "nin", "%c#>'{%j}' not in (%v)");
		se.addOperator("JSON/LIST/MIXED", "nin", "%c#>'{%j}' not in (%v)");
	}

	public SelectExpansion getSelectExpansion() {
		return se;
	}

}
