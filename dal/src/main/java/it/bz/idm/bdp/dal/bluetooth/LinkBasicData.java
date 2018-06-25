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
 * along with this program (see LICENSE/GPLv3). If not, see
 * <http://www.gnu.org/licenses/>.
 */
package it.bz.idm.bdp.dal.bluetooth;


import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import com.vividsolutions.jts.geom.LineString;

import it.bz.idm.bdp.dal.BasicData;
import it.bz.idm.bdp.dal.Station;

@Table(name="linkbasicdata",schema="intime")
@Entity
public class LinkBasicData extends BasicData{

	@ManyToOne(cascade=CascadeType.ALL)
	private Bluetoothstation origin;

	@ManyToOne(cascade=CascadeType.ALL)
	private Bluetoothstation destination;
	
	private Double length;
	
	@Type(type="org.hibernate.type.StringType")
	private String street_ids_ref;
	
	//@Type(type= "org.hibernate.spatial.JTSGeometryType")
	private LineString linegeometry;
	
	private Integer elapsed_time_default;

	
	public Integer getElapsed_time_default() {
		return elapsed_time_default;
	}

	public void setElapsed_time_default(Integer elapsed_time_default) {
		this.elapsed_time_default = elapsed_time_default;
	}

	public Double getLength() {
		return length;
	}

	public Bluetoothstation getOrigin() {
		return origin;
	}

	public void setOrigin(Bluetoothstation origin) {
		this.origin = origin;
	}

	public Bluetoothstation getDestination() {
		return destination;
	}

	public void setDestination(Bluetoothstation destination) {
		this.destination = destination;
	}

	public LineString getLinegeometry() {
		return linegeometry;
	}

	public void setLinegeometry(LineString linegeometry) {
		this.linegeometry = linegeometry;
	}

	public void setLength(Double length) {
		this.length = length;
	}

	public String getStreet_ids_ref() {
		return street_ids_ref;
	}

	public void setStreet_ids_ref(String street_ids_ref) {
		this.street_ids_ref = street_ids_ref;
	}

	@Override
	public BasicData findByStation(EntityManager em, Station station) {
		// TODO Auto-generated method stub
		return null;
	}

}
