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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import it.bz.idm.bdp.writer.config.PersistenceConfig;

@Import(PersistenceConfig.class)
public class PersistenceIT extends AbstractJUnit4SpringContextTests {

	@PersistenceUnit
	private EntityManagerFactory entityManagerFactory;

	private Map<String,Object> map = new HashMap<>();

	@Before
	public void setup() {
		map.put("He", new Integer(4));
	}

	@Test
	public void testSyncTypes() {
		EntityManager em = entityManagerFactory.createEntityManager();
		DataType type = new DataType("customType-2","customUnit","customDescription","customRType");
		DataTypeMetaData metadata = new DataTypeMetaData(type,map);
		type.setMetaData(metadata);
		type.getMetaDataHistory().add(metadata);
		em.persist(type);
		List<DataType> types = new ArrayList<DataType>();
		types.add(type);
	}
}
