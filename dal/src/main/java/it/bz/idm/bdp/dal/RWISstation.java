package it.bz.idm.bdp.dal;


import javax.persistence.Entity;
import javax.persistence.EntityManager;

import it.bz.idm.bdp.dto.StationDto;

@Entity
public class RWISstation extends MeasurementStation {

	@Override
	public void sync(EntityManager em, Station station, StationDto dto) {
	}

}
