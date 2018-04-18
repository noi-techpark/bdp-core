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
import javax.persistence.Transient;
import javax.persistence.TypedQuery;

import org.hibernate.annotations.NaturalId;

import it.bz.idm.bdp.dal.authentication.BDPRole;
import it.bz.idm.bdp.dto.meteo.SegmentDataPointDto;

@Table(name="measurement")
@Entity
public class Measurement{
	
	@Transient
	private static final long serialVersionUID = 2900270107783989197L;
	
    @Id
    @GeneratedValue(generator="incrementmeasurement",strategy=GenerationType.SEQUENCE)
    @SequenceGenerator(name="incrementmeasurement", sequenceName = "measurement_id_seq",schema="intime",allocationSize=1)
	private Integer id;
    
	private Date timestamp;
	private Double value;
	private Date created_on;

	@NaturalId
	@ManyToOne(cascade=CascadeType.ALL)
	private Station station;
	
	@NaturalId
	@ManyToOne(cascade = CascadeType.ALL)
	private DataType type;
	
	@NaturalId
	private Integer period;
	
	public Measurement() {
	}
	public Measurement(SegmentDataPointDto dataPoint) {
		this.value = dataPoint.getValue();
		this.created_on = new Date();
		this.timestamp = dataPoint.getDate();
	}

	public Measurement(Station station, DataType type,
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
	public Date getTimestamp() {
		return timestamp;
	}
	
	public void setTimestamp(Date date) {
		this.timestamp = date;
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
	public Date getCreated_on() {
		return created_on;
	}

	public void setCreated_on(Date created_on) {
		this.created_on = created_on;
	}

	public DataType getType() {
		return type;
	}

	public void setType(DataType type) {
		this.type = type;
	}
	
	public Integer getPeriod() {
		return period;
	}
	public void setPeriod(Integer period) {
		this.period = period;
	}
	public static Measurement findLatestEntry(EntityManager em, Station station, DataType type,Integer period) {
		if (station == null)
			return null;
		TypedQuery<Measurement> singleQuery = null;
		if (type == null){
			if (period == null){
				singleQuery = em.createQuery("SELECT record FROM Measurement record WHERE record.station = :station",Measurement.class);
			}else{
				singleQuery = em.createQuery("SELECT record FROM Measurement record WHERE record.station = :station AND record.period=:period",Measurement.class);
				singleQuery.setParameter("period", period);
			}
		}else if (period==null){
			singleQuery = em.createQuery("SELECT record FROM Measurement record WHERE record.station = :station AND record.type=:type",Measurement.class);
			singleQuery.setParameter("type", type);

		}else{
			singleQuery = em.createQuery("SELECT record FROM Measurement record WHERE record.station = :station AND record.type=:type AND record.period=:period",Measurement.class);
			singleQuery.setParameter("type", type);
			singleQuery.setParameter("period", period);
		}
		singleQuery.setParameter("station", station);
		List<Measurement> resultList = singleQuery.getResultList();
		return resultList.isEmpty()?null:resultList.get(0);
	}
	public static Measurement findLatestEntry(EntityManager em, Station station, DataType type,
			Integer period, BDPRole role) {
		if (station == null || role == null)
			return null;
		String baseQuery = "SELECT record FROM Measurement record JOIN BDPRules ru ON (record.station = ru.station OR ru.station = null)"
				+ " AND (record.type = ru.type OR ru.type = null) AND (record.period = ru.period OR ru.period = null) WHERE ru.role = :role AND record.station = :station";
		TypedQuery<Measurement> queryPromise = null;
		//set optional parameters
		if (type == null){
			if (period == null){
				queryPromise = em.createQuery(baseQuery,Measurement.class);
			}else{
				queryPromise = em.createQuery(baseQuery + " AND record.period=:period",Measurement.class);
				queryPromise.setParameter("period", period);
			}
		}else if (period==null){
			queryPromise = em.createQuery(baseQuery + " AND record.type=:type",Measurement.class);
			queryPromise.setParameter("type", type);

		}else{
			queryPromise = em.createQuery(baseQuery + " AND record.type=:type AND record.period=:period",Measurement.class);
			queryPromise.setParameter("type", type);
			queryPromise.setParameter("period", period);
		}
		//set required paramaters
		queryPromise.setParameter("station", station);
		queryPromise.setParameter("role", role);
		List<Measurement> resultList = queryPromise.getResultList();
		/* resulting example query which does return no records
		 * --------------------------------------------------------
		 * select measuremen0_.id as id1_26_, measuremen0_.created_on as created_2_26_, measuremen0_.period as period3_26_, measuremen0_.station_id as station_6_26_, 
		 * measuremen0_.timestamp as timestam4_26_, measuremen0_.type_id as type_id7_26_, measuremen0_.value as value5_26_ from intime.measurement measuremen0_ inner join intime.station station2_ 
		 * on measuremen0_.station_id=station2_.id inner join intime.type datatype5_ on measuremen0_.type_id=datatype5_.id inner join intime.BDPRules bdprules1_ on 
		 * ((measuremen0_.station_id=bdprules1_.station_id or bdprules1_.station_id is null) and (measuremen0_.type_id=bdprules1_.type_id or bdprules1_.type_id is null) and 
		 * (measuremen0_.period=bdprules1_.period or bdprules1_.period is null)) inner join intime.station station3_ on bdprules1_.station_id=station3_.id inner join intime.type datatype6_ 
		 * on bdprules1_.type_id=datatype6_.id where bdprules1_.role_id=3 and measuremen0_.station_id=1 and measuremen0_.type_id=1;
		 * */
		return resultList.isEmpty()?null:resultList.get(0);
	}

}
