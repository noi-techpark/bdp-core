package it.bz.idm.bdp.util;

import java.io.Serializable;

public class IntegreenException implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4321307588943252863L;

	public IntegreenException() {
	}
	
	public IntegreenException(String name, String descString) {
		this.exceptionName = name;
		this.exceptionMessage = descString;
	}
	public IntegreenException(Throwable exception) {
		this.exceptionMessage = exception.getMessage();
	}


	private String exceptionMessage;
	private String exceptionName;

	public String getExceptionMessage() {
		return exceptionMessage;
	}
	public void setExceptionMessage(String exceptionMessage) {
		this.exceptionMessage = exceptionMessage;
	}
	public String getExceptionName() {
		return exceptionName;
	}
	public void setExceptionName(String exceptionName) {
		this.exceptionName = exceptionName;
	}
	
}
