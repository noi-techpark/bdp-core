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

import org.hibernate.annotations.ColumnDefault;

import it.bz.idm.bdp.dal.util.JPAUtil;
import it.bz.idm.bdp.dto.authentication.UserDto;

@Entity
public class BDPUser {

	@Id
	@GeneratedValue(generator = "bdpuser_gen", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "bdpuser_gen", sequenceName = "bdpuser_seq", schema = "intime", allocationSize = 1)
	@ColumnDefault(value = "nextval('bdpuser_seq')")
	private Long id;

	@Column(unique = true, nullable = false)
	private String email;

	@Column(nullable = false)
	private String password;

	@Column(nullable = false)
	@ColumnDefault(value = "true")
	private boolean enabled;

	@Column(nullable = false)
	@ColumnDefault(value = "false")
	private boolean tokenExpired;

	@ManyToMany
	@JoinTable(name = "bdpusers_bdproles", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
	private List<BDPRole> roles;

	public BDPUser() {
	}

	public BDPUser(String email, String password) {
		this(email, password, true, false);
	}

	public BDPUser(String email, String password, boolean enabled, boolean tokenExpired) {
		this(email, password, enabled, tokenExpired, null);
	}

	public BDPUser(String email, String password, boolean enabled,
			boolean tokenExpired, List<BDPRole> roles) {
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
