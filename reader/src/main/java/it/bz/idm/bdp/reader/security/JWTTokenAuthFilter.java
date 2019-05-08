/**
 * reader - Data Reader for the Big Data Platform, that queries the database for web-services
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
package it.bz.idm.bdp.reader.security;

import java.io.IOException;
import java.text.MessageFormat;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;


/**
 * Authentication filter for JWT tokens
 *
 * @author Patrick Bertolla
 */
public class JWTTokenAuthFilter extends AbstractAuthenticationProcessingFilter{

	private static final String TOKEN_PREFIX = "Bearer ";
	@Autowired
	public JwtUtil util;

	protected JWTTokenAuthFilter(String defaultFilterProcessesUrl) {
		super(defaultFilterProcessesUrl);
	}
	private static final String HEADER_SECURITY_TOKEN = HttpHeaders.AUTHORIZATION;



	@Override
	public Authentication attemptAuthentication(HttpServletRequest request,
			HttpServletResponse response) throws AuthenticationException,
			IOException, ServletException {
		String token = request.getHeader(HEADER_SECURITY_TOKEN);
		UserDetails user= null;
		Authentication authRequest = null;
		if(token == null || !token.startsWith(TOKEN_PREFIX)) {
			response.setHeader("X-Error", "No auth token!");
			throw new AuthenticationServiceException(MessageFormat.format("Error | {0}", "Auth token not found or invalid"));
		}else{
			String authToken = token.substring(TOKEN_PREFIX.length());
			user = util.parseToken(authToken);
			if (user==null){
				throw new JwtTokenMalformedException("Malformed token");
			}
			authRequest = new JWTAuthenticationToken(authToken,user.getUsername(),user.getPassword(),user.getAuthorities());
		}
		SecurityContextHolder.getContext().setAuthentication(authRequest);
        return getAuthenticationManager().authenticate(authRequest);
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request,
			HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {
		chain.doFilter(request, response);
	}
	@Override
	protected boolean requiresAuthentication(HttpServletRequest request,
			HttpServletResponse response) {
		return request.getHeader(HEADER_SECURITY_TOKEN) != null;
	}

}
