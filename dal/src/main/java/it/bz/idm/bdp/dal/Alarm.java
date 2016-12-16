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

@Table(name="alarm")
@Entity
public class Alarm {

	@Id
	@GeneratedValue(generator="alarm_seq",strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name="alarm_seq", sequenceName = "alarm_id_seq",schema="intime",allocationSize=1)
	private Long id;

	@ManyToOne(cascade=CascadeType.ALL)
	private AlarmSpecification specification;

	private Date timestamp;

	private Date createDate;

	@ManyToOne
	private Station station;

	public Alarm() {
		this.createDate = new Date();
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public AlarmSpecification getSpecification() {
		return specification;
	}
	public void setSpecification(AlarmSpecification specification) {
		this.specification = specification;
	}

	public Date getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public Station getStation() {
		return station;
	}
	public void setStation(Station station) {
		this.station = station;
	}

	public static void createAllarm(EntityManager manager, String alarmName,
			String description, Station station, Date slotsTS) {
		AlarmSpecification spec = AlarmSpecification.findSpecificationByName(manager,alarmName);
		if (spec == null)
			spec = new AlarmSpecification(alarmName,description);
		Alarm alarm = new Alarm();
		alarm.setSpecification(spec);
		alarm.setStation(station);
		alarm.setTimestamp(slotsTS);
		manager.persist(alarm);
	}
	public static List<Alarm> findAlarmByStationAndTimestamp(EntityManager em, Station station, Date timestamp) {
		TypedQuery<Alarm> query = em.createQuery("SELECT alarm FROM Alarm alarm where alarm.station = :station AND alarm.timestamp=:timestamp",Alarm.class);
		query.setParameter("station", station);
		query.setParameter("timestamp", timestamp);
		List<Alarm> resultList = query.getResultList();
		return resultList;
	}

}