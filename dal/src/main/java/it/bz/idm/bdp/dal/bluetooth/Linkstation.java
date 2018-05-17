package it.bz.idm.bdp.dal.bluetooth;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;

import it.bz.idm.bdp.dal.ElaborationStation;
import it.bz.idm.bdp.dal.Station;
import it.bz.idm.bdp.dal.util.JPAUtil;
import it.bz.idm.bdp.dto.CoordinateDto;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.dto.bluetooth.LinkStationDto;

@Entity
public class Linkstation extends ElaborationStation {

	@Override
	public List<StationDto> convertToDtos(EntityManager em, List<Station> resultList) {
		List<StationDto> stationList = new ArrayList<StationDto>();
		for (Station station:resultList){
			TypedQuery<LinkBasicData> query = em.createQuery("select basicdata from LinkBasicData basicdata where basicdata.station=:station",LinkBasicData.class);
			query.setParameter("station",station);
			LinkBasicData basics = JPAUtil.getSingleResultOrNull(query);
			if (basics == null)
				continue;

			LinkStationDto dto = generateDto(station, basics);
			dto.setMunicipality(station.getMunicipality());
			stationList.add(dto);

		}
		return stationList;
	}
	public List<StationDto> findAvailableStations(EntityManager em) {
		TypedQuery<LinkBasicData> query = em.createQuery("select basicdata from LinkBasicData basicdata where basicdata.station.active=:active AND basicdata.station.available=:available",LinkBasicData.class);
		query.setParameter("active",true);
		query.setParameter("available",true);
		List<LinkBasicData> resultList = query.getResultList();
		return generateDtos(resultList);
	}

	private List<StationDto> generateDtos(List<LinkBasicData> resultList) {
		List<StationDto> dtos = new ArrayList<StationDto>();
		for (LinkBasicData basic: resultList){
			LinkStationDto dto = generateDto(basic.getStation(),basic);
			dtos.add(dto);
		}
		return dtos;
	}
	private LinkStationDto generateDto(Station station,LinkBasicData basicData) {
		Double x = null,y = null;
		if (station.getPointprojection() != null){
			y = station.getPointprojection().getY();
			x = station.getPointprojection().getX();
		}
		LinkStationDto dto = new LinkStationDto(station.getStationcode(),station.getName(),y,x);
		dto.setDestination(basicData.getDestination().getStationcode());
		dto.setOrigin(basicData.getOrigin().getStationcode());
		dto.setLength(basicData.getLength());
		dto.setStreet_ids_ref(basicData.getStreet_ids_ref());
		LineString linegeometry = (LineString) convertToEPSG4326(basicData.getLinegeometry());
		List<CoordinateDto> coordinates = parseCoordinates(linegeometry.getCoordinates());
		dto.setCoordinates(coordinates);
		dto.setElapsed_time_default(basicData.getElapsed_time_default());
		return dto;
	}
	private static Geometry convertToEPSG4326(Geometry geometry) {
		CoordinateReferenceSystem sourceCRS, targetCRS;
		Geometry targetGeometry = null;
		try {
			sourceCRS = CRS.decode("EPSG:" + geometry.getSRID());
			targetCRS = CRS.decode("EPSG:4326");
			MathTransform transform = CRS.findMathTransform(sourceCRS, targetCRS);
			targetGeometry = JTS.transform(geometry, transform);
		} catch (NoSuchAuthorityCodeException e) {
			e.printStackTrace();
		} catch (FactoryException e) {
			e.printStackTrace();
		} catch (MismatchedDimensionException e) {
			e.printStackTrace();
		} catch (TransformException e) {
			e.printStackTrace();
		}
		if (targetGeometry == null)
			targetGeometry = geometry;
		return targetGeometry;
	}

	@Override
	public Object pushRecords(EntityManager em, Object... object) {
		return null;
	}
	@Override
	public void sync(EntityManager em, Station station, StationDto dto) {
	}

}
