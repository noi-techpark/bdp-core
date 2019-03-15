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


/**
 * <p>
 * Station is a point in space, which measures something. Stations have a name and are categorized in station
 * types {@link Station#stationtype}.
 * </p>
 * <p>
 * Each station can also have a parent station, like a car can have a car sharing parking-lot as it's parent.
 * Finally, a station holds also a meta data JSON object, that can be anything, that provides additional
 * information about that station.
 * </p>
 *
 * @author Bertolla Patrick
 * @author Peter Moser
 */
@Table(name = "station", uniqueConstraints = @UniqueConstraint(columnNames = { "stationcode", "stationtype" }))
@Entity
public class Station {

	public static final String GEOM_CRS = "EPSG:4326";
	public static GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

	@Id
	@GeneratedValue(generator = "station_gen", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "station_gen", sequenceName = "station_seq", allocationSize = 1)
	@ColumnDefault(value = "nextval('station_seq')")
	protected Long id;

	@ManyToOne
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

	/**
	 * @param stationType typology of a specific station
	 * @param stationCode unique identifier of a station
	 * @param stationName good chosen name, preferably in english
	 */
	public Station(String stationType, String stationCode, String stationName) {
		this();
		setStationtype(stationType);
		setStationcode(stationCode);
		setName(stationName);
	}

	/**
	 * Queries database on metadata of the specified station. Metadata consists of
	 * defined fields like stationcode(uuid) and optional metadata which gets
	 * versioned and only the newest one is used
	 *
	 * @param em          entity manager
	 * @param stationType typology of the specific station e.g. MeteoStation,
	 *                    Environmentstation etc.
	 * @param station
	 * @return detail information/metadata of the specified station(s)
	 */
	public static List<StationDto> findStationsDetails(EntityManager em, String stationType, Station station){
		List<Station> resultList = new ArrayList<Station>();
		if (station == null)
			resultList = Station.findStations(em, stationType, true);
		else
			resultList.add(station);
		return convertToDto(resultList);
	}
	/**
	 * Takes informations from database fields and all metadata infos from jsonb field and converts it to a serializable objects
	 * @param resultList station entities to convert to valid dtos
	 * @return valid StationDto containing serializable informations of the station entity
	 */
	public static List<StationDto> convertToDto(List<Station> resultList) {
		List<StationDto> stationList = new ArrayList<StationDto>();
		for (Station s : resultList) {
			StationDto dto = convertToDto(s);
			stationList.add(dto);
		}
		return stationList;
	}


