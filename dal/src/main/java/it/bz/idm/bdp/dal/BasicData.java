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

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.TypedQuery;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DiscriminatorOptions;

@Entity
@Inheritance(strategy= InheritanceType.TABLE_PER_CLASS)
@DiscriminatorOptions(force=true)
public abstract class BasicData {

	@Id
	@GeneratedValue(generator = "basicdata_gen", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "basicdata_gen", sequenceName = "basicdata_seq", schema = "intime", allocationSize = 1)
	@ColumnDefault(value = "nextval('intime.basicdata_seq')")
	private Long id;

	@ManyToOne
	protected Station station;

	public Station getStation() {
		return station;
	}

	public void setStation(Station station) {
		this.station = station;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public abstract BasicData findByStation(EntityManager em, Station station);
	public List<BasicData> findAll(EntityManager em) {
		TypedQuery<BasicData> typedQuery = em.createQuery("select basic from BasicData basic where basic.station.active=:active",BasicData.class);
		typedQuery.setParameter("active",true);
		List<BasicData> resultList = typedQuery.getResultList();
		return resultList;
	}

}
