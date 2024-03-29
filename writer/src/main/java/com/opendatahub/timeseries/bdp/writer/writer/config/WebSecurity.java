// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package com.opendatahub.timeseries.bdp.writer.writer.config;

import static org.springframework.security.authorization.AuthenticatedAuthorizationManager.authenticated;
import static org.springframework.security.authorization.AuthorityAuthorizationManager.hasRole;
import static org.springframework.security.authorization.AuthorizationManagers.allOf;
import static org.springframework.security.authorization.AuthorizationManagers.anyOf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.keycloak.authorization.client.AuthzClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.authentication.session.NullAuthenticatedSessionStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

import com.opendatahub.timeseries.bdp.writer.writer.authz.Authorization;

@Configuration
@EnableWebSecurity
public class WebSecurity {

	@Value("${auth.client.id}")
	private String authClientId;

	private static final Logger log = LoggerFactory.getLogger(
			WebSecurity.class);

	/**
	 * Defines the session authentication strategy.
	 * For bearer-only applications there is no session needed and therefor
	 * we use the NullAuthenticatedSessionStrategy.
	 */
	@Bean
	public SessionAuthenticationStrategy sessionAuthenticationStrategy() {
		return new NullAuthenticatedSessionStrategy();
	}

	// For some reason, spring does not read the role claim from the jwt.
	// Since we're basing our authorization on roles, we have to extend the spring
	// security jwt converter to get that functionality.
	// see
	// https://stackoverflow.com/questions/65518172/spring-security-cant-extract-roles-from-jwt
	// for reference
	//
	// Note that this is pretty specific to our use case and only maps roles. If we
	// ever need scope or other claims,
	// implement that here (and make it a separate class implementing the Converter
	// interface)
	@SuppressWarnings("unchecked")
	private Converter<Jwt, AbstractAuthenticationToken> jwtConverter() {
		JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
		jwtConverter.setJwtGrantedAuthoritiesConverter(jwt -> {
			// see
			// org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter.java
			// This lambda functions as a replacement / reimplementation for that class
			Collection<String> roles = new ArrayList<>();
			Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
			if (resourceAccess != null) {
				Map<String, Object> resource = (Map<String, Object>) resourceAccess.get(authClientId);
				if (resource != null) {
					roles.addAll((Collection<String>) resource.get("roles"));
				}
			}

			if (roles.isEmpty()) {
				log.warn("OAuth client has no roles assigned.");
			}

			return roles
					.stream()
					.map(role -> new SimpleGrantedAuthority(role))
					.collect(Collectors.toList());
		});
		return jwtConverter;
	}
	
	// Must be authenticated, and either ADMIN or whatever else we want to check
	private static AuthorizationManager<RequestAuthorizationContext> adminOr(AuthorizationManager<RequestAuthorizationContext> f){
		return allOf(authenticated(), anyOf(hasRole("ADMIN"), f));
	}

	@Bean
	/** Main entry point authorization configuration for all endpoints */
	public SecurityFilterChain oauthFilter(HttpSecurity http) throws Exception {

		http.csrf(csrf -> csrf.disable());
		// Try this out to replace WebConfig cors configuration http.cors(cors -> cors.disable());

		http.authorizeHttpRequests(auth -> auth
				// health check always accessible
				.requestMatchers("/actuator/**").permitAll()
				// Databrowser station sync access
				.requestMatchers("/json/syncStations/{stationType}").access(adminOr(new UMAAuthorized()))
				// Authorize based on role claim ROLE_ADMIN
				.requestMatchers("/json/**").hasRole("ADMIN")
				// permitAll is ported over from legacy code, not sure if it doesn't make more
				// sense to deny all other requests
				.anyRequest().permitAll());

		// Configuration in application.properties
		http.oauth2Client(Customizer.withDefaults());

		// Register the oauth server, and our custom jwt converter
		http.oauth2ResourceServer(oauth2 -> oauth2
			.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtConverter())));

		return http.build();
	}

	@Autowired
	private ApplicationContext applicationContext;

	/** Authorization manager that checks resource acces via custom implementation of Keycloak UMA */
	private class UMAAuthorized implements AuthorizationManager<RequestAuthorizationContext> {
		@Override
		public AuthorizationDecision check(Supplier<Authentication> authenticationSupplier,
				RequestAuthorizationContext ctx) {
			log.debug("Beginning UMA check");
			
			var cred = (Jwt) authenticationSupplier.get().getCredentials();
			var authzClient = AuthzClient.create(applicationContext.getBean(org.keycloak.authorization.client.Configuration.class));
			Authorization authz = new Authorization(authzClient, cred.getTokenValue());
			
			if(!authz.hasAnyAuthorization()){
				return new AuthorizationDecision(false);
			}
			
			// delegate actual authorization decisions to endpoints
			ctx.getRequest().setAttribute(Authorization.ATTRIBUTE_AUTHORIZATION, authz);

			return new AuthorizationDecision(true);
		}
	}
	
	@Bean
	public org.keycloak.authorization.client.Configuration keycloakConfig(
		@Value("${authz.keycloak.authServerUrl}") String server,
		@Value("${authz.keycloak.realm}") String realm,
		@Value("${authz.keycloak.clientId}") String clientId,
		@Value("${authz.keycloak.clientSecret}") String clientSecret
	) {
		return new org.keycloak.authorization.client.Configuration(server, realm, clientId, Map.of("secret", clientSecret), null);
	}

}
