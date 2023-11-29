// Copyright © 2018 IDM Südtirol - Alto Adige (info@idm-suedtirol.com)
// Copyright © 2019 NOI Techpark - Südtirol / Alto Adige (info@opendatahub.com)
//
// SPDX-License-Identifier: GPL-3.0-only

package com.opendatahub.timeseries.bdp.dto.utils;

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
		this.exceptionName = exception.getClass().getName();	
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
