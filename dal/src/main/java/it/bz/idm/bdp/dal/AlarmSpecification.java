/**
 * BDP data - Data Access Layer for the Big Data Platform
 * Copyright © 2018 OpenDataHub (info@opendatahub.bz.it)
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

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.TypedQuery;

import org.hibernate.annotations.ColumnDefault;

import it.bz.idm.bdp.dal.util.JPAUtil;

@Table(name="alarmspecification")
@Entity
public class AlarmSpecification {

	@Id
	@GeneratedValue(generator = "alarmspecification_gen", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "alarmspecification_gen", sequenceName = "alarmspecification_seq", schema = "intime", allocationSize = 1)
	@ColumnDefault(value = "nextval('alarmspecification_seq')")
	private Long id;

	private String name;
	private String description;

	public AlarmSpecification() {
	}
	public AlarmSpecification(String name, String description) {
		this.name = name;
		this.description = description;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public static AlarmSpecification findSpecificationByName(EntityManager manager, String name) {
		TypedQuery<AlarmSpecification> q = manager.createQuery("select spec from AlarmSpecification spec where spec.name=:name", AlarmSpecification.class);
		q.setParameter("name", name);
		return JPAUtil.getSingleResultOrNull(q);
	}



}
