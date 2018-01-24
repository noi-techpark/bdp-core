package it.bz.idm.bdp.ws.security;

import org.springframework.security.authentication.AuthenticationServiceException;

public class JwtTokenMalformedException extends AuthenticationServiceException {

	public JwtTokenMalformedException(String msg) {
		super(msg);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
