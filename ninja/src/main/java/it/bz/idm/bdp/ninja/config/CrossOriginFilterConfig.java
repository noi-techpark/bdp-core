package it.bz.idm.bdp.ninja.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class CrossOriginFilterConfig implements WebMvcConfigurer {

    @Value("${ninja.security.cors.allowed-origins:*}")
    private String allowedOrigins;

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry
			.addMapping("/**")
			.allowedOrigins(allowedOrigins)
			.allowedHeaders(CorsConfiguration.ALL)
			.allowedMethods("GET", "HEAD", "OPTIONS")
			.allowCredentials(true);
	}

}
