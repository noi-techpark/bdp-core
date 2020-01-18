package it.bz.idm.bdp.ninja.config;

import it.bz.idm.bdp.ninja.utils.miniparser.Consumer;
import it.bz.idm.bdp.ninja.utils.miniparser.Token;
import it.bz.idm.bdp.ninja.utils.querybuilder.TargetList;
import it.bz.idm.bdp.ninja.utils.querybuilder.SelectExpansion;
import it.bz.idm.bdp.ninja.utils.querybuilder.Target;

public class SelectExpansionConfig {

	private SelectExpansion se;

	public SelectExpansionConfig() {
		super();

		se = new SelectExpansion();

		TargetList measurement = TargetList.init("measurement")
				.add(new Target("mvalidtime", "me.timestamp"))
				.add(new Target("mtransactiontime", "me.created_on"))
				.add(new Target("mperiod", "me.period"));

		se.add(measurement);

		TargetList measurementdouble = TargetList.init("measurementdouble")
				.add(new Target("mvalue_double", "me.double_value")
						.sqlAfter("null::character varying as mvalue_string").alias("mvalue"));

		se.add(measurementdouble);

		TargetList measurementstring = TargetList.init("measurementstring")
				.add(new Target("mvalue_string", "me.string_value")
						.sqlBefore("null::double precision as mvalue_double").alias("mvalue"));

		se.add(measurementstring);

		TargetList datatype = TargetList.init("datatype")
				.add(new Target("tname", "t.cname"))
				.add(new Target("tunit", "t.cunit"))
				.add(new Target("ttype", "t.rtype"))
				.add(new Target("tdescription", "t.description"))
				.add(new Target("tmeasurements", measurement));

		se.add(datatype);

		TargetList parent = TargetList.init("parent")
				.add(new Target("pname", "p.name"))
				.add(new Target("ptype", "p.stationtype"))
				.add(new Target("pcode", "p.stationcode"))
				.add(new Target("porigin", "p.origin"))
				.add(new Target("pactive", "p.active"))
				.add(new Target("pavailable", "p.available"))
				.add(new Target("pcoordinate", "p.pointprojection"))
				.add(new Target("pmetadata", "pm.json"));

		se.add(parent);

		TargetList station = TargetList.init("station")
				.add(new Target("sname", "s.name"))
				.add(new Target("stype", "s.stationtype"))
				.add(new Target("scode", "s.stationcode"))
				.add(new Target("sorigin", "s.origin"))
				.add(new Target("sactive", "s.active"))
				.add(new Target("savailable", "s.available"))
				.add(new Target("scoordinate", "s.pointprojection"))
				.add(new Target("smetadata", "m.json"))
				.add(new Target("sparent", parent))
				.add(new Target("sdatatypes", datatype));

		se.add(station);

		TargetList stationtype = TargetList.init("stationtype")
				.add(new Target("stations", station));

		se.add(stationtype);

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
