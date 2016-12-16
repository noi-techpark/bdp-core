package it.bz.idm.bdp.dto.parking;

import it.bz.idm.bdp.dto.StationDto;

public class ParkingStationDto extends StationDto{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6027613238042191912L;
	private Integer slots;
	private String address;
	private String phone;

	public Integer getSlots() {
		return slots;
	}
	public void setSlots(Integer slots) {
		this.slots = slots;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
}
