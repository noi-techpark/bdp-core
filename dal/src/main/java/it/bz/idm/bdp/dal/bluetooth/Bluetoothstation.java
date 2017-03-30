package it.bz.idm.bdp.dal.bluetooth;

import it.bz.idm.bdp.dal.DataType;
import it.bz.idm.bdp.dal.ElaborationStation;
import it.bz.idm.bdp.dal.MeasurementString;
import it.bz.idm.bdp.dal.MeasurementStringHistory;
import it.bz.idm.bdp.dal.Station;
import it.bz.idm.bdp.dto.OddsRecordDto;
import it.bz.idm.bdp.dto.StationDto;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

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
		List<Object> records =Arrays.asList(objects);
		DataType type = DataType.findByCname(em, VEHICLE_DETECTION);
		if (type == null)
			type = new DataType(VEHICLE_DETECTION);
		for (Object object : records) {
			em.getTransaction().begin();
			Station station;
			OddsRecordDto oRecord = (OddsRecordDto) object;
			if (oRecord.getStationcode() == null) {
				if (oRecord.getStation_id() != null) {
					station = findStation(em,oRecord.getStation_id());
				} else
					continue;
			} else
				station = findStation(em, oRecord.getStationcode());
			if (station == null){
				station = new Bluetoothstation(oRecord.getStationcode());
				em.persist(station);
			}

			MeasurementStringHistory history = MeasurementStringHistory.findRecord(em,station,type,oRecord.getMac(),oRecord.getGathered_on(),PERIOD); 
			if (history == null){
				history = new MeasurementStringHistory(station, type, oRecord.getMac(),oRecord.getGathered_on(),PERIOD);
				em.merge(history);
			}

			MeasurementString lastMeasurement = MeasurementString.findLastMeasurementByStationAndType(em, station,	type,PERIOD);
			if (lastMeasurement != null) {
				lastMeasurement.setTimestamp(oRecord.getGathered_on());
				lastMeasurement.setCreated_on(new Date());
				lastMeasurement.setValue(oRecord.getMac());
			} else
				lastMeasurement = new MeasurementString(station, type, oRecord.getMac(), oRecord.getGathered_on(),PERIOD);
			em.merge(lastMeasurement);
			em.getTransaction().commit();
		}
		return "";
	}

	@Override
	public void sync(EntityManager em, Station station, StationDto dto) {
	}

}
