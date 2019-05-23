package it.bz.idm.bdp.reader2;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
@ComponentScan("it.bz.opendatahub")
@PropertySource("classpath:database.properties")
public class Reader2Config {

	@Autowired
	Environment environment;

	private final String URL = "url";
	private final String USER = "dbuser";
	private final String DRIVER = "driver";
	private final String PASSWORD = "dbpassword";

	@Bean
	DataSource dataSource() {
		DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource();
		driverManagerDataSource.setUrl(environment.getProperty(URL));
		driverManagerDataSource.setUsername(environment.getProperty(USER));
		driverManagerDataSource.setPassword(environment.getProperty(PASSWORD));
		driverManagerDataSource.setDriverClassName(environment.getProperty(DRIVER));

		HikariConfig config = new HikariConfig("/hikari.properties");
        HikariDataSource dataSource = new HikariDataSource(config);

//		driverClassName=com.mysql.jdbc.Driver
//				jdbcUrl=jdbc:mysql://localhost:3306/testdb?useSSL=false
//				maximumPoolSize=20
//				username=testuser
//				password=test623
//				dataSource.cachePrepStmts=true
//				dataSource.prepStmtCacheSize=250
//				dataSource.prepStmtCacheSqlLimit=2048


		Connection c;
		try {
			c = driverManagerDataSource.getConnection();
			((org.postgresql.PGConnection)c).addDataType("geometry", org.postgis.PGgeometry.class);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

//		Connection c = npjt.getJdbcTemplate().getDataSource().getConnection();

//		return driverManagerDataSource;
		return dataSource;
	}
}