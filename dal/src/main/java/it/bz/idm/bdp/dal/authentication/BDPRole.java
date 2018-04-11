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
import javax.persistence.SequenceGenerator;
import javax.persistence.TypedQuery;

import it.bz.idm.bdp.dto.authentication.RoleDto;


@Entity
public class BDPRole {

	@Id
	@GeneratedValue(generator = "role_seq", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "role_seq", sequenceName = "role_seq", schema = "intime", allocationSize = 1)
	private Long id;
	@Column(unique = true, nullable = false)
	private String name;
	private String description;
	@ManyToMany(mappedBy = "roles")
	private Collection<BDPUser> users;

	public BDPRole() {
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

	public static BDPRole findByName(EntityManager manager, String name) {
		TypedQuery<BDPRole> query = manager.createQuery("SELECT r FROM BDPRole r where r.name = :name", BDPRole.class);
		query.setParameter("name", name);
		List<BDPRole> resultList = query.getResultList();
		return resultList.isEmpty() ? null : resultList.get(0);
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
