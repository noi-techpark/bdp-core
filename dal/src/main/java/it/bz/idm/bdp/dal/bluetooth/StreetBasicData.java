package it.bz.idm.bdp.dal.bluetooth;


import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Table;
import javax.persistence.TypedQuery;

import com.vividsolutions.jts.geom.LineString;

import it.bz.idm.bdp.dal.BasicData;
import it.bz.idm.bdp.dal.Station;
import it.bz.idm.bdp.dal.util.JPAUtil;

@Table(name="streetbasicdata",schema="intime")
@Entity
public class StreetBasicData extends BasicData{

	private Double length;

	private LineString linegeometry;

	private String description;

	private Integer speed_default;

	private Short old_idstr;

	public Double getLength() {
		return length;
	}

	public void setLength(Double length) {
		this.length = length;
	}

	public LineString getLinegeometry() {
		return linegeometry;
	}

	public void setLinegeometry(LineString linegeometry) {
		this.linegeometry = linegeometry;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getSpeed_default() {
		return speed_default;
	}

	public void setSpeed_default(Integer speed_default) {
		this.speed_default = speed_default;
	}

	public Short getOld_idstr() {
		return old_idstr;
	}

	public void setOld_idstr(Short old_idstr) {
		this.old_idstr = old_idstr;
	}

	@Override
	public BasicData findByStation(EntityManager em, Station station) {
		TypedQuery<StreetBasicData> query = em.createQuery("select basicdata from StreetBasicData basicdata where basicdata.station=:station",StreetBasicData.class);
		query.setParameter("station", station);
		return JPAUtil.getSingleResultOrNull(query);
	}

}
