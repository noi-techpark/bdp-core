package it.bz.idm.bdp.dal.carsharing;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.ManyToOne;
import javax.persistence.TypedQuery;

import it.bz.idm.bdp.dal.BasicData;
import it.bz.idm.bdp.dal.Station;
import it.bz.idm.bdp.dal.util.JPAUtil;

@Entity
public class CarsharingCarStationBasicData extends BasicData{

	@ManyToOne(cascade = CascadeType.MERGE)
	private Carsharingstation carsharingStation;

	private String brand;
	private String licensePlate;

	public Carsharingstation getCarsharingStation() {
		return carsharingStation;
	}
	public void setCarsharingStation(Carsharingstation carsharingStation) {
		this.carsharingStation = carsharingStation;
	}
	public String getBrand() {
		return brand;
	}
	public void setBrand(String brand) {
		this.brand = brand;
	}
	public String getLicensePlate() {
		return licensePlate;
	}
	public void setLicensePlate(String licensePlate) {
		this.licensePlate = licensePlate;
	}

	public static CarsharingCarStationBasicData findBasicByStation(EntityManager em, Station station) {
		TypedQuery<CarsharingCarStationBasicData> query = em.createQuery("Select basicData from CarsharingCarStationBasicData basicData where basicData.station=:station", CarsharingCarStationBasicData.class);
		query.setParameter("station", station);
		return JPAUtil.getSingleResultOrNull(query);
	}
	public static List<CarsharingCarStationBasicData> findAllCars(
			EntityManager em) {
		TypedQuery<CarsharingCarStationBasicData> query = em.createQuery("Select basic from CarsharingCarStationBasicData basic where basic.station.active=true", CarsharingCarStationBasicData.class);
		return query.getResultList();
	}
	@Override
	public BasicData findByStation(EntityManager em, Station station) {
		TypedQuery<CarsharingCarStationBasicData> query = em.createQuery("Select basicData from CarsharingCarStationBasicData basicData where basicData.station=:station AND basicData.station.active = true", CarsharingCarStationBasicData.class);
		query.setParameter("station", station);
		return JPAUtil.getSingleResultOrNull(query);
	}
}
