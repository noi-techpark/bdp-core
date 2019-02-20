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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.ColumnDefault;

import it.bz.idm.bdp.dal.authentication.BDPRole;

@Table(name = "measurement", indexes = { @Index(columnList = "timestamp desc", name = "measurement_tsdesc_idx") })
@Entity
public class Measurement extends M {

	@Transient
	private static final long serialVersionUID = 2900270107783989197L;

    @Id
	@GeneratedValue(generator = "measurement_gen", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "measurement_gen", sequenceName = "measurement_seq", allocationSize = 1)
	@ColumnDefault(value = "nextval('measurement_seq')")
	private Long id;

    /*
     * Make sure all subclasses of M contain different value names. If these
     * variable names would be called the same, but with different data types
     * Hibernate would complain about not being able to create a SQL UNION.
     * Ex. private String value; and private Double value; would not work
     *     inside MeasurementString and Measurement respectively
     */
    @Column(nullable = false)
	private Double doubleValue;

	public Measurement() {
	}

	public Measurement(Station station, DataType type, Double value, Date timestamp, Integer period) {
		super(station,type,timestamp,period);
		this.doubleValue = value;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Double getValue() {
		return doubleValue;
	}

	@Override
	public void setValue(Object value) {
		if (value instanceof Number) {
			this.doubleValue = ((Number) value).doubleValue();
		} else if (value instanceof Double) {
			this.doubleValue = (Double) value;
		}
	}

	@Override
	public M findLatestEntry(EntityManager em, Station station, DataType type, Integer period, BDPRole role) {
		return M.findLatestEntryImpl(em, station, type, period, role, this);
	}

	@Override
	public Date getDateOfLastRecord(EntityManager em, Station station, DataType type, Integer period, BDPRole role) {
		return M.getDateOfLastRecordImpl(em, station, type, period, role, this);
	}
}
