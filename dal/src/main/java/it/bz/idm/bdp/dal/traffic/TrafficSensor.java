package it.bz.idm.bdp.dal.traffic;

import javax.persistence.Entity;
import javax.persistence.EntityManager;

import it.bz.idm.bdp.dal.MeasurementStation;
import it.bz.idm.bdp.dal.Station;
import it.bz.idm.bdp.dto.StationDto;

@Entity
public class TrafficSensor extends MeasurementStation{

	@Override
	public void sync(EntityManager em, Station station, StationDto dto) {
		// TODO Auto-generated method stub
		
	}
}
