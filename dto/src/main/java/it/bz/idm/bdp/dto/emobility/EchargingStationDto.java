package it.bz.idm.bdp.dto.emobility;

import it.bz.idm.bdp.dto.StationDto;

public class EchargingStationDto extends StationDto {
	

	private static final long serialVersionUID = 4311121269301102980L;
	
	private Integer capacity;
	private String provider;
	private String city;
	private String state;
	private String paymentInfo;
	private String accessInfo;
	private String accessType;
	private String[] categories;
	private String flashInfo;
	private String locationServiceInfo;
	private String address;
	private Boolean reservable;
	
	/**
	 * 
	 */
	public EchargingStationDto() {
	}
	
	public EchargingStationDto(String stationcode, String name, Double y,
			Double x,Integer capacity, String provider, String city, String state,String address) {
		super(stationcode, name, y, x);
		this.capacity = capacity;
		this.provider = provider;
		this.city = city;
		this.state = state;
		this.address = address;
	}
	public Integer getCapacity() {
		return capacity;
	}

	public void setCapacity(Integer capacity) {
		this.capacity = capacity;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
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

	public Boolean getReservable() {
		return reservable;
	}

	public void setReservable(Boolean reservable) {
		this.reservable = reservable;
	}

	public String getAccessType() {
		return accessType;
	}

	public void setAccessType(String accessType) {
		this.accessType = accessType;
	}

	public String[] getCategories() {
		return categories;
	}

	public void setCategories(String[] categories) {
		this.categories = categories;
	}
}
