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

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.ColumnDefault;

import it.bz.idm.bdp.dal.authentication.BDPRole;

@Table(name="measurementstring", schema="intime")
@Entity
public class MeasurementString extends M {

	@Transient
	private static final long serialVersionUID = -4378235887347510723L;

	@Id
	@GeneratedValue(generator = "measurementstring_gen", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "measurementstring_gen", sequenceName = "measurementstring_seq", schema = "intime", allocationSize = 1)
	@ColumnDefault(value = "nextval('intime.measurementstring_seq')")
	private Long id;

    /*
     * Make sure all subclasses of M contain different value names. If these
     * variable names would be called the same, but with different data types
     * Hibernate would complain about not being able to create a SQL UNION.
     * Ex. private String value; and private Double value; would not work
     *     inside MeasurementString and Measurement respectively
     */
	private String stringValue;

	public MeasurementString() {
	}
	public MeasurementString(Station station, DataType type, String value, Date timestamp, Integer period) {
		this.setStation(station);
		this.setType(type);
		this.setTimestamp(timestamp);
		this.setCreated_on(new Date());
		this.setPeriod(period);
		this.stringValue = value;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getValue() {
		return stringValue;
	}

	public void setValue(String value) {
		this.stringValue = value;
	}

	@Override
	public void setValue(Object value) {
		if (value instanceof String)
			this.setValue(value);
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
