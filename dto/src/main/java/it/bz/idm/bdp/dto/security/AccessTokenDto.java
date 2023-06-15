// Copyright © 2018 IDM Südtirol - Alto Adige (info@idm-suedtirol.com)
// Copyright © 2019 NOI Techpark - Südtirol / Alto Adige (info@opendatahub.com)
//
// SPDX-License-Identifier: GPL-3.0-only

package it.bz.idm.bdp.dto.security;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;

/**
 * access token dto which grants time limited access the the API
 *
 * @author Patrick Bertolla
 */
public class AccessTokenDto implements Serializable{

	private static final long serialVersionUID = -698649583976751567L;

	@ApiModelProperty (notes = "The token to be used in API calls needing authentication.")
	private String token;

	@ApiModelProperty (notes = "The limit of validity of the token.")
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
