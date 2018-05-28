package it.bz.idm.bdp.dto.security;

import java.io.Serializable;

public class JwtTokenDto implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4803002714159014982L;
	private AccessTokenDto accessToken;
	private String refreshToken;
	public AccessTokenDto getAccessToken() {
		return accessToken;
	}
	public void setAccessToken(AccessTokenDto accessToken) {
		this.accessToken = accessToken;
	}
	public String getRefreshToken() {
		return refreshToken;
	}
	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
	

	
}
