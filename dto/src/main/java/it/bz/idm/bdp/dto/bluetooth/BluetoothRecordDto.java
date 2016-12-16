package it.bz.idm.bdp.dto.bluetooth;

import it.bz.idm.bdp.dto.RecordDtoImpl;


public class BluetoothRecordDto extends RecordDtoImpl{


	private static final long serialVersionUID = 5703758724961079739L;
	private Double value;
	public BluetoothRecordDto() {
	}
	public BluetoothRecordDto(Long timestamp,Double value) {
		this.timestamp = timestamp;
		this.value = value;
	}
	public Double getValue() {
		return value;
	}
	public void setValue(Double value) {
		this.value = value;
	}
}
