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
				"Open Data Hub / Mobility - Consumer API",
				"Documentation and examples can be found on <a href=https://github.com/noi-techpark/bdp-core/blob/readerv2/reader2/README.md>readerv2.bdp-core.git</a>",
				"2.0",
				"",
				new Contact("Peter Moser", "https://opendatahub.bz.it", "p.moser@noi.bz.it"),
				"AGPL-3.0",
				"https://opensource.org/licenses/AGPL-3.0",
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
