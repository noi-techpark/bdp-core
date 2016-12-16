package it.bz.idm.bdp.dal.bikesharing;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.ManyToOne;
import javax.persistence.TypedQuery;

import it.bz.idm.bdp.dal.BasicData;
import it.bz.idm.bdp.dal.DataType;
import it.bz.idm.bdp.dal.Station;

@Entity
public class BikesharingStationBasicData extends BasicData {

	@ManyToOne(cascade=CascadeType.MERGE)
	private DataType type;
	
	private Integer max_available;
	
	public BikesharingStationBasicData() {
	}
	
	public DataType getType() {
		return type;
	}

	public void setType(DataType type) {
		this.type = type;
	}

	public Integer getMax_available() {
		return max_available;
	}

	public void setMax_available(Integer max_available) {
		this.max_available = max_available;
	}

	public static List<BikesharingStationBasicData> findBasicByStation(EntityManager em, Station station) {
		TypedQuery<BikesharingStationBasicData> query = em.createQuery("Select basicData from BikesharingStationBasicData basicData where basicData.station=:station", BikesharingStationBasicData.class);
		query.setParameter("station", station);
		return query.getResultList();
	}

	public static BikesharingStationBasicData findByStationAndType(EntityManager entityManager, Station station, DataType type){
		TypedQuery<BikesharingStationBasicData> query = entityManager.createQuery("Select basicData from BikesharingStationBasicData basicData where basicData.station=:station AND basicData.type=:type", BikesharingStationBasicData.class);
		query.setParameter("station", station);
		query.setParameter("type", type);
		List<BikesharingStationBasicData> resultList = query.getResultList();
		return !resultList.isEmpty()?resultList.get(0):null;
	}

	public static List<BikesharingStationBasicData> findAllBikeStations(
			EntityManager em) {
		TypedQuery<BikesharingStationBasicData> query = em.createQuery("Select basic from BikesharingStationBasicData basic where basic.station.active=true", BikesharingStationBasicData.class);
		return query.getResultList();
	}

	@Override
	public BasicData findByStation(EntityManager em, Station station) {
		// TODO Auto-generated method stub
		return null;
	}
}
