package it.bz.idm.bdp.dto.bluetooth;

public class BluetoothRecordExtendedDto extends BluetoothRecordDto{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3113420019159385769L;
	private Long created_on;



	public BluetoothRecordExtendedDto() {
	}

	public BluetoothRecordExtendedDto(Long timestamp, Double value,Long created_on) {
		super(timestamp, value);
		this.created_on=created_on;
	}

	public Long getCreated_on() {
		return created_on;
	}

	public void setCreated_on(Long created_on) {
		this.created_on = created_on;
	}


}
