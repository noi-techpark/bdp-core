package it.bz.idm.bdp.dto.vehicles;

import java.util.List;

import it.bz.idm.bdp.dto.vehicles.Car;

public class CarRecord {
	 private List<Car> carData;
	 public CarRecord() {
	 }
	public List<Car> getCarData() {
		return carData;
	}
	public void setCarData(List<Car> carData) {
		this.carData = carData;
	}
	 
}
