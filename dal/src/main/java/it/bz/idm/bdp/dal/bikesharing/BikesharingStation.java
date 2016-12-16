package it.bz.idm.bdp.dal.bikesharing;

import it.bz.idm.bdp.dal.MeasurementStation;
import it.bz.idm.bdp.dal.Station;
import it.bz.idm.bdp.dto.ChildDto;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.dto.bikesharing.BikeDto;
import it.bz.idm.bdp.dto.bikesharing.BikesharingDetailsDto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.EntityManager;

@Entity
public class BikesharingStation extends MeasurementStation{

	@Override
	public List<StationDto> convertToDtos(EntityManager em, List<Station> resultList) {
		List<StationDto> stationList = new ArrayList<StationDto>();
		for (Station station: resultList){
			
			List<BikesharingStationBasicData> basicDetails = BikesharingStationBasicData.findBasicByStation(em,station);
			Map<String,Integer> map = new HashMap<String, Integer>();
			for (BikesharingStationBasicData detail : basicDetails){
				map.put(detail.getType().getCname(), detail.getMax_available());
			}
			Double x = null,y = null;
			if (station.getPointprojection() != null){
				y = station.getPointprojection().getY();
				x = station.getPointprojection().getX();
			}
			BikesharingDetailsDto dto = new BikesharingDetailsDto(station.getStationcode(),station.getName(),y,x,map);
			dto.setCrs(GEOM_CRS);
			stationList.add(dto);
		}
		return stationList;
	}

	@Override
	public void sync(EntityManager em, Station station, StationDto dto) {
	}
	@Override
	public List<ChildDto> findChildren(EntityManager em, String parent) {
		List<ChildDto> dtos = new ArrayList<ChildDto>();
		List<Station> bicycles = new ArrayList<Station>();
		if (parent == null)
			bicycles = new Bicycle().findStations(em);
		else {
			Station station = findStation(em, parent);
			if (station!= null && station instanceof BikesharingStation)
			bicycles = new Bicycle().findByParent(em,station);
		}
		if (! bicycles.isEmpty())
			for (Station s : bicycles){
				Bicycle bike = (Bicycle) s;
				Bicyclebasicdata basic = (Bicyclebasicdata) new Bicyclebasicdata().findByStation(em, bike);
				BikeDto dto = new BikeDto();
				dto.setIdentifier(bike.getStationcode());
				if (basic!= null){
					if (basic.getBikeSharingStation() != null)
						dto.setStation(basic.getBikeSharingStation().getStationcode());
					dto.setType(basic.getType().getCname());
				}
				dtos.add(dto);
			}
		return dtos;
	}
}
