package it.bz.idm.bdp.dal.parking;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.TypedQuery;

import it.bz.idm.bdp.dal.Station;
import it.bz.idm.bdp.dal.util.JPAUtil;

@Table(name="carparkingdynamic",schema="intime")
@Entity
public class CarParkingDynamic {

	@Id
    @GeneratedValue(generator="carparkingdynamic_id_seq",strategy=GenerationType.SEQUENCE)
	@SequenceGenerator(name="carparkingdynamic_id_seq", sequenceName = "carparkingdynamic_id_seq",schema="intime",allocationSize=1)
	private Integer id;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "station_id")
	private ParkingStation station;

	private String carparkstate;

	private String 	carparktrend;

	private Double exitrate;

	private Double fillrate;

	private Date lastupdate;

	private Date createdate;

	private Integer occupacy;

	private Integer occupacypercentage;

	public CarParkingDynamic() {
		// TODO Auto-generated constructor stub
	}

	public CarParkingDynamic(ParkingStation area,
			Integer occupacy, Date lastupdate) {
		this.station = area;
		this.occupacy = occupacy;
		this.lastupdate = lastupdate;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public ParkingStation getStation() {
		return station;
	}

	public void setStation(ParkingStation station) {
		this.station = station;
	}

	public String getCarparkstate() {
		return carparkstate;
	}

	public void setCarparkstate(String carparkstate) {
		this.carparkstate = carparkstate;
	}

	public String getCarparktrend() {
		return carparktrend;
	}

	public void setCarparktrend(String carparktrend) {
		this.carparktrend = carparktrend;
	}

	public Double getExitrate() {
		return exitrate;
	}

	public void setExitrate(Double exitrate) {
		this.exitrate = exitrate;
	}

	public Double getFillrate() {
		return fillrate;
	}

	public void setFillrate(Double fillrate) {
		this.fillrate = fillrate;
	}

	public Date getLastupdate() {
		return lastupdate;
	}

	public void setLastupdate(Date lastupdate) {
		this.lastupdate = lastupdate;
	}

	public Integer getOccupacy() {
		return occupacy;
	}

	public void setOccupacy(Integer occupacy) {
		this.occupacy = occupacy;
	}

	public Integer getOccupacypercentage() {
		return occupacypercentage;
	}

	public void setOccupacypercentage(Integer occupacypercentage) {
		this.occupacypercentage = occupacypercentage;
	}

	public Date getCreatedate() {
		return createdate;
	}

	public void setCreatedate(Date createdate) {
		this.createdate = createdate;
	}

	public static CarParkingDynamic findByParkingStation(EntityManager em,ParkingStation area) {
		TypedQuery<CarParkingDynamic> typedQuery = em.createQuery("select dynamic from CarParkingDynamic dynamic where dynamic.station.id = :area order by dynamic.lastupdate asc",CarParkingDynamic.class);
		typedQuery.setParameter("area", area.getId());
		return JPAUtil.getSingleResultOrNull(typedQuery);
	}

	public static CarParkingDynamic findLastRecord(EntityManager em,Station station, Integer period) {
		if (period == null)
			return findLastRecord(em,station);
		TypedQuery<CarParkingDynamic> query = em.createQuery("SELECT dynamic FROM CarParkingDynamic dynamic WHERE dynamic.station.id = :station order by dynamic.lastupdate asc",CarParkingDynamic.class);
		query.setParameter("station", station.getId());
		return JPAUtil.getSingleResultOrNull(query);
	}

	private static CarParkingDynamic findLastRecord(EntityManager em, Station station) {
		if (station == null)
			return null;
		TypedQuery<CarParkingDynamic> query = em.createQuery("SELECT dynamic FROM CarParkingDynamic dynamic WHERE dynamic.station.id = :station order by dynamic.lastupdate asc",CarParkingDynamic.class);
		query.setParameter("station", station.getId());
		return JPAUtil.getSingleResultOrNull(query);
	}

}