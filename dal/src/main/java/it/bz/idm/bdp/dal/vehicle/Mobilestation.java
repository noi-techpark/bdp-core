package it.bz.idm.bdp.dal.vehicle;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.apache.commons.beanutils.BeanMap;

import it.bz.idm.bdp.dal.DataType;
import it.bz.idm.bdp.dal.Station;
import it.bz.idm.bdp.dal.util.JPAUtil;
import it.bz.idm.bdp.dto.Car;
import it.bz.idm.bdp.dto.CarValue;
import it.bz.idm.bdp.dto.RecordDto;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.dto.TrafficVehicleRecordDto;
import it.bz.idm.bdp.dto.TypeDto;

@Entity
public class Mobilestation extends Station {
	private void addTrafficVehicleRecord(EntityManager em ,Car car) {
		Mobilestation trafficVehicle;
		Station station = this.findStation(em,car.getVehicle_id());
		if (station != null){
			trafficVehicle = (Mobilestation)station;
			trafficVehicle.persistRecords(em,car.getValues());
			trafficVehicle.persistNewestRecord(em,car.getValues(),trafficVehicle);
		}else{
			trafficVehicle = new Mobilestation();
			trafficVehicle.setStationcode(car.getVehicle_id());
			trafficVehicle.setActive(true);
			trafficVehicle.setName(car.getVehicle_id());
			em.persist(trafficVehicle);
			trafficVehicle.persistRecords(em,car.getValues());
			trafficVehicle.persistNewestRecord(em,car.getValues(),trafficVehicle);
		}

	}
	
	private void persistNewestRecord(
			EntityManager em, List<CarValue> values, Mobilestation trafficVehicle) {
		CarValue value = filterNewestRecord(values);
		if (value != null){
			TrafficVehicleRecord record = TrafficVehicleRecord.findRecordByVehicle(em,trafficVehicle);
			if (record == null)
				record = new TrafficVehicleRecord(value,trafficVehicle);
			else{
				Long id = record.getId();
				record = new TrafficVehicleRecord(value,trafficVehicle);
				record.setId(id);
			}
			em.merge(record);
		}
	}

	private CarValue filterNewestRecord(List<CarValue> values) {
		CarValue newestValue = null;
		for (CarValue value:values)
			if (newestValue==null || value.getTs_ms() > newestValue.getTs_ms())
				newestValue = value;
		return newestValue;
	}


	private void persistRecords(EntityManager em, List<CarValue> values) {
		for(CarValue value: values){
			TrafficVehicleRecordHistory vehicleRecord = new TrafficVehicleRecordHistory(value,this);
			if (vehicleRecord.alreadyExists())
				throw new IllegalStateException("The record with timestamp "+vehicleRecord.getTs_ms()+", epoc:"+vehicleRecord.getTs_ms().getTime()+" and vehicle id "+vehicleRecord.getStation().getStationcode()+" already exists in DB");
			em.persist(vehicleRecord);
		}
	}

	@Override
	public List<String[]> findDataTypes(EntityManager em,String stationId) {
		return TrafficVehicleRecord.DATATYPES;
	}
	@Override
	public Date getDateOfLastRecord(EntityManager em,Station station, DataType type, Integer period) {
		Date date = null;
		if (station != null){
			TypedQuery<Date> query;
			if (type != null){
				query = em.createQuery("select record.ts_ms from TrafficVehicleRecord record where record.station=:station AND record.type = :type",Date.class);
				query.setParameter("type", type);
			}
			else
				query = em.createQuery("select record.ts_ms from TrafficVehicleRecord record where record.station=:station ORDER BY record.ts_ms DESC",Date.class);
			query.setParameter("station", station);
			List<Date> resultList = query.getResultList();
			date = resultList.isEmpty() ? new Date(0) : resultList.get(0);
			}
		return date;
	}
	@Override
	public RecordDto findLastRecord(EntityManager em, String cname, Integer period) {
		TrafficVehicleRecordDto dto = null;
		TrafficVehicleRecord latestEntry = TrafficVehicleRecord.findRecordByVehicle(em,this,cname,period);
		if (latestEntry != null)
			if (cname == null)
				dto = new TrafficVehicleRecordDto(latestEntry.getTs_ms().getTime(),null);
			else {
				BeanMap beanMap = new BeanMap(latestEntry);
				Object object = beanMap.get(cname);
				dto = new TrafficVehicleRecordDto(latestEntry.getTs_ms().getTime(),object!=null?object.toString():null);
			}
		return dto;
	}

	@Override
	public List<RecordDto> getRecords(EntityManager em, String type, Date start, Date end,
			Integer period) {
		return TrafficVehicleRecordHistory.findTrafficVehicleRecords(em,this.stationcode,type,start,end,period);
	}

	@Override
	public Object pushRecords(EntityManager em2,Object... object) {
        EntityManager em = JPAUtil.createEntityManager();
		String retval = "";
		try{
			List<Object> cars =  Arrays.asList(object);
			em.getTransaction().begin();
			for (Object car : cars) {
				if (car instanceof Car)
					this.addTrafficVehicleRecord(em,(Car)car);
			}
			em.getTransaction().commit();
		} catch(IllegalStateException state){
			if (em.getTransaction().isActive())
				em.getTransaction().rollback();
			retval = state.getMessage();
		}
		catch(Exception exception){
			retval = exception.getMessage();
		}
		finally {
			em.close();
		}
		return retval;
	}

	@Override
	public void sync(EntityManager em, Station station, StationDto dto) {
		
	}

	@Override
	public List<TypeDto> findTypes(EntityManager em,String stationId) {
		List<TypeDto> dtos = new ArrayList<TypeDto>();
		for (String[] type:TrafficVehicleRecord.DATATYPES){
			dtos.add(new TypeDto(type[0], null));
		}
		return dtos;
	}
}
