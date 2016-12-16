package it.bz.idm.bdp.dal.carpooling;

import javax.persistence.Entity;

@Entity
public class HubTranslation extends Translation{
	private String name;
	private String address;
	private String city;
	
	public HubTranslation() {
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
}
