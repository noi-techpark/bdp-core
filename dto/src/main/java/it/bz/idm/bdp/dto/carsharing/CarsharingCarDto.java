package it.bz.idm.bdp.dto.carsharing;

import it.bz.idm.bdp.dto.ChildDto;

public class CarsharingCarDto extends ChildDto{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8760955240358094390L;
	private String brand;
	private String licensePlate;
	public String getBrand() {
		return brand;
	}
	public void setBrand(String brand) {
		this.brand = brand;
	}
	public String getLicensePlate() {
		return licensePlate;
	}
	public void setLicensePlate(String licensePlate) {
		this.licensePlate = licensePlate;
	}
	
	
	
}
