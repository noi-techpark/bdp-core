/**
 * reader - Data Reader for the Big Data Platform, that queries the database for web-services
 * Copyright © 2018 OpenDataHub (info@opendatahub.bz.it)
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
 * along with this program (see LICENSE/GPLv3). If not, see
 * <http://www.gnu.org/licenses/>.
 */
package it.bz.idm.bdp.reader.security;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import it.bz.idm.bdp.dto.security.AccessTokenDto;
import it.bz.idm.bdp.dto.security.JwtTokenDto;

@Component
public class JwtUtil {
	private static final SignatureAlgorithm TOKEN_SIGNATURE_ALGOITHM = SignatureAlgorithm.HS512;

	@Value("${jwt.secret}")
	private String secret;
	
	@Value("${jwt.accessTokenValidityInMinutes}")
	private Integer accessTokenValidityInMinutes;

	/**
	 * Tries to parse specified String as a JWT token. If successful, returns User object with username, id and role prefilled (extracted from token).
	 * If unsuccessful (token is invalid or not containing all required user properties), simply returns null.
	 * 
	 * @param token the JWT token to parse
	 * @return 
	 * @return the User object extracted from specified token or null if a token is invalid.
	 */
	public UserDetails parseToken(String token) {
		try {
			Claims body = Jwts.parser()
					.setSigningKey(secret)
					.parseClaimsJws(token)
					.getBody();

			String name = body.getSubject();
			String authoritiesString = (String) body.get("roles");
			List<GrantedAuthority> authorities = AuthorityUtils.commaSeparatedStringToAuthorityList(authoritiesString) ;
			UserDetails u = new JwtUser(name,authorities);
			return u;

		} catch (JwtException | ClassCastException e) {
			return null;
		}
	}

	/**
	 * Generates a JWT token containing username as subject, and userId and role as additional claims. These properties are taken from the specified
	 * User object. Tokens validity is infinite.
	 * 
	 * @param userDetails the user for which the token will be generated
	 * @return the JWT token object containing AccessToken and RefreshToken
	 */
	public JwtTokenDto generateToken(UserDetails userDetails) {
		JwtTokenDto token = new JwtTokenDto();
		Claims claims = Jwts.claims().setSubject(userDetails.getUsername());
		String roles =""; 
		for (GrantedAuthority auth: userDetails.getAuthorities()){
			if (!roles.isEmpty())
				roles+=",";
			roles+=auth.getAuthority();
		}
		claims.put("roles", roles);
		String refreshToken = Jwts.builder()
				.setClaims(claims)
				.signWith(TOKEN_SIGNATURE_ALGOITHM, secret)
				.compact();
		token.setRefreshToken(refreshToken);
		token.setAccessToken(getAccessToken(userDetails.getUsername(),userDetails.getAuthorities()));
		return token;
	}

	public AccessTokenDto generateAccessToken(UsernamePasswordAuthenticationToken principal) {
		AccessTokenDto accessToken = getAccessToken(principal.getName(),principal.getAuthorities());
		return accessToken;
	}

	private AccessTokenDto getAccessToken(String name, Collection<? extends GrantedAuthority> collection) {
		Date nowPlusMinutes= Timestamp.valueOf(LocalDateTime.now().plusMinutes(accessTokenValidityInMinutes));
		Claims claims = Jwts.claims().setSubject(name).setExpiration(nowPlusMinutes);
		String roles =""; 
		for (GrantedAuthority auth: collection){
			if (!roles.isEmpty())
				roles+=",";
			roles+=auth.getAuthority();
		}
		claims.put("roles", roles);
		String token = Jwts.builder()
				.setClaims(claims)
				.signWith(TOKEN_SIGNATURE_ALGOITHM, secret)
				.compact();
		AccessTokenDto accessToken = new AccessTokenDto();
		accessToken.setToken(token);
		accessToken.setExpireDate(nowPlusMinutes.getTime());
		return accessToken;
	}
}
