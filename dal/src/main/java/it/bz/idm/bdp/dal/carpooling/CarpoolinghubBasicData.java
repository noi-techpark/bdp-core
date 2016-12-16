package it.bz.idm.bdp.dal.carpooling;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.OneToMany;
import javax.persistence.TypedQuery;

import it.bz.idm.bdp.dal.BasicData;
import it.bz.idm.bdp.dal.Station;
@Entity
public class CarpoolinghubBasicData extends BasicData{

	@Override
	public BasicData findByStation(EntityManager em, Station station) {
		TypedQuery<CarpoolinghubBasicData> query = em.createQuery("Select basic from CarpoolinghubBasicData basic where basic.station=:station", CarpoolinghubBasicData.class);
		query.setParameter("station", station);
		List<CarpoolinghubBasicData> resultList = query.getResultList();
		return !resultList.isEmpty()?resultList.get(0):null;
	}
	
	@OneToMany(cascade = CascadeType.ALL)
	private Map<Locale, Translation> i18n = new HashMap<Locale, Translation>();

	public Map<Locale, Translation> getI18n() {
		return i18n;
	}

	public void setI18n(Map<Locale, Translation> i18n) {
		this.i18n = i18n;
	}
}
