// Copyright © 2018 IDM Südtirol - Alto Adige (info@idm-suedtirol.com)
// Copyright © 2019 NOI Techpark - Südtirol / Alto Adige (info@opendatahub.com)
//
// SPDX-License-Identifier: GPL-3.0-only

package it.bz.idm.bdp.dto;

@Deprecated
public class FullRecordDto extends RecordDtoImpl{

	private static final long serialVersionUID = 5279857732736895894L;
	private String station;
	private String type;
	private Object value;
	private Integer period;


	public FullRecordDto(Long timestamp, Object value, String station, String type, Integer period) {
		this.timestamp = timestamp;
		this.value = value;
		this.station = station;
		this.type = type;
		this.period = period;
	}

	public String getStation() {
		return station;
	}

	public void setStation(String station) {
		this.station = station;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public Object getValue() {
		return this.value;
	}


	public Integer getPeriod() {
		return period;
	}

	public void setPeriod(Integer period) {
		this.period = period;
	}

	@Override
	public boolean validate() {
		return this.getStation()!=null && this.getType()!=null && super.validate();
	}

}
