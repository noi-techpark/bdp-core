package it.bz.idm.bdp.dal;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.TypedQuery;

@Table(name="elaboration")
@Entity
public class Elaboration {
	
	@Id
    @GeneratedValue(generator="elaboration_id_seq",strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name="elaboration_id_seq", sequenceName = "elaboration_id_seq",schema="intime",allocationSize=1)
	protected Integer id;
	private Date created_on;
	private Date timestamp;
	
	@ManyToOne(cascade=CascadeType.MERGE)
	private DataType type;
	private Double value;
	
	@ManyToOne
	private Station station;
	private Integer period;
	
	public Elaboration() {
	}
	public Elaboration(Station station, DataType type,
			Double value, Date timestamp, Integer period) {
		this.station = station;
		this.type = type;
		this.value = value;
		this.timestamp = timestamp;
		this.period = period;
		this.created_on = new Date();
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Date getCreated_on() {
		return created_on;
	}
	public void setCreated_on(Date created_on) {
		this.created_on = created_on;
	}
	public Date getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	public DataType getType() {
		return type;
	}
	public void setType(DataType type) {
		this.type = type;
	}
	public Double getValue() {
		return value;
	}
	public void setValue(Double value) {
		this.value = value;
	}
	public Station getStation() {
		return station;
	}
	public void setStation(Station station) {
		this.station = station;
	}
	public Integer getPeriod() {
		return period;
	}
	public void setPeriod(Integer period) {
		this.period = period;
	}
	public Elaboration findLastRecord(EntityManager em,
			Station station, DataType type, Integer period) {
		Elaboration elaboration = null;
		if (type == null)
			elaboration = findLastRecord(em,station, period);
		else if (period == null){
			elaboration = findLastRecord(em,station, type);
		}
		else{
			TypedQuery<Elaboration> query = em.createQuery("SELECT elab FROM Elaboration elab WHERE elab.station=:station AND elab.type=:type AND elab.period=:period order by elab.timestamp desc",Elaboration.class);
			query.setParameter("station",station);
			query.setParameter("type", type);
			query.setParameter("period", period);
			List<Elaboration> resultList = query.getResultList();
			if (!resultList.isEmpty())
				elaboration = resultList.get(0);
		}
		return elaboration;
		

	}
	private Elaboration findLastRecord(EntityManager em, Station station, DataType type) {
		if (type == null)
			findLastRecord(em,station);
		TypedQuery<Elaboration> query = em.createQuery("SELECT elaboration FROM Elaboration elaboration WHERE elaboration.station=:station AND elaboration.type=:type order by elaboration.timestamp desc",Elaboration.class);
		query.setParameter("station",station);
		query.setParameter("type", type);
		List<Elaboration> resultList = query.getResultList();
		return resultList.isEmpty()?null:resultList.get(0);
	}
	private Elaboration findLastRecord(EntityManager em, Station station, Integer period) {
		if (period == null)
			return findLastRecord(em,station);
		TypedQuery<Elaboration> query = em.createQuery("SELECT elaboration FROM Elaboration elaboration WHERE elaboration.station=:station AND elaboration.period=:period order by elaboration.timestamp desc",Elaboration.class);
		query.setParameter("station",station);
		query.setParameter("period", period);
		List<Elaboration> resultList = query.getResultList();
		return resultList.isEmpty()?null:resultList.get(0);
		
	}
	private Elaboration findLastRecord(EntityManager em,Station station) {
		if (station == null)
			return null;
		TypedQuery<Elaboration> query = em.createQuery("SELECT elaboration FROM Elaboration elaboration WHERE elaboration.station=:station order by elaboration.timestamp desc",Elaboration.class);
		query.setParameter("station",station);
		List<Elaboration> resultList = query.getResultList();
		return resultList.isEmpty()?null:resultList.get(0);
	}
}
