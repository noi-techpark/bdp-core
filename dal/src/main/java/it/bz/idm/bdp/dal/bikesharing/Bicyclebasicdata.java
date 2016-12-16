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
public class Bicyclebasicdata extends BasicData {

	@ManyToOne(cascade=CascadeType.MERGE)
	private DataType type;
	
	@ManyToOne(cascade = CascadeType.MERGE)
	private BikesharingStation bikeSharingStation;
	
	public Bicyclebasicdata() {
	}
	
	public DataType getType() {
		return type;
	}

	public void setType(DataType type) {
		this.type = type;
	}

	public BikesharingStation getBikeSharingStation() {
		return bikeSharingStation;
	}

	public void setBikeSharingStation(BikesharingStation bikeSharingStation) {
		this.bikeSharingStation = bikeSharingStation;
	}
	@Override
	public BasicData findByStation(EntityManager em, Station station) {
		TypedQuery<Bicyclebasicdata> query = em.createQuery("Select basicData from Bicyclebasicdata basicData where basicData.station=:station", Bicyclebasicdata.class);
		query.setParameter("station", station);
		List<Bicyclebasicdata> resultList = query.getResultList();
		return !resultList.isEmpty()?resultList.get(0):null;
	}
	public static List<Bicyclebasicdata> findAllBikes(
			EntityManager em) {
		TypedQuery<Bicyclebasicdata> query = em.createQuery("Select basic from Bicyclebasicdata basic where basic.station.active=true", Bicyclebasicdata.class);
		return query.getResultList();
	}

}
