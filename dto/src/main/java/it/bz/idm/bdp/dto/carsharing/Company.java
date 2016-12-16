package it.bz.idm.bdp.dto.carsharing;

import java.io.Serializable;

public class Company implements Serializable{
	private String uid;
	private String shortName;
	private String fullName;
	
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getShortName() {
		return shortName;
	}
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	
}
