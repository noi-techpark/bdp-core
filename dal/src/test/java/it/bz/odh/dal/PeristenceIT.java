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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.junit.Before;
import org.junit.Test;

import it.bz.odh.dal.DataType;
import it.bz.odh.dal.DataTypeMetaData;
import it.bz.odh.dal.util.JPAUtil;

public class PeristenceIT {
	private EntityManager em = JPAUtil.createEntityManager();
	private Map<String,Object> map = new HashMap<String, Object>();

	@Before
	public void setup() {
		map.put("He", new Integer(4));
	}

	@Test
	public void testSyncTypes() {
		DataType type = new DataType("customType-2","customUnit","customDescription","customRType");
		DataTypeMetaData metadata = new DataTypeMetaData(type,map);
		type.setMetaData(metadata);
		type.getMetaDataHistory().add(metadata);
		em.persist(type);
		List<DataType> types = new ArrayList<DataType>();
		types.add(type);
	}
}
