package it.bz.idm.bdp.ninja.config;

import it.bz.idm.bdp.ninja.utils.miniparser.Consumer;
import it.bz.idm.bdp.ninja.utils.miniparser.Token;
import it.bz.idm.bdp.ninja.utils.querybuilder.TargetDefList;
import it.bz.idm.bdp.ninja.utils.querybuilder.Schema;
import it.bz.idm.bdp.ninja.utils.querybuilder.SelectExpansion;
import it.bz.idm.bdp.ninja.utils.querybuilder.TargetDef;

public class SelectExpansionConfig {

	private SelectExpansion se;

	public SelectExpansionConfig() {
		super();

		Schema schema = new Schema();

		TargetDefList measurement = TargetDefList
			.init("measurement")
			.add(new TargetDef("mvalidtime", "me.timestamp"))
			.add(new TargetDef("mtransactiontime", "me.created_on"))
			.add(new TargetDef("mperiod", "me.period"));

		schema.add(measurement);

		TargetDefList measurementdouble = TargetDefList
			.init("measurementdouble")
			.add(new TargetDef("mvalue_double", "me.double_value")
				.sqlAfter("null::character varying as mvalue_string")
				.alias("mvalue"));

		schema.add(measurementdouble);

		TargetDefList measurementstring = TargetDefList
			.init("measurementstring")
			.add(new TargetDef("mvalue_string", "me.string_value")
				.sqlBefore("null::double precision as mvalue_double")
				.alias("mvalue"));

		schema.add(measurementstring);

		TargetDefList datatype = TargetDefList
			.init("datatype")
			.add(new TargetDef("tname", "t.cname"))
			.add(new TargetDef("tunit", "t.cunit"))
			.add(new TargetDef("ttype", "t.rtype"))
			.add(new TargetDef("tdescription", "t.description"))
			.add(new TargetDef("tmetadata", "tm.json"))
			.add(new TargetDef("tmeasurements", measurement));

		schema.add(datatype);

		TargetDefList parent = TargetDefList
			.init("parent")
			.add(new TargetDef("pname", "p.name"))
			.add(new TargetDef("ptype", "p.stationtype"))
			.add(new TargetDef("pcode", "p.stationcode"))
			.add(new TargetDef("porigin", "p.origin"))
			.add(new TargetDef("pactive", "p.active"))
			.add(new TargetDef("pavailable", "p.available"))
			.add(new TargetDef("pcoordinate", "p.pointprojection"))
			.add(new TargetDef("pmetadata", "pm.json"));

		schema.add(parent);

		TargetDefList station = TargetDefList
			.init("station")
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

		TargetDefList stationtype = TargetDefList
			.init("stationtype")
			.add(new TargetDef("stations", station));

		schema.add(stationtype);

		se = new SelectExpansion();
		se.setSchema(schema);

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
