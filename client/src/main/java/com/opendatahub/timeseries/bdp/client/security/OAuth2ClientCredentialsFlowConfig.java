// Copyright © 2018 IDM Südtirol - Alto Adige (info@idm-suedtirol.com)
// Copyright © 2019 NOI Techpark - Südtirol / Alto Adige (info@opendatahub.com)
//
// SPDX-License-Identifier: GPL-3.0-only

package com.opendatahub.timeseries.bdp.client.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class OAuth2ClientCredentialsFlowConfig {

	private Environment env;

	@Autowired
	public OAuth2ClientCredentialsFlowConfig(Environment env) {
		super();
		this.env=env;
	}

    @Bean("oauthWebClient")
    WebClient webClient(OAuth2AuthorizedClientManager authorizedClientManager) {
        ServletOAuth2AuthorizedClientExchangeFilterFunction oauth2Client =
                new ServletOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager);
        oauth2Client.setDefaultClientRegistrationId("odh");
        oauth2Client.setDefaultOAuth2AuthorizedClient(true);
        return WebClient.builder()
                .apply(oauth2Client.oauth2Configuration())
                .baseUrl(env.getProperty("BASE_URI"))
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Bean
    OAuth2AuthorizedClientManager authorizedClientManager(ClientRegistrationRepository clientRegistrationRepository,
                                                             OAuth2AuthorizedClientService clientService) {
        OAuth2AuthorizedClientProvider authorizedClientProvider =
                OAuth2AuthorizedClientProviderBuilder.builder()
                        .authorizationCode()
                        .refreshToken()
                        .clientCredentials()
                        .build();
        AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientManager = new AuthorizedClientServiceOAuth2AuthorizedClientManager(
                clientRegistrationRepository, clientService);
        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);
        return authorizedClientManager;
    }

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        List<ClientRegistration> clients = new ArrayList<ClientRegistration>();
        ClientRegistration.Builder builder = ClientRegistration.withRegistrationId("odh");
        builder
        .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
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
            ninjaBuilder.clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
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
    public OAuth2AuthorizedClientService authorizedClientService(@Autowired ClientRegistrationRepository repository) {
        return new InMemoryOAuth2AuthorizedClientService(repository);
    }
}
