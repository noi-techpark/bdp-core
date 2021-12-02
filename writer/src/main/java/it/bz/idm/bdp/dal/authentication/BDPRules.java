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
package it.bz.idm.bdp.dal.authentication;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.ColumnDefault;

import it.bz.idm.bdp.dal.DataType;
import it.bz.idm.bdp.dal.Station;

/**
 * Rules defined by an administrator to decide which data set needs which permission access
 * @author Peter Moser
 *
 */
@Table(name = "bdprules")
@Entity
public class BDPRules {
    @Id
	@GeneratedValue(generator = "bdprules_gen", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "bdprules_gen", sequenceName = "bdprules_seq", allocationSize = 1)
	@ColumnDefault(value = "nextval('bdprules_seq')")
    private Long id;

    @ManyToOne
	private BDPRole role;

    @ManyToOne
	private Station station;

    @ManyToOne
	private DataType type;

	private Integer period;

	public BDPRules() {
		super();
	}

	/**
	 * @param role which to associate the rule with
	 * @param station entity for which the rule is valid
	 * @param type entity for which the rule is valid
	 * @param period for which the rule is valid
	 */
	public BDPRules(BDPRole role, Station station, DataType type, Integer period) {
		super();
		setRole(role);
		setStation(station);
		setType(type);
		setPeriod(period);
	}

	/**
	 * @param role which to associate the rule with
	 * @param station entity for which the rule is valid
	 * @param type entity for which the rule is valid
	 */
	public BDPRules(BDPRole role, Station station, DataType type) {
		this(role, station, type, null);
	}

	/**
	 * @param role which to associate the rule with
	 * @param station entity for which the rule is valid
	 */
	public BDPRules(BDPRole role, Station station) {
		this(role, station, null);
	}

	/**
	 * @param role which to associate the rule with
	 */
	public BDPRules(BDPRole role) {
		this(role, null);
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public BDPRole getRole() {
		return role;
	}
	public void setRole(BDPRole role) {
		this.role = role;
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

}
