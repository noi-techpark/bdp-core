/**
 * BDP data - Data Access Layer for the Big Data Platform
 *
 * Copyright © 2018 IDM Südtirol - Alto Adige (info@idm-suedtirol.com)
 * Copyright © 2019 NOI Techpark - Südtirol / Alto Adige (info@opendatahub.bz.it)
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
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.ColumnDefault;


/**
 *  Implementation for measurements of type string
 *
 * @author Peter Moser
 * @author Patrick Bertolla
 */
@Table(
	name = "measurementstring",
	indexes = {
		@Index(
			columnList = "timestamp desc"
		)
	},
	uniqueConstraints = {
		@UniqueConstraint(
			columnNames = {"station_id", "type_id", "period"}
		)
	}
)
@Entity
public class MeasurementString extends MeasurementAbstract {

	@Transient
	private static final long serialVersionUID = -4378235887347510723L;

	@Id
	@GeneratedValue(generator = "measurementstring_gen", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "measurementstring_gen", sequenceName = "measurementstring_seq", allocationSize = 1)
	@ColumnDefault(value = "nextval('measurementstring_seq')")
	private Long id;

    /*
     * Make sure all subclasses of M contain different value names. If these
     * variable names would be called the same, but with different data types
     * Hibernate would complain about not being able to create a SQL UNION.
     * Ex. private String value; and private Double value; would not work
     *     inside MeasurementString and Measurement respectively
     */
	@Column(nullable = false)
	private String stringValue;

	public MeasurementString() {
		super();
	}
	/**
	 * @param station entity associated with this measurement
	 * @param type entity associated with this measurement
	 * @param value string value for this measurement
	 * @param timestamp UTC time of measurement detection
	 * @param period standard interval between 2 measurements
	 */
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

	@Override
	public void setValue(Object value) {
		if (value instanceof String)
			this.stringValue = (String) value;
	}

	@Override
	public MeasurementAbstract findLatestEntry(EntityManager em, Station station, DataType type, Integer period) {
		return MeasurementAbstract.findLatestEntryImpl(em, station, type, period, this);
	}

	@Override
	public Date getDateOfLastRecord(EntityManager em, Station station, DataType type, Integer period) {
		return MeasurementAbstract.getDateOfLastRecordImpl(em, station, type, period, this);
	}
}
