package it.bz.idm.bdp.ws.util;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class SwaggerWebAdapter implements WebMvcConfigurer{
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		  registry.addResourceHandler("/**").addResourceLocations("classpath:/META-INF/resources/");
	}
	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
        registry.addRedirectViewController("/configuration/ui", "/swagger-resources/configuration/ui");
        registry.addRedirectViewController("/configuration/security", "/swagger-resources/configuration/security");
        registry.addRedirectViewController("", "/swagger-ui.html");
        registry.addRedirectViewController("/", "/swagger-ui.html");
	}
}
