package it.bz.idm.bdp.reader2;

import java.util.Collections;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.web.context.request.RequestContextListener;

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
@SpringBootApplication
public class Reader2Rest extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(Reader2Rest.class, args);
	}

	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2)
				.select()
				.apis(RequestHandlerSelectors.basePackage("it.bz.idm.bdp.reader2"))
				.build()
				.apiInfo(apiInfo());
	}

	private ApiInfo apiInfo() {
		return new ApiInfo(
				"API v2",
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

	/*
	 * To start the application startup data loader to create tables and insert default
	 * values.
	 */
	@Bean
	public RequestContextListener requestContextListener() {
		return new RequestContextListener();
	}

}
