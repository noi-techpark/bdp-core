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

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import it.bz.idm.bdp.dal.authentication.BDPRole;
import it.bz.idm.bdp.dal.util.QueryBuilder;

@MappedSuperclass
@Inheritance (strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class M implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(nullable = false)
	private Date created_on;

	@Column(nullable = false)
	private Date timestamp;

	@ManyToOne(cascade = CascadeType.ALL, optional = false)
	private Station station;

	@ManyToOne(cascade=CascadeType.PERSIST, optional = false)
	private DataType type;

	@Column(nullable = false)
	private Integer period;

	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	private Provenance provenance;

	public abstract M findLatestEntry(EntityManager em, Station station, DataType type, Integer period, BDPRole role);
	public abstract Date getDateOfLastRecord(EntityManager em, Station station, DataType type, Integer period, BDPRole role);
	public abstract void setValue(Object value);

	public M() {
		this.created_on = new Date();
	}

	public M(Station station, DataType type, Date timestamp, Integer period) {
		this.station = station;
		this.type = type;
		this.timestamp = timestamp;
		this.period = period;
		this.created_on = new Date();
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

	public Provenance getProvenance() {
		return provenance;
	}

	public void setProvenance(Provenance provenance) {
		this.provenance = provenance;
	}

	/**
	 * Retrieve the date of the last inserted record of {@code table}.
	 *
	 * Hibernate does not support {@code UNION ALL} queries, hence we must retrieve all
	 * last record entries of all subclasses and compare programmatically.
	 *
	 * @param em
	 * @param station
	 * @param type
	 * @param period
	 * @param role
	 * @param table
	 * @return
	 */
	public static <T> Date getDateOfLastRecordImpl(EntityManager em, Station station, DataType type, Integer period, BDPRole role, T table) {
		if (station == null)
			return null;

		return QueryBuilder
				.init(em)
				.addSql("SELECT record.timestamp FROM " + table.getClass().getSimpleName() + " record, BDPPermissions p",
						"WHERE (record.station = p.station OR p.station = null)",
						"AND (record.type = p.type OR p.type = null)",
						"AND (record.period = p.period OR p.period = null)",
						"AND p.role = :role",
						"AND record.station = :station")
				.setParameterIfNotNull("type", type, "AND record.type = :type")
				.setParameterIfNotNull("period", period, "AND record.period = :period")
				.setParameter("station", station)
				.setParameter("role", role == null ? BDPRole.fetchGuestRole(em) : role)
				.addSql("ORDER BY record.timestamp DESC")
				.buildSingleResultOrAlternative(Date.class, new Date(-1));
	}

	/**
	 * This is the {@link findLatestEntryImpl} implementation without permission control.
	 *
	 * <p> THIS METHOD SEES ALL DATA, SO CAREFUL WHEN YOU USE IT </p>
	 *
	 * Use {@link M#findLatestEntry(EntityManager, Station, DataType, Integer, BDPRole)},
	 * if you need permission handling.
	 *
	 * @param em
	 * @param station
	 * @param type
	 * @param period
	 * @param table
	 * @return
	 */
	public static <T extends M> M findLatestEntry(EntityManager em, Station station, DataType type, Class<T> subClass) {
		if (station == null)
			return null;

		return QueryBuilder
				.init(em)
				.addSql("SELECT record FROM " + subClass.getSimpleName() + " record WHERE record.station = :station")
				.setParameter("station", station)
				.setParameterIfNotNull("type", type, "AND record.type = :type")
				.addSql("ORDER BY record.timestamp DESC")
				.buildSingleResultOrNull(subClass);
	}

	protected static <T extends M> M findLatestEntryImpl(EntityManager em, Station station, DataType type, Integer period, BDPRole role, T table) {
		if (station == null)
			return null;

		return QueryBuilder
				.init(em)
				.addSql("SELECT record FROM " + table.getClass().getSimpleName() + " record, BDPPermissions p",
						"WHERE (record.station = p.station OR p.station = null)",
						"AND (record.type = p.type OR p.type = null)",
						"AND (record.period = p.period OR p.period = null)",
						"AND p.role = :role",
						"AND record.station = :station")
				.setParameterIfNotNull("period", period, "AND record.period = :period")
				.setParameterIfNotNull("type", type, "AND record.type = :type")
				.setParameter("station", station)
				.setParameter("role", role == null ? BDPRole.fetchGuestRole(em) : role)
				.addSql("ORDER BY record.timestamp DESC")
				.buildSingleResultOrNull(table.getClass());
	}
}