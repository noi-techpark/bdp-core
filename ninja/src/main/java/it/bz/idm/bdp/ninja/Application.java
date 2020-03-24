package it.bz.idm.bdp.ninja;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.web.context.request.RequestContextListener;

@SpringBootApplication
public class Application extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	/*
	 * To start the application startup data loader to create tables and insert
	 * default values.
	 */
	@Bean
	public RequestContextListener requestContextListener() {
		return new RequestContextListener();
	}
}
