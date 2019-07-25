package it.bz.idm.bdp.ninja.config;

import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.DocExpansion;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger.web.UiConfigurationBuilder;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@Configuration
public class SwaggerConfig {

	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2)
				.select()
				.apis(RequestHandlerSelectors.basePackage("it.bz.idm.bdp.ninja"))
				.build()
				.apiInfo(apiInfo());
	}

	private ApiInfo apiInfo() {
		return new ApiInfo(
				"Big Data Platform REST API",
				"This page contains the documentation about the API REST calls of the Big Data Platform, the core component of the ODH Project.\n"
	            + "More information about the project in its homepage: http://opendatahub.bz.it/ \n"
	            + "Tutorials and technical documentation can be found at https://github.com/noi-techpark/bdp-core/blob/master/ninja/README.md",
				"v2",
				"http://opendatahub.readthedocs.io/en/latest/licenses.html#apis-terms-of-service",
				new Contact("","",""),
				"API License",
	            "http://opendatahub.readthedocs.io/en/latest/licenses.html",
				Collections.emptyList());
	}

	/*
	 * Expand the first controller inside the Swagger web-interface
	 */
	@Bean
	public UiConfiguration uiConfig() {
		return UiConfigurationBuilder
				.builder()
				.docExpansion(DocExpansion.LIST)
				.build();
	}
}
