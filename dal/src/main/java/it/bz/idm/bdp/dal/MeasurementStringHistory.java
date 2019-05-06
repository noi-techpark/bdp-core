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
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.ColumnDefault;

import it.bz.idm.bdp.dal.authentication.BDPRole;
import it.bz.idm.bdp.dto.RecordDto;

@Table(
	name = "measurementstringhistory",
	indexes = {
		@Index(
			columnList = "station_id, type_id, timestamp DESC, period"
		)
	},
	uniqueConstraints = {
		@UniqueConstraint(
			columnNames = {"station_id", "type_id", "timestamp", "period", "string_value"}
		)
	}
)
@Entity
public class MeasurementStringHistory extends MeasurementAbstractHistory {

	@Transient
	private static final long serialVersionUID = 8968054299664379971L;

	@Id
	@GeneratedValue(generator = "measurementstringhistory_gen", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "measurementstringhistory_gen", sequenceName = "measurementstringhistory_seq", allocationSize = 1)
	@ColumnDefault(value = "nextval('measurementstringhistory_seq')")
	private Long id;

    /*
     * Make sure all subclasses of MHistory contain different value names. If these
     * variable names would be called the same, but with different data types
     * Hibernate would complain about not being able to create a SQL UNION.
     * Ex. private String value; and private Double value; would not work
     *     inside MeasurementStringHistory and MeasurementHistory respectively
     */
	@Column(nullable = false)
	private String stringValue;

	public MeasurementStringHistory() {
		super();
	}

	/**
	 * @param station entity associated with this measurement
	 * @param type entity associated with this measurement
	 * @param value string value for this measurement
	 * @param timestamp UTC time of measurement detection
	 * @param period standard interval between 2 measurements
	 */
	public MeasurementStringHistory(Station station, DataType type, String value, Date timestamp, Integer period) {
		setStation(station);
		setType(type);
		setTimestamp(timestamp);
		setPeriod(period);
		setValue(value);
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@Override
	public String getValue() {
		return stringValue;
	}

	public void setValue(String value) {
		this.stringValue = value;
	}

	@Override
	public List<RecordDto> findRecords(EntityManager em, String stationtype, String identifier, String cname, Date start, Date end, Integer period, BDPRole role) {
		return findRecordsImpl(em, stationtype, identifier, cname, start, end, period, role, this);
	}
	@Override
	public void setValue(Object value) {
		setValue(value.toString());
	}

}
