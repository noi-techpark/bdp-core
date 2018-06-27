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
package it.bz.idm.bdp.dal.charger;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.ManyToOne;
import javax.persistence.TypedQuery;

import it.bz.idm.bdp.dal.BasicData;
import it.bz.idm.bdp.dal.Station;
import it.bz.idm.bdp.dal.util.JPAUtil;

@Entity
public class EChargingPlugBasicData extends BasicData{

	@ManyToOne
	private EChargingStation eStation;


	public EChargingStation geteStation() {
		return eStation;
	}

	public void seteStation(EChargingStation eStation) {
		this.eStation = eStation;
	}


	public static List<EChargingPlugBasicData> findAllPlugs(
			EntityManager em) {
		TypedQuery<EChargingPlugBasicData> query = em.createQuery("Select basic from EChargingPlugBasicData basic where basic.station.active=true", EChargingPlugBasicData.class);
		return query.getResultList();
	}

	@Override
	public BasicData findByStation(EntityManager em, Station station) {
		TypedQuery<EChargingPlugBasicData> typedQuery = em.createQuery("select basic from EChargingPlugBasicData basic where basic.station = :station and basic.station.active=:active",EChargingPlugBasicData.class);
		typedQuery.setParameter("station", station);
		typedQuery.setParameter("active",true);
		return JPAUtil.getSingleResultOrNull(typedQuery);
	}


}
