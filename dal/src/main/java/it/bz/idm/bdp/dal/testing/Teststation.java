package it.bz.idm.bdp.dal.testing;


import javax.persistence.Entity;
import javax.persistence.EntityManager;

import it.bz.idm.bdp.dal.MeasurementStation;
import it.bz.idm.bdp.dal.Station;
import it.bz.idm.bdp.dto.StationDto;

@Entity(name = "Teststation")
public class Teststation extends MeasurementStation {

	public Teststation() {
		super();
	}
	public Teststation(String stationcode) {
		super();
		this.stationcode = stationcode;
		this.name = stationcode;
	}

	@Override
	public void sync(EntityManager em, Station station, StationDto dto) {

	}
}
