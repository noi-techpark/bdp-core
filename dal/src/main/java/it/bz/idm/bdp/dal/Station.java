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
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
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
import javax.persistence.TypedQuery;
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
import it.bz.idm.bdp.dal.util.JPAUtil;
import it.bz.idm.bdp.dto.CoordinateDto;
import it.bz.idm.bdp.dto.StationDto;

@Table(name = "station", uniqueConstraints = @UniqueConstraint(columnNames = { "stationcode", "stationtype" }))
@Entity
@DiscriminatorColumn(name = "stationcategory", discriminatorType = DiscriminatorType.STRING)
public class Station {

	public static final String GEOM_CRS = "EPSG:4326";
	public static GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

	@Id
	@GeneratedValue(generator = "station_gen", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "station_gen", sequenceName = "station_seq", schema = "intime", allocationSize = 1)
	@ColumnDefault(value = "nextval('intime.station_seq')")
	protected Long id;

	@ManyToOne
	protected Station parent;

	protected String name;

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

	public static List<StationDto> findStationsDetails(EntityManager em, String stationType, Station station){
		List<StationDto> dtos = null;
		List<Station> resultList = new ArrayList<Station>();
		if (station == null)
			resultList = Station.findStations(em, stationType);
		else
			resultList.add(station);
		dtos = convertToDto(resultList);
		return dtos;
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
		dto.setParentId(s.getParent() == null ? null : s.getParent().getStationcode());
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

	public static List<Station> findStations(EntityManager em, String stationType){
		TypedQuery<Station> query = em.createQuery("select station from " + stationType + " station where station.active=:active", Station.class);
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
		return em.createQuery("SELECT station.stationtype FROM Station station GROUP BY station.stationtype", String.class).getResultList();
	}

	public static Station findStation(EntityManager em, Integer integer) {
		TypedQuery<Station> stationquery = em.createQuery("SELECT station FROM Station station WHERE station.stationcode=:stationcode", Station.class);
		stationquery.setParameter("stationcode", integer);
		return JPAUtil.getSingleResultOrNull(stationquery);
	}

	public static Station findStation(EntityManager em, String stationcode) {
		if(stationcode == null||stationcode.isEmpty())
			return null;
		TypedQuery<Station> stationquery = em.createQuery("select station from Station station where station.stationcode = :stationcode", Station.class);
		stationquery.setParameter("stationcode", stationcode);
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

	public void syncStations(EntityManager em, List<StationDto> data) {
		syncActiveOfExistingStations(em, data);
		em.getTransaction().begin();
		for (StationDto dto : data){
			try {
				if (dto.getStationType() == null) {
					dto.setStationType(this.getStationtype());
				}
				sync(em, dto);
			} catch (Exception e) {
				/* continue */
				e.printStackTrace();
			}
		}
		em.getTransaction().commit();
	}

	/**
	 * @param em
	 * @param dto
	 * @throws JPAException
	 */
	private void sync(EntityManager em, StationDto dto) {
		Station existingStation = Station.findStation(em, dto.getId());
		if (existingStation == null) {
			existingStation = new Station();
			existingStation.setStationcode(dto.getId());
			existingStation.setStationtype(dto.getStationType());
			em.persist(existingStation);
		}
		if (existingStation.getName() == null)
			existingStation.setName(dto.getName());
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
		if (dto.getParentId() != null) {
			Station parent = Station.findStationByIdentifier(em, dto.getParentId());
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
	protected void syncMetaData(EntityManager em, Map<String, Object> metaData, Station existingStation) {
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
				TypedQuery<Station> query = em.createQuery("select station from Station station where station.origin = :origin", Station.class);
				query.setParameter("origin", origin);
				resultList = query.getResultList();
			}
		}
		return resultList;
	}

	public List<StationDto> findChildren(EntityManager em, String stationId) {
		TypedQuery<Station> stationquery = em.createQuery("select station from Station station where station.parent.stationcode = :parentId",Station.class);
		stationquery.setParameter("parentId", stationId);
		return convertToDto(stationquery.getResultList());
	}

	public static void patch(EntityManager em, StationDto dto) {
		Station station = Station.findStationByIdentifier(em,dto.getId());
		em.merge(station);
	}

	private static Station findStationByIdentifier(EntityManager em, String id) {
		TypedQuery<Station> stationquery = em.createQuery(
				"select s from Station s where s.stationcode=:stationcode",
				Station.class);
		stationquery.setParameter("stationcode", id);
		return JPAUtil.getSingleResultOrNull(stationquery);
	}

	public static List<Station> findStations(EntityManager em, String type, String origin) {
		String baseQuery = "Select station from Station station where station.active = :active and station.stationtype = :type";
		if (origin != null)
			baseQuery += " and origin = :origin";
		try {
			TypedQuery<Station> query = em.createQuery(baseQuery, Station.class);
			query.setParameter("active", true);
			query.setParameter("type", type);
			if (origin != null)
				query.setParameter("origin", origin);
			return query.getResultList();
		} catch (Exception e) {
			List<String> types = JPAUtil.getInstanceTypes(em);
			throw new JPAException("Unable to create query for station type '" + type + "'",
								   "Possible types are " + types.toString(), e);
		}
	}

}
