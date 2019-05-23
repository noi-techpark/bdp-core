package it.bz.idm.bdp.reader2;

import java.util.HashMap;
import java.util.Map;

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

		boolean ignoreNull = true;

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


		/* Set the query builder, JDBC template's row mapper and JSON parser up */
		QueryBuilder.setup(jdbcTemplate, se);

		// The API should have a flag to remove null values (what should be default? <-- true)
		ColumnMapRowMapper.setIgnoreNull(ignoreNull);
		JsonStream.setIndentionStep(4);
//		JsonIterUnicodeSupport.enable();
		JsonIterSqlTimestampSupport.enable("yyyy-MM-dd HH:mm:ss.SSSZ");
		JsonIterPostgresSupport.enable();
	}

}