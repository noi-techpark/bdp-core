package it.bz.idm.bdp.ninja.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
public class NinjaConfig implements ApplicationListener<ContextRefreshedEvent> {

	@Autowired
    NamedParameterJdbcTemplate jdbcTemplate;

	@Value("${server.compression.enabled:true}")
	private boolean enableCompression4JSON;

	@Value("${ninja.query.timeout:-1}")
	private int queryTimeout;

    private boolean alreadySetup = false;

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		if (alreadySetup) {
            return;
        }

		boolean ignoreNull = true;
		SelectExpansion se = new SelectExpansionConfig().getSelectExpansion();

		/* Set the query builder, JDBC template's row mapper and JSON parser up */
		QueryBuilder.setup(se);
		jdbcTemplate.getJdbcTemplate().setQueryTimeout(queryTimeout);
		QueryExecutor.setup(jdbcTemplate);

		ColumnMapRowMapper.setIgnoreNull(ignoreNull);
		ColumnMapRowMapper.setTargetDefNameToAliasMap(se.getSchema().getTargetDefNameToAliasMap());

		if (!enableCompression4JSON) {
			JsonStream.setIndentionStep(4);
		}
		JsonIterSqlTimestampSupport.enable("yyyy-MM-dd HH:mm:ss.SSSZ");
		JsonIterPostgresSupport.enable();
	}

}

