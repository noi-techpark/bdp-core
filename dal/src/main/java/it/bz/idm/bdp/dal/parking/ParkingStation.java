package it.bz.idm.bdp.dal.parking;
import it.bz.idm.bdp.dal.Alarm;
import it.bz.idm.bdp.dal.BasicData;
import it.bz.idm.bdp.dal.DataType;
import it.bz.idm.bdp.dal.Elaboration;
import it.bz.idm.bdp.dal.ElaborationHistory;
import it.bz.idm.bdp.dal.Station;
import it.bz.idm.bdp.dal.util.JPAUtil;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.RecordDto;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.dto.TypeDto;
import it.bz.idm.bdp.dto.bluetooth.BluetoothRecordDto;
import it.bz.idm.bdp.dto.parking.CarParkingDto;
import it.bz.idm.bdp.dto.parking.ParkingRecordExtendedDto;
import it.bz.idm.bdp.dto.parking.ParkingStationDto;
import it.bz.tis.integreen.util.IntegreenException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;

import org.opengis.geometry.MismatchedDimensionException;

@Entity
public class ParkingStation extends Station{
	private static final String DEFAULT_DATE_PATTERN = "dd/MM/yyyy HH:mm";
	public static final List<String[]> DATATYPES = new ArrayList<String[]>(){
		private static final long serialVersionUID = -9025329745332375781L;

		{
			add(new String[]{"occupied","","","300"});

			add(new String[]{"free","","","300"});

		}
	};
	private static final TypeDto staticAddtition1 = new TypeDto("occupied",300);
	private static final TypeDto staticAddtition2 = new TypeDto("free",300);

	@Override
	public List<StationDto> convertToDtos(EntityManager em, List<Station> resultList) {
		List<StationDto> parkingList = new ArrayList<StationDto>();
		for (Station s:resultList){
			ParkingStation station = (ParkingStation) s;
			CarParkingBasicData basicData = new CarParkingBasicData().findByStation(em, station);
			if (basicData == null)
				continue;
			Double x = null,y = null;
			if (station.getPointprojection() != null){
				y = station.getPointprojection().getY();
				x = station.getPointprojection().getX();
			}
			CarParkingDto dto = new CarParkingDto(station.getStationcode(),station.getName(),y,x);
			dto.setCapacity(basicData.getCapacity());
			dto.setDisabledcapacity(basicData.getDisabledcapacity());
			dto.setDisabledtoiletavailable(basicData.getDisabledtoiletavailable());
			dto.setEmail(basicData.getEmail());
			dto.setMainaddress(basicData.getMainaddress());
			dto.setOwneroperator(basicData.getOwneroperator());
			dto.setParkingtype(basicData.getParkingtype());
			dto.setPermittedvehicletypes(basicData.getPermittedvehicletypes());
			dto.setPhonenumber(basicData.getPhonenumber());
			dto.setState(basicData.getState());
			dto.setToiletsavailable(basicData.getToiletsavailable());
			dto.setUrl(basicData.getUrl());
			parkingList.add(dto);
		}
		return parkingList;

	}
	public static Map<String, Object> findParkingStation(String identifier){
		Map<String, Object> areaMap= new HashMap<String, Object>();
		EntityManager em = JPAUtil.createEntityManager();
		try {
			TypedQuery<CarParkingBasicData> query = em.createQuery("select basicdata from CarParkingBasicData basicdata where basicdata.station.stationcode=:code and basicdata.station.active=:active",CarParkingBasicData.class).setMaxResults(1);
			query.setParameter("code", identifier);
			query.setParameter("active",true);
			List<CarParkingBasicData> resultList = query.getResultList();
			if (resultList.size() == 1){
				CarParkingBasicData basicData = resultList.get(0);
				areaMap.put("name", basicData.getStation().getName());
				areaMap.put("slots", basicData.getCapacity());
				areaMap.put("latitude", basicData.getStation().getPointprojection().getY());
				areaMap.put("longitude", basicData.getStation().getPointprojection().getX());
				areaMap.put("address", basicData.getMainaddress());
				areaMap.put("phone", basicData.getPhonenumber());
				areaMap.put("description", basicData.getStation().getDescription());
			}
			return areaMap;
		} catch (MismatchedDimensionException e) {
			e.printStackTrace();
			return areaMap;
		}finally{
			em.close();
		}
	}	
	public static List<ParkingStationDto> findParkingStationsMetadata() {
		EntityManager em = JPAUtil.createEntityManager();
		TypedQuery<CarParkingBasicData> query =em.createQuery("select basicdata from CarParkingBasicData basicdata where basicdata.station.active=:active",CarParkingBasicData.class);
		query.setParameter("active",true);
		List<CarParkingBasicData> resultList = query.getResultList();
		em.close();
		List<ParkingStationDto> parkingList = new ArrayList<ParkingStationDto>();
		if (resultList.isEmpty())
			return new ArrayList<ParkingStationDto>();
		for (CarParkingBasicData basicData: resultList){
			ParkingStationDto dto = new ParkingStationDto();
			dto.setId(basicData.getStation().getStationcode());
			dto.setName(basicData.getStation().getName());
			dto.setSlots(basicData.getCapacity());
			dto.setLatitude(basicData.getStation().getPointprojection().getY());
			dto.setLongitude(basicData.getStation().getPointprojection().getX());
			dto.setAddress(basicData.getMainaddress());
			dto.setPhone(basicData.getPhonenumber());
			parkingList.add(dto);
		}
		return parkingList;
	}

