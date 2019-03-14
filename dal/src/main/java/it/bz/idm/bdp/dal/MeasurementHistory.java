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
import java.util.List;

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
import it.bz.idm.bdp.dto.RecordDto;

/**
 * @author Peter Moser
 * @author Patrick Bertolla
 *
 * Implementation for measurements of type double
 *
 */
@Table(
	name = "measurementhistory",
	indexes = {
		@Index(
			columnList = "station_id, type_id, timestamp DESC, period",
			unique = true
		)
	}
)
@Entity
public class MeasurementHistory extends MeasurementAbstractHistory {
	@Transient
	private static final long serialVersionUID = 2900270107783989197L;

    @Id
	@GeneratedValue(generator = "measurementhistory_gen", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "measurementhistory_gen", sequenceName = "measurementhistory_seq", allocationSize = 1)
	@ColumnDefault(value = "nextval('measurementhistory_seq')")
	private Long id;

    /*
     * Make sure all subclasses of MHistory contain different value names. If these
     * variable names would be called the same, but with different data types
     * Hibernate would complain about not being able to create a SQL UNION.
     * Ex. private String value; and private Double value; would not work
     *     inside MeasurementStringHistory and MeasurementHistory respectively
     */
    @Column(nullable = false)
	private Double doubleValue;

	public MeasurementHistory() {
		super();
	}

	/**
	 * TODO: remove created_on from the constructor since it's set automatically on creation time
	 * @param station entity associated with this measurement
	 * @param type entity associated with this measurement
	 * @param value number value for this measurement
	 * @param timestamp UTC time of measurement detection
	 * @param period standard interval between 2 measurements
	 */
	public MeasurementHistory(Station station, DataType type, Double value, Date timestamp, Integer period, Date created_on) {
		super(station,type,timestamp,period);
		setValue(value);
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public Double getValue() {
		return doubleValue;
	}

	public void setValue(Double value) {
		this.doubleValue = value;
	}

	@Override
	public List<RecordDto> findRecords(EntityManager em, String stationtype, String identifier, String cname, Date start, Date end, Integer period, BDPRole role) {
		return MeasurementAbstractHistory.findRecordsImpl(em, stationtype, identifier, cname, start, end, period, role, this);
	}
	@Override
	public void setValue(Object value) {
		this.setValue((Double)value);
	}

}
