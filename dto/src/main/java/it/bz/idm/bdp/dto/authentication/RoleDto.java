// Copyright © 2018 IDM Südtirol - Alto Adige (info@idm-suedtirol.com)
// Copyright © 2019 NOI Techpark - Südtirol / Alto Adige (info@opendatahub.bz.it)
//
// SPDX-License-Identifier: GPL-3.0-only

package it.bz.idm.bdp.dto.authentication;

import java.io.Serializable;
import java.util.Collection;

/**
 * Data transfer object representing the role which can be associated to multiple users and can have multiple permissions
 *
 * @author Peter Moser
 */
public class RoleDto implements Serializable {
	private static final long serialVersionUID = 8239304102791965397L;
	private String name;
	private String description;
	private Collection<UserDto> users;

	public RoleDto() {
	}

	/**
	 * @param name unique name
	 * @param description what is the main purpose of this role
	 */
	public RoleDto(String name, String description) {
		this.name = name;
		this.description = description;
		this.users = null;
	}

	/**
	 * @param name unique name
	 * @param description what is the main purpose of this role
	 * @param users
	 */
	public RoleDto(String name, String description, Collection<UserDto> users) {
		this.name = name;
		this.description = description;
		this.users = users;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Collection<UserDto> getUsers() {
		return users;
	}

	public void setUsers(Collection<UserDto> users) {
		this.users = users;
	}

}
