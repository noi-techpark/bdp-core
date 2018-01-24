package it.bz.idm.bdp.ws.security;

import java.util.Collection;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class JWTAuthenticationToken extends UsernamePasswordAuthenticationToken{
	private String token;

	/**
	 * 
	 */
	private static final long serialVersionUID = 2838491731803574279L;


	
	
	public JWTAuthenticationToken(String token,String name,String credentials, Collection<? extends GrantedAuthority> authorities) {
		super(name, credentials,authorities);
		this.token = token;
	}

	public String getToken() {
		return token;
	}

}
