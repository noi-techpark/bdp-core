/**
 * BDP data - Data Access Layer for the Big Data Platform
 *
 * Copyright © 2018 IDM Südtirol - Alto Adige (info@idm-suedtirol.com)
 * Copyright © 2019 NOI Techpark - Südtirol / Alto Adige (info@opendatahub.bz.it)
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
package it.bz.idm.bdp.dal.authentication;

import java.util.Collection;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.ColumnDefault;

import it.bz.idm.bdp.dal.util.QueryBuilder;
import it.bz.idm.bdp.dto.authentication.RoleDto;

/**
 * <p>
 * Each role has a set of permissions associated to multiple users.<br/>
 * A role can inherit all permissions from <strong>one parent role</strong>
 * </p>
 * @author Peter Moser
 *
 */
@Table(name = "bdprole",
	uniqueConstraints = {
			@UniqueConstraint(columnNames = {"name"})
			}
)
@Entity
public class BDPRole {

	public static final String ROLE_GUEST = "GUEST";
	public static final String ROLE_ADMIN = "ADMIN";

	@Id
	@GeneratedValue(generator = "bdprole_gen", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "bdprole_gen", sequenceName = "bdprole_seq", allocationSize = 1)
	@ColumnDefault(value = "nextval('bdprole_seq')")
	private Long id;

	@Column(nullable = false)
	private String name;

	private String description;

	@ManyToMany(mappedBy = "roles")
	private Collection<BDPUser> users;

	@ManyToOne
	private BDPRole parent;

	public BDPRole() {
	}

	public BDPRole(String name) {
		setName(name);
	}

	public BDPRole(String name, BDPRole parent) {
		this(name);
		setParent(parent);
	}

	public BDPRole(String name, String description) {
		setName(name);
		setDescription(description);
	}

	public BDPRole(String name, String description, BDPRole parent) {
		this(name, description);
		setParent(parent);
	}

	public static BDPRole fetchGuestRole(EntityManager manager) {
		return findByName(manager, ROLE_GUEST);
	}

	public static BDPRole fetchAdminRole(EntityManager manager) {
		return findByName(manager, ROLE_ADMIN);
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
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
	public Collection<BDPUser> getUsers() {
		return users;
	}

	public void setUsers(Collection<BDPUser> users) {
		this.users = users;
	}

	public BDPRole getParent() {
		return parent;
	}

	public void setParent(BDPRole parent) {
		this.parent = parent;
	}

	/**
	 * @param em entitymanager
	 * @param name unique identifier for a role
	 * @return a single role
	 */
	public static BDPRole findByName(EntityManager em, String name) {
		return QueryBuilder
				.init(em)
				.addSql("SELECT r FROM BDPRole r WHERE r.name = :name")
				.setParameter("name", name)
				.buildSingleResultOrNull(BDPRole.class);
	}

	/**
	 * TODO: fix return object, remove it or make it significant
	 * @param em entity manager
	 * @param data list of roles to upsert
	 * @return always null
	 */
	public static Object sync(EntityManager em, List<RoleDto> data) {
		em.getTransaction().begin();
		for (RoleDto dto : data) {
			BDPRole role = BDPRole.findByName(em, dto.getName());
			if (role == null) {
				role = new BDPRole(dto.getName(), dto.getDescription());
				em.persist(role);
			} else {
				if (dto.getDescription() != null)
					role.setDescription(dto.getDescription());
				em.merge(role);
			}
		}
		em.getTransaction().commit();
		return null;
	}
}
