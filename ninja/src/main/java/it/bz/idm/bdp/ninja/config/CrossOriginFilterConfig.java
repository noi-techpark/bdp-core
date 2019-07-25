package it.bz.idm.bdp.ninja.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CrossOriginFilterConfig implements WebMvcConfigurer {
	private static final Logger log = LoggerFactory.getLogger(CrossOriginFilterConfig.class);

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		log.info("Applying CORS filter");
		registry
			.addMapping("/**")
			.allowedOrigins("*")
			.allowedMethods("GET");
	}

}
