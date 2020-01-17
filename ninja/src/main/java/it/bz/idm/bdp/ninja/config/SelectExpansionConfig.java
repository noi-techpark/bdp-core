package it.bz.idm.bdp.ninja.config;

import it.bz.idm.bdp.ninja.utils.miniparser.Consumer;
import it.bz.idm.bdp.ninja.utils.miniparser.Token;
import it.bz.idm.bdp.ninja.utils.querybuilder.SelectDefinition;
import it.bz.idm.bdp.ninja.utils.querybuilder.SelectExpansion;
import it.bz.idm.bdp.ninja.utils.querybuilder.TargetEntry;

public class SelectExpansionConfig {

	private SelectExpansion se;

	public SelectExpansionConfig() {
		super();

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
				.addTargetEntry(new TargetEntry("tname", "t.cname"))
				.addTargetEntry(new TargetEntry("tunit", "t.cunit"))
				.addTargetEntry(new TargetEntry("ttype", "t.rtype"))
				.addTargetEntry(new TargetEntry("tdescription", "t.description"))
				.addTargetEntry(new TargetEntry("tmeasurements", measurement));

		se.add(datatype);

		SelectDefinition parent = SelectDefinition.init("parent")
				.addTargetEntry(new TargetEntry("pname", "p.name"))
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
