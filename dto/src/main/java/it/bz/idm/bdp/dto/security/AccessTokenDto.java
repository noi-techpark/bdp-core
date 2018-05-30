package it.bz.idm.bdp.dto.security;

import java.io.Serializable;

public class AccessTokenDto implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -698649583976751567L;
	private String token;
	private Long expireDate;
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public Long getExpireDate() {
		return expireDate;
	}
	public void setExpireDate(Long expireDate) {
		this.expireDate = expireDate;
	}
	
	
}
