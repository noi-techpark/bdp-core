package it.bz.idm.bdp.writer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import it.bz.idm.bdp.dal.util.JPAUtil;

@Configuration
@EnableScheduling
public class JobScheduler {
	
	@Value("classpath:META-INF/sql/opendatarules.sql")
	private Resource sql;
	
	@Scheduled(cron="0 0 * * * *")
	public void updateOpenData() throws Exception {
		JPAUtil.executeNativeQueries(sql.getInputStream());
	}

}
