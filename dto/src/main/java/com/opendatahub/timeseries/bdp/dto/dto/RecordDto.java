// Copyright © 2018 IDM Südtirol - Alto Adige (info@idm-suedtirol.com)
// Copyright © 2019 NOI Techpark - Südtirol / Alto Adige (info@opendatahub.com)
//
// SPDX-License-Identifier: GPL-3.0-only

package com.opendatahub.timeseries.bdp.dto.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

/**
 * describes the validity of a record and makes it serializable
 *
 * @author Patrick Bertolla
 */
@JsonTypeInfo(use = Id.DEDUCTION)
@JsonSubTypes({@JsonSubTypes.Type(SimpleRecordDto.class)})
// _t used to be the type information as a class name.
// now we just ignore it
@JsonIgnoreProperties(value = {"_t"})
public interface RecordDto extends Serializable{
	public abstract boolean validate();
}
