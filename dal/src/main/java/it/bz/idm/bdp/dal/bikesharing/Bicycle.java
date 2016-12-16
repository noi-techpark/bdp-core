package it.bz.idm.bdp.dal.bikesharing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Point;

import it.bz.idm.bdp.dal.DataType;
import it.bz.idm.bdp.dal.MeasurementStation;
import it.bz.idm.bdp.dal.Station;
import it.bz.idm.bdp.dto.DataTypeDto;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.dto.bikesharing.BikeSharingBikeDto;

@Entity
public class Bicycle extends MeasurementStation{

	@Override
	public Object syncStations(EntityManager em, Object[] data) {
		List<Object> dtos = Arrays.asList(data);
		for (Object object: dtos){
			if (object instanceof BikeSharingBikeDto){
				em.getTransaction().begin();
				BikeSharingBikeDto dto = (BikeSharingBikeDto) object;
				Station station = findStation(em,dto.getId());
				Bicyclebasicdata basicData = null;
				Double lon = dto.getLongitude();
				Double lat = dto.getLatitude();
				Point point = null;
				if (lon != null && lat != null){
					point = geometryFactory.createPoint(new Coordinate(lon, lat));
				}
				if (station == null){
					station = new Bicycle();
					station.setStationcode(dto.getId());
					station.setAvailable(true);
					basicData = new Bicyclebasicdata();
				}
				if (basicData == null){
					basicData = new Bicyclebasicdata();
				}else
					basicData = (Bicyclebasicdata) new Bicyclebasicdata().findByStation(em, station);
				basicData.setStation(station);
				station.setName(dto.getName());
				station.setPointprojection(point);
				station.setOrigin(dto.getOrigin());
				BikesharingStation bikeSharingStation =null;
				if (dto !=null && basicData.getBikeSharingStation()!=null && dto.getParentStation() != null)
					bikeSharingStation = (BikesharingStation) basicData.getBikeSharingStation().findStation(em,dto.getParentStation());
				DataType type = DataType.findByCname(em,dto.getType());
				if (type == null)
					type = new DataType(dto.getType());
				basicData.setType(type);
				basicData.setBikeSharingStation(bikeSharingStation);

				em.merge(basicData);
				em.getTransaction().commit();
			} 
		}
		em.getTransaction().begin();
		List<String> stations =  BikesharingStation.findActiveStations(em,BikesharingStation.class.getSimpleName());
		for(String stationId :stations){
			int numberOfBicycles=0;
			Map<String,Integer> numberAvailableByType = new HashMap<String, Integer>(); 
			for (Object object: dtos){
				if (object instanceof BikeSharingBikeDto){
					BikeSharingBikeDto dto = (BikeSharingBikeDto) object;
					if (dto.getParentStation().equals(stationId)){
						numberOfBicycles++;
						Integer numberOf;
						if((numberOf = numberAvailableByType.get(dto.getType())) != null){
							numberAvailableByType.put(dto.getType(), numberOf+1);
						}else
							numberAvailableByType.put(dto.getType(), 1);

					}
				}
			}
			numberAvailableByType.put(DataTypeDto.NUMBER_AVAILABE, numberOfBicycles);
			BikesharingStation bikeSharingStation = (BikesharingStation) new BikesharingStation().findStation(em, stationId);
			for (Map.Entry<String, Integer> entry : numberAvailableByType.entrySet()){
				DataType type = DataType.findByCname(em, entry.getKey());
				BikesharingStationBasicData basic = BikesharingStationBasicData.findByStationAndType(em, bikeSharingStation,type);
				if (basic == null){
					basic = new BikesharingStationBasicData();
					basic.setStation(bikeSharingStation);
					basic.setType(type);
				}
				if (basic.getMax_available() == null || basic.getMax_available() < entry.getValue())
					basic.setMax_available(entry.getValue());
				em.merge(basic);
			}
		}
		em.getTransaction().commit();

		return null;
	}

	@Override
	public List<StationDto> convertToDtos(EntityManager em, List<Station> resultList) {
		List<StationDto> stationList = new ArrayList<StationDto>();
		for (Station station: resultList){
			Bicycle bike = (Bicycle) station;
			Bicyclebasicdata basic = (Bicyclebasicdata) new Bicyclebasicdata().findByStation(em, bike);
			String bikesharingCode = (basic.getBikeSharingStation() == null) ? null : basic.getBikeSharingStation().getStationcode();
			BikeSharingBikeDto dto = new BikeSharingBikeDto(bikesharingCode,bike.getStationcode(),bike.getName(),basic.getType().getCname());
			stationList.add(dto);
		}
		return stationList;
	}
	@Override
	public void sync(EntityManager em, Station station, StationDto dto) {
		if (dto instanceof BikeSharingBikeDto){
			em.getTransaction().begin();
			BikeSharingBikeDto bikedto = (BikeSharingBikeDto) dto;
			Bicycle bike = (Bicycle) station;
			Bicyclebasicdata basicData;
			Double lon = bikedto.getLongitude();
			Double lat = bikedto.getLatitude();
			Point point = null;
			if (lon != null && lat != null){
				point = geometryFactory.createPoint(new Coordinate(lon, lat));
			}
			if (bike == null){
				bike = new Bicycle();
				bike.setStationcode(bikedto.getId());
				bike.setAvailable(true);
				basicData = new Bicyclebasicdata();
				basicData.setStation(bike);
			}else
				basicData = (Bicyclebasicdata) new Bicyclebasicdata().findByStation(em, bike);

			bike.setName(bikedto.getName());
			bike.setPointprojection(point);
			BikesharingStation bikeSharingStation = (BikesharingStation) station.findStation(em,bikedto.getParentStation());
			DataType type = DataType.findByCname(em,bikedto.getType());
			if (type == null)
				type = new DataType(bikedto.getType());
			basicData.setType(type);
			basicData.setBikeSharingStation(bikeSharingStation);

			em.merge(basicData);
			em.getTransaction().commit();
			//TODO: Finish refreshing station number of bikes
		}	
	}

	public List<Station> findByParent(EntityManager em, Station station) {
		BikesharingStation bikeStation = (BikesharingStation) station;
		TypedQuery<Station> query = em.createQuery("Select bike from Bicyclebasicdata b join b.station bike join b.bikeSharingStation s where s = :station", Station.class);
		query.setParameter("station", bikeStation);
		return query.getResultList();
	}

}
