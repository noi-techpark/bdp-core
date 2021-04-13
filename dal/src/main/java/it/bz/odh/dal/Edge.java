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
package it.bz.odh.dal;

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

/**
 * This class is a model of an edge with meta data.
 *
 * <p>We do not want to change the API for v2, hence we need to keep edges
 * as stations to retrieve "edge labels" with station API calls. This means that we use
 * "station" as container class.
 *
 * <p> An edge is therefore a station-triple like <code>(origin, destination, edge_data)</code>,
 * and some additional information, like directed, which describes if the edge has a direction, and
 * a line-geometry, which describe trajectories on a map. Some polylines on a map are not connected
 * to any station, therefore we need to make "origin" and "destination" optional, that is, nullable.
 *
 * @author Peter Moser
 */
@Table(name = "edge")
@Entity
public class Edge {

	public static final String GEOM_CRS = "EPSG:4326";
	public static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory(new PrecisionModel(), 4326);

	@Id
	@GeneratedValue(generator = "edge_gen", strategy = GenerationType.SEQUENCE)
	@SequenceGenerator(name = "edge_gen", sequenceName = "edge_seq", allocationSize = 1)
	@ColumnDefault(value = "nextval('edge_seq')")
	protected Long id;

	@Column(nullable = true)
	protected LineString linegeometry;

	@Column(nullable = false)
	@ColumnDefault(value = "true")
	protected boolean directed;

	@ManyToOne(optional = true)
	protected Station origin;

	@ManyToOne(optional = true)
	protected Station destination;

	/*
	 * FIXME This should be changed to use meta data separately, and point from edges
	 * and stations (=nodes) to it directly. We must think of how to group historical
	 * meta data records inside the meta data table itself.
	 */
	@OneToOne(optional = true)
	protected Station edgeData;
}
