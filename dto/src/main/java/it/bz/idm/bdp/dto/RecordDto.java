package it.bz.idm.bdp.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="_type")
public interface RecordDto extends Serializable{
	public abstract boolean validate();
}
