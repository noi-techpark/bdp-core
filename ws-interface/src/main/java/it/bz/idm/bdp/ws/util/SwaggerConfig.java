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

import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
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
			return new ApiInfo(
	            "Open Data Hub Mobility API (deprecated)",
	            "<b>WARNING: This API is deprecated</b>. Please use our <a href=\"https://mobility.api.opendatahub.bz.it\">Open Data Hub Mobility API V2</a>.\n"
	            + "More information about the project: <a href=\"http://opendatahub.readthedocs.io/en/latest/index.html\">Tutorials and technical documentation</a>\n",
	            "V1",
	            "https://opendatahub.readthedocs.io/en/latest/licenses.html#apis-terms-of-service",
	            new Contact("Open Data Hub","https://opendatahub.bz.it","help@opendatahub.bz.it"),
	            "API License",
	            "https://opendatahub.readthedocs.io/en/latest/licenses.html",
	            Collections.emptyList()
	        );
	    }
	    @Bean
	    UiConfiguration uiConfig() {
	    	UiConfigurationBuilder builder = UiConfigurationBuilder.builder();
	    	builder.docExpansion(DocExpansion.LIST).operationsSorter(OperationsSorter.ALPHA);
	    	return builder.build();
	    }
}