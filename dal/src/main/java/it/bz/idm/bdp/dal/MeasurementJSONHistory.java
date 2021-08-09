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
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Type;

import it.bz.idm.bdp.dal.authentication.BDPRole;
import it.bz.idm.bdp.dto.RecordDto;

/**
 * Implementation for a list of measurements of type <code>double</code>.
 *
 * <p>Extends {@link MeasurementAbstractHistory}.</p>
 *
 * @author Patrick Bertolla
 */
@Table(
	name = "measurementjsonhistory",
	uniqueConstraints = {
		@UniqueConstraint(
			columnNames = {"station_id", "type_id", "timestamp", "period", "json_value"}
		)
	}
)
@Entity
public class MeasurementJSONHistory extends MeasurementAbstractHistory {
	
	@Transient
	private static final long serialVersionUID = 3374278433057820376L;

	

    @Id
	@GeneratedValue(generator = "measurementhistory_json_gen", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "measurementhistory_json_gen", sequenceName = "measurementhistory_json_seq", allocationSize = 1)
	@ColumnDefault(value = "nextval('measurementhistory_json_seq')")
	private Long id;

	@Type(type = "jsonb")
	@Column(columnDefinition = "jsonb")
	private Map<String, Object> json;
	
	public Map<String, Object> getJson() {
		return json;
	}

	public void setJson(Map<String, Object> json) {
		this.json = json;
	}

	public MeasurementJSONHistory() {
		super();
	}

	/**
	 * @param station entity associated with this measurement
	 * @param type entity associated with this measurement
	 * @param value number value for this measurement
	 * @param timestamp UTC time of measurement detection
	 * @param period standard interval between 2 measurements
	 */
	public MeasurementJSONHistory(Station station, DataType type, Map<String,Object> value, Date timestamp, Integer period) {
		super(station, type, timestamp, period);
		setValue(value);
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public List<RecordDto> findRecords(EntityManager em, String stationtype, String identifier, String cname, Date start, Date end, Integer period, BDPRole role) {
		return MeasurementAbstractHistory.findRecordsImpl(em, stationtype, identifier, cname, start, end, period, role, this);
	}
	@Override
	public void setValue(Object value) {
		if (value instanceof Map) {
			this.setJson(((Map<String,Object>)value));
		}
	}

	@Override
	public Object getValue() {
		return json;
	}

}
