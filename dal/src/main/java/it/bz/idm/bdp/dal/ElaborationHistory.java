package it.bz.idm.bdp.dal;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.TypedQuery;

import it.bz.idm.bdp.dto.RecordDto;
import it.bz.idm.bdp.dto.bluetooth.BluetoothRecordExtendedDto;
@Entity
public class ElaborationHistory {
	
	@Id
    @GeneratedValue(generator="elaborationhistory_id_seq",strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name="elaborationhistory_id_seq", sequenceName = "elaborationhistory_id_seq",schema="intime",allocationSize=1)
	protected Long id;
	private Date created_on;
	private Date timestamp;

	@ManyToOne
	private DataType type;
	private Double value;
	
	@ManyToOne
	private Station station;
	private Integer period;

	public ElaborationHistory() {
	}
	public ElaborationHistory(Station station, DataType type, Double value, Date timestamp, Integer period) {
		super();
		this.created_on = new Date();
		this.timestamp = timestamp;
		this.type = type;
		this.value = value;
		this.station = station;
		this.period = period;
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
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

	public static List<RecordDto> findRecords(EntityManager em, String stationtype,
			String uuid, String type,  Date start, Date end, Integer period) {
		TypedQuery<ElaborationHistory> query;
		if (period!=null){
			query = em.createQuery("select record FROM ElaborationHistory record WHERE record.station.class= :stationtype AND record.station.stationcode= :stationid AND record.type.cname=:type AND record.period=:period AND record.timestamp between :start AND :end ORDER BY record.timestamp asc",ElaborationHistory.class);
			query.setParameter("period", period);
		}else
			query = em.createQuery("select record FROM ElaborationHistory record WHERE record.station.class= :stationtype AND record.station.stationcode= :stationid AND record.type.cname=:type AND record.timestamp between :start AND :end ORDER BY record.timestamp asc",ElaborationHistory.class);
		query.setParameter("stationtype", stationtype);
		query.setParameter("stationid", uuid);
		query.setParameter("type", type);
		query.setParameter("start", start);
		query.setParameter("end", end);
		List<RecordDto> dtos = new ArrayList<RecordDto>();
		parseDtos(dtos, query);
		return dtos;
	}
	private static void parseDtos(List<RecordDto> dtos, TypedQuery<ElaborationHistory> query) {
		for (ElaborationHistory history:query.getResultList()){
			Date date = history.getTimestamp();
			Long created_on = history.getCreated_on().getTime();
			Double value = history.getValue();
			BluetoothRecordExtendedDto dto = new BluetoothRecordExtendedDto(date.getTime(), value,created_on);
			dtos.add(dto);
		}
	}
	public static ElaborationHistory findRecordByProps(EntityManager em, Station station, DataType type, Date timestamp, Integer period) {
		TypedQuery<ElaborationHistory> query = em.createQuery("select record FROM ElaborationHistory record WHERE record.station= :station AND record.type=:type AND record.timestamp = :timestamp AND record.period=:period ORDER BY record.timestamp asc",ElaborationHistory.class);
		query.setParameter("station", station);
		query.setParameter("type", type);
		query.setParameter("timestamp", timestamp);
		query.setParameter("period",period);
		List<ElaborationHistory> resultList = query.getResultList();
		return resultList.isEmpty()?null:resultList.get(0);
	}
	
	

}
