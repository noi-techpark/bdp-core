package it.bz.idm.bdp.ninja.security;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;

@Component
public class JWTRequestFilter extends OncePerRequestFilter {

	@Value("${ninja.security.oauth2.pubkey}")
	private String oauthPubKey;

	@Value("${ninja.security.oauth2.issuer}")
	private String oauthIssuer;

	@Value("${ninja.security.oauth2.scopes}")
	private String oauthScopes;

	private static final String TOKEN_PREFIX = "Bearer ";
	private static final String HEADER_SECURITY_TOKEN = HttpHeaders.AUTHORIZATION;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {

		final String authHeader = request.getHeader(HEADER_SECURITY_TOKEN);

		/* No authentiaction header provided, just continue with the no-authentication flow */
		if (authHeader == null) {
			chain.doFilter(request, response);
			return;
		}

		/* Authentication given. Start evaluating header data */
		final String token = authHeader.replace(TOKEN_PREFIX, "");
		if (token == null || "".equals(token) || !authHeader.startsWith(TOKEN_PREFIX)) {
			/* We cannot handle these erros within a ControllerAdvice, since it is issued before a controller through
			 * AbstractSecurityInterceptor of Spring Security */
			response.setHeader("X-Error", "Invalid authentication token!");
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			throw new AuthenticationServiceException("Authentication token not found or invalid");
		}

		/* Parse public key to validate the JWT */
		RSAPublicKey publicKey = null;
		try {
			publicKey = (RSAPublicKey) SecurityUtils.getPublicKey(oauthPubKey);
		} catch (CertificateException e) {
			response.setHeader("X-Error", "Invalid public key to validate tokens!");
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			throw new AuthenticationServiceException("Invalid public key to validate authentication tokens");
		}

		/* Decode and verify the JWT */
		DecodedJWT jwt = null;
		try {
			Algorithm algorithm = Algorithm.RSA256(publicKey, null);

			/* Multiple scopes could be defined as comma-separated-list */
			JWTVerifier verifier = JWT.require(algorithm)
									  .withIssuer(oauthIssuer)
									  .withArrayClaim("scope", SecurityUtils.csvToList(oauthScopes)
																			.toArray(new String[0]))
									  .build();
			jwt = verifier.verify(token);
		} catch (JWTDecodeException | TokenExpiredException e) {
			response.setHeader("X-Error", e.getMessage());
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			throw new AuthenticationServiceException(e.getMessage());
		}

		/*
		 * Build an anonymous authentication token. This will be stored within the security
		 * context and can be retrieved inside each REST API call. The token must contain a
		 * principal, which is user-data containing an (anonymous) username and authorities
		 * (aka roles or privileges). In our case, this is for instance "ROLE_ADMIN" or any
		 * other role.
		 */
		String name = jwt.getClaim("username").asString();
		List<String> roles = jwt.getClaim("role").asList(String.class);
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>(roles.size());
		for (String role : roles) {
			authorities.add(new SimpleGrantedAuthority(role));
		}
		UserDetails principal = new JWTUser(name, authorities);
		Authentication authentication = new JWTAuthenticationToken(token, principal, authorities);
		SecurityContextHolder.getContext().setAuthentication(authentication);

		chain.doFilter(request, response);
	}

}
