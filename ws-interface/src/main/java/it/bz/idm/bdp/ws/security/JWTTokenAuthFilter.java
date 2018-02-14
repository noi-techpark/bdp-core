package it.bz.idm.bdp.ws.security;

import java.io.IOException;
import java.text.MessageFormat;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

public class JWTTokenAuthFilter extends AbstractAuthenticationProcessingFilter{

	@Autowired
	public JwtUtil util;
	
	protected JWTTokenAuthFilter(String defaultFilterProcessesUrl) {
		super(defaultFilterProcessesUrl);
	}
	private static final String HEADER_SECURITY_TOKEN = "Authorization";
	//private static final Integer TOKEN_EXPIRY_HOURS = 9490;



	@Override
	public Authentication attemptAuthentication(HttpServletRequest request,
			HttpServletResponse response) throws AuthenticationException,
			IOException, ServletException {
		String token = request.getHeader(HEADER_SECURITY_TOKEN);
		UserDetails user= null;
		Authentication authRequest = null;
		if(token == null || !token.startsWith("JWT ")) {
			response.setHeader("X-Error", "No auth token!");
			throw new AuthenticationServiceException(MessageFormat.format("Error | {0}", "Auth token not found or invalid"));
		}else{
			String authToken = token.substring(4);
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
