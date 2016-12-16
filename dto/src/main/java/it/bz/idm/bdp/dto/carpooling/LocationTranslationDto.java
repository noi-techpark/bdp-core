package it.bz.idm.bdp.dto.carpooling;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(value=Include.NON_EMPTY)
public class LocationTranslationDto implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1285307850287359319L;
	private String address;
	private String name;
	private String city;
	
	public LocationTranslationDto() {
	}
	
	public LocationTranslationDto(String name, String address, String city) {
		this.address = address;
		this.name = name;
		this.city = city;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}
}
