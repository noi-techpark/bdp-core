package it.bz.idm.bdp.reader2.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
@ComponentScan("it.bz.idm.bdp.reader2")
public class DatabaseConfig {

	@Bean
	DataSource dataSource() {
		HikariConfig config = new HikariConfig("/database.properties");
        HikariDataSource dataSource = new HikariDataSource(config);
		return dataSource;
	}
}