// Copyright © 2018 IDM Südtirol - Alto Adige (info@idm-suedtirol.com)
// Copyright © 2019 NOI Techpark - Südtirol / Alto Adige (info@opendatahub.com)
//
// SPDX-License-Identifier: GPL-3.0-only

package com.opendatahub.timeseries.bdp.dto.dto.security;

import java.io.Serializable;

/**
 * JSON web token dto which contains access and refresh tokens
 *
 * @author Patrick Bertolla
 */
public class JwtTokenDto implements Serializable {

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
