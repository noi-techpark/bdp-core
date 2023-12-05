// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.idm.bdp.writer.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.session.NullAuthenticatedSessionStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

@Configuration
@EnableWebSecurity
public class WebSecurity {
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
	// Since we're basing our authorization on roles, we have to extend the spring security jwt converter to get that functionality.
	// see https://stackoverflow.com/questions/65518172/spring-security-cant-extract-roles-from-jwt for reference
	//
	// Note that this is pretty specific to our use case and only maps roles. If we ever need scope or other claims, 
	// implement that here (and make it a separate class implementing the Converter interface)
	@SuppressWarnings("unchecked")
	private Converter<Jwt, AbstractAuthenticationToken> jwtConverter(){
		JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
		jwtConverter.setJwtGrantedAuthoritiesConverter(jwt -> {
			// see org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter.java
			// This lambda functions as a replacement / reimplementation for that class
			Collection<String> roles = new ArrayList<>();
			Object roleClaim = jwt.getClaim("roles");
			if (roleClaim instanceof Collection){
				roles.addAll((Collection<String>) roleClaim);
			}

			return roles
				.stream()
				.map(role -> new SimpleGrantedAuthority(role))
				.collect(Collectors.toList());
		});
		return jwtConverter;
	}
	
	@Bean
	public SecurityFilterChain oauthFilter(HttpSecurity http) throws Exception {
		http
			.csrf(csrf -> csrf.disable())
			.authorizeHttpRequests(auth -> auth
				// health check always accessible
				.requestMatchers("/actuator/**")
					.permitAll()
				// Authorize based on role claim ROLE_ADMIN
				.requestMatchers("/json/**")
					.hasRole("ADMIN")
				// permitAll is ported over from legacy code, not sure if it doesn't make more sense to deny all other requests
				.anyRequest().permitAll())
			.oauth2Client(Customizer.withDefaults());
		// Register the oauth server, and our custom jwt converter
		http.oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtConverter())));
		return http.build();
	}
}
