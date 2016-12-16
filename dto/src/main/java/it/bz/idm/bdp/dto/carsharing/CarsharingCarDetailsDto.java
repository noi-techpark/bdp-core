package it.bz.idm.bdp.dto.carsharing;

import it.bz.idm.bdp.dto.StationDto;


public class CarsharingCarDetailsDto extends StationDto{

	private static final long serialVersionUID = -899771900803503862L;
	private String brand;
	private String parentStation;
	private String licensePlate;

	public CarsharingCarDetailsDto() {
	}

	public CarsharingCarDetailsDto(String stationcode, String name, Double y,
			Double x, String brand, String parentStation, String licensePlate) {
		super(stationcode, name, y, x);
		this.brand = brand;
		this.parentStation = parentStation;
		this.licensePlate = licensePlate;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getParentStation() {
		return parentStation;
	}

	public void setParentStation(String station) {
		this.parentStation = station;
	}

	public String getLicensePlate() {
		return licensePlate;
	}

	public void setLicensePlate(String licensePlate) {
		this.licensePlate = licensePlate;
	}
	

	
}
