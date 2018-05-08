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

import it.bz.idm.bdp.dal.authentication.BDPRole;

@Table(name="measurementstring",schema="intime")
@Entity
public class MeasurementString {

	@Id
    @GeneratedValue(generator="measurementstring_id_seq",strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name="measurementstring_id_seq", sequenceName = "measurementstring_id_seq",schema="intime",allocationSize=1)
	private Integer id;
	private Date created_on;
	private Date timestamp;
	private String value;

	@ManyToOne(cascade=CascadeType.ALL)
	private Station station;

	@ManyToOne(cascade=CascadeType.ALL)
	private DataType type;

	private Integer period;

	public MeasurementString() {
	}
	public MeasurementString(Station station, DataType type,
			String value, Date timestamp, Integer period) {
		this.station = station;
		this.type = type;
		this.value = value;
		this.timestamp = timestamp;
		this.created_on = new Date();
		this.period = period;
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

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Station getStation() {
		return station;
	}

	public void setStation(Station station) {
		this.station = station;
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

	public static MeasurementString findLastMeasurementByStationAndType(
			EntityManager em, Station station, DataType type, Integer period, BDPRole role) {
		TypedQuery<MeasurementString> q = em.createQuery("SELECT measurement "
				+ "FROM MeasurementString measurement, BDPPermissions p "
				+ "WHERE (measurement.station = p.station OR p.station = null) "
				+ "AND (measurement.type = p.type OR p.type = null) "
				+ "AND (measurement.period = p.period OR p.period = null) "
				+ "AND p.role = :role "
				+ "AND measurement.station = :station "
				+ "AND measurement.type=:type "
				+ "AND measurement.period=:period",MeasurementString.class);
		q.setParameter("station",station);
		q.setParameter("type",type);
		q.setParameter("period", period);
		q.setParameter("role", role == null ? BDPRole.fetchGuestRole(em) : role);
		List<MeasurementString> resultList = q.getResultList();
		return resultList.isEmpty() ? null : resultList.get(0);
	}
}
