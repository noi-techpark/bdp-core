package it.bz.idm.bdp.dto.carpooling;

import java.util.Map;

import it.bz.idm.bdp.dto.StationDto;

public class CarpoolingUserDto extends StationDto {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6758764073755896059L;
	private Character gender;
	private Boolean pendular;
	private String type;
	private String carType;
	private String hub;
	private String arrival;
	private String departure;
	private Map<String,LocationTranslationDto> hubI18n;
	private Map<String,LocationTranslationDto> location;
	
	public CarpoolingUserDto() {
	}
	public CarpoolingUserDto(String stationcode, String name, Double y, Double x, String carType, Character gender,
			String type, Boolean pendular,  String arrival, String departure, String hub, Map<String,LocationTranslationDto> hubI18n,Map<String,LocationTranslationDto> userLocation) {
		super(stationcode, name, y, x);
		this.carType = carType;
		this.gender = gender;
		this.type = type;
		this.pendular = pendular;
		this.hub = hub;
		this.hubI18n = hubI18n;
		this.location = userLocation;
		this.arrival = arrival;
		this.departure = departure;
	}
	
	public String getHub() {
		return hub;
	}
	public void setHub(String hub) {
		this.hub = hub;
	}
	public Map<String, LocationTranslationDto> getHubI18n() {
		return hubI18n;
	}
	public void setHubI18n(Map<String, LocationTranslationDto> hubI18n) {
		this.hubI18n = hubI18n;
	}
	public Character getGender() {
		return gender;
	}
	public void setGender(Character gender) {
		this.gender = gender;
	}
	
	public Boolean getPendular() {
		return pendular;
	}
	public void setPendular(Boolean pendular) {
		this.pendular = pendular;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getCarType() {
		return carType;
	}
	public void setCarType(String carType) {
		this.carType = carType;
	}
	public String getArrival() {
		return arrival;
	}
	public void setArrival(String arrival) {
		this.arrival = arrival;
	}
	public String getDeparture() {
		return departure;
	}
	public void setDeparture(String departure) {
		this.departure = departure;
	}
	public Map<String, LocationTranslationDto> getLocation() {
		return location;
	}
	public void setLocation(Map<String, LocationTranslationDto> location) {
		this.location = location;
	}
	
}
