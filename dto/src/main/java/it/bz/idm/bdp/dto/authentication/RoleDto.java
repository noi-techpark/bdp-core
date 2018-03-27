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
