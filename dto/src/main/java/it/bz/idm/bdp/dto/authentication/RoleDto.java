/**
 * dto - Data Transport Objects for an object-relational mapping
 * Copyright © 2018 IDM Südtirol - Alto Adige (info@idm-suedtirol.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program (see LICENSES/GPL-3.0.txt). If not, see
 * <http://www.gnu.org/licenses/>.
 *
 * SPDX-License-Identifier: GPL-3.0
 */
package it.bz.idm.bdp.dto.authentication;

import java.io.Serializable;
import java.util.Collection;

/**
 * Data transfer object representing the role which can be associated to multiple users and can have multiple permissions
 * @author Peter Moser
 *
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
