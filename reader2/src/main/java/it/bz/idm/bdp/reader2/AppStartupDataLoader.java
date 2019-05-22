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

		/* Set the query builder, JDBC template's row mapper and JSON parser up */
		QueryBuilder.setup(jdbcTemplate);

		// The API should have a flag to remove null values (what should be default? <-- true)
		ColumnMapRowMapper.setIgnoreNull(ignoreNull);
		JsonStream.setIndentionStep(4);
//		JsonIterUnicodeSupport.enable();
		JsonIterSqlTimestampSupport.enable("yyyy-MM-dd HH:mm:ss.SSSZ");
		JsonIterPostgresSupport.enable();
	}

}