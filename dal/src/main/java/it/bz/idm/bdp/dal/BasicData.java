package it.bz.idm.bdp.dal;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.TypedQuery;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DiscriminatorOptions;

@Entity
@Inheritance(strategy= InheritanceType.TABLE_PER_CLASS)
@DiscriminatorOptions(force=true)
public abstract class BasicData {

	@Id
	@GeneratedValue(generator = "basicdata_gen", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "basicdata_gen", sequenceName = "basicdata_seq", schema = "intime", allocationSize = 1)
	@ColumnDefault(value = "nextval('basicdata_seq')")
	private Long id;

	@ManyToOne
	protected Station station;

	public Station getStation() {
		return station;
	}

	public void setStation(Station station) {
		this.station = station;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public abstract BasicData findByStation(EntityManager em, Station station);
	public List<BasicData> findAll(EntityManager em) {
		TypedQuery<BasicData> typedQuery = em.createQuery("select basic from BasicData basic where basic.station.active=:active",BasicData.class);
		typedQuery.setParameter("active",true);
		List<BasicData> resultList = typedQuery.getResultList();
		return resultList;
	}

}
