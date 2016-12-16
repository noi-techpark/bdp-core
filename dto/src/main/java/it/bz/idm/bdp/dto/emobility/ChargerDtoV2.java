package it.bz.idm.bdp.dto.emobility;

import java.util.List;

import it.bz.idm.bdp.dto.StationDto;

public class ChargerDtoV2 extends StationDto{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8562182298961356540L;

	private String address;
	private String provider;
	private String code;
	private String model;
	private String state;
	private boolean isOnline;
	private String paymentInfo;
	private String accessInfo;
	private String flashInfo;
	private String locationServiceInfo;
	private Boolean isReservable; 
	private ChargingPositionDto position; 
	private List<ChargingPointsDtoV2> chargingPoints;
	public String getProvider() {
		return provider;
	}
	public void setProvider(String provider) {
		this.provider = provider;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
		this.id = code;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public boolean getIsOnline() {
		return isOnline;
	}
	public void setIsOnline(boolean isOnline) {
		this.isOnline = isOnline;
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
	public Boolean getIsReservable() {
		return isReservable;
	}
	public void setIsReservable(Boolean isReservable) {
		this.isReservable = isReservable;
	}
	public ChargingPositionDto getPosition() {
		return position;
	}
	public void setPosition(ChargingPositionDto position) {
		this.position = position;
		this.longitude = this.position.getLongitude();
		this.latitude = this.position.getLatitude();
		this.address = this.position.getAddress();
	}
	public List<ChargingPointsDtoV2> getChargingPoints() {
		return chargingPoints;
	}
	public void setChargingPoints(List<ChargingPointsDtoV2> chargingPoints) {
		this.chargingPoints = chargingPoints;
	}
	public String getAddress() {
		return address;
	}
}
