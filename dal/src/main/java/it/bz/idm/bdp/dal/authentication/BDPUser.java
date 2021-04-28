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

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.ColumnDefault;

import it.bz.idm.bdp.dal.util.QueryBuilder;
import it.bz.idm.bdp.dto.authentication.UserDto;

/**
 * User entity defining a person, that wants to access data. Each user has an
 * associated role, which defines the access to specific data sets.
 *
 * @author Peter Moser
 */
@Table(name = "bdpuser",
uniqueConstraints = {
		@UniqueConstraint(columnNames = {"email"})
		}
)
@Entity
public class BDPUser {

	@Id
	@GeneratedValue(generator = "bdpuser_gen", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "bdpuser_gen", sequenceName = "bdpuser_seq", allocationSize = 1)
	@ColumnDefault(value = "nextval('bdpuser_seq')")
	private Long id;

	@Column(nullable = false)
	private String email;

	@Column(nullable = false)
	private String password;

	@Column(nullable = false)
	@ColumnDefault(value = "true")
	private boolean enabled;

	// TODO: tokenExpired is deprecated and should be removed
	@Column(nullable = false)
	@ColumnDefault(value = "false")
	private boolean tokenExpired;

	@ManyToMany
	@JoinTable(name = "bdpusers_bdproles", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
	private List<BDPRole> roles;

	public BDPUser() {
		super();
	}

	/**
	 * @param email unique email for each user
	 * @param password JSON Web Token (JWT) string
	 */
	public BDPUser(String email, String password) {
		this(email, password, true, false);
	}

	/**
	 * @param email unique email for each user
	 * @param password JSON Web Token (JWT) string
	 * @param enabled if user is currently allowed to do anything
	 * @param tokenExpired if token is still valid
	 */
	public BDPUser(String email, String password, boolean enabled, boolean tokenExpired) {
		this(email, password, enabled, tokenExpired, null);
	}

	/**
	 * @param email unique email for each user
	 * @param password JSON Web Token (JWT) string
	 * @param enabled if user is currently allowed to do anything
	 * @param tokenExpired if token is still valid
	 * @param roles authentication roles associated to a single user, currently it's always one
	 */
	public BDPUser(String email, String password, boolean enabled, boolean tokenExpired, List<BDPRole> roles) {
		this.email = email;
		this.password = password;
		this.enabled = enabled;
		this.tokenExpired = tokenExpired;
		this.roles = roles;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public List<BDPRole> getRoles() {
		return roles;
	}

	public void setRoles(List<BDPRole> roles) {
		this.roles = roles;
	}

	/**
	 * Find a user by an unique email-address
	 *
	 * @param em entity manager
	 * @param email unique identifier for a user
	 * @return user identified by the email
	 */
	public static BDPUser findByEmail(EntityManager em, String email) {
		return QueryBuilder
				.init(em)
				.addSql("SELECT u FROM BDPUser u WHERE email = :email")
				.setParameter("email", email)
				.buildSingleResultOrNull(BDPUser.class);
	}

	/**
	 * Inserts or updates the list of existing users.
	 *
	 * @param em entity manager
	 * @param data list of users
	 */
	public static void sync(EntityManager em, List<UserDto> data) {
		em.getTransaction().begin();
		for (UserDto dto : data) {
			BDPUser user = BDPUser.findByEmail(em, dto.getEmail());
			if (user == null) {
				user = new BDPUser(dto.getEmail(), dto.getPassword());
				em.persist(user);
			} else {
				user.setPassword(dto.getPassword());
				em.merge(user);
			}
		}
		em.getTransaction().commit();
	}
}
