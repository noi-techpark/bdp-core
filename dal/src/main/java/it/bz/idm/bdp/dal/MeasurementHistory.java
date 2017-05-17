package it.bz.idm.bdp.dal;


import java.util.ArrayList;
import java.util.Calendar;
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

import it.bz.idm.bdp.dto.RecordDto;
import it.bz.idm.bdp.dto.SimpleRecordDto;

@Table(name="measurementhistory")
@Entity
public class MeasurementHistory{
	@Transient
	private static final long serialVersionUID = 2900270107783989197L;
	
    @Id
    @GeneratedValue(generator="incrementhistory",strategy=GenerationType.SEQUENCE)
    @SequenceGenerator(name="incrementhistory", sequenceName = "measurementhistory_seq",schema="intime",allocationSize=1)
	private Long id;
    
    @NaturalId
	private Date timestamp;
	private Double value;
	private Date created_on;

	@NaturalId
	@ManyToOne(cascade=CascadeType.ALL)
	private Station station;
	
	@NaturalId
	@ManyToOne(cascade = CascadeType.PERSIST)
	private DataType type;
	
	@NaturalId
	private Integer period;
	
	public MeasurementHistory() {
	}
	public MeasurementHistory(Station station, DataType type,
			Double value, Date timestamp, Integer period,Date created_on) {
		this.station = station;
		this.type = type;
		this.value = value;
		this.timestamp = timestamp;
		this.period = period;
		this.created_on = created_on;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Date getDate() {
		return timestamp;
	}
	
	public void setDate(Date date) {
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
	
	
	public Date getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	public Integer getPeriod() {
		return period;
	}
	public void setPeriod(Integer period) {
		this.period = period;
	}
	
	public static List<RecordDto> findRecords(EntityManager em, String stationtype,
			String identifier, String cname,  Long seconds, Integer period) {
		Date past = new Date(Calendar.getInstance().getTimeInMillis()-(1000*seconds));
		TypedQuery<Object[]> query;
		if (period != null){
			query = em.createQuery("select record.timestamp,record.value FROM MeasurementHistory record WHERE record.station.class=:stationtype AND record.station.stationcode= :stationcode AND  record.type.cname = :cname AND record.period=:period AND record.timestamp > :date order by record.timestamp",Object[].class);
			query.setParameter("period", period);
		}else
			query = em.createQuery("select record.timestamp,record.value FROM MeasurementHistory record WHERE record.station.class=:stationtype AND record.station.stationcode= :stationcode AND record.type.cname = :cname AND record.timestamp > :date order by record.timestamp" ,Object[].class);
		query.setParameter("stationtype", stationtype);
		query.setParameter("stationcode", identifier);
		query.setParameter("cname", cname);
		query.setParameter("date", past);
		List<Object[]> resultList = query.getResultList();
		List<RecordDto> dtos = castToDtos(resultList);
		return dtos;
	}
	private static List<RecordDto> castToDtos(List<Object[]> resultList) {
		List<RecordDto> dtos = new ArrayList<RecordDto>();
		for (Object[] row: resultList){
			SimpleRecordDto dto = new SimpleRecordDto(((Date)row[0]).getTime(),Double.parseDouble(String.valueOf(row[1])));
			if (row.length>2)
				dto.setPeriod(Integer.parseInt(row[2].toString()));
			dtos.add(dto);
		}
		return dtos;
	}
	public static List<RecordDto> findRecords(EntityManager em, String stationtype,
			String identifier, String cname, Date start, Date end, Integer period) {
		TypedQuery<Object[]> query;
		if (period != null){
			query= em.createQuery("select record.timestamp, record.value FROM MeasurementHistory record WHERE record.station.class=:stationtype AND record.station.stationcode= :stationcode AND  record.type.cname = :cname AND record.period=:period AND record.timestamp between :start AND :end ORDER BY record.timestamp",Object[].class);
			query.setParameter("period", period);
		}else
			query = em
					.createQuery(
							"select record.timestamp, record.value, record.period FROM MeasurementHistory record "
							+ "WHERE record.station.class=:stationtype AND record.station.stationcode= :stationcode AND  record.type.cname = :cname AND record.timestamp between :start AND :end "
							+ "ORDER BY record.timestamp",
							Object[].class);
		query.setParameter("stationtype", stationtype);
		query.setParameter("stationcode", identifier);
		query.setParameter("cname", cname);
		query.setParameter("start", start);
		query.setParameter("end", end);
		List<Object[]> resultList = query.getResultList();
		List<RecordDto> dtos = castToDtos(resultList);
		return dtos;
	}

	public static boolean recordExists(
			EntityManager em, Station station, DataType type, Date date, Integer period) {
		TypedQuery<MeasurementHistory> preparedQuery = em.createQuery("SELECT record FROM MeasurementHistory record WHERE record.station= :station AND record.type=:type AND record.timestamp =:timestamp AND record.period=:period",MeasurementHistory.class);
		preparedQuery.setParameter("station",station);
		preparedQuery.setParameter("type",type);
		preparedQuery.setParameter("timestamp",date);
		preparedQuery.setParameter("period",period);
		List<MeasurementHistory> resultList = preparedQuery.getResultList();
		return !resultList.isEmpty();
	}
}
