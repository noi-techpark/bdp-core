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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.ColumnDefault;


/**
 * <p>Data provenance combines each measurement with it's origin (data collector). It is a tool to
 * create traceability in a data analytics environment and collection process, to find the root
 * cause of data errors. It provides cleansing capabilities to a data warehouse.
 *
 * <p>For example, if we find out after some time, that the data collector "Parking Collector v1.3"
 * had a bug, it is easy to remove all wrongly inserted data, because we had an association between
 * that data collector and each collected measurement.
 *
 * @author Peter Moser
 */
@Table(
	name = "provenance",
	indexes = {
		@Index(
			unique = true,
			columnList = "lineage, data_collector, data_collector_version"
		)
	}
)
@Entity
public class Provenance {

	@Id
	@GeneratedValue(generator = "provenance_gen", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "provenance_gen", sequenceName = "provenance_seq", allocationSize = 1)
	@ColumnDefault(value = "nextval('provenance_seq')")
	protected Long id;

	@Column(nullable = false)
	protected String lineage;

	@Column(nullable = false)
	protected String dataCollector;

	@Column(nullable = true)
	protected String dataCollectorVersion;
}
