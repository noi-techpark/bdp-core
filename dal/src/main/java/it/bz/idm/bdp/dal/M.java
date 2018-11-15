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

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.TypedQuery;

import it.bz.idm.bdp.dal.authentication.BDPRole;
import it.bz.idm.bdp.dal.util.JPAUtil;

@Entity
@Inheritance (strategy=InheritanceType.TABLE_PER_CLASS)
public abstract class M {

	@Id
	private Long id;

	private Date created_on;
	private Date timestamp;
	@ManyToOne(cascade=CascadeType.ALL)
	private Station station;

	@ManyToOne(cascade=CascadeType.PERSIST)
	private DataType type;

	private Integer period;

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

	public static Date getDateOfLastRecord(EntityManager em, Station station, DataType type, Integer period, BDPRole role) {
		if (station == null)
			return null;

		String queryString = "select record.timestamp "
				+ "from M record, BDPPermissions p "
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

	public static <T extends M> M findLatestEntryImpl(EntityManager em, Station station, DataType type, Integer period, BDPRole role,T table) {
		if (station == null)
			return null;
		String baseQuery = "select record from " + table.getClass().getSimpleName() + " record, BDPPermissions p"
						 + " where (record.station = p.station or p.station = null)"
						 + " and (record.type = p.type or p.type = null)"
						 + " and (record.period = p.period or p.period = null)"
					 	 + " and p.role = :role "
					 	 + "and record.station = :station";
		String order = " ORDER BY record.timestamp DESC";

		TypedQuery<? extends M> query = null;
		//set optional parameters
		if (type == null){
			if (period == null){
				query = em.createQuery(baseQuery + order, M.class);
			}else{
				query = em.createQuery(baseQuery + " AND record.period=:period" + order, table.getClass());
				query.setParameter("period", period);
			}
		}else if (period==null){
			query = em.createQuery(baseQuery + " AND record.type=:type" + order, table.getClass());
			query.setParameter("type", type);

		}else{
			query = em.createQuery(baseQuery + " AND record.type=:type AND record.period=:period" + order,
					M.class);
			query.setParameter("type", type);
			query.setParameter("period", period);
		}

		//set required paramaters
		query.setParameter("station", station);
		query.setParameter("role", role == null ? BDPRole.fetchGuestRole(em) : role);
		return JPAUtil.getSingleResultOrNull(query);
	}
	public abstract M findLatestEntry(EntityManager em, Station station, DataType type, Integer period, BDPRole role);
	public abstract void setValue(Object value);
}