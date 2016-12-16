package it.bz.idm.bdp.dal.carsharing;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import it.bz.idm.bdp.dal.BasicData;
import it.bz.idm.bdp.dal.Station;

@Entity
public class CarsharingStationBasicData extends BasicData{

	private Boolean hasFixedParking;
	private Boolean canBookAhead;
	private Boolean spontaneously;
	private Integer parking;
	private String companyShortName;
	
	public Boolean isHasFixedParking() {
		return hasFixedParking;
	}
	public void setHasFixedParking(Boolean hasFixedParking) {
		this.hasFixedParking = hasFixedParking;
	}
	public Boolean isCanBookAhead() {
		return canBookAhead;
	}
	public void setCanBookAhead(Boolean canBookAhead) {
		this.canBookAhead = canBookAhead;
	}
	public Boolean isSpontaneously() {
		return spontaneously;
	}
	public void setSpontaneously(Boolean spontaneously) {
		this.spontaneously = spontaneously;
	}
	public Integer getParking() {
		return parking;
	}
	public void setParking(Integer parking) {
		this.parking = parking;
	}
	public String getCompanyShortName() {
		return companyShortName;
	}
	public void setCompanyShortName(String companyShortName) {
		this.companyShortName = companyShortName;
	}
	public static CarsharingStationBasicData findBasicByStation(EntityManager em, Station station) {
		TypedQuery<CarsharingStationBasicData> query = em.createQuery("Select basic from CarsharingStationBasicData basic where basic.station=:station", CarsharingStationBasicData.class);
		query.setParameter("station", station);
		List<CarsharingStationBasicData> resultList = query.getResultList();
		return !resultList.isEmpty()?resultList.get(0):null;
	}
	public static List<CarsharingStationBasicData> findAllCarsharingStations(EntityManager em) {
		TypedQuery<CarsharingStationBasicData> query = em.createQuery("Select basic from CarsharingStationBasicData basic where basic.station.active=true", CarsharingStationBasicData.class);
		return query.getResultList();

	}
	@Override
	public BasicData findByStation(EntityManager em, Station station) {
		TypedQuery<CarsharingStationBasicData> query = em.createQuery("Select basic from CarsharingStationBasicData basic where basic.station=:station", CarsharingStationBasicData.class);
		query.setParameter("station", station);
		List<CarsharingStationBasicData> resultList = query.getResultList();
		return !resultList.isEmpty()?resultList.get(0):null;
	}
}