	public static Integer findNumberOfFreeSlots(String identifier) {
		EntityManager em = JPAUtil.createEntityManager();
		TypedQuery<Integer> query = em.createQuery("SELECT dynamic.occupacy from CarParkingDynamic dynamic WHERE dynamic.station.stationcode=?1 AND dynamic.station.active=?2",Integer.class).setMaxResults(1);
		query.setParameter(1, identifier);
		query.setParameter(2,true);
		List<Integer> resultList = query.getResultList();
		em.close();
		if (resultList.size() == 1){
			Integer occupiedSlots= resultList.get(0);
			Integer parkingStationCapacity = getParkingStationCapacity(identifier);
			return parkingStationCapacity-occupiedSlots;
		}
		return -1;
	}

	public static List<Object[]> findStoricData(String identifier, Integer minutes) {
		Calendar cal = Calendar.getInstance();
		Date longAgo = new Date(cal.getTimeInMillis()-(minutes*60l*1000l)); 
		EntityManager em = JPAUtil.createEntityManager();
		TypedQuery<Object[]> query = em.createQuery("SELECT dynamic.occupacy,dynamic.lastupdate FROM CarParkingDynamicHistory dynamic WHERE dynamic.station.stationcode=?1 AND dynamic.station.active=?3 AND dynamic.lastupdate > ?2 order by dynamic.lastupdate",Object[].class);
		query.setParameter(1, identifier);
		query.setParameter(2, longAgo,TemporalType.TIMESTAMP);
		query.setParameter(3,true);
		List<Object[]> results = query.getResultList();
		em.close();
		return results;
	}

	public static List<Object> findFreeSlotsByTimeFrame(String identifier,
			String startDateString, String endDateString, String datePattern) {
		if (datePattern==null)
			datePattern=DEFAULT_DATE_PATTERN;
		List<Object> results = null;
		Date startDate,endDate;
		EntityManager em = JPAUtil.createEntityManager();
		try {
			SimpleDateFormat formatter = new SimpleDateFormat(datePattern);
			startDate = formatter.parse(startDateString);
			endDate = formatter.parse(endDateString);
			TypedQuery<Object> query = em.createQuery("SELECT dynamic.occupacy,dynamic.lastupdate FROM CarParkingDynamicHistory dynamic WHERE dynamic.station.stationcode=?1 AND dynamic.station.active=?4 AND dynamic.lastupdate BETWEEN ?2 AND ?3 order by dynamic.lastupdate desc",Object.class);
			query.setParameter(1, identifier);
			query.setParameter(4,true);
			query.setParameter(3, endDate,TemporalType.TIMESTAMP);
			query.setParameter(2, startDate,TemporalType.TIMESTAMP);
			results = query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			em.close();
		}
		return results;
	}

	public static Integer getParkingStationCapacity(String identifier) {
		EntityManager em = JPAUtil.createEntityManager();
		TypedQuery<Integer> query = em.createQuery("select basic.capacity from CarParkingBasicData basic where basic.station.stationcode=:station_id",Integer.class).setMaxResults(1);
		query.setParameter("station_id",identifier);
		Integer result = query.getSingleResult();
		em.close();
		return result;
	}

