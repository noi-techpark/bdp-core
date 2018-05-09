package it.bz.idm.bdp.dal.meteo;


import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Table;
import javax.persistence.TypedQuery;

import it.bz.idm.bdp.dal.BasicData;
import it.bz.idm.bdp.dal.Station;

@Table(name="meteostationbasicdata",schema="intime")
@Entity
public class MeteoBasicData extends BasicData{

	private String area;

	private String zeus;

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getZeus() {
		return zeus;
	}

	public void setZeus(String zeus) {
		this.zeus = zeus;
	}

	@Override
	public BasicData findByStation(EntityManager em, Station station) {
		TypedQuery<MeteoBasicData> query = em.createQuery("Select basicData from MeteoBasicData basicData where basicData.station=:station", MeteoBasicData.class);
		query.setParameter("station", station);
		List<MeteoBasicData> resultList = query.getResultList();
		return !resultList.isEmpty()?resultList.get(0):null;
	}
}
