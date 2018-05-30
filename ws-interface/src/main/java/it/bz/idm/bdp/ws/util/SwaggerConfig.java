package it.bz.idm.bdp.ws.util;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.VendorExtension;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.DocExpansion;
import springfox.documentation.swagger.web.OperationsSorter;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger.web.UiConfigurationBuilder;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig{

	 @Bean
	    public Docket api(){
	        return new Docket(DocumentationType.SWAGGER_2)
	            .select()
	            .apis(RequestHandlerSelectors.any())
	            .paths(PathSelectors.any())
	            .build()
	            .apiInfo(apiInfo());
	    }

	    private ApiInfo apiInfo() {
		@SuppressWarnings("rawtypes")
		Collection<VendorExtension> extensions = new ArrayList<VendorExtension>();
			ApiInfo apiInfo = new ApiInfo(
	            "Big data platform REST API",
	            "This API contains all documentation about the big data platform ",
	            "",
	            "API TOS",
	            new Contact("","",""),
	            "API License",
	            "API License URL", extensions
	        );
	        return apiInfo;
	    }
	    @Bean
	    UiConfiguration uiConfig() {
	    	UiConfigurationBuilder builder = UiConfigurationBuilder.builder();
	    	builder.docExpansion(DocExpansion.LIST).operationsSorter(OperationsSorter.ALPHA);
	    	return builder.build();
	    }
}
