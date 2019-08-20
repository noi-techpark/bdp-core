package it.bz.idm.bdp.ninja.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import it.bz.idm.bdp.ninja.security.JWTRequestFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class ResourceServerConfig extends WebSecurityConfigurerAdapter {

	/*
	 * We need to autowire this filter, because we want to use @Value inside,
	 * hence Spring has to take care of instantiation.
	 */
	@Autowired
	JWTRequestFilter jwtRequestFilter;

	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.cors()
			.and().csrf().disable()
			.authorizeRequests()
			.antMatchers("/**")
			.permitAll()
			.and()
			.addFilterAfter(jwtRequestFilter, BasicAuthenticationFilter.class)
			;
	}

}
