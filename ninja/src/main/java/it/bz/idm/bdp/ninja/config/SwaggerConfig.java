package it.bz.idm.bdp.ninja.config;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Value;
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

	@Value("${ninja.swagger.readme-url}")
	private String readmeURL;


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
				"Open Data Hub Mobility API",
	            "More information about the project: <a href=\"" + readmeURL + "\">Tutorials and technical documentation</a>\n",
				"V2",
				"https://opendatahub.readthedocs.io/en/latest/licenses.html#apis-terms-of-service",
				new Contact("Open Data Hub","https://opendatahub.bz.it","help@opendatahub.bz.it"),
				"API License",
				"https://opendatahub.readthedocs.io/en/latest/licenses.html",
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
