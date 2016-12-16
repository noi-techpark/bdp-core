package it.bz.idm.bdp.dto.parking;

import it.bz.idm.bdp.dto.StationDto;

public class CarParkingDto extends StationDto {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7949755147529403260L;

	private Integer disabledcapacity;
	
	private Boolean disabledtoiletavailable;
	
	private String owneroperator;
	
	private String parkingtype;
	
	private String permittedvehicletypes;
	
	private Boolean toiletsavailable;
	
	private Integer capacity;
	
	private String phonenumber;
	
	private String email;
	
	private String url;
	
	private String mainaddress;
	
	private Integer state;

	public CarParkingDto() {
	}
	public CarParkingDto(String stationcode, String name, Double y, Double x) {
		super(stationcode,name,y,x);
	}

	public Integer getDisabledcapacity() {
		return disabledcapacity;
	}

	public void setDisabledcapacity(Integer disabledcapacity) {
		this.disabledcapacity = disabledcapacity;
	}

	public Boolean getDisabledtoiletavailable() {
		return disabledtoiletavailable;
	}

	public void setDisabledtoiletavailable(Boolean disabledtoiletavailable) {
		this.disabledtoiletavailable = disabledtoiletavailable;
	}

	public String getOwneroperator() {
		return owneroperator;
	}

	public void setOwneroperator(String owneroperator) {
		this.owneroperator = owneroperator;
	}

	public String getParkingtype() {
		return parkingtype;
	}

	public void setParkingtype(String parkingtype) {
		this.parkingtype = parkingtype;
	}

	public String getPermittedvehicletypes() {
		return permittedvehicletypes;
	}

	public void setPermittedvehicletypes(String permittedvehicletypes) {
		this.permittedvehicletypes = permittedvehicletypes;
	}

	public Boolean getToiletsavailable() {
		return toiletsavailable;
	}

	public void setToiletsavailable(Boolean toiletsavailable) {
		this.toiletsavailable = toiletsavailable;
	}

	public Integer getCapacity() {
		return capacity;
	}

	public void setCapacity(Integer capacity) {
		this.capacity = capacity;
	}

	public String getPhonenumber() {
		return phonenumber;
	}

	public void setPhonenumber(String phonenumber) {
		this.phonenumber = phonenumber;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getMainaddress() {
		return mainaddress;
	}

	public void setMainaddress(String mainaddress) {
		this.mainaddress = mainaddress;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}
	
	
}
