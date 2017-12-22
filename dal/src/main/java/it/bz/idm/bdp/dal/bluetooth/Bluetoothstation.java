package it.bz.idm.bdp.dal.bluetooth;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import it.bz.idm.bdp.dal.DataType;
import it.bz.idm.bdp.dal.ElaborationStation;
import it.bz.idm.bdp.dal.MeasurementString;
import it.bz.idm.bdp.dal.MeasurementStringHistory;
import it.bz.idm.bdp.dal.Station;
import it.bz.idm.bdp.dto.DataMapDto;
import it.bz.idm.bdp.dto.RecordDtoImpl;
import it.bz.idm.bdp.dto.SimpleRecordDto;
import it.bz.idm.bdp.dto.StationDto;

@Entity
public class Bluetoothstation extends ElaborationStation {

	public static final String VEHICLE_DETECTION = "vehicle detection";
	public static final String STATION_TYPE="Bluetoothstation";
	public static final Integer PERIOD = 1;

	public Bluetoothstation() {
		super();
	}

	public Bluetoothstation(String stationcode) {
		this.stationcode = stationcode;
		this.name = stationcode;
	}

	@Override
	public Date getDateOfLastRecord(EntityManager em, Station station, DataType type, Integer period) {
		Date date = null;
		if (station != null){
			String queryString = "select record.timestamp from MeasurementString record where record.station=:station";
			if (type != null){
				queryString += " AND record.type = :type";
			}
			if (period != null){
				queryString += " AND record.period=:period";
			}
			queryString += " ORDER BY record.timestamp DESC";
			TypedQuery<Date> query = em.createQuery(queryString, Date.class);
			query.setParameter("station", station);
			if (type !=null)
				query.setParameter("type", type);
			if (period!=null)
				query.setParameter("period", period);
			List<Date> resultList = query.getResultList();
			date = resultList.isEmpty() ? new Date(0) : resultList.get(0);
		}
		return date;
	}

	@Override
	public Object pushRecords(EntityManager em, Object... objects) {
		if (objects.length>0 && objects[0] instanceof DataMapDto<?>)
		{	
			DataMapDto<RecordDtoImpl> dataMap = (DataMapDto<RecordDtoImpl>) objects[0];
			DataType type = DataType.findByCname(em, VEHICLE_DETECTION);
			if (type == null)
				type = new DataType(VEHICLE_DETECTION); //create it if it does not exists in DB

			for(Map.Entry<String,DataMapDto<RecordDtoImpl>> entry : dataMap.getBranch().entrySet()){
				em.getTransaction().begin();
				Station station = findStation(em, entry.getKey());
				if (station == null){
					station = new Bluetoothstation(entry.getKey());
					em.persist(station);
				}
				List<? extends RecordDtoImpl> data = entry.getValue().getBranch().get(VEHICLE_DETECTION).getData();
				for (RecordDtoImpl record: data){
					SimpleRecordDto dto = (SimpleRecordDto) record;
					MeasurementStringHistory history = MeasurementStringHistory.findRecord(em,station,type,dto.getValue().toString(),new Date(dto.getTimestamp()),PERIOD); 
					if (history == null){
						history = new MeasurementStringHistory(station, type, dto.getValue().toString(),new Date(dto.getTimestamp()),PERIOD);
						em.persist(history);
					}
					MeasurementString lastMeasurement = MeasurementString.findLastMeasurementByStationAndType(em, station,	type,PERIOD);
					if (lastMeasurement != null) {
						lastMeasurement.setTimestamp(new Date(dto.getTimestamp()));
						lastMeasurement.setCreated_on(new Date());
						lastMeasurement.setValue(dto.getValue().toString());
					} else
						lastMeasurement = new MeasurementString(station, type, dto.getValue().toString(), new Date(dto.getTimestamp()),PERIOD);
					em.merge(lastMeasurement);		
				}
				em.getTransaction().commit();
			}
		}
		return "";
	}

	@Override
	public void sync(EntityManager em, Station station, StationDto dto) {
	}

}
