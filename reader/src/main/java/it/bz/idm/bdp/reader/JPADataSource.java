package it.bz.idm.bdp.reader;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;

import it.bz.idm.bdp.dal.util.JPAUtil;

public class JPADataSource {
	
	@Bean
	public DataSource getJpaDatasource() {
		return JPAUtil.emFactory.unwrap(DataSource.class);
	}

}
