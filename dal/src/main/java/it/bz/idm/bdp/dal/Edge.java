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
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.ColumnDefault;

import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.PrecisionModel;

@Table(name = "edge")
@Entity
public class Edge {

	public static final String GEOM_CRS = "EPSG:4326";
	public static GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

	@Id
	@GeneratedValue(generator = "edge_gen", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "edge_gen", sequenceName = "edge_seq", schema = "intime", allocationSize = 1)
	@ColumnDefault(value = "nextval('intime.edge_seq')")
	protected Long id;

	protected LineString linegeometry;

	@Column(nullable = false)
	@ColumnDefault(value = "true")
	protected boolean directed;

	@ManyToOne(optional = false)
	protected Station origin;

	@ManyToOne(optional = false)
	protected Station destination;

	/**
	 * We model an edge with meta data, therefore we use "station" as container class.
	 * In addition, we do not want to change the API for v2, hence we need to keep edges
	 * as stations to retrieve "edge labels" with station API calls.
	 *
	 * FIXME This should be changed to use meta data separately, and point from edges
	 * and stations (=nodes) to it directly. We must think of how to group historical
	 * meta data records inside the meta data table itself.
	 */
	@OneToOne(optional = true)
	protected Station edgeData;
}
