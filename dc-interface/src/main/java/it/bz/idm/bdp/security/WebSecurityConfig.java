/**
 * dc-interface - Data Collector Interface for the Big Data Platform
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
package it.bz.idm.bdp.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;

@EnableWebSecurity
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter{

    private Environment env;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().oauth2Client().clientRegistrationRepository(clientRegistrationRepository());
    }
    @Autowired
    public WebSecurityConfig(Environment env) {
        super();
        this.env= env;
    }

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        List<ClientRegistration> clients = new ArrayList<ClientRegistration>();
        ClientRegistration.Builder builder = ClientRegistration.withRegistrationId("odh");
        builder
        .clientAuthenticationMethod(ClientAuthenticationMethod.POST)
        .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
        .scope(env.getProperty("scope"))
        .authorizationUri(env.getProperty("authorizationUri"))
        .tokenUri(env.getProperty("tokenUri"))
        .clientName(env.getProperty("clientName"))
        .clientId(env.getProperty("clientId"))
        .clientSecret(env.getProperty("clientSecret"));
        clients.add(builder.build());
        String ninjaClient = env.getProperty("NINJA_CLIENT");
        String ninjaSecret= env.getProperty("NINJA_SECRET");
        if (ninjaClient != null && !ninjaClient.isEmpty() && ninjaSecret != null && !ninjaSecret.isEmpty()) {
            ClientRegistration.Builder ninjaBuilder = ClientRegistration.withRegistrationId("ninja");
            ninjaBuilder.clientAuthenticationMethod(ClientAuthenticationMethod.POST)
            .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
            .scope(env.getProperty("scope"))
            .authorizationUri(env.getProperty("authorizationUri"))
            .tokenUri(env.getProperty("tokenUri"))
            .clientName(ninjaClient)
            .clientId(ninjaClient)
            .clientSecret(ninjaSecret);
            clients.add(ninjaBuilder.build());
        }
        return new InMemoryClientRegistrationRepository(clients);
    }
    @Bean
    public OAuth2AuthorizedClientService authorizedClientService() {
        return new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository());
    }
}