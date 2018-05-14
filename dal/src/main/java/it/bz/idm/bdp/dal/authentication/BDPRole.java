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
import javax.persistence.TypedQuery;

import org.hibernate.annotations.ColumnDefault;

import it.bz.idm.bdp.dal.util.JPAUtil;
import it.bz.idm.bdp.dto.authentication.RoleDto;


@Entity
public class BDPRole {

	public static final String ROLE_GUEST = "ROLE_GUEST";
	public static final String ROLE_ADMIN = "ROLE_ADMIN";

	@Id
	@GeneratedValue(generator = "bdprole_gen", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "bdprole_gen", sequenceName = "bdprole_seq", schema = "intime", allocationSize = 1)
	@ColumnDefault(value = "nextval('bdprole_seq')")
	private Long id;

	@Column(unique = true, nullable = false)
	private String name;

	private String description;

	@ManyToMany(mappedBy = "roles")
	private Collection<BDPUser> users;

	@ManyToOne
	private BDPRole parent;

	public BDPRole() {
	}

	public static BDPRole fetchGuestRole(EntityManager manager) {
		return findByName(manager, ROLE_GUEST);
	}

	public static BDPRole fetchAdminRole(EntityManager manager) {
		return findByName(manager, ROLE_ADMIN);
	}

	public BDPRole(String name, String description) {
		this.setName(name);
		this.setDescription(description);
	}

	public BDPRole(String name) {
		this.setName(name);
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

	public static BDPRole findByName(EntityManager manager, String name) {
		TypedQuery<BDPRole> query = manager.createQuery("SELECT r FROM BDPRole r where r.name = :name", BDPRole.class);
		query.setParameter("name", name);
		return JPAUtil.getSingleResultOrNull(query);
	}

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
