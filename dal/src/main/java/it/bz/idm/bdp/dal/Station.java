/**
 * BDP data - Data Access Layer for the Big Data Platform
 * Copyright © 2018 IDM Südtirol - Alto Adige (info@idm-suedtirol.com)
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
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.hibernate.annotations.ColumnDefault;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.PrecisionModel;

import it.bz.idm.bdp.dal.util.JPAException;
import it.bz.idm.bdp.dal.util.QueryBuilder;
import it.bz.idm.bdp.dto.CoordinateDto;
import it.bz.idm.bdp.dto.StationDto;

@Table(name = "station", uniqueConstraints = @UniqueConstraint(columnNames = { "stationcode", "stationtype" }))
@Entity
public class Station {

	public static final String GEOM_CRS = "EPSG:4326";
	public static GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

	@Id
	@GeneratedValue(generator = "station_gen", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "station_gen", sequenceName = "station_seq", schema = "intime", allocationSize = 1)
	@ColumnDefault(value = "nextval('intime.station_seq')")
	protected Long id;

	@ManyToOne(optional = false)
	protected Station parent;

	@Column(nullable = false)
	protected String name;

	@Column(nullable = true)
	protected Point pointprojection;

	@Column(nullable = false)
	protected String stationcode;

	@Column(nullable = false)
	protected String stationtype;

	protected Boolean active;

	protected Boolean available;

	private String origin;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "station", fetch = FetchType.LAZY)
	private Collection<MetaData> metaDataHistory;

	@OneToOne
	private MetaData metaData;

	public Station() {
		this.available = true;
		this.active = true;
	}

	public Station(String stationType, String stationCode, String stationName) {
		this();
		setStationtype(stationType);
		setStationcode(stationCode);
		setName(stationName);
	}

	public static List<StationDto> findStationsDetails(EntityManager em, String stationType, Station station){
		List<Station> resultList = new ArrayList<Station>();
		if (station == null)
			resultList = Station.findStations(em, stationType, true);
		else
			resultList.add(station);
		return convertToDto(resultList);
	}

	public static List<StationDto> convertToDto(List<Station> resultList) {
		List<StationDto> stationList = new ArrayList<StationDto>();
		for (Station s : resultList) {
			StationDto dto = convertToDto(s);
			stationList.add(dto);
		}
		return stationList;
	}

	public static StationDto convertToDto(Station s) {
		Double x = null,y = null;
		if (s.getPointprojection() != null){
			y = s.getPointprojection().getY();
			x = s.getPointprojection().getX();
		}
		StationDto dto = new StationDto(s.getStationcode(),s.getName(),y,x);
		dto.setCoordinateReferenceSystem(GEOM_CRS);
		dto.setParentStation(s.getParent() == null ? null : s.getParent().getStationcode());
		dto.setOrigin(s.getOrigin());
		if (s.getMetaData() != null)
			dto.setMetaData(s.getMetaData().getJson());
		return dto;
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

	public String getStationtype() {
		return stationtype;
	}

	public void setStationtype(String stationtype) {
		this.stationtype = stationtype;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
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

	public Station getParent() {
		return parent;
	}

	public void setParent(Station parent) {
		this.parent = parent;
	}

	public MetaData getMetaData() {
		return metaData;
	}

	public void setMetaData(MetaData metaData) {
		this.metaData = metaData;
	}

	public Collection<MetaData> getMetaDataHistory() {
		return metaDataHistory;
	}

	public static List<String> findStationCodes(EntityManager em, String stationType, boolean isActive) {
		return em.createQuery("select station.stationcode from Station station where station.active = :active and station.stationtype = :stationtype", String.class)
				 .setParameter("active", isActive)
				 .setParameter("stationtype", stationType)
				 .getResultList();
	}


	public static List<Station> findStations(EntityManager em, String stationType, boolean isActive) {
		return em.createQuery("select station from Station station where station.active = :active and station.stationtype = :stationtype", Station.class)
				 .setParameter("active", isActive)
				 .setParameter("stationtype", stationType)
				 .getResultList();
	}
	public static List<Station> findStations(EntityManager em){
		return em.createQuery("select station from Station station", Station.class)
				 .getResultList();
	}

	public static List<String> findStationTypes(EntityManager em) {
		return em.createQuery("SELECT station.stationtype FROM Station station GROUP BY station.stationtype", String.class)
				 .getResultList();
	}

	private static Station findStation(EntityManager em, String stationType, Object stationCode) {
		if(stationCode == null || stationType == null || stationType.isEmpty())
			return null;
		return QueryBuilder
				.init(em)
				.addSql("SELECT station FROM Station station",
						"WHERE station.stationcode = :stationcode AND station.stationtype = :stationtype")
				.setParameter("stationcode", stationCode)
				.setParameter("stationtype", stationType)
				.buildSingleResultOrNull(Station.class);
	}

	public static Station findStation(EntityManager em, String stationType, Integer stationCode) {
		return findStation(em, stationType, (Object) stationCode);
	}

	public static Station findStation(EntityManager em, String stationType, String stationCode) {
		if(stationCode.isEmpty())
			return null;
		return findStation(em, stationType, (Object) stationCode);
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

	protected static List<CoordinateDto> parseCoordinate(Coordinate[] coordinates) {
		List<CoordinateDto> dtos = new ArrayList<CoordinateDto>();
		for (Coordinate coordinate: coordinates){
			dtos.add(parseCoordinate(coordinate));
		}
		return dtos;
	}

	protected static CoordinateDto parseCoordinate(Coordinate coordinate) {
		return new CoordinateDto(coordinate.x,coordinate.y);
	}

	public static void syncStations(EntityManager em, String stationType, List<StationDto> data) {
		syncActiveOfExistingStations(em, data);
		em.getTransaction().begin();
		for (StationDto dto : data){
			try {
				if (dto.getStationType() == null) {
					dto.setStationType(stationType);
				}
				if (! dto.isValid()) {
					throw new JPAException("Invalid JSON for " + StationDto.class.getSimpleName(), StationDto.class);
				}
				sync(em, dto);
			} catch (Exception e) {
				em.getTransaction().rollback();
				if (e instanceof JPAException)
					throw (JPAException) e;
				e.printStackTrace();
				throw new JPAException(e.getMessage(), e);
			}
		}
		em.getTransaction().commit();
	}

	/**
	 * @param em
	 * @param dto
	 * @throws JPAException
	 */
	private static void sync(EntityManager em, StationDto dto) {
		Station existingStation = Station.findStation(em, dto.getStationType(), dto.getId());
		if (existingStation == null) {
			existingStation = new Station();
			existingStation.setStationcode(dto.getId());
			existingStation.setStationtype(dto.getStationType());
			existingStation.setName(dto.getName());
			em.persist(existingStation);
		}
		if (dto.getLatitude() != null && dto.getLongitude() != null) {
			Point point = geometryFactory.createPoint(new Coordinate(dto.getLongitude(), dto.getLatitude()));
			if (dto.getCoordinateReferenceSystem() != null && !GEOM_CRS.equals(dto.getCoordinateReferenceSystem())) {
				CoordinateReferenceSystem thirdPartyCRS;
				try {
					thirdPartyCRS = CRS.decode(dto.getCoordinateReferenceSystem(), true);
					CoordinateReferenceSystem crs = CRS.decode(GEOM_CRS, true);
					Geometry geometry = JTS.transform(point, CRS.findMathTransform(thirdPartyCRS, crs));
					point = geometry.getCentroid();
				} catch (FactoryException | MismatchedDimensionException | TransformException e) {
					e.printStackTrace();
					throw new JPAException("Unable to create a valid coordinate reference system for station " + existingStation.getName(), e);
					// XXX PEMOSER Continue here: Should an invalid CRS terminate any insertion or continue without?
					// Should we return an message that some station had problems with their CRS, but were nevertheless inserted
				}
			}
			point.setSRID(4326);
			existingStation.setPointprojection(point);
		}
		existingStation.setOrigin(dto.getOrigin());
		if (dto.getParentStation() != null) {
			Station parent = Station.findStationByIdentifier(em, dto.getParentStation());
			if (parent != null)
				existingStation.setParent(parent);
		}
		syncMetaData(em, dto.getMetaData(), existingStation);
		em.merge(existingStation);
	}

	/**
	 * Create a new meta data entry, if it does not yet exist or if it is different from
	 * the previously inserted one.
	 */
	protected static void syncMetaData(EntityManager em, Map<String, Object> metaData, Station existingStation) {
		if (metaData == null)
			return;

		boolean metaDataExists = (existingStation.getMetaData() != null && existingStation.getMetaData().getJson() != null);
		if (!metaDataExists || (metaDataExists && !existingStation.getMetaData().getJson().equals(metaData))) {
			existingStation.setMetaData(metaData);
			em.persist(existingStation.getMetaData());
		}
	}

	/**
	 * Always create a new MetaData, since we want to persist a new record
	 * and not update an existing one, to keep the history of all meta data
	 * changes.
	 *
	 * @param metaData
	 */
	private void setMetaData(Map<String, Object> metaData) {
		this.metaData = new MetaData();
		this.metaData.setStation(this);
		this.metaData.setJson(metaData);
	}

	public static void syncActiveOfExistingStations(EntityManager em, List<StationDto> dtos) {
		if (dtos == null || dtos.isEmpty()) {
			return;
		}
		List<Station> stations = findStationsByOrigin(em, dtos.get(0).getOrigin());
		if (stations == null) {
			return;
		}
		em.getTransaction().begin();
		for (Station station : stations){
			boolean isActive = false;
			for (StationDto dto : dtos) {
				if (station.getStationcode().equals(dto.getId())) {
					isActive = true;
					break;
				}
			}
			if (station.getActive() == null || isActive != station.getActive()){
				station.setActive(isActive);
				em.merge(station);
			}
		}
		em.getTransaction().commit();
	}

	private static List<Station> findStationsByOrigin(EntityManager em, String origin) {
		if (origin == null || origin.isEmpty()) {
			return null;
		}
		return em.createQuery("select station from Station station where station.origin = :origin", Station.class)
				 .setParameter("origin", origin)
				 .getResultList();
	}

	public List<StationDto> findChildren(EntityManager em, String stationId) {
		List<Station> stations = em.createQuery("select station from Station station where station.parent.stationcode = :parentId", Station.class)
								   .setParameter("parentId", stationId)
								   .getResultList();
		return convertToDto(stations);
	}

	public static void patch(EntityManager em, StationDto dto) {
		Station station = Station.findStationByIdentifier(em,dto.getId());
		em.merge(station);
	}

	private static Station findStationByIdentifier(EntityManager em, String id) {
		return QueryBuilder
				.init(em)
				.addSql("SELECT s FROM Station s WHERE s.stationcode = :stationcode")
				.setParameter("stationcode", id)
				.buildSingleResultOrNull(Station.class);
	}

	public static List<Station> findStations(EntityManager em, String stationType, String origin) {
		try {
			return QueryBuilder
					.init(em)
					.addSql("SELECT station FROM Station station",
							"WHERE station.active = :active AND station.stationtype = :type")
					.setParameterIfNotNull("origin", origin, "AND origin = :origin")
					.setParameter("active", true)
					.setParameter("type", stationType)
					.buildResultList(Station.class);
		} catch (Exception e) {
			throw new JPAException("Unable to create query for station type '" + stationType + "'", e);
		}
	}

}
