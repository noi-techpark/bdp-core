package it.bz.idm.bdp.dal;


import java.util.Date;

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
import it.bz.idm.bdp.dal.util.JPAUtil;
import it.bz.idm.bdp.dto.meteo.SegmentDataPointDto;

@Table(name="measurement")
@Entity
public class Measurement {

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

	public static Measurement findLatestEntry(EntityManager em, Station station, DataType type, Integer period, BDPRole role) {
		if (station == null)
			return null;
		String baseQuery = "SELECT record FROM Measurement record, BDPPermissions p"
						 + " WHERE (record.station = p.station OR p.station = null)"
						 + " AND (record.type = p.type OR p.type = null)"
						 + " AND (record.period = p.period OR p.period = null)"
					 	 + " AND p.role = :role "
					 	 + "AND record.station = :station";
		String order = " ORDER BY record.timestamp DESC";

		TypedQuery<Measurement> query = null;
		//set optional parameters
		if (type == null){
			if (period == null){
				query = em.createQuery(baseQuery + order, Measurement.class);
			}else{
				query = em.createQuery(baseQuery + " AND record.period=:period" + order, Measurement.class);
				query.setParameter("period", period);
			}
		}else if (period==null){
			query = em.createQuery(baseQuery + " AND record.type=:type" + order, Measurement.class);
			query.setParameter("type", type);

		}else{
			query = em.createQuery(baseQuery + " AND record.type=:type AND record.period=:period" + order,
					Measurement.class);
			query.setParameter("type", type);
			query.setParameter("period", period);
		}

		//set required paramaters
		query.setParameter("station", station);
		query.setParameter("role", role == null ? BDPRole.fetchGuestRole(em) : role);
		query.setMaxResults(1);
		return (Measurement) JPAUtil.getSingleResultOrNull(query);
	}

}
