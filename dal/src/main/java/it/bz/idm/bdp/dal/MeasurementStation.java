package it.bz.idm.bdp.dal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.apache.log4j.Logger;

import it.bz.idm.bdp.dto.FullRecordDto;
import it.bz.idm.bdp.dto.RecordDto;
import it.bz.idm.bdp.dto.SimpleRecordDto;
import it.bz.idm.bdp.dto.TypeDto;
import it.bz.idm.bdp.dto.TypeMapDto;


public abstract class MeasurementStation extends Station {
	private Logger logger = Logger.getLogger(MeasurementStation.class);
	
	@Override
	public List<String[]> findDataTypes(EntityManager em,String stationId) {
		TypedQuery<Object[]> query;
		if (stationId != null){
			query = em
					.createQuery(
							"SELECT record.type.cname,record.type.cunit,record.type.description,record.period FROM Measurement record INNER JOIN record.type  "
							+ "where record.station.class=:stationtype AND record.station.stationcode=:station GROUP BY record.type.cname,record.type.cunit,record.type.description,record.period)",
							Object[].class);
			query.setParameter("station", stationId);
		}else
			query = em
					.createQuery(
							"SELECT record.type.cname,record.type.cunit,record.type.description,record.period FROM Measurement record INNER JOIN record.type "
							+ " where record.station.class=:stationtype GROUP BY record.type.cname,record.type.cunit,record.type.description,record.period)",
							Object[].class);
		query.setParameter("stationtype", this.getClass().getSimpleName());
		List<Object[]> resultList = query.getResultList();
		return getDataTypesFromQuery(resultList);
	}
	
	public List<TypeDto> findTypes(EntityManager em, String stationId) {
		TypedQuery<Object[]> query;
		if (stationId == null) {
			query = em.createQuery("SELECT type,record.period FROM Measurement record INNER JOIN record.type type  "
					+ "where record.station.class=:stationType GROUP BY type,record.period",Object[].class);
			query.setParameter("stationType", this.getClass().getSimpleName());
		} else {
			query = em.createQuery("SELECT type,record.period FROM Measurement record INNER JOIN record.type type "
					+ "where record.station.class=:stationType AND record.station.stationcode=:station GROUP BY type,record.period",Object[].class);
			query.setParameter("stationType", this.getClass().getSimpleName());
			query.setParameter("station",stationId);
		}
		List<Object[]> resultList = query.getResultList();
		List<TypeDto> types = new ArrayList<TypeDto>();
		Map<String,TypeDto> dtos = new HashMap<String, TypeDto>();

		for (Object obj:resultList){
			Object[] results = (Object[]) obj;
			DataType type = (DataType) results[0];
			Integer acqIntervall = (Integer) results[1];
			String id = type.getCname();
			TypeDto dto = dtos.get(id);
			if (dto == null){
				dto = new TypeDto();
				dto.getDesc().putAll(type.getI18n());
				dto.setId(id);
				dto.setUnit(type.getCunit());
				dto.setTypeOfMeasurement(type.getRtype());
				dtos.put(id, dto);
			}
			dto.getAquisitionIntervalls().add(acqIntervall);
		}
		for (Map.Entry<String, TypeDto> entry : dtos.entrySet())
			types.add(entry.getValue());
		return types;
	}

	@Override
	public Date getDateOfLastRecord(EntityManager em, Station station, DataType type,
			Integer period) {
		Date date = null;
		if (station != null){
			String queryString = "select record.timestamp from Measurement record where record.station=:station";
			if (type != null){
				queryString += " AND record.type = :type";
			}
			if (period != null){
				queryString += " AND record.period=:period";
			}
			queryString += " ORDER BY record.timestamp DESC";
			TypedQuery<Date> query = em.createQuery(queryString, Date.class);
			query.setParameter("station", station);
			if (type!=null)
				query.setParameter("type", type);
			if (period!=null)
				query.setParameter("period", period);
			List<Date> resultList = query.getResultList();
			date = resultList.isEmpty() ? new Date(0) : resultList.get(0);
		}
		return date;
	}

	@Override
	public RecordDto findLastRecord(EntityManager em, String stringType, Integer period) {
		SimpleRecordDto dto = null;
		DataType type = DataType.findByCname(em,stringType);
		Measurement latestEntry = Measurement.findLatestEntry(em,this, type, period);
		if (latestEntry != null){
			dto = new SimpleRecordDto(latestEntry.getTimestamp().getTime(),latestEntry.getValue());
			dto.setPeriod(latestEntry.getPeriod());
		}
		return dto;
	}

