// Copyright © 2018 IDM Südtirol - Alto Adige (info@idm-suedtirol.com)
// Copyright © 2019 NOI Techpark - Südtirol / Alto Adige (info@opendatahub.bz.it)
//
// SPDX-License-Identifier: GPL-3.0-only

package it.bz.idm.bdp.dto.authentication;

import java.io.Serializable;

/**
 * Data transfer object identifying a user which needs access to the database
 *
 * @author Peter Moser
 */
public class UserDto implements Serializable {
	private static final long serialVersionUID = -151039076520008111L;
	private String firstName;
	private String lastName;
	private String email;
	private String password;
	private boolean enabled;
	private boolean tokenExpired;

	public UserDto() {
		super();
	}

	/**
	 * @param firstName it's exactly what your thinking of
	 * @param lastName see firstName
	 * @param email a valid email-address
	 * @param password as plain text
	 * @param enabled if user is not enabled he can't do anything
	 * @param tokenExpired currently the refresh token does not expire, so no worry
	 */
	public UserDto(String firstName, String lastName, String email, String password, boolean enabled,
			boolean tokenExpired) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.password = password;
		this.enabled = enabled;
		this.tokenExpired = tokenExpired;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isTokenExpired() {
		return tokenExpired;
	}

	public void setTokenExpired(boolean tokenExpired) {
		this.tokenExpired = tokenExpired;
	}
}
