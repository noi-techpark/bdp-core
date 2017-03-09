package it.bz.idm.bdp.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtUtil {
	@Value("${jwt.secret}")
	private String secret;

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
			UserDetails u = new JwtUser(name, authorities);
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
	 * @return the JWT token
	 */
	public String generateToken(UserDetails userDetails) {
		Claims claims = Jwts.claims().setSubject(userDetails.getUsername());
		String roles =""; 
		for (GrantedAuthority auth: userDetails.getAuthorities()){
			if (!roles.isEmpty())
				roles+=",";
			roles+=auth.getAuthority();
		}
		claims.put("roles", roles);
		return Jwts.builder()
				.setClaims(claims)
				.signWith(SignatureAlgorithm.HS512, secret)
				.compact();
	}
}
