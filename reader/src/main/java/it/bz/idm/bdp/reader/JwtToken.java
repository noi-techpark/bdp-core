package it.bz.idm.bdp.reader;

import java.io.Serializable;

import it.bz.idm.bdp.reader.security.AccessToken;

public class JwtToken implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4803002714159014982L;
	private AccessToken accessToken;
	private String refreshToken;
	public AccessToken getAccessToken() {
		return accessToken;
	}
	public void setAccessToken(AccessToken accessToken) {
		this.accessToken = accessToken;
	}
	public String getRefreshToken() {
		return refreshToken;
	}
	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
	

	
}
