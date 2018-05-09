package it.bz.idm.bdp.dal;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.TypedQuery;

import it.bz.idm.bdp.dal.util.JPAUtil;

@Table(name="alarmspecification")
@Entity
public class AlarmSpecification {

	@Id
    @GeneratedValue(generator="alarm_specs_id", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name="alarm_specs_id", sequenceName = "alarm_spec_seq",schema="intime",allocationSize=1)
	private Long id;

	private String name;
	private String description;

	public AlarmSpecification() {
	}
	public AlarmSpecification(String name, String description) {
		this.name = name;
		this.description = description;
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
	public static AlarmSpecification findSpecificationByName(EntityManager manager, String name) {
		TypedQuery<AlarmSpecification> q = manager.createQuery("select spec from AlarmSpecification spec where spec.name=:name", AlarmSpecification.class);
		q.setParameter("name", name);
		return (AlarmSpecification) JPAUtil.getSingleResultOrNull(q);
	}



}
