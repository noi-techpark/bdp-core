/**
 * dto - Data Transport Objects for an object-relational mapping
 * Copyright © 2018 OpenDataHub (info@opendatahub.bz.it)
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
 * along with this program (see LICENSE/GPLv3). If not, see
 * <http://www.gnu.org/licenses/>.
 */
package it.bz.idm.bdp.dto.authentication;

import java.io.Serializable;
import java.util.Collection;

public class RoleDto implements Serializable {
	private static final long serialVersionUID = 8239304102791965397L;
	private String name;
	private String description;
	private Collection<UserDto> users;

	public RoleDto() {
	}

	public RoleDto(String name, String description) {
		this.name = name;
		this.description = description;
		this.users = null;
	}

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
