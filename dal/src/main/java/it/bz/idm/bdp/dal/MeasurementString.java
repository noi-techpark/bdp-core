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
 * along with this program (see LICENSE/GPLv3). If not, see
 * <http://www.gnu.org/licenses/>.
 */
package it.bz.idm.bdp.dal;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.TypedQuery;

import org.hibernate.annotations.ColumnDefault;

import it.bz.idm.bdp.dal.authentication.BDPRole;
import it.bz.idm.bdp.dal.util.JPAUtil;

@Table(name="measurementstring",schema="intime")
@Entity
public class MeasurementString {

	@Id
	@GeneratedValue(generator = "measurementstring_gen", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "measurementstring_gen", sequenceName = "measurementstring_seq", schema = "intime", allocationSize = 1)
	@ColumnDefault(value = "nextval('measurementstring_seq')")
	private Integer id;

	private Date created_on;
	private Date timestamp;
	private String value;

	@ManyToOne(cascade=CascadeType.ALL)
	private Station station;

	@ManyToOne(cascade=CascadeType.ALL)
	private DataType type;

	private Integer period;

	public MeasurementString() {
	}
	public MeasurementString(Station station, DataType type,
			String value, Date timestamp, Integer period) {
		this.station = station;
		this.type = type;
		this.value = value;
		this.timestamp = timestamp;
		this.created_on = new Date();
		this.period = period;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getCreated_on() {
		return created_on;
	}

	public void setCreated_on(Date created_on) {
		this.created_on = created_on;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Station getStation() {
		return station;
	}

	public void setStation(Station station) {
		this.station = station;
	}

	public DataType getType() {
		return type;
	}

	public void setType(DataType type) {
		this.type = type;
	}
	public Integer getPeriod() {
		return period;
	}
	public void setPeriod(Integer period) {
		this.period = period;
	}

	public static MeasurementString findLastMeasurementByStationAndType(
			EntityManager em, Station station, DataType type, Integer period, BDPRole role) {
		TypedQuery<MeasurementString> q = em.createQuery("SELECT measurement "
				+ "FROM MeasurementString measurement, BDPPermissions p "
				+ "WHERE (measurement.station = p.station OR p.station = null) "
				+ "AND (measurement.type = p.type OR p.type = null) "
				+ "AND (measurement.period = p.period OR p.period = null) "
				+ "AND p.role = :role "
				+ "AND measurement.station = :station "
				+ "AND measurement.type=:type "
				+ "AND measurement.period=:period",MeasurementString.class);
		q.setParameter("station",station);
		q.setParameter("type",type);
		q.setParameter("period", period);
		q.setParameter("role", role == null ? BDPRole.fetchGuestRole(em) : role);
		return JPAUtil.getSingleResultOrNull(q);
	}
}
