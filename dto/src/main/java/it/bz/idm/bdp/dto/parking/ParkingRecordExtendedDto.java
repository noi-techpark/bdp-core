package it.bz.idm.bdp.dto.parking;

public class ParkingRecordExtendedDto extends ParkingRecordDto{

	/**
	 * 
	 */
	private static final long serialVersionUID = -285380889738388646L;
	private Long created_on;
	
	public ParkingRecordExtendedDto() {
	}
	
	public ParkingRecordExtendedDto(Long lastupdate, int abs, Long date) {
		super(lastupdate,abs);
		this.created_on = date;
	}

	public Long getCreated_on() {
		return created_on;
	}

	public void setCreated_on(Long created_on) {
		this.created_on = created_on;
	}
	
}