	@Override
	public List<String[]> findDataTypes(EntityManager em, String stationId) {
		List<String[]> dataTypes;
		TypedQuery<Object[]> query;
		if (stationId == null) {
			query = em.createQuery("SELECT elab.type.cname,elab.type.cunit,elab.type.description,elab.period FROM Elaboration elab INNER JOIN elab.type where elab.station.class=:stationType GROUP BY elab.type.cname,elab.type.cunit,elab.type.description,elab.period",Object[].class);
			query.setParameter("stationType", this.getClass().getSimpleName());
		} else {
			query = em.createQuery("SELECT elab.type.cname,elab.type.cunit,elab.type.description,elab.period FROM Elaboration elab INNER JOIN elab.type where elab.station.class=:stationType AND elab.station.stationcode=:station GROUP BY elab.type.cname,elab.type.cunit,elab.type.description,elab.period",Object[].class);
			query.setParameter("stationType", this.getClass().getSimpleName());
			query.setParameter("station",stationId);
		}
		List<Object[]> resultList = query.getResultList();
		dataTypes= getDataTypesFromQuery(resultList);
		dataTypes.addAll(ParkingStation.DATATYPES);
		return dataTypes;
	}
	@Override
	public List<TypeDto> findTypes(EntityManager em, String stationId) {
		TypedQuery<Object[]> query;
		if (stationId == null) {
			query = em.createQuery("SELECT type,elab.period FROM Elaboration elab INNER JOIN elab.type type  where elab.station.class=:stationType GROUP BY type,elab.period",Object[].class);
			query.setParameter("stationType", this.getClass().getSimpleName());
		} else {
			query = em.createQuery("SELECT type,elab.period FROM Elaboration elab INNER JOIN elab.type type where elab.station.class=:stationType AND elab.station.stationcode=:station GROUP BY type,elab.period",Object[].class);
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
		types.add(staticAddtition1);
		types.add(staticAddtition2);

		return types;
	}
	@Override
	public Date getDateOfLastRecord(EntityManager em, Station station, DataType type,Integer period) {
		Date date = null;
		if (station != null){
			String queryString = "select record.timestamp from Elaboration record where record.station=:station";
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
	public RecordDto findLastRecord(EntityManager em, String cname, Integer period) {
		DataType type = DataType.findByCname(em,cname);
		int capacity = 0;
		if (!"occupied".equals(cname)){
			CarParkingBasicData data =  new CarParkingBasicData().findByStation(em,this);
			capacity = data.getCapacity();
		}
		ParkingRecordExtendedDto dto;
		//TODO: change if condition, once free and occupied are in db "Parking forecast".equals(cname)
		if (type != null) {
			Elaboration elab = new  Elaboration().findLastRecord(em,this, type, period);
			dto = new ParkingRecordExtendedDto(elab.getTimestamp().getTime(), Math.abs(capacity-elab.getValue().intValue()),elab.getCreated_on().getTime());
		}else{
			CarParkingDynamic dynamic = CarParkingDynamic.findLastRecord(em,this,period);
			Integer value = ("free".equals(cname)) ? capacity-dynamic.getOccupacy() : dynamic.getOccupacy();
			dto = new ParkingRecordExtendedDto(dynamic.getLastupdate().getTime(),value,dynamic.getCreatedate().getTime());
		}
		return dto;
	}

	@Override
	public List<RecordDto> getRecords(EntityManager em, String type, Date start, Date end,
			Integer period) {
		DataType dataType = DataType.findByCname(em,type);
		List<RecordDto> records = new ArrayList<RecordDto>();
		//TODO: change if free and occupied are mapped in db
		if (dataType != null) {
			records = ElaborationHistory.findRecords(em,ParkingStation.class.getSimpleName(), this.stationcode, type, start,end,period);
			records = changeToFree(records);
		}else
			records = CarParkingDynamicHistory.findRecords(em,this.stationcode,type,start,end);
		return records;
	}
	private List<RecordDto> changeToFree(List<RecordDto> records) {
		List<RecordDto> recordDtos = new ArrayList<RecordDto>();
		Integer capacity = getParkingStationCapacity(stationcode);
		for (RecordDto dto : records){
			BluetoothRecordDto pdto = (BluetoothRecordDto) dto;
			pdto.setValue(capacity-pdto.getValue());
			recordDtos.add(pdto);
		}
		return recordDtos;
	}

	@Override
	public Object pushRecords(EntityManager em, Object... objects) {
		Object object = objects[0];
		if (object instanceof Map){
			Map<String, Object> parkingData = (Map) object;
			String stationcode=parkingData.get("station-code").toString();
			//Integer state =Integer.valueOf(parkingData.get("state-integer").toString()); 
			Integer slots = Integer.valueOf(parkingData.get("numberOfParkingSlots-integer").toString());
			Date timestamp = new Date(1000l*Integer.valueOf(parkingData.get("timestamp-integer").toString()));
			Boolean communicationState = Byte.valueOf(parkingData.get("communicationState-boolean").toString())==1?true:false;
			Boolean controlUnit = Byte.valueOf(parkingData.get("controlUnit-boolean").toString())==1?true:false;
			Boolean totalChangeAllarm = Byte.valueOf(parkingData.get("totalChangeAllarm-boolean").toString())==1?true:false;
			Boolean inactiveAllarm = Byte.valueOf(parkingData.get("inactiveAllarm-boolean").toString())==1?true:false;
			Boolean occupiedSlotsAllarm = Byte.valueOf(parkingData.get("occupiedSlotsAllarm-boolean").toString())==1?true:false;

			ParkingStation station = (ParkingStation) findStation(em,stationcode);
			if (station == null || ! station.getActive())
				return new IntegreenException("Station does not exist","One or more stations do not exist. Resynchronize stations");
			CarParkingDynamic lastRecord = CarParkingDynamic.findByParkingStation(em,station);
			if (lastRecord == null){
				lastRecord = new CarParkingDynamic();
				lastRecord.setStation(station);
			}
			BasicData basicData = new CarParkingBasicData().findByStation(em,station);
			CarParkingBasicData carData = (CarParkingBasicData) basicData;

			em.getTransaction().begin();
			int occupacy = carData.getCapacity() - slots;
			int occupacypercentage = Math.round(100f * occupacy/carData.getCapacity());
			lastRecord.setOccupacy(occupacy);
			lastRecord.setOccupacypercentage(occupacypercentage);
			lastRecord.setLastupdate(timestamp);
			lastRecord.setCreatedate(new Date());
			em.merge(lastRecord);

			CarParkingDynamicHistory historyRecord = new CarParkingDynamicHistory(station,occupacy,timestamp,occupacypercentage);
			em.persist(historyRecord);
			if (communicationState)
				Alarm.createAllarm(em,"communication-status","Stato communicazione",station,timestamp);
			if (controlUnit)
				Alarm.createAllarm(em,"control-unit-status","Stato centralina",station,timestamp);
			if (totalChangeAllarm)
				Alarm.createAllarm(em,"capacity","cambio significativo dei posti totali della periferia",station,timestamp);
			if (inactiveAllarm)
				Alarm.createAllarm(em,"inactive","lungo periodo di inattività del parcheggio",station,timestamp);
			if (occupiedSlotsAllarm)
				Alarm.createAllarm(em,"occupiedSlots","cambio brusco del numero di posti occupati",station,timestamp);
			em.getTransaction().commit();
		}else if (object instanceof Object[]){
			Object[] recordParameters =(Object[]) object;
			DataType type = DataType.findByCname(em,DataTypeDto.PARKING_FORECAST);
			ParkingStation station = (ParkingStation) findStation(em,recordParameters[0].toString());
			Date timestamp = (Date) recordParameters[1];
			Double slots = (Double) recordParameters[2];
			Integer period = Integer.valueOf(recordParameters[3].toString());
			ElaborationHistory prediction = ElaborationHistory.findRecordByProps(em,station, type,timestamp, period);
			if (prediction == null) {
				Elaboration lastPrediction = new Elaboration().findLastRecord(em,station, type, period);
				double value = slots != null ? slots.doubleValue() : -1.;
				if (lastPrediction == null){
					lastPrediction = new Elaboration(station,type,value,timestamp, period);
				}else{
					lastPrediction.setCreated_on(new Date());
					lastPrediction.setTimestamp(timestamp);
					lastPrediction.setValue(value);
				}
				prediction = new ElaborationHistory(station,type,value,timestamp,period);
				em.getTransaction().begin();
				em.merge(lastPrediction);
				em.persist(prediction);
				em.getTransaction().commit();
			}
		}
		return "";
	}
	@Override
	public void sync(EntityManager em, Station station, StationDto dto) {
		if (dto instanceof ParkingStationDto){
			ParkingStationDto stationDto = (ParkingStationDto) dto;
			CarParkingBasicData basic = CarParkingBasicData.findBasicByStation(em, station);
			if (basic == null)  {
				basic = new CarParkingBasicData();
				basic.setStation(station);
				em.persist(basic);
			}
			basic.setCapacity(stationDto.getSlots());
			if (stationDto.getAddress() != null)
				basic.setMainaddress(stationDto.getAddress());
			em.merge(basic);
		}
	}


}
