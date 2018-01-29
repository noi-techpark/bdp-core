package it.bz.idm.bdp.dal.meteo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EntityManager;

import it.bz.idm.bdp.dal.MeasurementStation;
import it.bz.idm.bdp.dal.Station;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.dto.meteo.MeteoStationDto;

@Entity
public class Meteostation extends MeasurementStation {
	
	@Override
	public List<StationDto> convertToDtos(EntityManager em, List<Station> resultList) {
		List<StationDto> stationList = new ArrayList<StationDto>();
		for (Station station : resultList){
			Double x = null,y = null;
			if (station.getPointprojection() != null){
				y = station.getPointprojection().getY();
				x = station.getPointprojection().getX();
			}
			MeteoBasicData basicData = (MeteoBasicData) new MeteoBasicData().findByStation(em, station);
			MeteoStationDto dto = new MeteoStationDto(station.getStationcode(),station.getName(),y,x);
			dto.setMunicipality(station.getMunicipality());
			dto.setArea(basicData.getArea());
			stationList.add(dto);
		}
		return stationList;
	}
	public void sync(EntityManager em, Station station,StationDto stationDto) {
		if (stationDto instanceof MeteoStationDto){
			MeteoStationDto dto = (MeteoStationDto) stationDto;
			MeteoBasicData basic = (MeteoBasicData) new MeteoBasicData().findByStation(em, station);
			if (basic == null){
				basic = new MeteoBasicData();
				basic.setStation(station);
				em.persist(basic);
			}
			basic.setArea(dto.getArea());
			basic.setZeus(dto.getId());
			em.merge(basic);
		}
	}
}
