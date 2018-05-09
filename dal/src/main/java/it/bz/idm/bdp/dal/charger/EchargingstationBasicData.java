package it.bz.idm.bdp.dal.charger;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Lob;
import javax.persistence.TypedQuery;

import org.hibernate.annotations.Type;

import it.bz.idm.bdp.dal.BasicData;
import it.bz.idm.bdp.dal.Station;
import it.bz.idm.bdp.dal.util.JPAUtil;


@Entity
public class EchargingstationBasicData extends BasicData{
	private String assetProvider;
	private String city;
	private String address;
	private Integer chargingPointsCount;
	private String state;
	@Lob
	@Type(type="text")
	private String paymentInfo;
	private String accessInfo;
	private String flashInfo;
	private String locationServiceInfo;
	private Boolean reservable;
	private String accessType;

	private String categories;


	public Boolean getReservable() {
		return reservable;
	}
	public void setReservable(Boolean reservable) {
		this.reservable = reservable;
	}
	public String getAssetProvider() {
		return assetProvider;
	}
	public void setAssetProvider(String assetProvider) {
		this.assetProvider = assetProvider;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public Integer getChargingPointsCount() {
		return chargingPointsCount;
	}
	public void setChargingPointsCount(Integer chargingPointsCount) {
		this.chargingPointsCount = chargingPointsCount;
	}

	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getPaymentInfo() {
		return paymentInfo;
	}
	public void setPaymentInfo(String paymentInfo) {
		this.paymentInfo = paymentInfo;
	}
	public String getAccessInfo() {
		return accessInfo;
	}
	public void setAccessInfo(String accessInfo) {
		this.accessInfo = accessInfo;
	}
	public String getFlashInfo() {
		return flashInfo;
	}
	public void setFlashInfo(String flashInfo) {
		this.flashInfo = flashInfo;
	}
	public String getLocationServiceInfo() {
		return locationServiceInfo;
	}
	public void setLocationServiceInfo(String locationServiceInfo) {
		this.locationServiceInfo = locationServiceInfo;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getAccessType() {
		return accessType;
	}
	public void setAccessType(String accessType) {
		this.accessType = accessType;
	}
	public String getCategories() {
		return categories;
	}
	public void setCategories(String categories) {
		this.categories = categories;
	}
	@Override
	public BasicData findByStation(EntityManager em, Station station)  {
		TypedQuery<EchargingstationBasicData> typedQuery = em.createQuery("select basic from EchargingstationBasicData basic where basic.station = :station and basic.station.active=:active",EchargingstationBasicData.class);
		typedQuery.setParameter("station", station);
		typedQuery.setParameter("active",true);
		return (EchargingstationBasicData) JPAUtil.getSingleResultOrNull(typedQuery);
	}
	public static List<EchargingstationBasicData> findAllEchargingStations(
			EntityManager entityManager) {
		TypedQuery<EchargingstationBasicData> query = entityManager.createQuery("Select basic from EchargingstationBasicData basic where basic.station.active=true", EchargingstationBasicData.class);
		return query.getResultList();
	}
	public static EchargingstationBasicData findBasicByStation(
			EntityManager em, Station station) {
		TypedQuery<EchargingstationBasicData> typedQuery = em.createQuery("select basic from EchargingstationBasicData basic where basic.station = :station",EchargingstationBasicData.class);
		typedQuery.setParameter("station", station);
		return (EchargingstationBasicData) JPAUtil.getSingleResultOrNull(typedQuery);
	}
}
