package it.bz.idm.bdp.dto.bluetooth;

import java.util.Date;

import it.bz.idm.bdp.dto.bluetooth.BluetoothRecordDto;

public class BluetoothRecordExtendedDto extends BluetoothRecordDto{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3113420019159385769L;
	private Date created_on;



	public BluetoothRecordExtendedDto() {
	}

	public BluetoothRecordExtendedDto(Long timestamp, Double value,Date created_on) {
		super(timestamp, value);
		this.created_on=created_on;
	}

	public Date getCreated_on() {
		return created_on;
	}

	public void setCreated_on(Date created_on) {
		this.created_on = created_on;
	}


}
