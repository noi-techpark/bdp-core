package it.bz.idm.bdp.ninja.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import com.jsoniter.output.JsonStream;

import it.bz.idm.bdp.ninja.utils.jsonserializer.JsonIterPostgresSupport;
import it.bz.idm.bdp.ninja.utils.jsonserializer.JsonIterSqlTimestampSupport;
import it.bz.idm.bdp.ninja.utils.querybuilder.QueryBuilder;
import it.bz.idm.bdp.ninja.utils.querybuilder.SelectExpansion;
import it.bz.idm.bdp.ninja.utils.queryexecutor.ColumnMapRowMapper;
import it.bz.idm.bdp.ninja.utils.queryexecutor.QueryExecutor;


@Component
public class SelectExpansionConfig implements ApplicationListener<ContextRefreshedEvent> {

	@Autowired
    NamedParameterJdbcTemplate jdbcTemplate;

    private boolean alreadySetup = false;

	@Override
	public void onApplicationEvent(ContextRefreshedEvent arg0) {
		if (alreadySetup) {
            return;
        }

		boolean ignoreNull = true;

		SelectExpansion se = new SelectExpansion();

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
		se.addOperator("VALUE", "eq", "= %s");
		se.addOperator("VALUE", "neq", "<> %s");
		se.addOperator("NULL", "eq", "is %s");
		se.addOperator("NULL", "neq", "is not %s");
		se.addOperator("VALUE", "lt", "< %s");
		se.addOperator("VALUE", "gt", "> %s");
		se.addOperator("VALUE", "lteq", "=< %s");
		se.addOperator("VALUE", "gteq", ">= %s");
		se.addOperator("VALUE", "re", "~ %s");
		se.addOperator("VALUE", "ire", "~* %s");
		se.addOperator("VALUE", "nre", "!~ %s");
		se.addOperator("VALUE", "nire", "!~* %s");
		se.addOperator("LIST", "in", "in (%s)", t -> {
			return !(t.getChildCount() == 1 && t.getChild("VALUE").getValue() == null);
		});
		se.addOperator("LIST", "bbi", "&& ST_MakeEnvelope(%s)", t -> {
			return t.getChildCount() == 4 || t.getChildCount() == 5;
		});
		se.addOperator("LIST", "bbc", "@ ST_MakeEnvelope(%s)", t -> {
			return t.getChildCount() == 4 || t.getChildCount() == 5;
		});

		/* Set the query builder, JDBC template's row mapper and JSON parser up */
		QueryBuilder.setup(se);
		QueryExecutor.setup(jdbcTemplate);

		// The API should have a flag to remove null values (what should be default? <-- true)
		ColumnMapRowMapper.setIgnoreNull(ignoreNull);
		JsonStream.setIndentionStep(4);
//		JsonIterUnicodeSupport.enable();
		JsonIterSqlTimestampSupport.enable("yyyy-MM-dd HH:mm:ss.SSSZ");
		JsonIterPostgresSupport.enable();
	}

}

