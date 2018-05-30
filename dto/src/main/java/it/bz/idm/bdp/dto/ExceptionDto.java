package it.bz.idm.bdp.dto;

import java.io.Serializable;

public class ExceptionDto implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2644337056228195945L;
	private Integer status;
	private String name;
	private String description;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
}
