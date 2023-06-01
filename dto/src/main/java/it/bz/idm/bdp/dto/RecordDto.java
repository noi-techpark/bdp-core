// Copyright © 2018 IDM Südtirol - Alto Adige (info@idm-suedtirol.com)
// Copyright © 2019 NOI Techpark - Südtirol / Alto Adige (info@opendatahub.bz.it)
//
// SPDX-License-Identifier: GPL-3.0-only

package it.bz.idm.bdp.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * describes the validity of a record and makes it serializable
 *
 * @author Patrick Bertolla
 */
@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="_t")
public interface RecordDto extends Serializable{
	public abstract boolean validate();
}
