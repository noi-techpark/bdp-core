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

@Table(name="elaboration")
@Entity
public class Elaboration {

	@Id
	@GeneratedValue(generator = "elaboration_gen", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "elaboration_gen", sequenceName = "elaboration_seq", schema = "intime", allocationSize = 1)
	@ColumnDefault(value = "nextval('elaboration_seq')")
	protected Integer id;
	private Date created_on;
	private Date timestamp;

	@ManyToOne(cascade=CascadeType.MERGE)
	private DataType type;
	private Double value;

	@ManyToOne
	private Station station;
	private Integer period;

	public Elaboration() {
	}
	public Elaboration(Station station, DataType type,
			Double value, Date timestamp, Integer period) {
		this.station = station;
		this.type = type;
		this.value = value;
		this.timestamp = timestamp;
		this.period = period;
		this.created_on = new Date();
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
	public DataType getType() {
		return type;
	}
	public void setType(DataType type) {
		this.type = type;
	}
	public Double getValue() {
		return value;
	}
	public void setValue(Double value) {
		this.value = value;
	}
	public Station getStation() {
		return station;
	}
	public void setStation(Station station) {
		this.station = station;
	}
	public Integer getPeriod() {
		return period;
	}
	public void setPeriod(Integer period) {
		this.period = period;
	}

	public Elaboration findLastRecord(EntityManager em, Station station, DataType type, Integer period, BDPRole role) {
		if (station == null)
			return null;

		String querySQL1 = "SELECT elab FROM Elaboration elab, BDPPermissions p "
				+ "WHERE (elab.station = p.station OR p.station = null) "
				+ "AND (elab.type = p.type OR p.type = null) "
				+ "AND (elab.period = p.period OR p.period = null) "
				+ "AND p.role = :role "
				+ "AND elab.station=:station ";
		String querySQL2 = "order by elab.timestamp desc";
		String queryType = "AND elab.type=:type ";
		String queryPeriod = "AND elab.period=:period ";

		TypedQuery<Elaboration> query;

		if (type == null && period == null) {
			query = em.createQuery(querySQL1 + querySQL2, Elaboration.class);
		} else if (type == null) {
			query = em.createQuery(querySQL1 + queryPeriod + querySQL2, Elaboration.class);
			query.setParameter("period", period);
		} else if (period == null) {
			query = em.createQuery(querySQL1 + queryType + querySQL2, Elaboration.class);
			query.setParameter("type", type);
		} else {
			query = em.createQuery(querySQL1 + queryPeriod + queryType + querySQL2, Elaboration.class);
			query.setParameter("period", period);
			query.setParameter("type", type);
		}

		query.setParameter("station", station);
		query.setParameter("role", role == null ? BDPRole.fetchGuestRole(em) : role);

		return JPAUtil.getSingleResultOrNull(query);
	}

}
