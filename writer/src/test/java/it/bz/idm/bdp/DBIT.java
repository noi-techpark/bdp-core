/**
 * writer - Data Writer for the Big Data Platform
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
package it.bz.idm.bdp;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import javax.persistence.EntityManager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import it.bz.idm.bdp.dal.DataType;
import it.bz.idm.bdp.dal.M;
import it.bz.idm.bdp.dal.Measurement;
import it.bz.idm.bdp.dal.Station;
import it.bz.idm.bdp.dal.authentication.BDPRole;
import it.bz.idm.bdp.dal.util.JPAUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations= {"/META-INF/spring/applicationContext.xml"})
public class DBIT extends AbstractJUnit4SpringContextTests{

	EntityManager em = JPAUtil.createEntityManager();


	@Test
	public void testStationFetch() {
		Station station = Station.findStation(em, "stationtype", "hey");
		assertNull(station);
	}

	@Test
	public void testAdminRole() {
		BDPRole role = BDPRole.fetchAdminRole(em);
		assertNotNull(role);
		assertFalse(role.getUsers()==null || role.getUsers().isEmpty());
	}


	@Test
	public void testFindLatestEntry() {
		BDPRole role = BDPRole.fetchAdminRole(em);
		Integer period = 600;
		DataType type = DataType.findByCname(em, "air-humidity");
		Station station = Station.findStation(em, "EnvironmentStation", "00390SF");
		M latestEntry = new Measurement().findLatestEntry(em, station, type, period, role);
		assertNotNull(latestEntry);
	};
}
