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

import org.apache.commons.lang3.RandomStringUtils;
import org.hibernate.annotations.ColumnDefault;

import it.bz.idm.bdp.dal.util.QueryBuilder;
import it.bz.idm.bdp.dto.ProvenanceDto;


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

	@Column(unique=true,nullable = false)
	protected String uuid;

	@Column(nullable = false)
	protected String lineage;

	@Column(nullable = false)
	protected String dataCollector;

	@Column(nullable = true)
	protected String dataCollectorVersion;

	public Provenance() {
		this.uuid = RandomStringUtils.randomAlphanumeric(8);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getLineage() {
		return lineage;
	}

	public void setLineage(String lineage) {
		this.lineage = lineage;
	}

	public String getDataCollector() {
		return dataCollector;
	}

	public void setDataCollector(String dataCollector) {
		this.dataCollector = dataCollector;
	}

	public String getDataCollectorVersion() {
		return dataCollectorVersion;
	}

	public void setDataCollectorVersion(String dataCollectorVersion) {
		this.dataCollectorVersion = dataCollectorVersion;
	}

	public static List<Provenance> find(EntityManager em, String uuid, String name, String version, String lineage) {
		return QueryBuilder
				.init(em)
				.addSql("SELECT p FROM Provenance p where 1=1")
				.setParameterIfNotEmpty("uuid", uuid, "and uuid = :uuid")
				.setParameterIfNotEmpty("name", name, "and dataCollector = :name")
				.setParameterIfNotEmpty("version", version, "and dataCollectorVersion = :version")
				.setParameterIfNotEmpty("lineage", lineage, "and lineage = :lineage")
				.buildResultList(Provenance.class);
	}

	public static String add(EntityManager em, ProvenanceDto provenance) {
		List<Provenance> list = find(em, null, provenance.getDataCollector(), provenance.getDataCollectorVersion(), provenance.getLineage());
		if (!list.isEmpty())
			return list.get(0).getUuid();
		Provenance p = new Provenance();
		p.setDataCollector(provenance.getDataCollector());
		p.setDataCollectorVersion(provenance.getDataCollectorVersion());
		p.setLineage(provenance.getLineage());
		em.persist(p);
		return p.getUuid();
	}

	public static Provenance findByUuid(EntityManager em, String uuid) {
		List<Provenance> list = find(em, uuid, null, null, null);
		return list.size()==1 ? list.get(0) : null;
	}
}
