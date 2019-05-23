package it.bz.idm.bdp.reader2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import com.jsoniter.output.JsonStream;

import it.bz.idm.bdp.reader2.utils.ColumnMapRowMapper;
import it.bz.idm.bdp.reader2.utils.JsonIterPostgresSupport;
import it.bz.idm.bdp.reader2.utils.JsonIterSqlTimestampSupport;
import it.bz.idm.bdp.reader2.utils.QueryBuilder;
import it.bz.idm.bdp.reader2.utils.QueryExecutor;
import it.bz.idm.bdp.reader2.utils.SelectExpansion;


@Component
public class AppStartupDataLoader implements ApplicationListener<ContextRefreshedEvent> {

	@Autowired
    NamedParameterJdbcTemplate jdbcTemplate;

    private boolean alreadySetup = false;

	@Override
	public void onApplicationEvent(ContextRefreshedEvent arg0) {
		if (alreadySetup) {
            return;
        }

		boolean ignoreNull = false;

		SelectExpansion se = new SelectExpansion();

		se.addExpansion("station", "sname", "s.name");
		se.addExpansion("station", "stype", "s.stationtype");
		se.addExpansion("station", "scode", "s.stationcode");
		se.addExpansion("station", "sorigin", "s.origin");
		se.addExpansion("station", "scoordinate", "s.pointprojection");
		se.addExpansion("station", "smetadata", "m.json");
		se.addSubExpansion("station", "sparent", "parent");
		se.addSubExpansion("station", "sdatatypes", "datatype");

		se.addExpansion("parent", "pname", "p.name");
		se.addExpansion("parent", "ptype", "p.stationtype");
		se.addExpansion("parent", "pcoordinate", "p.pointprojection");
		se.addExpansion("parent", "pcode", "p.stationcode");
		se.addExpansion("parent", "porigin", "p.origin");

		se.addExpansion("datatype", "tname", "t.cname");
		se.addExpansion("datatype", "tunit", "t.cunit");
		se.addExpansion("datatype", "ttype", "t.rtype");
		se.addExpansion("datatype", "tdescription", "t.description");
		se.addSubExpansion("datatype", "tlastmeasurement", "measurement");

		se.addExpansion("measurement", "mvalidtime", "me.timestamp");
		se.addExpansion("measurement", "mtransactiontime", "me.created_on");
		se.addExpansion("measurement", "mperiod", "me.period");
		se.addExpansion("measurement", "mvalue", "me.double_value");

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

