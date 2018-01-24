package it.bz.idm.bdp.dto.vehicles;

import java.io.Serializable;
import java.util.List;

import it.bz.idm.bdp.dto.vehicles.CarValue;

public class Car implements Serializable{
	
	private String vehicle_id;
	private List<CarValue> values;
	
	public String getVehicle_id() {
		return vehicle_id;
	}
	public void setVehicle_id(String vehicle_id) {
		this.vehicle_id = vehicle_id;
	}
	public List<CarValue> getValues() {
		return values;
	}
	public void setValues(List<CarValue> values) {
		this.values = values;
	}

	
}
