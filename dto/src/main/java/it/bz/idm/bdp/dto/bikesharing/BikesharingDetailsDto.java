package it.bz.idm.bdp.dto.bikesharing;

import java.util.HashMap;
import java.util.Map;

import it.bz.idm.bdp.dto.StationDto;


public class BikesharingDetailsDto extends StationDto{

	private static final long serialVersionUID = -899771900803503862L;
	private Map<String , Integer> bikes = new HashMap<String, Integer>();

	public Map<String, Integer> getBikes() {
		return bikes;
	}

	public void setBikes(Map<String, Integer> bikes) {
		this.bikes = bikes;
	}
	public BikesharingDetailsDto() {
	}
	public BikesharingDetailsDto(String stationcode, String name, Double y, Double x, Map<String , Integer> bikes) {
		super(stationcode, name, y, x);
		this.bikes = bikes;
	}

}
