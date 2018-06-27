/**
 * BDP data - Data Access Layer for the Big Data Platform
 * Copyright © 2018 OpenDataHub (info@opendatahub.bz.it)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program (see LICENSES/GPL-3.0.txt). If not, see
 * <http://www.gnu.org/licenses/>.
 *
 * SPDX-License-Identifier: GPL-3.0
 */
package it.bz.idm.bdp.dal;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.TypedQuery;
import javax.persistence.UniqueConstraint;

import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.hibernate.annotations.ColumnDefault;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.PrecisionModel;

import it.bz.idm.bdp.dal.authentication.BDPRole;
import it.bz.idm.bdp.dal.util.JPAUtil;
import it.bz.idm.bdp.dto.ChildDto;
import it.bz.idm.bdp.dto.CoordinateDto;
import it.bz.idm.bdp.dto.RecordDto;
import it.bz.idm.bdp.dto.StationDto;
import it.bz.idm.bdp.dto.TypeDto;

@Table(name = "station", uniqueConstraints = @UniqueConstraint(columnNames = { "stationcode", "stationtype" }))
@Entity
@DiscriminatorColumn(name="stationtype", discriminatorType=DiscriminatorType.STRING)
public abstract class Station {

	public static final String GEOM_CRS = "EPSG:4326";
	public static GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

	@Id
	@GeneratedValue(generator = "station_gen", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "station_gen", sequenceName = "station_seq", schema = "intime", allocationSize = 1)
	@ColumnDefault(value = "nextval('station_seq')")
	protected Long id;

	protected String name;

	protected String description;

	protected String shortname;

	protected Point pointprojection;

	@Column(nullable = false)
	protected String stationcode;

	protected Boolean active;

	protected Boolean available;

	private String origin;

	private String municipality;

	public Station() {
		this.available = true;
		this.active = true;
	}

	public List<StationDto> findStationsDetails(EntityManager em, Station station){
		List<StationDto> dtos = null;
		List<Station> resultList = new ArrayList<Station>();
		if (station == null)
			resultList = findStations(em);
		else
			resultList.add(station);
		dtos = this.convertToDtos(em,resultList);
		return dtos;
	};
	public List<StationDto> convertToDtos(EntityManager em, List<Station> resultList) {
		List<StationDto> stationList = new ArrayList<StationDto>();
		if (resultList.isEmpty())
			return new ArrayList<StationDto>();
		for (Station s: resultList){
			StationDto dto = convertToDto(s);
			stationList.add(dto);
		}
		return stationList;
	}

	public StationDto convertToDto(Station s) {
		Double x = null,y = null;
		if (s.getPointprojection() != null){
			y = s.getPointprojection().getY();
			x = s.getPointprojection().getX();
		}
		StationDto dto = new StationDto(s.getStationcode(),s.getName(),y,x);
		dto.setCrs(GEOM_CRS);
		dto.setOrigin(s.getOrigin());
		dto.setMunicipality(s.getMunicipality());
		return dto;
	}

	public abstract List<String[]> findDataTypes(EntityManager em, String stationId);
	public abstract List<TypeDto> findTypes(EntityManager em, String stationId);

	public abstract Date getDateOfLastRecord(EntityManager em, Station station, DataType type, Integer period,
			BDPRole role);

	public abstract RecordDto findLastRecord(EntityManager em, String cname, Integer period, BDPRole role);

	public abstract List<RecordDto> getRecords(EntityManager em, String type, Date start, Date end, Integer period,
			BDPRole role);
	public abstract Object pushRecords(EntityManager em, Object... object);

