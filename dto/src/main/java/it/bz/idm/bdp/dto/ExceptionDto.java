package it.bz.idm.bdp.dto;

import java.io.Serializable;

public class ExceptionDto implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2644337056228195945L;
	private String name;
	private String stackTrace;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getStackTrace() {
		return stackTrace;
	}
	public void setStackTrace(String stackTrace) {
		this.stackTrace = stackTrace;
	}
}