	private static StationDto convertToDto(Station s) {
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

	public void setStationcode(String stationCode) {
		this.stationcode = stationCode;
	}

	/**
	 * <p>The category or typology of this station.</p>
	 * For example:<br />
	 * <code>car, parkingLot, weatherStation</code>
	 */
	public String getStationtype() {
		return stationtype;
	}

	public void setStationtype(String stationType) {
		this.stationtype = stationType;
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

	/**
	 * @param em          entitymanager
	 * @param stationType typology of the specific station e.g. MeteoStation,
	 *                    Environmentstation etc.
	 * @param isActive
	 * @return list of unique stringidentifier for each active station of a specific stationtype
	 */
	public static List<String> findStationCodes(EntityManager em, String stationType, boolean isActive) {
		return em.createQuery("select station.stationcode from Station station where station.active = :active and station.stationtype = :stationtype", String.class)
				 .setParameter("active", isActive)
				 .setParameter("stationtype", stationType)
				 .getResultList();
	}


	/**
	 * @param em          entitymanager
	 * @param stationType typology of the specific station e.g. MeteoStation,
	 *                    Environmentstation etc.
	 * @param isActive    activity state provided by the datacollector
	 * @return			  a list of station entities filtered by their activitystate and station typology
	 */
	public static List<Station> findStations(EntityManager em, String stationType, boolean isActive) {
		return em.createQuery("select station from Station station where station.active = :active and station.stationtype = :stationtype", Station.class)
				 .setParameter("active", isActive)
				 .setParameter("stationtype", stationType)
				 .getResultList();
	}
	/**
	 * @param em entitymanager
	 * @return unfiltered station entities
	 */
	public static List<Station> findStations(EntityManager em){
		return em.createQuery("select station from Station station", Station.class)
				 .getResultList();
	}

	/**
	 * @param em entitymanager
	 * @return unique stringidentifiers for each existing stationtype
	 */
	public static List<String> findStationTypes(EntityManager em) {
		return em.createQuery("SELECT station.stationtype FROM Station station GROUP BY station.stationtype", String.class)
				 .getResultList();
	}

	/**
	 * @param em entitymanager
	 * @param stationType typology of the specific station e.g. MeteoStation,
	 *                    Environmentstation etc.
	 * @param stationCode unique identifier of a station
	 * @return station entity filtered by stationcode and stationtype
	 */
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

	/**
	 * Overrides all stations metadata with the current provided by the specific datacollector. Keep in mind that metadata gets versioned.
	 * @param em entitymanager
	 * @param stationType  typology of the specific station e.g. MeteoStation,
	 *                    Environmentstation etc.
	 * @param data list of stationdtos provided by a datacollector
	 */
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
				throw JPAException.unnest(e);
			}
		}
		em.getTransaction().commit();
	}

	/**
	 * @param em entity manager
	 * @param dto stationdto
	 * @throws JPAException is thrown if geographical transformation from one projection to another fails
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

		/* We do not need to check for NULL, nor empty strings, because the writer will take care of that */
		existingStation.setName(dto.getName());
		em.merge(existingStation);
	}

	/**
	 * Create a new meta data entry, if it does not yet exist or if it is different from
	 * the previously inserted one.
	 * @param em entitymanager
	 * @param metaData new metadata map provided by the datacollector
	 * @param station current entity retrieved from database
	 */
	protected static void syncMetaData(EntityManager em, Map<String, Object> metaData, Station station) {
		if (metaData == null)
			return;

		boolean metaDataExists = (station.getMetaData() != null && station.getMetaData().getJson() != null);
		if (!metaDataExists || (metaDataExists && !station.getMetaData().getJson().equals(metaData))) {
			station.setMetaData(metaData);
			em.persist(station.getMetaData());
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

	/**
	 * Synchronizes stations state, active stations are provided by a  data collector.
	 * Queries database for stations with a specific origin and if provided, stationtype.
	 * Deactivates stations in db which are not in the provided list and activates the ones which are.
	 * @param em   entity manager
	 * @param dtos active stations, provided by the corresponding data-collector
	 */
	public static void syncActiveOfExistingStations(EntityManager em, List<StationDto> dtos) {
		if (dtos == null || dtos.isEmpty()) {
			return;
		}
		List<Station> stations = findStationsByOrigin(em, dtos.get(0).getOrigin(),dtos.get(0).getStationType());
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

	/**
	 *
	 * @param em entitymanager
	 * @param origin datacollector identifier where the data origins from
	 * @param stationType typology of the specific station e.g. MeteoStation,
	 *                    Environmentstation etc.
	 * @return list of station enities filtered by stationtype and datacollector origin
	 */
	private static List<Station> findStationsByOrigin(EntityManager em, String origin, String stationType) {
		if (origin == null || origin.isEmpty()) {
			return null;
		}
		QueryBuilder builder = QueryBuilder.init(em)
				.addSql("select station from Station station where station.origin = :origin")
				.setParameter("origin", origin)
				.setParameterIfNotNull("stationtype", stationType, " and stationtype=:stationtype");
		return builder.buildResultList(Station.class);
	}

	/**
	 * Retrieves and serializes all child stations which have the station with stationId as identifier as their parent
	 * @param em entitymanager
	 * @param stationCode unique identifier of a station
	 * @return list of serializable pojos representing the station entities
	 */
	public List<StationDto> findChildren(EntityManager em, String stationCode) {
		List<Station> stations = em.createQuery("select station from Station station where station.parent.stationcode = :parentId", Station.class)
								   .setParameter("parentId", stationCode)
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

	/**
	 * @param em entitymanager
	 * @param stationType typology of the specific station e.g. MeteoStation,
	 *                    Environmentstation etc.
	 * @param origin stringidentifier of the datacollector from where data origins
	 * @return list of stationentities filtered by stationtype and origin
	 */
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
