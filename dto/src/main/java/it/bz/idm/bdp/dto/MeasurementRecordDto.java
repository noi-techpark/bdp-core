package it.bz.idm.bdp.dto;

import it.bz.idm.bdp.dto.RecordDtoImpl;

@Deprecated
/*
 * Use SimpleRecordDto instead
 * */
public class MeasurementRecordDto extends RecordDtoImpl{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1985973952169964811L;
	private Long timestamp;
	private Double value;
	private String stationId;
	private String typeId;
	private Integer period;
	
	public MeasurementRecordDto() {
	}

	public MeasurementRecordDto(Long timestamp, Double value, String stationId,
			String typeId) {
		super();
		this.timestamp = timestamp;
		this.value = value;
		this.stationId = stationId;
		this.typeId = typeId;
	}

	public String getStationId() {
		return stationId;
	}

	public void setStationId(String stationId) {
		this.stationId = stationId;
	}

	public String getTypeId() {
		return typeId;
	}

	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}

	public Long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}
	public Double getValue() {
		return value;
	}
	public void setValue(Double value) {
		this.value = value;
	}

	public Integer getPeriod() {
		return period;
	}

	public void setPeriod(Integer period) {
		this.period = period;
	}
	
}
