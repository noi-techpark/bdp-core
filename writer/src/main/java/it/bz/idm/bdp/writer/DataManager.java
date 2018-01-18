package it.bz.idm.bdp.writer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Table;

import org.springframework.stereotype.Component;

import it.bz.idm.bdp.dal.DataType;
import it.bz.idm.bdp.dal.MeasurementStringHistory;
import it.bz.idm.bdp.dal.Station;
import it.bz.idm.bdp.dal.util.JPAUtil;
import it.bz.idm.bdp.dto.StationDto;

@Component
public class DataManager {

	public Object pushRecords(String stationType, Object... data){
		EntityManager em = JPAUtil.createEntityManager();
		Station station;
		try {
			station = (Station) JPAUtil.getInstanceByType(em,stationType);
			if (station != null)
				return station.pushRecords(em,data);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if (em.isOpen())
				em.close();
		}
		return null;
	}
	public Object syncStations(String stationType, Object...data){
		EntityManager em = JPAUtil.createEntityManager();
		try{
			Station station = (Station) JPAUtil.getInstanceByType(em,stationType);
			if (station != null)
				return station.syncStations(em,data);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			em.close();
		}
		return null;
	}
	public Object syncDataTypes(String stationType, Object...data){
		Object object = null;
		EntityManager em = JPAUtil.createEntityManager();
		try{
			object = DataType.sync(em,data);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			em.close();
		}
		return object;
	}

	public Object getDateOfLastRecord(String stationtype,String stationcode,String type,Integer period){
		EntityManager em = JPAUtil.createEntityManager();
		Date date = new Date(-1);
		try{
			Station s = (Station) JPAUtil.getInstanceByType(em, stationtype);
			Station station = s.findStation(em,stationcode);
			DataType dataType = DataType.findByCname(em,type);
			if (station != null) {
				date = station.getDateOfLastRecord(em,station, dataType, period);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		finally{
			em.close();	
		}
		return date;
	}

	public Object getLatestMeasurementStringRecord(String stationtype, String id){
		Date date = null;
		EntityManager em = JPAUtil.createEntityManager();
		date = MeasurementStringHistory.findTimestampOfNewestRecordByStationId(em,stationtype, id);
		em.close();
		return date;
	}
	public List<StationDto> getStationsWithoutMunicipality(){
		List<StationDto> stationsDtos = new ArrayList<StationDto>();
		EntityManager em = JPAUtil.createEntityManager();
		List<Station> stations = Station.findStationsWithoutMunicipality(em);
		for (Station station : stations) {
			StationDto dto = station.convertToDto(station);
			String name = JPAUtil.getEntityNameByObject(station);
			dto.setStationType(name);
			stationsDtos.add(dto);
		}
		em.close();
		return stationsDtos;
	}
	public void patchStations(List<StationDto> stations) {
		EntityManager em = JPAUtil.createEntityManager();
		em.getTransaction().begin();
		for (StationDto dto:stations) {
			Station.patch(em,dto);
		}
		em.getTransaction().commit();
	}

}