	@Override
	public List<RecordDto> getRecords(EntityManager em, String type, Date start, Date end,
			Integer period) {
		List<RecordDto> records = MeasurementHistory.findRecords(em,this.getClass().getSimpleName(), this.stationcode, type, start,end,period);
		return records;
	}

	@Override
	public Object pushRecords(EntityManager em,Object... objects) {
		Object object = objects[0];
		if (object instanceof Map){
			try{
				Map<String,TypeMapDto> data = (Map) object;
				logger.info("Start of transactions");
				for (Map.Entry<String, TypeMapDto> entry:data.entrySet()){
					Station station = findStation(em,entry.getKey());
					for(Map.Entry<String,Set<SimpleRecordDto>> typeEntry:entry.getValue().getRecordsByType().entrySet()){
						DataType type = DataType.findByCname(em, typeEntry.getKey());
						if (station != null && this.getClass().isInstance(station) && type != null){
							List<SimpleRecordDto> records = new ArrayList<SimpleRecordDto>(typeEntry.getValue());
							Measurement lastEntry = null;
							if (!records.isEmpty())
								lastEntry = Measurement.findLatestEntry(em, station,type,records.get(0).getPeriod());
							try{
								logger.info("BEGIN");
								em.getTransaction().begin();
								SimpleRecordDto temp = null;
								if (lastEntry != null)
									temp = new SimpleRecordDto(lastEntry.getTimestamp().getTime(),lastEntry.getValue());
								for (SimpleRecordDto dto : typeEntry.getValue()){
									Integer period = dto.getPeriod();
									Long dateOfMeasurement = dto.getTimestamp();
									Double value = dto.getValue();
									if(lastEntry == null || lastEntry.getTimestamp().getTime() < dateOfMeasurement){
										MeasurementHistory record = new MeasurementHistory(station,type,value,new Date(dateOfMeasurement),period);
										em.persist(record);								
									}
									if (temp == null || temp.getTimestamp() < dateOfMeasurement){
										temp = dto;
									}
								}
								if (lastEntry == null && temp!= null && temp.isValid()){
									lastEntry = new Measurement(station, type, temp.getValue(), new Date(temp.getTimestamp()), temp.getPeriod());
									em.persist(lastEntry);
								}
								else if (temp != null){
									lastEntry.setTimestamp(new Date(temp.getTimestamp()));
									lastEntry.setValue(temp.getValue());
									em.merge(lastEntry);
								}
								em.getTransaction().commit();
								logger.info("COMMIT");
								
							}catch(Exception ex){
								ex.printStackTrace();
								if (em.getTransaction().isActive())
									em.getTransaction().rollback();
								continue;
							}
						}
					}
				}
				logger.info("End of transactions");
			}catch(Exception ex){
				ex.printStackTrace();
			}finally{
				em.clear();
				em.close();
			}
		}else if (object instanceof Object[]){
			List<Object> dtos = Arrays.asList((Object[]) object);
			try{
				em.getTransaction().begin();
				Station station = null;
				DataType type = null;
				String tempSS = null,tempTS = null;
				for (Object dto : dtos){
					FullRecordDto full = (FullRecordDto)dto;
					if (full.validate()){
						if (!full.getStation().equals(tempSS)){
								station = findStation(em,full.getStation());
								if (station == null)
									continue;
								tempSS = station.getStationcode();
						}
						if (!full.getType().equals(tempTS)){
							type = DataType.findByCname(em, full.getType());
							if (type == null)
								continue;
							tempTS = full.getType();
						}
						Measurement lastEntry = Measurement.findLatestEntry(em, station,type,full.getPeriod());
						Number value = (Number) full.getValue();
						if (lastEntry == null){
							 lastEntry = new Measurement(station, type,value.doubleValue(), new Date(full.getTimestamp()), full.getPeriod());
							 em.persist(lastEntry);
						}
						else if (lastEntry.getTimestamp().getTime()<full.getTimestamp()){
							lastEntry.setValue(value.doubleValue());
							lastEntry.setTimestamp(new Date(full.getTimestamp()));
							em.merge(lastEntry);
						}
						MeasurementHistory record = new MeasurementHistory(station,type,value.doubleValue(), new Date(full.getTimestamp()),full.getPeriod());
						em.persist(record);
					}
				}
				em.getTransaction().commit();
			}catch(Exception ex){
				ex.printStackTrace();
				if (em.getTransaction().isActive())
					em.getTransaction().rollback();
			}
		}
		return null;
	}
}
