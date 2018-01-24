package it.bz.idm.bdp.dto.parking;

import it.bz.idm.bdp.dto.RecordDtoImpl;

public class ParkingRecordDto extends RecordDtoImpl{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7799663790649466864L;

	private Integer value;
	
	public ParkingRecordDto() {
	}
	
	public ParkingRecordDto(Long timestamp, Integer value) {
		super();
		this.timestamp = timestamp;
		this.value = value;
	}
	public Integer getValue() {
		return value;
	}
	public void setValue(Integer value) {
		this.value = value;
	}
}
