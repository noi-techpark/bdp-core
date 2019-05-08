/**
 * ws-interface - Web Service Interface for the Big Data Platform
 *
 * Copyright © 2018 IDM Südtirol - Alto Adige (info@idm-suedtirol.com)
 * Copyright © 2019 NOI Techpark - Südtirol / Alto Adige (info@opendatahub.bz.it)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program (see LICENSES/GPL-3.0.txt). If not, see
 * <http://www.gnu.org/licenses/>.
 *
 * SPDX-License-Identifier: GPL-3.0
 */
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

/**
 * TODO Please, describe it!
 *
 * @author Patrick Bertolla
 */
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
	            "Big Data Platform REST API",
	            "This page contains the documentation about the API REST calls of the Big Data Platform, the core component of the ODH Project.\n"
	            + "More information about the project in its homepage: http://opendatahub.bz.it/ \n"
	            + "Tutorials and technical documentation can be found at http://opendatahub.readthedocs.io/en/latest/index.html\n\n",
	            "v1",
	            "http://opendatahub.readthedocs.io/en/latest/licenses.html#apis-terms-of-service",
	            new Contact("","",""),
	            "API License",
	            "http://opendatahub.readthedocs.io/en/latest/licenses.html", extensions
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