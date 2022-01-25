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
import java.util.Map;

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
import org.hibernate.annotations.Type;

import it.bz.idm.bdp.dal.authentication.BDPRole;

/**
 * Implementation for measurements cache of type double
 *
 * @author Patrick Bertolla
 */
@Table(
	name = "measurementjson",
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
public class MeasurementJSON extends MeasurementAbstract {

    /**
	 *
	 */
	@Transient
	private static final long serialVersionUID = 8498633392410463424L;

	@Id
	@GeneratedValue(generator = "measurement_json_gen", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "measurement_json_gen", sequenceName = "measurement_json_seq", allocationSize = 1)
	@ColumnDefault(value = "nextval('measurement_json_seq')")
	private Long id;


	public MeasurementJSON() {
	}

	/**
	 * @param station entity associated with this measurement
	 * @param type entity associated with this measurement
	 * @param value number value for this measurement
	 * @param timestamp UTC time of measurement detection
	 * @param period standard interval between 2 measurements
	 */
	public MeasurementJSON(Station station, DataType type, Map<String, Object> json, Date timestamp, Integer period) {
		super(station,type,timestamp,period);
		this.jsonValue = json;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Type(type = "jsonb")
	@Column(columnDefinition = "jsonb")
	private Map<String, Object> jsonValue;

	public Map<String, Object> getJsonValue() {
		return jsonValue;
	}

	public void setJsonValue(Map<String, Object> jsonValue) {
		this.jsonValue = jsonValue;
	}

	@Override
	public MeasurementAbstract findLatestEntry(EntityManager em, Station station, DataType type, Integer period, BDPRole role) {
		return MeasurementAbstract.findLatestEntryImpl(em, station, type, period, role, this);
	}

	@Override
	public Date getDateOfLastRecord(EntityManager em, Station station, DataType type, Integer period, BDPRole role) {
		return MeasurementAbstract.getDateOfLastRecordImpl(em, station, type, period, role, this);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void setValue(Object value) {
		if (value instanceof Map) {
			this.setJsonValue((Map<String,Object>) value);
		}
	}
}