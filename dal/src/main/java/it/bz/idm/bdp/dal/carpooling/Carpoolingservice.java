package it.bz.idm.bdp.dal.carpooling;

import javax.persistence.Entity;
import javax.persistence.EntityManager;

import it.bz.idm.bdp.dal.MeasurementStation;
import it.bz.idm.bdp.dal.Station;
import it.bz.idm.bdp.dto.StationDto;

@Entity
public class Carpoolingservice extends MeasurementStation{

	@Override
	public void sync(EntityManager em, Station station, StationDto dto) {
	}

}