	protected static Date getDateOfLastRecordImpl(EntityManager em, Station station, DataType type, Integer period,
			BDPRole role, String table) {
		if (station == null)
			return null;

		if (!table.matches("[a-zA-Z_]+")) {
			throw new IllegalArgumentException("Table '" + table + "' contains illegal characters.");
		}

		String queryString = "select record.timestamp "
				+ "from " + table + " record, BDPPermissions p "
				+ "WHERE (record.station = p.station OR p.station = null) "
				+ "AND (record.type = p.type OR p.type = null) "
				+ "AND (record.period = p.period OR p.period = null) "
				+ "AND p.role = :role "
				+ "AND record.station=:station";

		if (type != null) {
			queryString += " AND record.type = :type";
		}
		if (period != null) {
			queryString += " AND record.period=:period";
		}
		queryString += " ORDER BY record.timestamp DESC";
		TypedQuery<Date> query = em.createQuery(queryString, Date.class);
		query.setParameter("station", station);
		query.setParameter("role", role == null ? BDPRole.fetchGuestRole(em) : role);
		if (type != null)
			query.setParameter("type", type);
		if (period != null)
			query.setParameter("period", period);
		return JPAUtil.getSingleResultOrAlternative(query, new Date(0));
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getShortname() {
		return shortname;
	}

	public void setShortname(String shortname) {
		this.shortname = shortname;
	}

	public Point getPointprojection() {
		return pointprojection;
	}

	public void setPointprojection(Point pointprojection) {
		this.pointprojection = pointprojection;
	}

	public String getStationcode() {
		return stationcode;
	}

	public void setStationcode(String stationcode) {
		this.stationcode = stationcode;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getAvailable() {
		return available;
	}
	public void setAvailable(Boolean available) {
		this.available = available;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public String getMunicipality() {
		return municipality;
	}

	public void setMunicipality(String municipality) {
		this.municipality = municipality;
	}

	public List<Station> findStations(EntityManager em){
		TypedQuery<Station> query = em.createQuery("select station from "+this.getClass().getSimpleName()+" station where station.active=:active",Station.class);
		query.setParameter("active",true);
		List<Station> resultList = query.getResultList();
		return resultList;
	}
	public List<Station> findAllStations(EntityManager em){
		TypedQuery<Station> query = em.createQuery("select station from "+this.getClass().getSimpleName()+" station",Station.class);
		List<Station> resultList = query.getResultList();
		return resultList;
	}
	public static List<String> findActiveStations(EntityManager em, String type) {
		TypedQuery<String> query = em.createQuery("Select station.stationcode from "+type+" station where station.active = :active",String.class);
		query.setParameter("active", true);
		List<String> resultList = query.getResultList();
		return resultList;
	}


	public static List<String> findStationTypes(EntityManager em) {
		TypedQuery<String> query = em.createQuery("select station.class from Station station GROUP BY type(station)",String.class);
		return query.getResultList();
	}

	public static Station findStation(EntityManager em, Integer integer) {
		TypedQuery<Station> stationquery = em.createQuery(
				"select station from Station station where station.stationcode=:stationcode", Station.class);
		stationquery.setParameter("stationcode", integer);
		return JPAUtil.getSingleResultOrNull(stationquery);
	}
	public Station findStation(EntityManager em, String stationcode) {
		if(stationcode == null||stationcode.isEmpty())
			return null;
		TypedQuery<Station> stationquery = em.createQuery(
				"select station from Station station where station.stationcode=:stationcode AND type(station)= :stationtype",
				Station.class);
		stationquery.setParameter("stationcode", stationcode);
		stationquery.setParameter("stationtype", this.getClass());
		return JPAUtil.getSingleResultOrNull(stationquery);
	}
	protected static List<String[]> getDataTypesFromQuery(List<Object[]> resultList){
		List<String[]> stringlist = new ArrayList<String[]>();
		for(Object[] objects : resultList){
			String[] stringarray= new String[objects.length];
			for (int i = 0; i< objects.length;i++){
				String value = String.valueOf(objects[i]);
				stringarray[i]= "null".equals(value) ? "" : value;
			}
			stringlist.add(stringarray);
		}
		return stringlist;
	}

	protected List<CoordinateDto> parseCoordinates(Coordinate[] coordinates) {
		List<CoordinateDto> dtos = new ArrayList<CoordinateDto>();
		for (Coordinate coordinate: coordinates){
			dtos.add(parseCoordinate(coordinate));
		}
		return dtos;

	}

	protected CoordinateDto parseCoordinate(Coordinate coordinate) {
		return new CoordinateDto(coordinate.x,coordinate.y);
	}
	public Object syncStations(EntityManager em, List<StationDto> data){
		syncActiveOfExistingStations(em, data);
		em.getTransaction().begin();
		for (StationDto dto:data){
			Station existingStation = findStation(em,dto.getId());
			if (existingStation == null){
				try {
					existingStation = this.getClass().newInstance();
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
				existingStation.setStationcode(dto.getId());
				em.persist(existingStation);
			}
			this.sync(em,existingStation,dto);									//sync instance specific metadata
			if (existingStation.getName() == null)
				existingStation.setName(dto.getName());
			if (dto.getLatitude() != null && dto.getLongitude() != null){
				Point point = geometryFactory.createPoint(new Coordinate(dto.getLongitude(),dto.getLatitude()));
				CoordinateReferenceSystem crs;
				try {
					crs = CRS.decode(GEOM_CRS,true);
					if (dto.getCrs() != null && !GEOM_CRS.equals(dto.getCrs())){
						CoordinateReferenceSystem thirdPartyCRS = CRS.decode(dto.getCrs(),true);
						Geometry geometry = JTS.transform(point,CRS.findMathTransform(thirdPartyCRS, crs));
						point = geometry.getCentroid();
					}
					point.setSRID(4326);
					existingStation.setPointprojection(point);
				} catch (NoSuchAuthorityCodeException e) {
					e.printStackTrace();
				} catch (FactoryException e) {
					e.printStackTrace();
				} catch (MismatchedDimensionException e) {
					e.printStackTrace();
				} catch (TransformException e) {
					e.printStackTrace();
				}

			}
			existingStation.setOrigin(dto.getOrigin());
			em.merge(existingStation);
		}
		em.getTransaction().commit();
		return null;
	};

	public abstract void sync(EntityManager em,Station station, StationDto dto);

	public void syncActiveOfExistingStations(EntityManager em, List<StationDto> dtos) {
		List<Station> stations = this.findStationsByOrigin(em, dtos);
		if (stations != null){
			em.getTransaction().begin();
			for (Station station: stations){
				boolean isActive = false;
				for (Object obj: dtos){
					if (obj instanceof StationDto){
						StationDto dto = (StationDto) obj;
						if (station.getStationcode().equals(dto.getId()))
							isActive = true;
					}
				}
				if (station.getActive() == null || isActive != station.getActive()){
					station.setActive(isActive);
					em.merge(station);
				}
			}
			em.getTransaction().commit();
		}
	}

	private List<Station> findStationsByOrigin(EntityManager em, List<StationDto> dtos) {
		List<Station> resultList = null;
		if (dtos != null && !dtos.isEmpty()){
			String origin = dtos.get(0).getOrigin();
			if (origin != null){
				TypedQuery<Station> query = em.createQuery("select station from "+this.getClass().getSimpleName()+" station where station.origin = :origin",Station.class);
				query.setParameter("origin", origin);
				resultList = query.getResultList();
			}
		}
		return resultList;
	}

	public List<ChildDto> findChildren(EntityManager em, String parent) {
		return null;
	}

	public static List<Station> findStationsWithoutMunicipality(EntityManager em) {
		TypedQuery<Station> stationquery = em.createQuery("select station from Station station where station.municipality is null and pointprojection is not null",Station.class);
		return stationquery.getResultList();
	}

	public static void patch(EntityManager em, StationDto dto) {
		Station station = Station.findStationByIdentifier(em,dto.getId(),dto.getStationType());
		station.setMunicipality(dto.getMunicipality());
		em.merge(station);
	}

	private static Station findStationByIdentifier(EntityManager em, String id, String stationType) {
		TypedQuery<Station> stationquery = em.createQuery(
				"select station from " + stationType + " station where station.stationcode=:stationcode",
				Station.class);
		stationquery.setParameter("stationcode", id);
		return JPAUtil.getSingleResultOrNull(stationquery);
	}

}