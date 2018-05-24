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
import javax.persistence.TypedQuery;

import it.bz.idm.bdp.dal.util.JPAUtil;
import it.bz.idm.bdp.dto.authentication.UserDto;

@Entity
public class BDPUser {

	@Id
	@GeneratedValue(generator = "user_seq", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "user_seq", sequenceName = "user_seq", schema = "intime", allocationSize = 1)
	private Long id;
	private String firstName;
	private String lastName;
	@Column(unique = true, nullable = false)
	private String email;
	@Column(nullable = false)
	private String password;
	@Column(nullable = false)
	private boolean enabled;
	private boolean tokenExpired;

	@ManyToMany
	@JoinTable(name = "users_roles", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
	private List<BDPRole> roles;

	public BDPUser() {
	}

	public BDPUser(String email, String password) {
		this(null, null, email, password, true, false);
	}

	public BDPUser(String firstName, String lastName, String email, String password, boolean enabled,
			boolean tokenExpired) {
		this(firstName, lastName, email, password, enabled, tokenExpired, null);
	}

	public BDPUser(String firstName, String lastName, String email, String password, boolean enabled,
			boolean tokenExpired, List<BDPRole> roles) {
		this.firstName = firstName;
		this.lastName = lastName;
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

	public List<BDPRole> getRoles() {
		return roles;
	}

	public void setRoles(List<BDPRole> roles) {
		this.roles = roles;
	}

	public static BDPUser findByEmail(EntityManager manager, String email) {
		TypedQuery<BDPUser> query = manager.createQuery("SELECT u FROM BDPUser u where email = :email", BDPUser.class);
		query.setParameter("email", email);
		return JPAUtil.getSingleResultOrNull(query);
	}

	public static Object sync(EntityManager em, List<UserDto> data) {
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
		return null;
	}
}
