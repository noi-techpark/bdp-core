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
package it.bz.idm.bdp.dal.bikesharing;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.ManyToOne;
import javax.persistence.TypedQuery;

import it.bz.idm.bdp.dal.BasicData;
import it.bz.idm.bdp.dal.DataType;
import it.bz.idm.bdp.dal.Station;
import it.bz.idm.bdp.dal.util.JPAUtil;

@Entity
public class Bicyclebasicdata extends BasicData {

	@ManyToOne(cascade=CascadeType.MERGE)
	private DataType type;

	@ManyToOne(cascade = CascadeType.MERGE)
	private BikesharingStation bikeSharingStation;

	public Bicyclebasicdata() {
	}

	public DataType getType() {
		return type;
	}

	public void setType(DataType type) {
		this.type = type;
	}

	public BikesharingStation getBikeSharingStation() {
		return bikeSharingStation;
	}

	public void setBikeSharingStation(BikesharingStation bikeSharingStation) {
		this.bikeSharingStation = bikeSharingStation;
	}
	@Override
	public BasicData findByStation(EntityManager em, Station station) {
		TypedQuery<Bicyclebasicdata> query = em.createQuery("Select basicData from Bicyclebasicdata basicData where basicData.station=:station", Bicyclebasicdata.class);
		query.setParameter("station", station);
		return JPAUtil.getSingleResultOrNull(query);

	}
	public static List<Bicyclebasicdata> findAllBikes(
			EntityManager em) {
		TypedQuery<Bicyclebasicdata> query = em.createQuery("Select basic from Bicyclebasicdata basic where basic.station.active=true", Bicyclebasicdata.class);
		return query.getResultList();
	}

}
