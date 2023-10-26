// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.idm.bdp.writer.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

	@Value("${security.cors.allowedOrigins:*}")
	private String[] allowedOrigins;

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry
			.addMapping("/**")
			.allowedOriginPatterns(allowedOrigins)
			.allowedMethods(CorsConfiguration.ALL)
			.allowedHeaders(CorsConfiguration.ALL)
			.allowCredentials(true);
	}
	
	@Override
	public void configurePathMatch(PathMatchConfigurer configurer) {
		// Spring deprecated accepting trailing slashes: https://github.com/spring-projects/spring-framework/issues/28552
		// but are considering adding the feature back in: https://github.com/spring-projects/spring-framework/issues/31366
		// For the time being we use this deprecated setting to re-enable trailing slashes since our client libraries use them a lot.

		// In parallel, we're updating the client library (dc-interface) to not use trailing slashes anymore
		// So whoever finds this in the future: hopefully all clients have by now been updated so you can either remove this setting, 
		// or implement whichever solution the spring team comes up with in case you still have to support it
		configurer.setUseTrailingSlashMatch(true);
	}
}
