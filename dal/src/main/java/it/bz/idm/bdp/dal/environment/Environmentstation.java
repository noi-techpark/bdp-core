package it.bz.idm.bdp.dal.environment;


import javax.persistence.Entity;
import javax.persistence.EntityManager;

import it.bz.idm.bdp.dal.MeasurementStation;
import it.bz.idm.bdp.dal.Station;
import it.bz.idm.bdp.dto.StationDto;

@Entity(name="Environmentstation")
public class Environmentstation extends MeasurementStation{

	public Environmentstation() {
		super();
	}
	public Environmentstation(String stationcode) {
		super();
		this.stationcode = stationcode;
		this.name = stationcode;
	}
	
	@Override
	public void sync(EntityManager em, Station station, StationDto dto) {
		
	}
}
