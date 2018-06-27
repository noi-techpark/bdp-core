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
package it.bz.idm.bdp.dal.carpooling;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.OneToMany;
import javax.persistence.TypedQuery;

import it.bz.idm.bdp.dal.BasicData;
import it.bz.idm.bdp.dal.Station;
import it.bz.idm.bdp.dal.util.JPAUtil;
@Entity
public class CarpoolinghubBasicData extends BasicData{

	@Override
	public BasicData findByStation(EntityManager em, Station station) {
		TypedQuery<CarpoolinghubBasicData> query = em.createQuery("Select basic from CarpoolinghubBasicData basic where basic.station=:station", CarpoolinghubBasicData.class);
		query.setParameter("station", station);
		return JPAUtil.getSingleResultOrNull(query);
	}

	@OneToMany(cascade = CascadeType.ALL)
	private Map<Locale, Translation> i18n = new HashMap<Locale, Translation>();

	public Map<Locale, Translation> getI18n() {
		return i18n;
	}

	public void setI18n(Map<Locale, Translation> i18n) {
		this.i18n = i18n;
	}
}
